package com.watabou.noosa;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;

public class Halo extends Image {
	
	private static final Object CACHE_KEY = Halo.class;
	
	protected static final int RADIUS	= 64;
	
	protected float radius = RADIUS;
	protected float brightness = 1;

	public Halo() {
		super();
		
		if (!TextureCache.contains( CACHE_KEY )) {
			Pixmap pixmap = new Pixmap(RADIUS * 2, RADIUS * 2, Pixmap.Format.RGBA8888);
			pixmap.setColor( 0xFFFFFF0A );
			for (int i = 0; i < 50; i++) {
				pixmap.fillCircle(RADIUS, RADIUS, (int)(RADIUS * (i+1)/50f));
			}
			TextureCache.add( CACHE_KEY, new SmartTexture( pixmap ) );
		}
		
		texture( CACHE_KEY );
	}
	
	public Halo( float radius, int color, float brightness ) {
		
		this();
		
		hardlight( color );
		alpha( this.brightness = brightness );
		radius( radius );
	}
	
	public Halo point( float x, float y ) {
		this.x = x - (width()/2f);
		this.y = y - (height()/2f);
		return this;
	}
	
	public void radius( float value ) {
		scale.set(  (this.radius = value) / RADIUS );
	}
}
