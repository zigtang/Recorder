package com.zig.interview.util;

import java.io.File;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.zig.interview.Appconstants;
import com.zig.interview.interfaces.MyRecorder;

public class MyMediaRecorder extends MyRecorder {
	int audioSource = MediaRecorder.AudioSource.MIC;
	int outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
	int audioEncoder = MediaRecorder.AudioEncoder.AMR_NB;
	MediaRecorder mRecorder;

	private boolean isAmr = true;

	public MyMediaRecorder(int fileFormat) {
		super();
		if (fileFormat == Appconstants.FORMAT_AMR) {
			isAmr = true;
		} else {
			isAmr = false;
		}
		audioEncoder = isAmr ? MediaRecorder.AudioEncoder.AMR_NB : MediaRecorder.AudioEncoder.AAC;
	}

	boolean isRecording = false;

	public void startRecord() {
		try {
			isRecording = true;
			initRecorder();
			setMinMaxVolume(0, 30000);
			mRecorder.prepare();// 调用start开始录音之前，一定要调用prepare方法。
			mRecorder.start();
		} catch (Exception e) {
			Log.e("MyMediaRecoder", "error:  startRecord()  ");
			e.printStackTrace();
		}
	}

	public void stopRecord() {
		if (mRecorder != null && isRecording) {
			isRecording = false;
			mRecorder.stop();
			mRecorder.release();
		}
	}

	// public void onPause(){
	// if (mRecorder != null) {
	// mRecorder.
	// }
	// }

	private void initRecorder() {
		mRecorder = new MediaRecorder();
		/**
		 * mediaRecorder.setAudioSource设置声音来源。 MediaRecorder.AudioSource这个内部类详细的介绍了声音来源。
		 * 该类中有许多音频来源，不过最主要使用的还是手机上的麦克风，MediaRecorder.AudioSource.MIC
		 */
		mRecorder.setAudioSource(audioSource);
		/**
		 * mediaRecorder.setOutputFormat代表输出文件的格式。该语句必须在setAudioSource之后， 在prepare之前。
		 * OutputFormat内部类，定义了音频输出的格式，主要包含MPEG_4、THREE_GPP、RAW_AMR……等。
		 */
		mRecorder.setOutputFormat(outputFormat);
		/**
		 * mediaRecorder.setAddioEncoder()方法可以设置音频的编码 AudioEncoder内部类详细定义了两种编码：AudioEncoder.DEFAULT、AudioEncoder.AMR_NB
		 */
		mRecorder.setAudioEncoder(audioEncoder);
		/**
		 * 设置录音之后，保存音频文件的位置
		 */
		mRecorder.setOutputFile(initRecordFile());
	}

	private String initRecordFile() {
		String fileName = "" + System.currentTimeMillis();
		String suffix = isAmr ? ".amr" : ".aac";
		fileName += suffix;
		// switch (audioEncoder) {
		// case MediaRecorder.AudioEncoder.DEFAULT:
		// case MediaRecorder.AudioEncoder.AMR_NB:
		// fileName += ".amr";
		// break;
		// case MediaRecorder.AudioEncoder.AAC:
		// fileName += ".aac";
		// break;
		// }

		File file = new File(Appconstants.DIR_PATH, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return file.getAbsolutePath();
	}

	// AudioRecord audioRecord;
	// int bufferSizeInBytes;

	// private void initAudioRecord() {
	// int sampleRateInHz = 44100;
	// int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
	// int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	// bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
	// audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
	// }

	public int getVolume() {

		// audioRecord.startRecording();
		//
		// byte[] buffer = new byte[bufferSizeInBytes];
		// // boolean isRun = true;
		// // while (isRun) {
		// int r = audioRecord.read(buffer, 0, bufferSizeInBytes);
		// int v = 0;
		// // 将 buffer 内容取出，进行平方和运算
		// for (int i = 0; i < buffer.length; i++) {
		// // 这里没有做运算的优化，为了更加清晰的展示代码
		// v += buffer[i] * buffer[i];
		// }
		// System.out.println("getVolume:" + (v / r));
		// return v / r;
		// }
		int volume = 10 * mRecorder.getMaxAmplitude();

		System.out.println("volume:" + volume);
		return getVolumeWeight(volume);
	}

}
