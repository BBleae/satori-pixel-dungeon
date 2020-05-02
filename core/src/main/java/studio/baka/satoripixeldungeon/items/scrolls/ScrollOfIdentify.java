package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.effects.Identification;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Objects;

public class ScrollOfIdentify extends InventoryScroll {

    {
        initials = 0;
        mode = WndBag.Mode.UNIDENTIFED;

        bones = true;
    }

    @Override
    public void empoweredRead() {
        ArrayList<Item> unIDed = new ArrayList<>();

        for (Item i : curUser.belongings) {
            if (!i.isIdentified()) {
                unIDed.add(i);
            }
        }

        if (unIDed.size() > 1) {
            Objects.requireNonNull(Random.element(unIDed)).identify();
            Sample.INSTANCE.play(Assets.SND_TELEPORT);
        }

        doRead();
    }

    @Override
    protected void onItemSelected(Item item) {

        curUser.sprite.parent.add(new Identification(curUser.sprite.center().offset(0, -16)));

        item.identify();
        GLog.i(Messages.get(this, "it_is", item));

        Badges.validateItemLevelAquired(item);
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
