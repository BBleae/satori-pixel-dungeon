package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Ooze;
import studio.baka.satoripixeldungeon.items.quest.GooBlob;
import studio.baka.satoripixeldungeon.sprites.CausticSlimeSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class CausticSlime extends Slime {

    {
        spriteClass = CausticSlimeSprite.class;

        properties.add(Property.ACIDIC);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (Random.Int(2) == 0) {
            Buff.affect(enemy, Ooze.class).set(20f);
            enemy.sprite.burst(0x000000, 5);
        }

        return super.attackProc(enemy, damage);
    }

    @Override
    public void rollToDropLoot() {
        if (Dungeon.hero.lvl > maxLvl + 2) return;

        super.rollToDropLoot();

        int ofs;
        do {
            ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
        } while (!Dungeon.level.passable[pos + ofs]);
        Dungeon.level.drop(new GooBlob(), pos + ofs).sprite.drop(pos);
    }
}
