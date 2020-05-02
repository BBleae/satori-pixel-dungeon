package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.traps.ExplosiveTrap;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class MinefieldRoom extends StandardRoom {

    @Override
    public float[] sizeCatProbs() {
        return new float[]{4, 1, 0};
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);
        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }

        int mines = (int) Math.round(Math.sqrt(square()));

        switch (sizeCat) {
            case NORMAL:
                mines -= 3;
                break;
            case LARGE:
                mines += 3;
                break;
            case GIANT:
                mines += 9;
                break;
        }

        for (int i = 0; i < mines; i++) {
            int pos;
            do {
                pos = level.pointToCell(random(1));
            } while (level.traps.get(pos) != null);

            //randomly places some embers around the mines
            for (int j = 0; j < 8; j++) {
                int c = PathFinder.NEIGHBOURS8[Random.Int(8)];
                if (level.traps.get(pos + c) == null && level.map[pos + c] == Terrain.EMPTY) {
                    Painter.set(level, pos + c, Terrain.EMBERS);
                }
            }

            Painter.set(level, pos, Terrain.SECRET_TRAP);
            level.setTrap(new ExplosiveTrap().hide(), pos);

        }

    }
}
