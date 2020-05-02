package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.items.keys.IronKey;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class ArmoryRoom extends SpecialRoom {

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        Door entrance = entrance();
        Point statue = null;
        if (entrance.x == left) {
            statue = new Point(right - 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
        } else if (entrance.x == right) {
            statue = new Point(left + 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
        } else if (entrance.y == top) {
            statue = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, bottom - 1);
        } else if (entrance.y == bottom) {
            statue = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, top + 1);
        }
        if (statue != null) {
            Painter.set(level, statue, Terrain.STATUE);
        }

        int n = Random.IntRange(2, 3);
        for (int i = 0; i < n; i++) {
            int pos;
            do {
                pos = level.pointToCell(random());
            } while (level.map[pos] != Terrain.EMPTY || level.heaps.get(pos) != null);
            level.drop(prize(level), pos);
        }

        entrance.set(Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }

    private static Item prize(Level level) {
        switch (Random.Int(4)) {
            case 0:
                return new Bomb().random();
            case 1:
                return Generator.randomWeapon();
            case 2:
                return Generator.randomArmor();
            case 3:
            default:
                return Generator.randomMissile();
        }
    }
}
