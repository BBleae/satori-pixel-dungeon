package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Flail extends MeleeWeapon {

    {
        image = ItemSpriteSheet.FLAIL;

        tier = 4;
        ACC = 0.9f; //0.9x accuracy
        //also cannot surprise attack, see Hero.canSurpriseAttack
    }

    @Override
    public int max(int lvl) {
        return Math.round(7 * (tier + 1)) +        //35 base, up from 25
                lvl * Math.round(1.6f * (tier + 1));  //+8 per level, up from +5
    }
}
