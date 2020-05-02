package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

import java.util.Calendar;

public class RatKingSprite extends MobSprite {

	public boolean festive;
	
	public RatKingSprite() {
		super();

		final Calendar calendar = Calendar.getInstance();
		//once a year the rat king feels a bit festive!
		festive = (calendar.get(Calendar.MONTH) == Calendar.DECEMBER
				&& calendar.get(Calendar.WEEK_OF_MONTH) > 2);

		final int c = festive ? 8 : 0;
		
		texture( Assets.RATKING );
		
		TextureFilm frames = new TextureFilm( texture, 16, 17 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, c, c, c, c+1 );
		
		run = new Animation( 10, true );
		run.frames( frames, c+2, c+3, c+4, c+5, c+6 );
		
		attack = new Animation( 15, false );
		attack.frames( frames, c);
		
		die = new Animation( 10, false );
		die.frames( frames, c);
		
		play( idle );
	}
}
