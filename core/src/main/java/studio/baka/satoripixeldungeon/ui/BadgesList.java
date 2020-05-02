package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.effects.BadgeBanner;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.windows.WndBadge;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class BadgesList extends ScrollPane {

    private final ArrayList<ListItem> items = new ArrayList<>();

    public BadgesList(boolean global) {
        super(new Component());

        for (Badges.Badge badge : Badges.filtered(global)) {

            if (badge.image == -1) {
                continue;
            }

            ListItem item = new ListItem(badge);
            content.add(item);
            items.add(item);
        }
    }

    @Override
    protected void layout() {

        float pos = 0;

        int size = items.size();
        for (ListItem item : items) {
            item.setRect(0, pos, width, ListItem.HEIGHT);
            pos += ListItem.HEIGHT;
        }

        content.setSize(width, pos);

        super.layout();
    }

    @Override
    public void onClick(float x, float y) {
        int size = items.size();
        for (ListItem item : items) {
            if (item.onClick(x, y)) {
                break;
            }
        }
    }

    private static class ListItem extends Component {

        private static final float HEIGHT = 20;

        private final Badges.Badge badge;

        private Image icon;
        private RenderedTextBlock label;

        public ListItem(Badges.Badge badge) {
            super();

            this.badge = badge;
            icon.copy(BadgeBanner.image(badge.image));
            label.text(badge.desc());
        }

        @Override
        protected void createChildren() {
            icon = new Image();
            add(icon);

            label = PixelScene.renderTextBlock(6);
            add(label);
        }

        @Override
        protected void layout() {
            icon.x = x;
            icon.y = y + (height - icon.height) / 2;
            PixelScene.align(icon);

            label.setPos(
                    icon.x + icon.width + 2,
                    y + (height - label.height()) / 2
            );
            PixelScene.align(label);
        }

        public boolean onClick(float x, float y) {
            if (inside(x, y)) {
                Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f);
                Game.scene().add(new WndBadge(badge));
                return true;
            } else {
                return false;
            }
        }
    }
}
