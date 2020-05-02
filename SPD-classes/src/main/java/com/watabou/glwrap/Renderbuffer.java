package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;

public class Renderbuffer {

	public static final int RGBA8		= Gdx.gl.GL_RGBA;	// ?
	public static final int DEPTH16		= Gdx.gl.GL_DEPTH_COMPONENT16;
	public static final int STENCIL8	= Gdx.gl.GL_STENCIL_INDEX8;
	
	private final int id;
	
	public Renderbuffer() {
		id = Gdx.gl.glGenRenderbuffer();
	}
	
	public int id() {
		return id;
	}
	
	public void bind() {
		Gdx.gl.glBindRenderbuffer( Gdx.gl.GL_RENDERBUFFER, id );
	}
	
	public void delete() {
		Gdx.gl.glDeleteRenderbuffer( id );
	}
	
	public void storage( int format, int width, int height ) {
		Gdx.gl.glRenderbufferStorage( Gdx.gl.GL_RENDERBUFFER, format , width, height );
	}
}
