package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Glaive extends MeleeWeapon {

    {
        image = ItemSpriteSheet.GLAIVE;

        tier = 5;
        DLY = 1.5f; //0.67x speed
        RCH = 2;    //extra reach
    }

    @Override
    public int max(int lvl) {
        return Math.round(6.67f * (tier + 1)) +    //40 base, up from 30
                lvl * Math.round(1.33f * (tier + 1)); //+8 per level, up from +6
    }

}
