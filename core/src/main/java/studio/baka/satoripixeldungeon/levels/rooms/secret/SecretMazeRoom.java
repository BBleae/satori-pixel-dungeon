package studio.baka.satoripixeldungeon.levels.rooms.secret;

import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.features.Maze;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class SecretMazeRoom extends SecretRoom {

    @Override
    public int minWidth() {
        return 14;
    }

    @Override
    public int minHeight() {
        return 14;
    }

    @Override
    public int maxWidth() {
        return 18;
    }

    @Override
    public int maxHeight() {
        return 18;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        //true = space, false = wall
        Maze.allowDiagonals = false;
        boolean[][] maze = Maze.generate(this);
        boolean[] passable = new boolean[width() * height()];

        Painter.fill(level, this, 1, Terrain.EMPTY);
        for (int x = 0; x < maze.length; x++) {
            for (int y = 0; y < maze[0].length; y++) {
                if (maze[x][y] == Maze.FILLED) {
                    Painter.fill(level, x + left, y + top, 1, 1, Terrain.WALL);
                }
                passable[x + width() * y] = maze[x][y] == Maze.EMPTY;
            }
        }

        PathFinder.setMapSize(width(), height());
        Point entrance = entrance();
        int entrancePos = (entrance.x - left) + width() * (entrance.y - top);

        PathFinder.buildDistanceMap(entrancePos, passable);

        int bestDist = 0;
        Point bestDistP = new Point();
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] != Integer.MAX_VALUE
                    && PathFinder.distance[i] > bestDist) {
                bestDist = PathFinder.distance[i];
                bestDistP.x = (i % width()) + left;
                bestDistP.y = (i / width()) + top;
            }
        }

        Item prize;
        //1 floor set higher in probability, never cursed
        do {
            if (Random.Int(2) == 0) {
                prize = Generator.randomWeapon((Dungeon.depth / 5) + 1);
            } else {
                prize = Generator.randomArmor((Dungeon.depth / 5) + 1);
            }
        } while (prize.cursed || Challenges.isItemBlocked(prize));

        //33% chance for an extra update.
        if (Random.Int(3) == 0) {
            prize.upgrade();
        }

        level.drop(prize, level.pointToCell(bestDistP)).type = Heap.Type.CHEST;

        PathFinder.setMapSize(level.width(), level.height());

        entrance().set(Door.Type.HIDDEN);
    }
}
