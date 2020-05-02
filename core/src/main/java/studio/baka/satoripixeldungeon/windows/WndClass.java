package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Group;

public class WndClass extends WndTabbed {

	private static final int WIDTH			= 110;

	private static final int TAB_WIDTH	= 50;

	private final HeroClass cl;

    public WndClass( HeroClass cl ) {

		super();

		this.cl = cl;

        PerksTab tabPerks = new PerksTab();
		add(tabPerks);

		Tab tab = new RankingTab( cl.title().toUpperCase(), tabPerks);
		tab.setSize( TAB_WIDTH, tabHeight() );
		add( tab );

		if (Badges.isUnlocked( cl.masteryBadge() )) {
            MasteryTab tabMastery = new MasteryTab();
			add(tabMastery);

			tab = new RankingTab( Messages.get(this, "mastery"), tabMastery);
			add( tab );

			resize(
					(int)Math.max( tabPerks.width, tabMastery.width ),
					(int)Math.max( tabPerks.height, tabMastery.height ) );
		} else {
			resize( (int) tabPerks.width, (int) tabPerks.height );
		}

		layoutTabs();

		select( 0 );
	}

	private class RankingTab extends LabeledTab {

		private final Group page;

		public RankingTab( String label, Group page ) {
			super( label );
			this.page = page;
		}

		@Override
		protected void select( boolean value ) {
			super.select( value );
			if (page != null) {
				page.visible = page.active = selected;
			}
		}
	}

	private class PerksTab extends Group {

		private static final int MARGIN	= 4;
		private static final int GAP	= 4;

		public float height;
		public float width;

		public PerksTab() {
			super();

			float dotWidth = 0;

			String[] items = cl.perks();
			float pos = MARGIN;

			for (int i=0; i < items.length; i++) {

				if (i > 0) {
					pos += GAP;
				}

				RenderedTextBlock item = PixelScene.renderTextBlock( "-" + items[i], 6 );
				item.maxWidth((int)(WIDTH - MARGIN * 2 - dotWidth));
				item.setPos(0, pos);
				add( item );

				pos += item.height();
				float w = item.width();
				if (w > width) {
					width = w;
				}
			}

			width += MARGIN + dotWidth;
			height = pos + MARGIN;
		}
	}

	private class MasteryTab extends Group {

		private static final int MARGIN	= 4;

		public float height;
		public float width;

		public MasteryTab() {
			super();

			String message = null;
			switch (cl) {
				case WARRIOR:
					message = HeroSubClass.GLADIATOR.desc() + "\n\n" + HeroSubClass.BERSERKER.desc();
					break;
				case MAGE:
					message = HeroSubClass.BATTLEMAGE.desc() + "\n\n" + HeroSubClass.WARLOCK.desc();
					break;
				case ROGUE:
					message = HeroSubClass.FREERUNNER.desc() + "\n\n" + HeroSubClass.ASSASSIN.desc();
					break;
				case HUNTRESS:
					message = HeroSubClass.SNIPER.desc() + "\n\n" + HeroSubClass.WARDEN.desc();
					break;
			}

			RenderedTextBlock text = PixelScene.renderTextBlock( 6 );
			text.text( message, WIDTH - MARGIN * 2 );
			text.setPos( MARGIN, MARGIN );
			add( text );

			height = text.bottom() + MARGIN;
			width = text.right() + MARGIN;
		}
	}
}