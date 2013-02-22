package com.zig.interview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.zig.interview.interfaces.MyRecorder;
import com.zig.interview.util.MyAudioRecorder;
import com.zig.interview.util.MyMediaRecorder;
import com.zig.interview.util.MyTimer;

public class RecordActivity extends BaseActivity {
	private MyRecorder mRecorder;

	Runnable volumeRun;
	ImageView volumeBlank;
	ImageView volumeProgress;
	MyTimer myTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		volumeBlank = (ImageView) findViewById(R.id.img_record_blank);
		volumeProgress = (ImageView) findViewById(R.id.img_record_progress);
		myTimer = (MyTimer) findViewById(R.id.mTimer);
		myTimer.setTextSize(30);

		// mRecorder = new MyMediaRecorder();
		volumeRun = new Runnable() {
			@Override
			public void run() {
				System.out.println("changeVolume~");
				handler.sendEmptyMessage(mRecorder.getVolume());
				handler.postDelayed(volumeRun, 100);
			}
		};

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			startActivity(new Intent(this, FileManageActivity.class));
			break;
		case R.id.btn_title_right:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.btn_record_record:
			mRecorder = getRecorder();
			mRecorder.startRecord();
			handler.post(volumeRun);
			myTimer.start();
			break;
		case R.id.btn_record_stop:
			if (mRecorder != null) {
				mRecorder.stopRecord();
				handler.sendEmptyMessage(1);// 将音量设为1
				handler.removeCallbacks(volumeRun);
				myTimer.stop();
				showToast("点击左上角录音管理，查看已录文件");
			}

			break;

		default:
			break;
		}
	}

	// 取音量值为1500--3500之间

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int weight = msg.what;
			volumeBlank.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (100 - weight)));
			volumeProgress.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, weight));
		}

	};

	private MyRecorder getRecorder() {
		MyRecorder temp = null;
		int selectFormat = preferences.getInt(Appconstants.PREFERCE_ITEM_FORMAT, Appconstants.FORMAT_PCM);
		switch (selectFormat) {
		case Appconstants.FORMAT_PCM:
		case Appconstants.FORMAT_WAV:
			temp = new MyAudioRecorder(getFileFormat());
			break;
		case Appconstants.FORMAT_AMR:
		case Appconstants.FORMAT_AAC:
			temp = new MyMediaRecorder(getFileFormat());
			break;
		}
		return temp;
	}

}
