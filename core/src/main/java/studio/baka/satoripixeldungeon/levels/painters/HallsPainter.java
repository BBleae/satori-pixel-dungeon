package studio.baka.satoripixeldungeon.levels.painters;

import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class HallsPainter extends RegularPainter {

    @Override
    protected void decorate(Level level, ArrayList<Room> rooms) {

        int[] map = level.map;
        int w = level.width();
        int l = level.length();

        for (int i = w + 1; i < l - w - 1; i++) {
            if (map[i] == Terrain.EMPTY) {

                int count = 0;
                for (int j = 0; j < PathFinder.NEIGHBOURS8.length; j++) {
                    if ((Terrain.flags[map[i + PathFinder.NEIGHBOURS8[j]]] & Terrain.PASSABLE) > 0) {
                        count++;
                    }
                }

                if (Random.Int(80) < count) {
                    map[i] = Terrain.EMPTY_DECO;
                }

            } else if (map[i] == Terrain.WALL &&
                    map[i - 1] != Terrain.WALL_DECO && map[i - w] != Terrain.WALL_DECO &&
                    Random.Int(20) == 0) {

                map[i] = Terrain.WALL_DECO;

            }
        }
    }
}
