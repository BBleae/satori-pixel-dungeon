package studio.baka.satoripixeldungeon.levels.painters;

import studio.baka.satoripixeldungeon.actors.mobs.npcs.Wandmaker;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.rooms.standard.EntranceRoom;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PrisonPainter extends RegularPainter {

    @Override
    protected void decorate(Level level, ArrayList<Room> rooms) {

        for (Room r : rooms) {
            if (r instanceof EntranceRoom) {
                Wandmaker.Quest.spawnWandmaker(level, r);
                break;
            }
        }

        int w = level.width();
        int l = level.length();
        int[] map = level.map;

        for (int i = w + 1; i < l - w - 1; i++) {
            if (map[i] == Terrain.EMPTY) {

                float c = 0.05f;
                if (map[i + 1] == Terrain.WALL && map[i + w] == Terrain.WALL) {
                    c += 0.2f;
                }
                if (map[i - 1] == Terrain.WALL && map[i + w] == Terrain.WALL) {
                    c += 0.2f;
                }
                if (map[i + 1] == Terrain.WALL && map[i - w] == Terrain.WALL) {
                    c += 0.2f;
                }
                if (map[i - 1] == Terrain.WALL && map[i - w] == Terrain.WALL) {
                    c += 0.2f;
                }

                if (Random.Float() < c) {
                    map[i] = Terrain.EMPTY_DECO;
                }
            }
        }

        for (int i = 0; i < w; i++) {
            if (map[i] == Terrain.WALL &&
                    (map[i + w] == Terrain.EMPTY || map[i + w] == Terrain.EMPTY_SP) &&
                    Random.Int(6) == 0) {

                map[i] = Terrain.WALL_DECO;
            }
        }

        for (int i = w; i < l - w; i++) {
            if (map[i] == Terrain.WALL &&
                    map[i - w] == Terrain.WALL &&
                    (map[i + w] == Terrain.EMPTY || map[i + w] == Terrain.EMPTY_SP) &&
                    Random.Int(3) == 0) {

                map[i] = Terrain.WALL_DECO;
            }
        }
    }
}
