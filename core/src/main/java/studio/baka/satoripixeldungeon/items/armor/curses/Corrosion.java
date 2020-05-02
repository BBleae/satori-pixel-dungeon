package studio.baka.satoripixeldungeon.items.armor.curses;

import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Ooze;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Corrosion extends Armor.Glyph {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        if (Random.Int(10) == 0) {
            int pos = defender.pos;
            for (int i : PathFinder.NEIGHBOURS9) {
                Splash.at(pos + i, 0x000000, 5);
                if (Actor.findChar(pos + i) != null)
                    Buff.affect(Actor.findChar(pos + i), Ooze.class).set(20f);
            }
        }

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    @Override
    public boolean curse() {
        return true;
    }
}
