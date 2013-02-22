package com.zig.interview;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class SettingActivity extends BaseActivity {

	int selectFormat = 0;
	RadioButton rbPcm, rbWav, rbAmr, rbAac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		preferences = getSharedPreferences(Appconstants.PREFERCE_NAME_FORMAT, 0);
		selectFormat = getFileFormat();
		findviews();
		setRadioButton();
		((Button) findViewById(R.id.btn_title_right)).setText("录音界面");
	}

	private void findviews() {
		rbPcm = (RadioButton) findViewById(R.id.rb_pcm);
		rbWav = (RadioButton) findViewById(R.id.rb_wav);
		rbAmr = (RadioButton) findViewById(R.id.rb_amr);
		rbAac = (RadioButton) findViewById(R.id.rb_aac);
	}

	private void setRadioButton() {
		switch (selectFormat) {
		case Appconstants.FORMAT_PCM:
			rbPcm.setChecked(true);
			break;
		case Appconstants.FORMAT_WAV:
			rbWav.setChecked(true);
			break;
		case Appconstants.FORMAT_AMR:
			rbAmr.setChecked(true);
			break;
		case Appconstants.FORMAT_AAC:
			rbAac.setChecked(true);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_aac:
			selectFormat = Appconstants.FORMAT_AAC;
			break;
		case R.id.rb_amr:
			selectFormat = Appconstants.FORMAT_AMR;
			break;
		case R.id.rb_pcm:
			selectFormat = Appconstants.FORMAT_PCM;
			break;
		case R.id.rb_wav:
			selectFormat = Appconstants.FORMAT_WAV;
			break;

		case R.id.btn_title_left:
			startActivity(new Intent(this, FileManageActivity.class));
			this.finish();
			break;
		case R.id.btn_title_right:
			this.finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Editor editor = preferences.edit();
		editor.putInt(Appconstants.PREFERCE_ITEM_FORMAT, selectFormat);
		editor.commit();
	}

}
