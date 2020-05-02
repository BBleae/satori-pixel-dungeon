package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class BattleAxe extends MeleeWeapon {

    {
        image = ItemSpriteSheet.BATTLE_AXE;

        tier = 4;
        ACC = 1.24f; //24% boost to accuracy
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //20 base, down from 25
                lvl * (tier + 1);   //scaling unchanged
    }

}
