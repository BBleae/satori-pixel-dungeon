package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public abstract class Runestone extends Item {

    {
        stackable = true;
        defaultAction = AC_THROW;
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.pit[cell] || !defaultAction.equals(AC_THROW)) {
            super.onThrow(cell);
        } else {
            activate(cell);
        }
    }

    protected abstract void activate(int cell);

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int price() {
        return 10 * quantity;
    }

    public static class PlaceHolder extends Runestone {

        {
            image = ItemSpriteSheet.STONE_HOLDER;
        }

        @Override
        protected void activate(int cell) {
            //does nothing
        }

        @Override
        public boolean isSimilar(Item item) {
            return item instanceof Runestone;
        }

        @Override
        public String info() {
            return "";
        }
    }
}
