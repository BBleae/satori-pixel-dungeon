package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ParalyticDart extends TippedDart {

    {
        image = ItemSpriteSheet.PARALYTIC_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.prolong(defender, Paralysis.class, 5f);
        return super.proc(attacker, defender, damage);
    }

}
