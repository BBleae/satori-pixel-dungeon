package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Adrenaline;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class AdrenalineDart extends TippedDart {

    {
        image = ItemSpriteSheet.ADRENALINE_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        Buff.prolong(defender, Adrenaline.class, Adrenaline.DURATION);

        if (attacker.alignment == defender.alignment) {
            return 0;
        }

        return super.proc(attacker, defender, damage);
    }
}
