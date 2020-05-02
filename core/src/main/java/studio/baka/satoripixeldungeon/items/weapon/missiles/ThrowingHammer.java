package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingHammer extends MissileWeapon {

    {
        image = ItemSpriteSheet.THROWING_HAMMER;

        tier = 5;
        baseUses = 15;
        sticky = false;
    }

    @Override
    public int max(int lvl) {
        return 4 * tier +                  //20 base, down from 25
                (tier) * lvl;               //scaling unchanged
    }
}
