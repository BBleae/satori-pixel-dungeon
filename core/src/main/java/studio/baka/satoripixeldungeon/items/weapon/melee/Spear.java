package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Spear extends MeleeWeapon {

    {
        image = ItemSpriteSheet.SPEAR;

        tier = 2;
        DLY = 1.5f; //0.67x speed
        RCH = 2;    //extra reach
    }

    @Override
    public int max(int lvl) {
        return Math.round(6.67f * (tier + 1)) +    //20 base, up from 15
                lvl * Math.round(1.33f * (tier + 1)); //+4 per level, up from +3
    }

}
