package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;

public class Framebuffer {

	public static final int COLOR	= Gdx.gl.GL_COLOR_ATTACHMENT0;
	public static final int DEPTH	= Gdx.gl.GL_DEPTH_ATTACHMENT;
	public static final int STENCIL	= Gdx.gl.GL_STENCIL_ATTACHMENT;
	
	public static final Framebuffer	system	= new Framebuffer( 0 );
	
	private int id;
	
	public Framebuffer() {
		id = Gdx.gl.glGenBuffer();
	}
	
	private Framebuffer( int n ) {
		
	}
	
	public void bind() {
		Gdx.gl.glBindFramebuffer( Gdx.gl.GL_FRAMEBUFFER, id );
	}
	
	public void delete() {
		Gdx.gl.glDeleteBuffer(id);
	}
	
	public void attach( int point, Texture tex ) {
		bind();
		Gdx.gl.glFramebufferTexture2D( Gdx.gl.GL_FRAMEBUFFER, point, Gdx.gl.GL_TEXTURE_2D, tex.id, 0 );
	}
	
	public void attach( int point, Renderbuffer buffer ) {
		bind();
		Gdx.gl.glFramebufferRenderbuffer( Gdx.gl.GL_RENDERBUFFER, point, Gdx.gl.GL_TEXTURE_2D, buffer.id() );
	}
	
	public boolean status() {
		bind();
		return Gdx.gl.glCheckFramebufferStatus( Gdx.gl.GL_FRAMEBUFFER ) == Gdx.gl.GL_FRAMEBUFFER_COMPLETE;
	}
}
