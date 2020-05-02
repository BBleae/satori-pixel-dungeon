package studio.baka.satoripixeldungeon.ui.changelist;

import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

//not actually a button, but functions as one.
public class ChangeButton extends Component {

    protected Image icon;
    protected String title;
    protected String message;

    public ChangeButton(Image icon, String title, String message) {
        super();

        this.icon = icon;
        add(this.icon);

        this.title = Messages.titleCase(title);
        this.message = message;

        layout();
    }

    public ChangeButton(Item item, String message) {
        this(new ItemSprite(item), item.name(), message);
    }

    protected void onClick() {
        SatoriPixelDungeon.scene().add(new ChangesWindow(new Image(icon), title, message));
    }

    @Override
    protected void layout() {
        super.layout();

        icon.x = x + (width - icon.width) / 2f;
        icon.y = y + (height - icon.height) / 2f;
        PixelScene.align(icon);
    }
}
