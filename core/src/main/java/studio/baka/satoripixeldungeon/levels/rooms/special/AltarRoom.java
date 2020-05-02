package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

//import studio.baka.satoripixeldungeon.actors.blobs.SacrificialFire;

public class AltarRoom extends SpecialRoom {

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Dungeon.bossLevel(Dungeon.depth + 1) ? Terrain.HIGH_GRASS : Terrain.CHASM);

        Point c = center();
        Door door = entrance();
        if (door.x == left || door.x == right) {
            Point p = Painter.drawInside(level, this, door, Math.abs(door.x - c.x) - 2, Terrain.EMPTY_SP);
            for (; p.y != c.y; p.y += p.y < c.y ? +1 : -1) {
                Painter.set(level, p, Terrain.EMPTY_SP);
            }
        } else {
            Point p = Painter.drawInside(level, this, door, Math.abs(door.y - c.y) - 2, Terrain.EMPTY_SP);
            for (; p.x != c.x; p.x += p.x < c.x ? +1 : -1) {
                Painter.set(level, p, Terrain.EMPTY_SP);
            }
        }

        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, Terrain.EMBERS);
        Painter.set(level, c, Terrain.PEDESTAL);

        //TODO: find some use for sacrificial fire... but not the vanilla one. scroll of wipe out is too strong.
		/*SacrificialFire fire = (SacrificialFire)level.blobs.get( SacrificialFire.class );
		if (fire == null) {
			fire = new SacrificialFire();
		}
		fire.seed( c.x + c.y * Level.WIDTH, 5 + Dungeon.depth * 5 );
		level.blobs.put( SacrificialFire.class, fire );*/

        door.set(Door.Type.EMPTY);
    }
}