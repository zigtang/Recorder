package com.zig.interview;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.zig.interview.info.FileInfo;
import com.zig.interview.interfaces.MyPlayer;
import com.zig.interview.util.MyMediaPlayer;
import com.zig.interview.util.MyTimer;

public class FileManageActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener,
		OnCompletionListener {

	ArrayList<FileInfo> fileList;
	ListView lv;
	Adapter adapter;
	SeekBar sb;
	final int DIALOG_CHOOSE = 100;
	final int DIALOG_DELETE = 101;
	final int DIALOG_RENAME = 102;
	final int DIALOG_UPLOAD = 103;
	Handler handler = new Handler();
	Runnable updateSeekBar;
	MyTimer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_manage);
		fileList = getFiles();
		lv = (ListView) findViewById(R.id.lv_file);
		mTimer = (MyTimer) findViewById(R.id.mTimer);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);

		sb = (SeekBar) findViewById(R.id.sb);
		// sb.setOnSeekBarChangeListener(this);
		sb.setEnabled(false);

		updateSeekBar = new Runnable() {
			@Override
			public void run() {
				System.out.println("Runnable:" + System.currentTimeMillis());
				sb.setProgress(player.getCurrentPosition());
				handler.postDelayed(updateSeekBar, 100);
			}
		};

		showToast("单击播放，长按：删除，重命名");
		((Button) findViewById(R.id.btn_title_left)).setText("录音界面");
	}

	// private void startTimer() {
	//
	// Timer mTimer = new Timer();
	// TimerTask task = new TimerTask() {
	// @Override
	// public void run() {
	// System.out.println("task:" + System.currentTimeMillis());
	// if (player != null) {
	// System.out.println("setProgress");
	// sb.setProgress(player.getCurrentPosition());
	// }
	// }
	// };
	// mTimer.schedule(task, 0, 500);
	//
	// }

	private ArrayList<FileInfo> getFiles() {
		ArrayList<FileInfo> tempList = new ArrayList<FileInfo>();
		File folde = new File(Appconstants.DIR_PATH);
		File[] files = folde.listFiles();
		if (files != null) {
			FileInfo fileInfo = null;
			for (File file : files) {
				fileInfo = new FileInfo();
				fileInfo.fileName = file.getName();
				fileInfo.size = "" + file.length();
				fileInfo.time = "" + file.lastModified();
				fileInfo.filePath = file.getAbsolutePath();
				tempList.add(fileInfo);
			}
		}
		return tempList;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			this.finish();
			break;
		case R.id.btn_title_right:
			startActivity(new Intent(this, SettingActivity.class));
			this.finish();
			break;
		}
	}

	class Adapter extends BaseAdapter {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH时mm分ss");

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_file, null);
			}
			((TextView) convertView.findViewById(R.id.tv_item_name)).setText(fileList.get(position).fileName);
			((TextView) convertView.findViewById(R.id.tv_item_time)).setText(sdf.format(new Date(Long
					.parseLong(fileList.get(position).time))));
			((TextView) convertView.findViewById(R.id.tv_item_size))
					.setText(getFileSizeString(fileList.get(position).size));

			return convertView;
		}

		DecimalFormat df = new java.text.DecimalFormat("#.00");

		private String getFileSizeString(String sizeStr) {
			long size = Long.parseLong(sizeStr);
			String str;
			if (size < 1024) {
				str = size + "B";
			} else if (size < 1024 * 1024) {
				str = df.format(size / 1024.00) + "KB";
			} else {
				str = df.format(size / (1024 * 1024.00)) + "MB";
			}
			return str;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		playOrPause(position);
	}

	MyPlayer player;

	private void playOrPause(int position) {

		if (getFileSuffix(position).equals(".pcm")) {
			useAudioTrack(position);
		} else {
			useMediaplayer(position);
		}

	}

	private void useAudioTrack(int position) {
		try {
			int sampleRateInHz = 44100;
			int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
			int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
			int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

			int streamType = AudioManager.STREAM_MUSIC;
			int mode = AudioTrack.MODE_STREAM;
			AudioTrack audioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat,
					bufferSizeInBytes, mode);

			audioTrack.play();
			byte[] audioData = new byte[bufferSizeInBytes];
			FileInputStream fis = new FileInputStream(new File(fileList.get(position).filePath));

			while (fis.read(audioData) != -1) {
				audioTrack.write(audioData, 0, bufferSizeInBytes);
			}

			fis.close();
			audioTrack.stop();
			audioTrack.release();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void useMediaplayer(int position) {
		player = new MyMediaPlayer(fileList.get(position));
		player.play();
		player.setOnCompletionListener(this);
		sb.setEnabled(true);
		sb.setProgress(0);
		sb.setMax(player.getDuration());
		handler.post(updateSeekBar);
		mTimer.start();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		showChooseDialog(position);
		return false;
	}

	// @Override
	// protected Dialog onCreateDialog(int id, final Bundle bundle) {
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// final int position = bundle.getInt("position");
	// switch (id) {
	// case DIALOG_CHOOSE:
	//
	// break;
	// case DIALOG_DELETE:
	//
	// break;
	//
	// case DIALOG_RENAME:
	//
	// break;
	//
	// case DIALOG_UPLOAD:
	//
	// break;
	// }
	//
	// dialog = builder.create();
	// return dialog;
	// }
	private void uploadFile(int position) {
		showToast("缺少服务器支持~");
	}

	private void renameFile(int position, String fileName) {
		if (fileName.contains(".") || fileName.trim().equals("")) {
			showToast("文件名不规范!");
			return;
		}
		System.out.println("renameFile");
		File oldFile = new File(fileList.get(position).filePath);
		File newFile = new File(Appconstants.DIR_PATH, fileName + getFileSuffix(position));
		if (oldFile.renameTo(newFile)) {
			showToast("重命名文件成功~");
			refreshList();
		} else {
			showToast("重命名文件失败~");
		}
	}

	private void deleteFile(int position) {
		File file = new File(fileList.get(position).filePath);
		if (file.delete()) {
			showToast("删除文件成功~");
			refreshList();
		} else {
			showToast("删除文件失败~");
		}

	}

	private void refreshList() {
		fileList = getFiles();
		adapter.notifyDataSetChanged();
	}

	private String getFileSuffix(int position) {
		String temp = ".pcm";
		String fileName = fileList.get(position).fileName;
		String[] str = fileName.split("\\.");
		if (str.length == 2) {
			temp = "." + str[1];// 有可能取得错的后缀~ 源自不规范的命名
		}
		return temp;

	}

	private void showChooseDialog(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String[] str = new String[] { "删除", "重命名", "上传" };
		builder.setTitle("选择您要的操作：");
		builder.setSingleChoiceItems(str, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					showDeleteDialog(position);
					break;
				case 1:
					showRenameDialog(position);
					break;
				case 2:
					uploadFile(position);
					break;
				}
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	private void showDeleteDialog(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("删除确认");
		builder.setMessage("确定删除文件：" + fileList.get(position).fileName + "?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteFile(position);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	private void showRenameDialog(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.rename_dialog, null);
		String suffix = getFileSuffix(position);
		((TextView) view.findViewById(R.id.tv_rename)).setText(suffix);
		final EditText et = (EditText) view.findViewById(R.id.et_rename);
		et.setHint(fileList.get(position).fileName.substring(0, fileList.get(position).fileName.length() - 4));
		builder.setView(view);
		builder.setTitle("输入新文件名");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String fileName = et.getText().toString();
				renameFile(position, fileName);
			}
		});

		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		sb.setProgress(0);
		sb.setEnabled(false);
		handler.removeCallbacks(updateSeekBar);
		mTimer.stop();
	}
}
