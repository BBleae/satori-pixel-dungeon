package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.ShieldHalo;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;

public class WandmakerSprite extends MobSprite {
	
	private ShieldHalo shield;
	
	public WandmakerSprite() {
		super();
		
		texture( Assets.MAKER );
		
		TextureFilm frames = new TextureFilm( texture, 12, 14 );
		
		idle = new Animation( 10, true );
		idle.frames( frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1 );
		
		run = new Animation( 20, true );
		run.frames( frames, 0 );
		
		die = new Animation( 20, false );
		die.frames( frames, 0 );
		
		play( idle );
	}
	
	@Override
	public void link( Char ch ) {
		super.link( ch );
		add(State.SHIELDED);
	}
	
	@Override
	public void die() {
		super.die();
		
		remove(State.SHIELDED);
		emitter().start( ElmoParticle.FACTORY, 0.03f, 60 );

		if (visible) {
			Sample.INSTANCE.play( Assets.SND_BURNING );
		}
	}

}
