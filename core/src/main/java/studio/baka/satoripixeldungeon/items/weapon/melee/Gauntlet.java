package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Gauntlet extends MeleeWeapon {

    {
        image = ItemSpriteSheet.GAUNTLETS;

        tier = 5;
        DLY = 0.5f; //2x speed
    }

    @Override
    public int max(int lvl) {
        return Math.round(2.5f * (tier + 1)) +     //15 base, down from 30
                lvl * Math.round(0.5f * (tier + 1));  //+3 per level, down from +6
    }

    @Override
    public int defenseFactor(Char owner) {
        return 4;    //4 extra defence
    }
}
