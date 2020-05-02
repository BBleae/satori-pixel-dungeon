package com.watabou.input;

import com.watabou.utils.PointF;
import com.watabou.utils.Signal;

import java.util.ArrayList;
import java.util.HashMap;

public class PointerEvent {
	
	public PointF start;
	public PointF current;
	public int id;
	public boolean down;
	
	public PointerEvent( int x, int y, int id, boolean down){
		start = current = new PointF(x, y);
		this.id = id;
		this.down = down;
	}
	
	public void update( PointerEvent other ){
		this.current = other.current;
	}
	
	public void update( int x, int y ){
		current.set( x, y );
	}
	
	public PointerEvent up() {
		down = false;
		return this;
	}
	
	// **********************
	// *** Static members ***
	// **********************
	
	private static final Signal<PointerEvent> pointerSignal = new Signal<>( true );
	
	public static void addPointerListener( Signal.Listener<PointerEvent> listener ){
		pointerSignal.add(listener);
	}
	
	public static void removePointerListener( Signal.Listener<PointerEvent> listener ){
		pointerSignal.remove(listener);
	}
	
	public static void clearListeners(){
		pointerSignal.removeAll();
	}
	
	// Accumulated pointer events
	private static final ArrayList<PointerEvent> pointerEvents = new ArrayList<>();
	private static final HashMap<Integer, PointerEvent> activePointers = new HashMap<>();
	
	public static synchronized void addPointerEvent( PointerEvent event ){
		pointerEvents.add( event );
	}
	
	public static synchronized void processPointerEvents(){
		for (PointerEvent p : pointerEvents){
			if (activePointers.containsKey(p.id)){
				PointerEvent existing = activePointers.get(p.id);
				existing.current = p.current;
				if (existing.down == p.down){
					pointerSignal.dispatch( null );
				} else if (p.down) {
					pointerSignal.dispatch( existing );
				} else {
					activePointers.remove(existing.id);
					pointerSignal.dispatch(existing.up());
				}
			} else {
				if (p.down) {
					activePointers.put(p.id, p);
				}
				pointerSignal.dispatch(p);
			}
		}
		pointerEvents.clear();
	}
}
