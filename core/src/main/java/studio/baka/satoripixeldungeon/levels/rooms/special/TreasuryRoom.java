package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Gold;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.keys.IronKey;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;

public class TreasuryRoom extends SpecialRoom {

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        Painter.set(level, center(), Terrain.STATUE);

        Heap.Type heapType = Random.Int(2) == 0 ? Heap.Type.CHEST : Heap.Type.HEAP;

        int n = Random.IntRange(2, 3);
        for (int i = 0; i < n; i++) {
            int pos;
            do {
                pos = level.pointToCell(random());
            } while (level.map[pos] != Terrain.EMPTY || level.heaps.get(pos) != null);
            level.drop(new Gold().random(), pos).type = (Random.Int(20) == 0 && heapType == Heap.Type.CHEST ? Heap.Type.MIMIC : heapType);
        }

        if (heapType == Heap.Type.HEAP) {
            for (int i = 0; i < 6; i++) {
                int pos;
                do {
                    pos = level.pointToCell(random());
                } while (level.map[pos] != Terrain.EMPTY);
                level.drop(new Gold(Random.IntRange(5, 12)), pos);
            }
        }

        entrance().set(Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }
}
