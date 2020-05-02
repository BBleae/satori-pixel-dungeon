package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class CausticSlimeSprite extends MobSprite {
	
	public CausticSlimeSprite() {
		super();
		
		texture( Assets.SLIME );
		
		TextureFilm frames = new TextureFilm( texture, 14, 12 );
		
		int c = 9;
		
		idle = new Animation( 3, true );
		idle.frames( frames, c+0, c+1, c+1, c+0 );
		
		run = new Animation( 10, true );
		run.frames( frames, c+0, c+2, c+3, c+3, c+2, c+0 );
		
		attack = new Animation( 10, false );
		attack.frames( frames, c+2, c+3, c+4, c+5, c+2 );
		
		die = new Animation( 10, false );
		die.frames( frames, c+0, c+5, c+6, c+7 );
		
		play(idle);
	}
	
}
