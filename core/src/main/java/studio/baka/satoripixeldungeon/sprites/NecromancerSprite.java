package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.mobs.Necromancer;
import com.watabou.noosa.TextureFilm;

public class NecromancerSprite extends MobSprite {
	
	private final Animation charging;
	
	public NecromancerSprite(){
		super();
		
		texture( Assets.NECRO );
		TextureFilm film = new TextureFilm( texture, 16, 16 );
		
		idle = new Animation( 1, true );
		idle.frames( film, 0, 0, 0, 1, 0, 0, 0, 0, 1 );
		
		run = new Animation( 8, true );
		run.frames( film, 0, 0, 0, 2, 3, 4 );
		
		zap = new Animation( 10, false );
		zap.frames( film, 5, 6, 7, 8 );
		
		charging = new Animation( 5, true );
		charging.frames( film, 7, 8 );
		
		die = new Animation( 10, false );
		die.frames( film, 9, 10, 11, 12 );
		
		attack = zap.clone();
		
		idle();
	}
	
	public void charge(){
		play(charging);
	}
	
	@Override
	public void onComplete(Animation anim) {
		super.onComplete(anim);
		if (anim == zap){
			if (ch instanceof Necromancer){
				if (((Necromancer) ch).summoning){
					charge();
				} else {
					((Necromancer)ch).onZapComplete();
					idle();
				}
			} else {
				idle();
			}
		}
	}
}
