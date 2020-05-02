package com.watabou.noosa.ui;

import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.PointerArea;

public class Button extends Component {

	public static float longClick = 1f;
	
	protected PointerArea hotArea;
	
	protected boolean pressed;
	protected float pressTime;
	
	protected boolean processed;
	
	@Override
	protected void createChildren() {
		hotArea = new PointerArea( 0, 0, 0, 0 ) {
			@Override
			protected void onPointerDown( PointerEvent event ) {
				pressed = true;
				pressTime = 0;
				processed = false;
				Button.this.onPointerDown();
			}
			@Override
			protected void onPointerUp( PointerEvent event ) {
				pressed = false;
				Button.this.onPointerUp();
			}
			@Override
			protected void onClick( PointerEvent event ) {
				if (!processed) {
					Button.this.onClick();
				}
			}
		};
		add( hotArea );
	}
	
	@Override
	public void update() {
		super.update();
		
		hotArea.active = visible;
		
		if (pressed) {
			if ((pressTime += Game.elapsed) >= longClick) {
				pressed = false;
				if (onLongClick()) {

					hotArea.reset();
					processed = true;
					onPointerUp();
					
					Game.vibrate( 50 );
				}
			}
		}
	}
	
	protected void onPointerDown() {}
	protected void onPointerUp() {}
	protected void onClick() {}
	protected boolean onLongClick() {
		return false;
	}
	
	@Override
	protected void layout() {
		hotArea.x = x;
		hotArea.y = y;
		hotArea.width = width;
		hotArea.height = height;
	}
}
