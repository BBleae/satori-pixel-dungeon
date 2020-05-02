package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Gloves extends MeleeWeapon {

    {
        image = ItemSpriteSheet.GLOVES;

        tier = 1;
        DLY = 0.5f; //2x speed

        bones = false;


    }

    @Override
    public int max(int lvl) {
        return (int) (3f * (tier + 1)) +    //6 base, down from 10
                2 * lvl * tier;               //+2 per level, down from +2
    }


}
