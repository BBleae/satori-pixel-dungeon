package studio.baka.satoripixeldungeon.levels.rooms.connection;

import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.features.Maze;
import studio.baka.satoripixeldungeon.levels.painters.Painter;

public class MazeConnectionRoom extends ConnectionRoom {

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 1, Terrain.EMPTY);

        //true = space, false = wall
        Maze.allowDiagonals = false;
        boolean[][] maze = Maze.generate(this);

        Painter.fill(level, this, 1, Terrain.EMPTY);
        for (int x = 0; x < maze.length; x++)
            for (int y = 0; y < maze[0].length; y++) {
                if (maze[x][y] == Maze.FILLED) {
                    Painter.fill(level, x + left, y + top, 1, 1, Terrain.WALL);
                }
            }

        for (Door door : connected.values()) {
            door.set(Door.Type.HIDDEN);
        }
    }

    @Override
    public int maxConnections(int direction) {
        return 2;
    }
}
