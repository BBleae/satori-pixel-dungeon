package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.Heap;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class NPC extends Mob {

    {
        HP = HT = 999;
        EXP = 0;

        alignment = Alignment.NEUTRAL;
        state = PASSIVE;
    }

    protected void throwItem() {
        Heap heap = Dungeon.level.heaps.get(pos);
        if (heap != null) {
            int n;
            do {
                n = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (!Dungeon.level.passable[n] && !Dungeon.level.avoid[n]);
            Dungeon.level.drop(heap.pickUp(), n).sprite.drop(pos);
        }
    }

    @Override
    public void beckon(int cell) {
    }

}