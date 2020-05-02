package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.RotHeart;
import studio.baka.satoripixeldungeon.actors.mobs.RotLasher;
import studio.baka.satoripixeldungeon.items.keys.IronKey;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RotGardenRoom extends SpecialRoom {

    @Override
    public int minWidth() {
        return 7;
    }

    @Override
    public int minHeight() {
        return 7;
    }

    public void paint(Level level) {

        Door entrance = entrance();
        entrance.set(Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.GRASS);


        int heartX = Random.IntRange(left + 1, right - 1);
        int heartY = Random.IntRange(top + 1, bottom - 1);

        if (entrance.x == left) {
            heartX = right - 1;
        } else if (entrance.x == right) {
            heartX = left + 1;
        } else if (entrance.y == top) {
            heartY = bottom - 1;
        } else if (entrance.y == bottom) {
            heartY = top + 1;
        }

        placePlant(level, heartX + heartY * level.width(), new RotHeart());

        int lashers = ((width() - 2) * (height() - 2)) / 8;

        for (int i = 1; i <= lashers; i++) {
            int pos;
            do {
                pos = level.pointToCell(random());
            } while (!validPlantPos(level, pos));
            placePlant(level, pos, new RotLasher());
        }
    }

    private static boolean validPlantPos(Level level, int pos) {
        if (level.map[pos] != Terrain.GRASS) {
            return false;
        }

        for (int i : PathFinder.NEIGHBOURS9) {
            if (level.findMob(pos + i) != null) {
                return false;
            }
        }

        return true;
    }

    private static void placePlant(Level level, int pos, Mob plant) {
        plant.pos = pos;
        level.mobs.add(plant);

        for (int i : PathFinder.NEIGHBOURS8) {
            if (level.map[pos + i] == Terrain.GRASS) {
                Painter.set(level, pos + i, Terrain.HIGH_GRASS);
            }
        }
    }
}
