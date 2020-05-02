package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.effects.BadgeBanner;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import com.watabou.noosa.Image;

public class WndBadge extends Window {
	
	private static final int WIDTH = 120;
	private static final int MARGIN = 4;
	
	public WndBadge( Badges.Badge badge ) {
		
		super();
		
		Image icon = BadgeBanner.image( badge.image );
		icon.scale.set( 2 );
		add( icon );

		//TODO: this used to be centered, should probably figure that out.
		RenderedTextBlock info = PixelScene.renderTextBlock( badge.desc(), 8 );
		info.maxWidth(WIDTH - MARGIN * 2);
		PixelScene.align(info);
		add(info);
		
		float w = Math.max( icon.width(), info.width() ) + MARGIN * 2;
		
		icon.x = (w - icon.width()) / 2f;
		icon.y = MARGIN;
		PixelScene.align(icon);

		info.setPos((w - info.width()) / 2, icon.y + icon.height() + MARGIN);
		resize( (int)w, (int)(info.bottom() + MARGIN) );
		
		BadgeBanner.highlight( icon, badge.image );
	}
}
