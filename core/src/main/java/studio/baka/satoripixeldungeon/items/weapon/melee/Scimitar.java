package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Scimitar extends MeleeWeapon {

    {
        image = ItemSpriteSheet.SCIMITAR;

        tier = 3;
        DLY = 0.8f; //1.25x speed
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //16 base, down from 20
                lvl * (tier + 1);   //scaling unchanged
    }

}
