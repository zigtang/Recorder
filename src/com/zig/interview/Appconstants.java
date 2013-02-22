package com.zig.interview;

import java.io.File;

import android.os.Environment;

public class Appconstants {
	public final static String DIR_PATH = Environment.getExternalStorageDirectory() + File.separator + "Vobile";

	// setting
	public final static String PREFERCE_NAME_FORMAT = "sharedPreferenceFormat";
	public final static String PREFERCE_ITEM_FORMAT = "selectedFormat";
	public final static int FORMAT_PCM = 100;
	public final static int FORMAT_WAV = 101;
	public final static int FORMAT_AMR = 102;
	public final static int FORMAT_AAC = 103;

}
