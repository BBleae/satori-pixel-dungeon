package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Crossbow extends MeleeWeapon {

    {
        image = ItemSpriteSheet.CROSSBOW;

        //check Dart.class for additional properties

        tier = 4;
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //20 base, down from 25
                lvl * (tier);     //+4 per level, down from +5
    }
}
