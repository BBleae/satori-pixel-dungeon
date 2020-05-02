package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class WarHammer extends MeleeWeapon {

    {
        image = ItemSpriteSheet.WAR_HAMMER;

        tier = 5;
        ACC = 1.20f; //20% boost to accuracy
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //24 base, down from 30
                lvl * (tier + 1);   //scaling unchanged
    }

}
