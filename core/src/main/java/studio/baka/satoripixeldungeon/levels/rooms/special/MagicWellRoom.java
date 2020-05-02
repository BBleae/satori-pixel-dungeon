package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.actors.blobs.WaterOfAwareness;
import studio.baka.satoripixeldungeon.actors.blobs.WaterOfHealth;
import studio.baka.satoripixeldungeon.actors.blobs.WellWater;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class MagicWellRoom extends SpecialRoom {

    private static final Class<?>[] WATERS =
            {WaterOfAwareness.class, WaterOfHealth.class};

    public Class<? extends WellWater> overrideWater = null;

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        Point c = center();
        Painter.set(level, c.x, c.y, Terrain.WELL);

        @SuppressWarnings("unchecked")
        Class<? extends WellWater> waterClass =
                overrideWater != null ?
                        overrideWater :
                        (Class<? extends WellWater>) Random.element(WATERS);


        WellWater.seed(c.x + level.width() * c.y, 1, waterClass, level);

        entrance().set(Door.Type.REGULAR);
    }
}
