package com.watabou.input;

import com.badlogic.gdx.InputAdapter;
import com.watabou.noosa.Game;
import com.watabou.utils.PointF;

public class InputHandler extends InputAdapter {
	
	public void processAllEvents(){
		PointerEvent.processPointerEvents();
		KeyEvent.processKeyEvents();
		ScrollEvent.processScrollEvents();
	}
	
	// *********************
	// *** Pointer Input ***
	// *********************
	
	@Override
	public synchronized boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenX /= (Game.dispWidth / (float)Game.width);
		screenY /= (Game.dispHeight / (float)Game.height);
		PointerEvent.addPointerEvent(new PointerEvent(screenX, screenY, pointer, true));
		return true;
	}
	
	@Override
	public synchronized boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenX /= (Game.dispWidth / (float)Game.width);
		screenY /= (Game.dispHeight / (float)Game.height);
		PointerEvent.addPointerEvent(new PointerEvent(screenX, screenY, pointer, false));
		return true;
	}
	
	@Override
	public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
		screenX /= (Game.dispWidth / (float)Game.width);
		screenY /= (Game.dispHeight / (float)Game.height);
		PointerEvent.addPointerEvent(new PointerEvent(screenX, screenY, pointer, true));
		return true;
	}
	
	//TODO tracking this should probably be in PointerEvent
	private static final PointF pointerHoverPos = new PointF();
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		screenX /= (Game.dispWidth / (float)Game.width);
		screenY /= (Game.dispHeight / (float)Game.height);
		pointerHoverPos.x = screenX;
		pointerHoverPos.y = screenY;
		return true;
	}
	
	// *****************
	// *** Key Input ***
	// *****************
	
	@Override
	public synchronized boolean keyDown( int keyCode ) {
		
		if (keyCode != KeyEvent.BACK && keyCode != KeyEvent.MENU) {
			return false;
		}
		
		KeyEvent.addKeyEvent( new KeyEvent(keyCode, true) );
		return true;
	}
	
	@Override
	public synchronized boolean keyUp( int keyCode ) {
		
		if (keyCode != KeyEvent.BACK && keyCode != KeyEvent.MENU) {
			return false;
		}
		
		KeyEvent.addKeyEvent( new KeyEvent(keyCode, false) );
		return true;
	}
	
	// ********************
	// *** Scroll Input ***
	// ********************
	
	@Override
	public boolean scrolled(int amount) {
		ScrollEvent.addScrollEvent( new ScrollEvent(pointerHoverPos, amount));
		return true;
	}
}
