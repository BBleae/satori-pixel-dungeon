package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class RotLasherSprite extends MobSprite {

	public RotLasherSprite() {
		super();

		texture( Assets.ROT_LASH );

		TextureFilm frames = new TextureFilm( texture, 12, 16 );

		idle = new Animation( 0, true );
		idle.frames( frames, 0);

		run = new Animation( 0, true );
		run.frames( frames, 0);

		attack = new Animation( 24, false );
		attack.frames( frames, 0, 1, 2, 2, 1 );

		die = new Animation( 12, false );
		die.frames( frames, 3, 4, 5, 6 );

		play( idle );
	}
}
