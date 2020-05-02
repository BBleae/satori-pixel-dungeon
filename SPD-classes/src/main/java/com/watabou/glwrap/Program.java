package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.IntBuffer;

public class Program {

	private final int handle;
	
	public Program() {
		handle = Gdx.gl.glCreateProgram();
	}
	
	public int handle() {
		return handle;
	}
	
	public void attach( Shader shader ) {
		Gdx.gl.glAttachShader( handle, shader.handle() );
	}
	
	public void link() {
		Gdx.gl.glLinkProgram( handle );
		
		IntBuffer status = BufferUtils.newIntBuffer(1);
		Gdx.gl.glGetProgramiv( handle, Gdx.gl.GL_LINK_STATUS, status );
		if (status.get() == Gdx.gl.GL_FALSE) {
			throw new Error( Gdx.gl.glGetProgramInfoLog( handle ) );
		}
	}
	
	public Attribute attribute( String name ) {
		return new Attribute( Gdx.gl.glGetAttribLocation( handle, name ) );
	}
	
	public Uniform uniform( String name ) {
		return new Uniform( Gdx.gl.glGetUniformLocation( handle, name ) );
	}
	
	public void use() {
		Gdx.gl.glUseProgram( handle );
	}
	
	public void delete() {
		Gdx.gl.glDeleteProgram( handle );
	}
	
	public static Program create( Shader ...shaders ) {
		Program program = new Program();
        for (Shader shader : shaders) {
            program.attach(shader);
        }
		program.link();
		return program;
	}
}
