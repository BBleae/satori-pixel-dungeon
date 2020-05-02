package com.watabou.input;

import com.badlogic.gdx.Input;
import com.watabou.utils.Signal;

import java.util.ArrayList;

public class KeyEvent {
	
	public int code;
	public boolean pressed;
	
	public KeyEvent( int code, boolean pressed ) {
		this.code = code;
		this.pressed = pressed;
	}
	
	// **********************
	// *** Static members ***
	// **********************
	
	public static final int BACK		= Input.Keys.BACK;
	public static final int MENU		= Input.Keys.MENU;
	
	private static final Signal<KeyEvent> keySignal = new Signal<>( true );
	
	public static void addKeyListener( Signal.Listener<KeyEvent> listener ){
		keySignal.add(listener);
	}
	
	public static void removeKeyListener( Signal.Listener<KeyEvent> listener ){
		keySignal.remove(listener);
	}
	
	public static void clearListeners(){
		keySignal.removeAll();
	}
	
	//Accumulated key events
	private static final ArrayList<KeyEvent> keyEvents = new ArrayList<>();
	
	public static synchronized void addKeyEvent( KeyEvent event ){
		keyEvents.add( event );
	}
	
	public static synchronized void processKeyEvents(){
		for (KeyEvent k : keyEvents){
			keySignal.dispatch(k);
		}
		keyEvents.clear();
	}
}
