package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Sai extends MeleeWeapon {

    {
        image = ItemSpriteSheet.SAI;

        tier = 3;
        DLY = 0.5f; //2x speed
    }

    @Override
    public int max(int lvl) {
        return Math.round(2.5f * (tier + 1)) +     //10 base, down from 20
                lvl * Math.round(0.5f * (tier + 1));  //+2 per level, down from +4
    }

    @Override
    public int defenseFactor(Char owner) {
        return 2;    //2 extra defence
    }
}
