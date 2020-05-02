package studio.baka.satoripixeldungeon.levels.rooms.secret;

import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.traps.SummoningTrap;
import com.watabou.utils.Point;

public class SecretSummoningRoom extends SecretRoom {

    //minimum of 3x3 traps, max of 6x6 traps

    @Override
    public int maxWidth() {
        return 8;
    }

    @Override
    public int maxHeight() {
        return 8;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.SECRET_TRAP);

        Point center = center();
        level.drop(Generator.random(), level.pointToCell(center)).setHauntedIfCursed(1f).type = Heap.Type.SKELETON;

        for (Point p : getPoints()) {
            int cell = level.pointToCell(p);
            if (level.map[cell] == Terrain.SECRET_TRAP) {
                level.setTrap(new SummoningTrap().hide(), cell);
            }
        }

        entrance().set(Door.Type.HIDDEN);
    }

}
