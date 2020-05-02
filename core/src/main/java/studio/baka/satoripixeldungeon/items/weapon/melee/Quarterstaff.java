package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Quarterstaff extends MeleeWeapon {

    {
        image = ItemSpriteSheet.QUARTERSTAFF;

        tier = 2;
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //12 base, down from 15
                lvl * (tier + 1);   //scaling unchanged
    }

    @Override
    public int defenseFactor(Char owner) {
        return 3;    //3 extra defence
    }
}
