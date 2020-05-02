package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;

//other rooms should only extend emptyRoom if they do not add significant terrain
public class EmptyRoom extends StandardRoom {

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }
    }
}
