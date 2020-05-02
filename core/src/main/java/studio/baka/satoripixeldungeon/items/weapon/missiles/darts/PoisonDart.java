package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Poison;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class PoisonDart extends TippedDart {

    {
        image = ItemSpriteSheet.POISON_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        Buff.affect(defender, Poison.class).set(3 + Dungeon.depth / 3);

        return super.proc(attacker, defender, damage);
    }
}
