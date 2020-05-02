package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Chill;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ChillingDart extends TippedDart {

    {
        image = ItemSpriteSheet.CHILLING_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        if (Dungeon.level.water[defender.pos]) {
            Buff.prolong(defender, Chill.class, 10f);
        } else {
            Buff.prolong(defender, Chill.class, 6f);
        }

        return super.proc(attacker, defender, damage);
    }
}
