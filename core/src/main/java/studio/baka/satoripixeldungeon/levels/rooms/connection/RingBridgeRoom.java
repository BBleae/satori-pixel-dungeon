package studio.baka.satoripixeldungeon.levels.rooms.connection;

import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import com.watabou.utils.Rect;

public class RingBridgeRoom extends RingTunnelRoom {

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 1, Terrain.CHASM);

        super.paint(level);

        for (Room r : neigbours) {
            if (r instanceof BridgeRoom || r instanceof RingBridgeRoom || r instanceof WalkwayRoom) {
                Rect i = intersect(r);
                if (i.width() != 0) {
                    i.left++;
                    i.right--;
                } else {
                    i.top++;
                    i.bottom--;
                }
                Painter.fill(level, i.left, i.top, i.width() + 1, i.height() + 1, Terrain.CHASM);
            }
        }
    }
}
