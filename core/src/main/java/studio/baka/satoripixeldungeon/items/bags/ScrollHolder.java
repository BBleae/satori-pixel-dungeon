package studio.baka.satoripixeldungeon.items.bags;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.items.spells.BeaconOfReturning;
import studio.baka.satoripixeldungeon.items.spells.Spell;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ScrollHolder extends Bag {

    {
        image = ItemSpriteSheet.HOLDER;

        size = 20;
    }

    @Override
    public boolean grab(Item item) {
        return item instanceof Scroll || item instanceof Spell;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (Item item : items) {
            if (item instanceof BeaconOfReturning) {
                ((BeaconOfReturning) item).returnDepth = -1;
            }
        }
    }

    @Override
    public int price() {
        return 40;
    }

}
