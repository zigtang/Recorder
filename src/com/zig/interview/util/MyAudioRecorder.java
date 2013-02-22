package com.zig.interview.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.zig.interview.Appconstants;
import com.zig.interview.interfaces.MyRecorder;

import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MyAudioRecorder extends MyRecorder {

	private final int sampleRateInHz = 44100;
	private final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
	private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	private final int audioSource = MediaRecorder.AudioSource.MIC;

	private int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
	private AudioRecord audioRecord;
	private boolean isRecording = false;
	private String rawFileName;

	private boolean isPCM = true;

	public MyAudioRecorder(int fileFormat) {
		super();
		if (fileFormat == Appconstants.FORMAT_PCM) {
			isPCM = true;
		} else {
			isPCM = false;
		}
	}

	public void startRecord() {
		audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
		setMinMaxVolume(1500, 3500);
		isRecording = true;
		audioRecord.startRecording();
		rawFileName = System.currentTimeMillis() + ".pcm";

		new Thread() {
			@Override
			public void run() {
				System.out.println("storeRawData start");
				storeRawData();
				System.out.println("storeRawData end");
			}

		}.start();

	}

	public void stopRecord() {
		if (audioRecord != null && isRecording) {
			isRecording = false;
			audioRecord.stop();
			audioRecord.release();
		}
	}

	public void storeRawData() {
		try {
			byte[] audioData = new byte[bufferSizeInBytes];
			FileOutputStream fos = null;
			int readSize = 0;

			File file = new File(Appconstants.DIR_PATH, rawFileName);
			fos = new FileOutputStream(file);

			while (isRecording) {
				readSize = audioRecord.read(audioData, 0, bufferSizeInBytes);
				if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {
					fos.write(audioData);
				}
			}

			if (isPCM) {// 如果是PCM 直接输出
				fos.flush();
			} else {// AAc
				addWavHead();
			}

			fos.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void addWavHead() {
		try {
			File outFile = new File(Appconstants.DIR_PATH, System.currentTimeMillis() + ".wav");
			FileOutputStream fos = new FileOutputStream(outFile);
			File inFile = new File(Appconstants.DIR_PATH, rawFileName);
			FileInputStream fis = new FileInputStream(inFile);
			long totalAudioLength = fis.getChannel().size();
			long totalDataLength = totalAudioLength + 36;
			int channels = 2;
			long byteRate = 16 * sampleRateInHz * channels / 8;

			{
				byte[] header = new byte[44];
				header[0] = 'R'; // RIFF/WAVE header
				header[1] = 'I';
				header[2] = 'F';
				header[3] = 'F';
				header[4] = (byte) (totalDataLength & 0xff);
				header[5] = (byte) ((totalDataLength >> 8) & 0xff);
				header[6] = (byte) ((totalDataLength >> 16) & 0xff);
				header[7] = (byte) ((totalDataLength >> 24) & 0xff);
				header[8] = 'W';
				header[9] = 'A';
				header[10] = 'V';
				header[11] = 'E';
				header[12] = 'f'; // 'fmt ' chunk
				header[13] = 'm';
				header[14] = 't';
				header[15] = ' ';
				header[16] = 16; // 4 bytes: size of 'fmt ' chunk
				header[17] = 0;
				header[18] = 0;
				header[19] = 0;
				header[20] = 1; // format = 1
				header[21] = 0;
				header[22] = (byte) channels;
				header[23] = 0;
				header[24] = (byte) (sampleRateInHz & 0xff);
				header[25] = (byte) ((sampleRateInHz >> 8) & 0xff);
				header[26] = (byte) ((sampleRateInHz >> 16) & 0xff);
				header[27] = (byte) ((sampleRateInHz >> 24) & 0xff);
				header[28] = (byte) (byteRate & 0xff);
				header[29] = (byte) ((byteRate >> 8) & 0xff);
				header[30] = (byte) ((byteRate >> 16) & 0xff);
				header[31] = (byte) ((byteRate >> 24) & 0xff);
				header[32] = (byte) (2 * 16 / 8); // block align
				header[33] = 0;
				header[34] = 16; // bits per sample
				header[35] = 0;
				header[36] = 'd';
				header[37] = 'a';
				header[38] = 't';
				header[39] = 'a';
				header[40] = (byte) (totalAudioLength & 0xff);
				header[41] = (byte) ((totalAudioLength >> 8) & 0xff);
				header[42] = (byte) ((totalAudioLength >> 16) & 0xff);
				header[43] = (byte) ((totalAudioLength >> 24) & 0xff);
				fos.write(header, 0, 44);
			}

			byte[] data = new byte[bufferSizeInBytes];
			while (fis.read(data) != -1) {
				fos.write(data);
			}
			fos.flush();
			inFile.delete();
			fis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getVolume() {

		byte[] buffer = new byte[bufferSizeInBytes];
		// boolean isRun = true;
		// while (isRun) {
		int r = audioRecord.read(buffer, 0, bufferSizeInBytes);
		int v = 0;
		// 将 buffer 内容取出，进行平方和运算
		for (int i = 0; i < buffer.length; i++) {
			// 这里没有做运算的优化，为了更加清晰的展示代码
			v += buffer[i] * buffer[i];
		}
		System.out.println("getVolume:" + (v / r));
		return getVolumeWeight(v / r);
		// }
	}

}
