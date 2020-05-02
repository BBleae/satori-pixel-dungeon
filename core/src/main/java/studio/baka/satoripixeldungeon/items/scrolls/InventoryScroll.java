package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.windows.WndBag;
import studio.baka.satoripixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;

public abstract class InventoryScroll extends Scroll {

    protected String inventoryTitle = Messages.get(this, "inv_title");
    protected WndBag.Mode mode = WndBag.Mode.ALL;

    @Override
    public void doRead() {

        if (!isKnown()) {
            setKnown();
            identifiedByUse = true;
        } else {
            identifiedByUse = false;
        }

        GameScene.selectItem(itemSelector, mode, inventoryTitle);
    }

    private void confirmCancelation() {
        GameScene.show(new WndOptions(Messages.titleCase(name()), Messages.get(this, "warning"),
                Messages.get(this, "yes"), Messages.get(this, "no")) {
            @Override
            protected void onSelect(int index) {
                switch (index) {
                    case 0:
                        curUser.spendAndNext(TIME_TO_READ);
                        identifiedByUse = false;
                        break;
                    case 1:
                        GameScene.selectItem(itemSelector, mode, inventoryTitle);
                        break;
                }
            }

            public void onBackPressed() {
            }
        });
    }

    protected abstract void onItemSelected(Item item);

    protected static boolean identifiedByUse = false;
    protected static WndBag.Listener itemSelector = item -> {

        //FIXME this safety check shouldn't be necessary
        //it would be better to eliminate the curItem static variable.
        if (!(curItem instanceof InventoryScroll)) {
            return;
        }

        if (item != null) {

            ((InventoryScroll) curItem).onItemSelected(item);
            ((InventoryScroll) curItem).readAnimation();

            Sample.INSTANCE.play(Assets.SND_READ);
            Invisibility.dispel();

        } else if (identifiedByUse && !((Scroll) curItem).anonymous) {

            ((InventoryScroll) curItem).confirmCancelation();

        } else if (!((Scroll) curItem).anonymous) {

            curItem.collect(curUser.belongings.backpack);

        }
    };
}
