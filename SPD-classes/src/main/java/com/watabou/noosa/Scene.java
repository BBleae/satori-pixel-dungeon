package com.watabou.noosa;

import com.watabou.input.KeyEvent;
import com.watabou.utils.Signal;

public class Scene extends Group {
	
	private Signal.Listener<KeyEvent> keyListener;
	
	public void create() {
		KeyEvent.addKeyListener( keyListener = event -> {
            if (Game.instance != null && event.pressed) {
                switch (event.code) {
                case KeyEvent.BACK:
                    onBackPressed();
                    break;
                case KeyEvent.MENU:
                    onMenuPressed();
                    break;
                }
            }
            return false;
        });
	}
	
	@Override
	public void destroy() {
		KeyEvent.removeKeyListener( keyListener );
		super.destroy();
	}
	
	public void onPause() {
		
	}
	
	public void onResume(){
	
	}
	
	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public Camera camera() {
		return Camera.main;
	}
	
	protected void onBackPressed() {
		Game.instance.finish();
	}
	
	protected void onMenuPressed() {
		
	}

}
