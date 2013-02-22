package com.zig.interview;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class BaseActivity extends Activity {
	protected SharedPreferences preferences;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_choose_operation, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		preferences = getSharedPreferences(Appconstants.PREFERCE_NAME_FORMAT, 0);
	}

	public void onClick(View v) {
	}

	public void showToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public int getFileFormat() {
		return preferences.getInt(Appconstants.PREFERCE_ITEM_FORMAT, Appconstants.FORMAT_AMR);
	}
}
