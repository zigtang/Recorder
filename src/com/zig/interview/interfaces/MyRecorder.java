package com.zig.interview.interfaces;

import java.io.File;

import com.zig.interview.Appconstants;

public abstract class MyRecorder {
	String folderPath = Appconstants.DIR_PATH;

	protected MyRecorder() {
		checkFolder();
	}

	public abstract void startRecord();

	public abstract void stopRecord();

	public abstract int getVolume();

	protected void checkFolder() {
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	int MIN_VOLUME;
	int MAX_VOLUME;

	protected int getVolumeWeight(int volume) {
		int vaule = volume - MIN_VOLUME;
		int weight = 1;
		if (vaule > 0) {
			weight = vaule * 100 / (MAX_VOLUME - MIN_VOLUME);
		}
		System.out.println("volume-weight:" + weight);
		return weight;
	}

	protected void setMinMaxVolume(int min, int max) {
		MIN_VOLUME = min;
		MAX_VOLUME = max;
		System.out.println("Max:" + MAX_VOLUME + "  Min:" + MIN_VOLUME);
	}

}
