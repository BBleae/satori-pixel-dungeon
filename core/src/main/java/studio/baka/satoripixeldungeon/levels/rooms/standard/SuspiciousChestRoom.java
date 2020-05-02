package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.items.Gold;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;

public class SuspiciousChestRoom extends EmptyRoom {

    @Override
    public int minWidth() {
        return Math.max(5, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(5, super.minHeight());
    }

    @Override
    public void paint(Level level) {
        super.paint(level);

        Item i = level.findPrizeItem();

        if (i == null) {
            i = new Gold().random();
        }

        int center = level.pointToCell(center());

        Painter.set(level, center, Terrain.PEDESTAL);

        if (Random.Int(3) == 0) {
            level.drop(i, center).type = Heap.Type.MIMIC;
        } else {
            level.drop(i, center).type = Heap.Type.CHEST;
        }
    }
}
