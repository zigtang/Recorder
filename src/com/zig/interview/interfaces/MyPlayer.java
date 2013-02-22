package com.zig.interview.interfaces;

import android.media.MediaPlayer.OnCompletionListener;

public abstract class MyPlayer {
	public abstract void play();

	public abstract void stop();

	public abstract int getDuration();

	public abstract int getCurrentPosition();

	public abstract void setOnCompletionListener(OnCompletionListener listener);

}
