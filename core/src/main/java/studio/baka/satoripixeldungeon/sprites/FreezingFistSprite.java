package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class FreezingFistSprite extends MobSprite {

	public FreezingFistSprite() {
		super();
		
		texture( Assets.FREEZING );
		
		TextureFilm frames = new TextureFilm( texture, 24, 17 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 1 );
		
		run = new Animation( 3, true );
		run.frames( frames, 0, 1 );
		
		attack = new Animation( 8, false );
		attack.frames( frames, 0, 5, 6 );
		
		die = new Animation( 10, false );
		die.frames( frames, 0, 2, 3, 4 );
		
		play( idle );
	}
	
	private int posToShoot;
	
	@Override
	public void attack( int cell ) {
		posToShoot = cell;
		super.attack( cell );
	}
	
	@Override
	public void onComplete( Animation anim ) {
		if (anim == attack) {

			Sample.INSTANCE.play( Assets.SND_ZAP );
			MagicMissile.boltFromChar( parent,
					MagicMissile.SHADOW,
					this,
					posToShoot,
                    () -> ch.onAttackComplete());

			idle();
			
		} else {
			super.onComplete( anim );
		}
	}
}
