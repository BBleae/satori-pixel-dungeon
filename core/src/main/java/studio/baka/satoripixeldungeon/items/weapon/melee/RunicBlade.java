package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class RunicBlade extends MeleeWeapon {

    {
        image = ItemSpriteSheet.RUNIC_BLADE;

        tier = 4;
    }

    //Essentially it's a tier 4 weapon, with tier 3 base max damage, and tier 5 scaling.
    //equal to tier 4 in damage at +5

    @Override
    public int max(int lvl) {
        return 5 * (tier) +                    //20 base, down from 25
                Math.round(lvl * (tier + 2));    //+6 per level, up from +5
    }
}
