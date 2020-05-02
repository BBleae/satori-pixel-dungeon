package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Mace extends MeleeWeapon {

    {
        image = ItemSpriteSheet.MACE;

        tier = 3;
        ACC = 1.28f; //28% boost to accuracy
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //16 base, down from 20
                lvl * (tier + 1);   //scaling unchanged
    }

}
