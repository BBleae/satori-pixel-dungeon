package com.watabou.noosa;

import com.watabou.input.PointerEvent;
import com.watabou.utils.Signal;

public class PointerArea extends Visual implements Signal.Listener<PointerEvent> {
	
	// Its target can be pointerarea itself
	public Visual target;
	
	protected PointerEvent curEvent = null;
	
	//if true, this PointerArea will always block input, even when it is inactive
	public boolean blockWhenInactive = false;
	
	public PointerArea( Visual target ) {
		super( 0, 0, 0, 0 );
		this.target = target;
		
		PointerEvent.addPointerListener( this );
	}
	
	public PointerArea( float x, float y, float width, float height ) {
		super( x, y, width, height );
		this.target = this;
		
		visible = false;
		
		PointerEvent.addPointerListener( this );
	}
	
	@Override
	public boolean onSignal( PointerEvent event ) {
		
		boolean hit = event != null && target.overlapsScreenPoint( (int)event.current.x, (int)event.current.y );
		
		if (!isActive()) {
			return (hit && blockWhenInactive);
		}
		
		if (hit) {
			
			boolean returnValue = (event.down || event == curEvent);
			
			if (event.down) {
				
				if (curEvent == null) {
					curEvent = event;
				}
				onPointerDown( event );
				
			} else {
				
				onPointerUp( event );
				
				if (curEvent == event) {
					curEvent = null;
					onClick( event );
				}
				
			}
			
			return returnValue;
			
		} else {
			
			if (event == null && curEvent != null) {
				onDrag(curEvent);
			}
			
			else if (curEvent != null && !event.down) {
				onPointerUp( event );
				curEvent = null;
			}
			
			return false;
			
		}
	}
	
	protected void onPointerDown( PointerEvent event ) { }
	
	protected void onPointerUp( PointerEvent event) { }
	
	protected void onClick( PointerEvent event ) { }
	
	protected void onDrag( PointerEvent event ) { }
	
	public void reset() {
		curEvent = null;
	}
	
	@Override
	public void destroy() {
		PointerEvent.removePointerListener( this );
		super.destroy();
	}
}
