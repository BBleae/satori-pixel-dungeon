package studio.baka.satoripixeldungeon.levels.rooms.secret;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.keys.GoldenKey;
import studio.baka.satoripixeldungeon.items.potions.PotionOfLevitation;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

public class SecretChestChasmRoom extends SecretRoom {

    //width and height are controlled here so that this room always requires 2 levitation potions

    @Override
    public int minWidth() {
        return 8;
    }

    @Override
    public int maxWidth() {
        return 9;
    }

    @Override
    public int minHeight() {
        return 8;
    }

    @Override
    public int maxHeight() {
        return 9;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.CHASM);

        int chests = 0;

        Point p = new Point(left + 3, top + 3);
        Painter.set(level, p, Terrain.EMPTY_SP);
        level.drop(Generator.random(), level.pointToCell(p)).type = Heap.Type.LOCKED_CHEST;
        if (level.heaps.get(level.pointToCell(p)) != null) chests++;

        p.x = right - 3;
        Painter.set(level, p, Terrain.EMPTY_SP);
        level.drop(Generator.random(), level.pointToCell(p)).type = Heap.Type.LOCKED_CHEST;
        if (level.heaps.get(level.pointToCell(p)) != null) chests++;

        p.y = bottom - 3;
        Painter.set(level, p, Terrain.EMPTY_SP);
        level.drop(Generator.random(), level.pointToCell(p)).type = Heap.Type.LOCKED_CHEST;
        if (level.heaps.get(level.pointToCell(p)) != null) chests++;

        p.x = left + 3;
        Painter.set(level, p, Terrain.EMPTY_SP);
        level.drop(Generator.random(), level.pointToCell(p)).type = Heap.Type.LOCKED_CHEST;
        if (level.heaps.get(level.pointToCell(p)) != null) chests++;

        p = new Point(left + 1, top + 1);
        Painter.set(level, p, Terrain.EMPTY_SP);
        if (chests > 0) {
            level.drop(new GoldenKey(Dungeon.depth), level.pointToCell(p));
            chests--;
        }

        p.x = right - 1;
        Painter.set(level, p, Terrain.EMPTY_SP);
        if (chests > 0) {
            level.drop(new GoldenKey(Dungeon.depth), level.pointToCell(p));
            chests--;
        }

        p.y = bottom - 1;
        Painter.set(level, p, Terrain.EMPTY_SP);
        if (chests > 0) {
            level.drop(new GoldenKey(Dungeon.depth), level.pointToCell(p));
            chests--;
        }

        p.x = left + 1;
        Painter.set(level, p, Terrain.EMPTY_SP);
        if (chests > 0) {
            level.drop(new GoldenKey(Dungeon.depth), level.pointToCell(p));
        }

        level.addItemToSpawn(new PotionOfLevitation());

        entrance().set(Door.Type.HIDDEN);
    }
}
