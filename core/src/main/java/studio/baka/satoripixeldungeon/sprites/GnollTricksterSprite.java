package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.weapon.missiles.darts.ParalyticDart;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class GnollTricksterSprite extends MobSprite {

	private final Animation cast;

	public GnollTricksterSprite() {
		super();

		texture( Assets.GNOLL );

		TextureFilm frames = new TextureFilm( texture, 12, 15 );

		idle = new MovieClip.Animation( 2, true );
		idle.frames( frames, 21, 21, 21, 22, 21, 21, 22, 22 );

		run = new MovieClip.Animation( 12, true );
		run.frames( frames, 25, 26, 27, 28 );

		attack = new MovieClip.Animation( 12, false );
		attack.frames( frames, 23, 24, 21 );

		cast = attack.clone();

		die = new MovieClip.Animation( 12, false );
		die.frames( frames, 29, 30, 31 );

		play( idle );
	}

	@Override
	public void attack( int cell ) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {

			((MissileSprite)parent.recycle( MissileSprite.class )).
					reset( ch.pos, cell, new ParalyticDart(), () -> ch.onAttackComplete());

			play( cast );
			turnTo( ch.pos , cell );

		} else {

			super.attack( cell );

		}
	}
}