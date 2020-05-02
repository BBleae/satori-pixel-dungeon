package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;

public class Blending {
	
	public static void useDefault(){
		enable();
		setNormalMode();
	}
	
	public static void enable(){
		Gdx.gl.glEnable( Gdx.gl.GL_BLEND );
	}
	
	public static void disable(){
		Gdx.gl.glDisable( Gdx.gl.GL_BLEND );
	}
	
	//in this mode colors overwrite eachother, based on alpha value
	public static void setNormalMode(){
		Gdx.gl.glBlendFunc( Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA );
	}
	
	//in this mode colors add to eachother, eventually reaching pure white
	public static void setLightMode(){
		Gdx.gl.glBlendFunc( Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE );
	}
	
}
