package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingStone extends MissileWeapon {

    {
        image = ItemSpriteSheet.THROWING_STONE;

        bones = false;

        tier = 1;
        baseUses = 5;
        sticky = false;
    }

    @Override
    public int price() {
        return super.price() / 2; //half normal value
    }
}
