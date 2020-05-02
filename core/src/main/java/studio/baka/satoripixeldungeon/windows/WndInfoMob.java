package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.ui.HealthBar;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ui.Component;

public class WndInfoMob extends WndTitledMessage {
	
	public WndInfoMob( Mob mob ) {
		
		super( new MobTitle( mob ), mob.description() );
		
	}
	
	private static class MobTitle extends Component {

		private static final int GAP	= 2;
		
		private final CharSprite image;
		private final RenderedTextBlock name;
		private final HealthBar health;
		private final BuffIndicator buffs;
		
		public MobTitle( Mob mob ) {
			
			name = PixelScene.renderTextBlock( Messages.titleCase( mob.name ), 9 );
			name.hardlight( TITLE_COLOR );
			add( name );
			
			image = mob.sprite();
			add( image );

			health = new HealthBar();
			health.level(mob);
			add( health );

			buffs = new BuffIndicator( mob );
			add( buffs );
		}
		
		@Override
		protected void layout() {
			
			image.x = 0;
			image.y = Math.max( 0, name.height() + health.height() - image.height );

			name.setPos(x + image.width + GAP,
					image.height > name.height() ? y +(image.height() - name.height()) / 2 : y);

			float w = width - image.width - GAP;

			health.setRect(image.width + GAP, name.bottom() + GAP, w, health.height());

			buffs.setPos(
				name.right() + GAP-1,
				name.bottom() - BuffIndicator.SIZE-2
			);

			height = health.bottom();
		}
	}
}
