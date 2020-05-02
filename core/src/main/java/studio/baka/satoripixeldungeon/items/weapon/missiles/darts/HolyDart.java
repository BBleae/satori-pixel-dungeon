package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Bless;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class HolyDart extends TippedDart {

    {
        image = ItemSpriteSheet.HOLY_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        Buff.affect(defender, Bless.class, 20f);

        if (attacker.alignment == defender.alignment) {
            return 0;
        }

        return super.proc(attacker, defender, damage);
    }
}
