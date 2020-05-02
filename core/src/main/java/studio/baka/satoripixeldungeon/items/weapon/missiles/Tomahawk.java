package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Bleeding;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Tomahawk extends MissileWeapon {

    {
        image = ItemSpriteSheet.TOMAHAWK;

        tier = 4;
        baseUses = 5;
    }

    @Override
    public int min(int lvl) {
        return Math.round(1.5f * tier) +   //6 base, down from 8
                2 * lvl;                    //scaling unchanged
    }

    @Override
    public int max(int lvl) {
        return Math.round(3.75f * tier) +  //15 base, down from 20
                (tier) * lvl;                 //scaling unchanged
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.affect(defender, Bleeding.class).set(Math.round(damage * 0.6f));
        return super.proc(attacker, defender, damage);
    }
}
