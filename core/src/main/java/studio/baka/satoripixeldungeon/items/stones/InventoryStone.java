package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public abstract class InventoryStone extends Runestone {

    protected String inventoryTitle = Messages.get(this, "inv_title");
    protected WndBag.Mode mode = WndBag.Mode.ALL;

    {
        defaultAction = AC_USE;
    }

    public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)) {
            curItem = detach(hero.belongings.backpack);
            activate(curUser.pos);
        }
    }

    @Override
    protected void activate(int cell) {
        GameScene.selectItem(itemSelector, mode, inventoryTitle);
    }

    protected void useAnimation() {
        curUser.spend(1f);
        curUser.busy();
        curUser.sprite.operate(curUser.pos);

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();
    }

    protected abstract void onItemSelected(Item item);

    protected static WndBag.Listener itemSelector = item -> {

        //FIXME this safety check shouldn't be necessary
        //it would be better to eliminate the curItem static variable.
        if (!(curItem instanceof InventoryStone)) {
            return;
        }

        if (item != null) {

            ((InventoryStone) curItem).onItemSelected(item);

        } else {
            curItem.collect(curUser.belongings.backpack);
        }
    };

}
