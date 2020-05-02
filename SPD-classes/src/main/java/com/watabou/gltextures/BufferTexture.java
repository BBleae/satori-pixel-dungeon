package com.watabou.gltextures;

import com.badlogic.gdx.Gdx;
import com.watabou.glwrap.Texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

//provides a native intbuffer implementation because pixmap is too slow
//TODO: should evaluate this again, seeing as I've moved to LibGDX
public class BufferTexture extends SmartTexture {
	
	public IntBuffer pixels;
	
	public BufferTexture(int w, int h) {
		super();
		width = w;
		height = h;
		pixels = ByteBuffer.
				allocateDirect( w * h * 4 ).
				order( ByteOrder.nativeOrder() ).
				asIntBuffer();
	}
	
	@Override
	protected void generate() {
		id = Gdx.gl.glGenTexture();
	}
	
	@Override
	public void reload() {
		super.reload();
		update();
	}
	
	public void update(){
		bind();
		filter( Texture.LINEAR, Texture.LINEAR );
		wrap( Texture.CLAMP, Texture.CLAMP);
		pixels.position(0);
		Gdx.gl.glTexImage2D(
				Gdx.gl.GL_TEXTURE_2D,
				0,
				Gdx.gl.GL_RGBA,
				width,
				height,
				0,
				Gdx.gl.GL_RGBA,
				Gdx.gl.GL_UNSIGNED_BYTE,
				pixels );
	}
	
	//allows partially updating the texture
	public void update(int top, int bottom){
		bind();
		filter( Texture.LINEAR, Texture.LINEAR );
		wrap( Texture.CLAMP, Texture.CLAMP);
		pixels.position(top*width);
		Gdx.gl.glTexSubImage2D(Gdx.gl.GL_TEXTURE_2D,
				0,
				0,
				top,
				width,
				bottom - top,
				Gdx.gl.GL_RGBA,
				Gdx.gl.GL_UNSIGNED_BYTE,
				pixels);
	}
}
