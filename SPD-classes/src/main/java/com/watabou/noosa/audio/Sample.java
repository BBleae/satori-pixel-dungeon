package com.watabou.noosa.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public enum Sample {

	INSTANCE;

	protected HashMap<Object, Sound> ids = new HashMap<>();

	private boolean enabled = true;
	private float globalVolume = 1f;

	public void reset() {

		for (Sound sound : ids.values()){
			sound.dispose();
		}
		
		ids.clear();

	}

	public void pause() {
		for (Sound sound : ids.values()) {
			sound.pause();
		}
	}

	public void resume() {
		for (Sound sound : ids.values()) {
			sound.resume();
		}
	}

	public void load( String... assets ) {

		//FIXME there used to be a queue here so that assets were loaded async.
		//This was to prevent hanging on specific android versions (implement in vanilla v1.7.5)
		//Maybe LibGDX already handles this?
		for (String asset : assets){
			if (!ids.containsKey(asset)){
				ids.put(asset, Gdx.audio.newSound(Gdx.files.internal(asset)));
			}
		}
		
	}

	public void unload( Object src ) {
		if (ids.containsKey( src )) {
			ids.get( src ).dispose();
			ids.remove( src );
		}
	}

	public long play( Object id ) {
		return play( id, 1 );
	}

	public long play( Object id, float volume ) {
		return play( id, volume, volume, 1 );
	}
	
	public long play( Object id, float volume, float pitch ) {
		return play( id, volume, volume, pitch );
	}
	
	public long play( Object id, float leftVolume, float rightVolume, float pitch ) {
		float volume = Math.max(leftVolume, rightVolume);
		float pan = rightVolume - leftVolume;
		if (enabled && ids.containsKey( id )) {
			return ids.get(id).play( globalVolume*volume, pitch, pan );
		} else {
			return -1;
		}
	}

	public void enable( boolean value ) {
		enabled = value;
	}

	public void volume( float value ) {
		globalVolume = value;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
}