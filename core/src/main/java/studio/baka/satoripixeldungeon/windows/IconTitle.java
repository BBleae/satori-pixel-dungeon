package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.ui.HealthBar;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class IconTitle extends Component {

	private static final float FONT_SIZE = 9;

	private static final float GAP = 2;

	protected Image imIcon;
	protected RenderedTextBlock tfLabel;
	protected HealthBar health;

	private float healthLvl = Float.NaN;

	public IconTitle() {
		super();
	}

	public IconTitle( Item item ) {
		ItemSprite icon = new ItemSprite();
		icon( icon );
		label( Messages.titleCase( item.toString() ) );
		icon.view( item );
	}
	
	public IconTitle( Heap heap ){
		ItemSprite icon = new ItemSprite();
		icon( icon );
		label( Messages.titleCase( heap.toString() ) );
		icon.view( heap );
	}

	public IconTitle( Image icon, String label ) {
		super();

		icon( icon );
		label( label );
	}

	@Override
	protected void createChildren() {
		imIcon = new Image();
		add( imIcon );

		tfLabel = PixelScene.renderTextBlock( (int)FONT_SIZE );
		tfLabel.hardlight( Window.TITLE_COLOR );
		tfLabel.setHightlighting(false);
		add( tfLabel );

		health = new HealthBar();
		add( health );
	}

	@Override
	protected void layout() {

		health.visible = !Float.isNaN( healthLvl );

		imIcon.x = x + (Math.max(0, 8 - imIcon.width()/2));
		imIcon.y = y + (Math.max(0, 8 - imIcon.height()/2));
		PixelScene.align(imIcon);

		int imWidth = (int)Math.max(imIcon.width(), 16);
		int imHeight = (int)Math.max(imIcon.height(), 16);

		tfLabel.maxWidth((int)(width - (imWidth + GAP)));
		tfLabel.setPos(x + imWidth + GAP,
						imHeight > tfLabel.height() ? y +(imHeight - tfLabel.height()) / 2 : y);
		PixelScene.align(tfLabel);

		if (health.visible) {
			health.setRect( tfLabel.left(), tfLabel.bottom(), tfLabel.maxWidth(), 0 );
			height = Math.max( imHeight, health.bottom() );
		} else {
			height = Math.max( imHeight, tfLabel.height() );
		}
	}

	public void icon( Image icon ) {
		remove( imIcon );
		add( imIcon = icon );
	}

	public void label( String label ) {
		tfLabel.text( label );
	}

	public void label( String label, int color ) {
		tfLabel.text( label );
		tfLabel.hardlight( color );
	}

	public void color( int color ) {
		tfLabel.hardlight( color );
	}

	public void health( float value ) {
		health.level( healthLvl = value );
		layout();
	}
}
