package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Whip extends MeleeWeapon {

    {
        image = ItemSpriteSheet.WHIP;

        tier = 3;
        RCH = 3;    //lots of extra reach
    }

    @Override
    public int max(int lvl) {
        return 3 * (tier + 1) +    //12 base, down from 20
                lvl * (tier);     //+3 per level, down from +4
    }

}
