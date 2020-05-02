package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingClub extends MissileWeapon {

    {
        image = ItemSpriteSheet.THROWING_CLUB;

        tier = 2;
        baseUses = 15;
        sticky = false;
    }

    @Override
    public int max(int lvl) {
        return 4 * tier +                  //8 base, down from 10
                (tier) * lvl;               //scaling unchanged
    }
}
