package com.watabou.noosa.audio;

import com.badlogic.gdx.Gdx;
import com.watabou.noosa.Game;

public enum Music {
	
	INSTANCE;
	
	private com.badlogic.gdx.audio.Music player;
	
	private String lastPlayed;
	private boolean looping;
	
	private boolean enabled = true;
	private float volume = 1f;
	
	public synchronized void play( String assetName, boolean looping ) {
		
		if (isPlaying() && lastPlayed != null && lastPlayed.equals( assetName )) {
			return;
		}
		
		stop();
		
		lastPlayed = assetName;
		this.looping = looping;

		if (!enabled || assetName == null) {
			return;
		}
		
		try {
			player = Gdx.audio.newMusic(Gdx.files.internal(assetName));
			player.setLooping(looping);
			player.setVolume(volume);
			player.play();
		} catch (Exception e){
			Game.reportException(e);
			player = null;
		}
		
	}
	
	public synchronized void mute() {
		lastPlayed = null;
		stop();
	}
	
	public synchronized void pause() {
		if (player != null) {
			player.pause();
		}
	}
	
	public synchronized void resume() {
		if (player != null) {
			player.play();
			player.setLooping(looping);
		}
	}
	
	public synchronized void stop() {
		if (player != null) {
			player.stop();
			player.dispose();
			player = null;
		}
	}
	
	public synchronized void volume( float value ) {
		volume = value;
		if (player != null) {
			player.setVolume( value );
		}
	}
	
	public synchronized boolean isPlaying() {
		return player != null && player.isPlaying();
	}
	
	public synchronized void enable( boolean value ) {
		enabled = value;
		if (isPlaying() && !value) {
			stop();
		} else
		if (!isPlaying() && value) {
			play( lastPlayed, looping );
		}
	}
	
	public synchronized boolean isEnabled() {
		return enabled;
	}
	
}
