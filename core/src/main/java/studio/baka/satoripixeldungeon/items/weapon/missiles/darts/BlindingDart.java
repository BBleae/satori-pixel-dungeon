package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;


public class BlindingDart extends TippedDart {

    {
        image = ItemSpriteSheet.BLINDING_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        Buff.affect(defender, Blindness.class, 10f);

        return super.proc(attacker, defender, damage);
    }
}
