package studio.baka.satoripixeldungeon.items.bags;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class MagicalHolster extends Bag {

    {
        image = ItemSpriteSheet.HOLSTER;

        size = 20;
    }

    public static final float HOLSTER_SCALE_FACTOR = 0.85f;
    public static final float HOLSTER_DURABILITY_FACTOR = 1.2f;

    @Override
    public boolean grab(Item item) {
        return item instanceof Wand || item instanceof MissileWeapon || item instanceof Bomb;
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (owner != null) {
                for (Item item : items) {
                    if (item instanceof Wand) {
                        ((Wand) item).charge(owner, HOLSTER_SCALE_FACTOR);
                    } else if (item instanceof MissileWeapon) {
                        ((MissileWeapon) item).holster = true;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (Item item : items) {
            if (item instanceof Wand) {
                ((Wand) item).stopCharging();
            } else if (item instanceof MissileWeapon) {
                ((MissileWeapon) item).holster = false;
            }
        }
    }

    @Override
    public int price() {
        return 60;
    }

}
