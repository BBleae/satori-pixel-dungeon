package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class HandAxe extends MeleeWeapon {

    {
        image = ItemSpriteSheet.HAND_AXE;

        tier = 2;
        ACC = 1.32f; //32% boost to accuracy
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //12 base, down from 15
                lvl * (tier + 1);   //scaling unchanged
    }

}
