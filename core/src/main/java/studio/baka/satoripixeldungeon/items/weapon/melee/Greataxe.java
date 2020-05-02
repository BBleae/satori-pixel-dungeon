package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Greataxe extends MeleeWeapon {

    {
        image = ItemSpriteSheet.GREATAXE;

        tier = 5;
    }

    @Override
    public int max(int lvl) {
        return 5 * (tier + 5) +    //50 base, up from 30
                lvl * (tier + 1);   //scaling unchanged
    }

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);
        //20 base strength req, up from 18
        return (10 + tier * 2) - (int) (Math.sqrt(8 * lvl + 1) - 1) / 2;
    }

}
