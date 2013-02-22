package com.zig.interview.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zig.interview.R;

public class MyTimer extends LinearLayout {

	TextView tvMin;
	TextView tvSec;

	public MyTimer(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context, R.layout.time_min_sec, this);
		tvMin = (TextView) findViewById(R.id.min);
		tvSec = (TextView) findViewById(R.id.sec);
	}

	public void start() {
		reset();
		handler.postDelayed(runnable, 1000);
	}

	public void stop() {
		handler.removeCallbacks(runnable);
		reset();
	}

	int min, sec;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			handler.sendEmptyMessage(++sec);
			handler.postDelayed(runnable, 1000);
		}
	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			sec %= 60;
			if (sec == 0) {
				min++;
				tvMin.setText((min < 10 ? "0" : "") + min);
			}

			tvSec.setText((sec < 10 ? "0" : "") + sec);
		}

	};

	private void reset() {
		min = 0;
		sec = 0;
		tvSec.setText("00");
		tvMin.setText("00");
	}

	public void setTextSize(int size) {
		tvSec.setTextSize(size);
		tvMin.setTextSize(size);
	}

}
