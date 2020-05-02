package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.Speck;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class RotHeartSprite extends MobSprite {

	private Emitter cloud;

	public RotHeartSprite(){
		super();

		perspectiveRaise = 0.2f;

		texture( Assets.ROT_HEART );

		TextureFilm frames = new TextureFilm( texture, 16, 16 );

		idle = new MovieClip.Animation( 1, true );
		idle.frames( frames, 0);

		run = new MovieClip.Animation( 1, true );
		run.frames( frames, 0 );

		attack = new MovieClip.Animation( 1, false );
		attack.frames( frames, 0 );

		die = new MovieClip.Animation( 8, false );
		die.frames( frames, 1, 2, 3, 4, 5, 6, 7, 7, 7 );

		play( idle );
	}

	@Override
	public void link( Char ch ) {
		super.link( ch );

		renderShadow = false;

		if (cloud == null) {
			cloud = emitter();
			cloud.pour( Speck.factory(Speck.TOXIC), 0.7f );
		}
	}

	@Override
	public void turnTo(int from, int to) {
		//do nothing
	}

	@Override
	public void update() {

		super.update();

		if (cloud != null) {
			cloud.visible = visible;
		}
	}

	@Override
	public void die() {
		super.die();

		if (cloud != null) {
			cloud.on = false;
		}
	}
}
