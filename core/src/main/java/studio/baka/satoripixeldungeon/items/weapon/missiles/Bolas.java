package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Bolas extends MissileWeapon {

    {
        image = ItemSpriteSheet.BOLAS;

        tier = 3;
        baseUses = 5;
    }

    @Override
    public int max(int lvl) {
        return 3 * tier +                      //9 base, down from 15
                (tier == 1 ? 2 * lvl : tier * lvl); //scaling unchanged
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.prolong(defender, Cripple.class, Cripple.DURATION);
        return super.proc(attacker, defender, damage);
    }
}
