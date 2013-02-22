package com.zig.interview.util;

import java.io.File;

import com.zig.interview.info.FileInfo;
import com.zig.interview.interfaces.MyPlayer;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MyMediaPlayer extends MyPlayer {
	FileInfo fileInfo;
	MediaPlayer player;

	public MyMediaPlayer(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
		player = new MediaPlayer();
	}

	@Override
	public void play() {
		try {
			player.reset();
			player.setDataSource(fileInfo.filePath);
			player.prepare();
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOnCompletionListener(OnCompletionListener listener) {
		player.setOnCompletionListener(listener);
	}

	@Override
	public void stop() {

	}

	@Override
	public int getDuration() {
		return player.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

}
