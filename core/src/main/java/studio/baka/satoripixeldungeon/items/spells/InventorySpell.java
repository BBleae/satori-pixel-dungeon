package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

public abstract class InventorySpell extends Spell {

    protected String inventoryTitle = Messages.get(this, "inv_title");
    protected WndBag.Mode mode = WndBag.Mode.ALL;

    @Override
    protected void onCast(Hero hero) {
        curItem = detach(hero.belongings.backpack);
        GameScene.selectItem(itemSelector, mode, inventoryTitle);
    }

    protected abstract void onItemSelected(Item item);

    protected static WndBag.Listener itemSelector = item -> {

        //FIXME this safety check shouldn't be necessary
        //it would be better to eliminate the curItem static variable.
        if (!(curItem instanceof InventorySpell)) {
            return;
        }

        if (item != null) {

            ((InventorySpell) curItem).onItemSelected(item);
            curUser.spend(1f);
            curUser.busy();
            (curUser.sprite).operate(curUser.pos);

            Sample.INSTANCE.play(Assets.SND_READ);
            Invisibility.dispel();

        } else {
            curItem.collect(curUser.belongings.backpack);
        }
    };
}
