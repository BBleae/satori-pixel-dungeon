package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.weapon.SpiritBow;
import studio.baka.satoripixeldungeon.items.weapon.melee.Crossbow;
import studio.baka.satoripixeldungeon.items.weapon.missiles.Bolas;
import studio.baka.satoripixeldungeon.items.weapon.missiles.FishingSpear;
import studio.baka.satoripixeldungeon.items.weapon.missiles.HeavyBoomerang;
import studio.baka.satoripixeldungeon.items.weapon.missiles.Javelin;
import studio.baka.satoripixeldungeon.items.weapon.missiles.Kunai;
import studio.baka.satoripixeldungeon.items.weapon.missiles.Shuriken;
import studio.baka.satoripixeldungeon.items.weapon.missiles.ThrowingKnife;
import studio.baka.satoripixeldungeon.items.weapon.missiles.ThrowingSpear;
import studio.baka.satoripixeldungeon.items.weapon.missiles.Trident;
import studio.baka.satoripixeldungeon.items.weapon.missiles.darts.Dart;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

import java.util.HashMap;

public class MissileSprite extends ItemSprite implements Tweener.Listener {

	private static final float SPEED	= 240f;
	
	private Callback callback;
	
	public void reset( int from, int to, Item item, Callback listener ) {
		reset( DungeonTilemap.tileToWorld( from ), DungeonTilemap.tileToWorld( to ), item, listener);
	}

	public void reset( Visual from, Visual to, Item item, Callback listener ) {
		reset(from.center(this), to.center(this), item, listener );
	}

	public void reset( Visual from, int to, Item item, Callback listener ) {
		reset(from.center(this), DungeonTilemap.tileToWorld( to ), item, listener );
	}
	
	public void reset( int from, Visual to, Item item, Callback listener ) {
		reset(DungeonTilemap.tileToWorld( from ), to.center(this), item, listener );
	}

	public void reset( PointF from, PointF to, Item item, Callback listener) {
		revive();

		if (item == null)   view(0, null);
		else                view( item );

		setup( from,
				to,
				item,
				listener );
	}
	
	private static final int DEFAULT_ANGULAR_SPEED = 720;
	
	private static final HashMap<Class<?extends Item>, Integer> ANGULAR_SPEEDS = new HashMap<>();
	static {
		ANGULAR_SPEEDS.put(Dart.class,          0);
		ANGULAR_SPEEDS.put(ThrowingKnife.class, 0);
		ANGULAR_SPEEDS.put(FishingSpear.class,  0);
		ANGULAR_SPEEDS.put(ThrowingSpear.class, 0);
		ANGULAR_SPEEDS.put(Kunai.class,         0);
		ANGULAR_SPEEDS.put(Javelin.class,       0);
		ANGULAR_SPEEDS.put(Trident.class,       0);
		
		ANGULAR_SPEEDS.put(SpiritBow.SpiritArrow.class,       0);
		ANGULAR_SPEEDS.put(ScorpioSprite.ScorpioShot.class,   0);
		
		//720 is default
		
		ANGULAR_SPEEDS.put(HeavyBoomerang.class,1440);
		ANGULAR_SPEEDS.put(Bolas.class,         1440);
		
		ANGULAR_SPEEDS.put(Shuriken.class,      2160);
		
		ANGULAR_SPEEDS.put(TenguSprite.TenguShuriken.class,      2160);
	}

	//TODO it might be nice to have a source and destination angle, to improve thrown weapon visuals
	private void setup( PointF from, PointF to, Item item, Callback listener ){

		originToCenter();

		this.callback = listener;

		point( from );

		PointF d = PointF.diff( to, from );
		speed.set(d).normalize().scale(SPEED);
		
		angularSpeed = DEFAULT_ANGULAR_SPEED;
		for (Class<?extends Item> cls : ANGULAR_SPEEDS.keySet()){
			if (cls.isAssignableFrom(item.getClass())){
				angularSpeed = ANGULAR_SPEEDS.get(cls);
				break;
			}
		}
		
		angle = 135 - (float)(Math.atan2( d.x, d.y ) / 3.1415926 * 180);
		
		if (d.x >= 0){
			flipHorizontal = false;
			updateFrame();
			
		} else {
			angularSpeed = -angularSpeed;
			angle += 90;
			flipHorizontal = true;
			updateFrame();
		}
		
		float speed = SPEED;
		if (item instanceof Dart && Dungeon.hero.belongings.weapon instanceof Crossbow){
			speed *= 3f;
			
		} else if (item instanceof SpiritBow.SpiritArrow
				|| item instanceof ScorpioSprite.ScorpioShot
				|| item instanceof TenguSprite.TenguShuriken){
			speed *= 1.5f;
		}
		
		PosTweener tweener = new PosTweener( this, to, d.length() / speed );
		tweener.listener = this;
		parent.add( tweener );
	}

	@Override
	public void onComplete( Tweener tweener ) {
		kill();
		if (callback != null) {
			callback.call();
		}
	}
}
