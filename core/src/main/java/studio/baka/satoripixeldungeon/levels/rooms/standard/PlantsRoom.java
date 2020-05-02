package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.plants.Firebloom;
import studio.baka.satoripixeldungeon.plants.Plant;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class PlantsRoom extends StandardRoom {

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 5);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 5);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{3, 1, 0};
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.GRASS);
        Painter.fill(level, this, 2, Terrain.HIGH_GRASS);

        if (Math.min(width(), height()) >= 7) {
            Painter.fill(level, this, 3, Terrain.GRASS);
        }

        Point center = center();

        //place at least 2 plants for rooms with at least 9 in one dimensions
        if (Math.max(width(), height()) >= 9) {

            //place 4 plants for very large rooms
            if (Math.min(width(), height()) >= 11) {
                Painter.drawLine(level, new Point(left + 2, center.y), new Point(right - 2, center.y), Terrain.HIGH_GRASS);
                Painter.drawLine(level, new Point(center.x, top + 2), new Point(center.x, bottom - 2), Terrain.HIGH_GRASS);
                level.plant(randomSeed(), level.pointToCell(new Point(center.x - 1, center.y - 1)));
                level.plant(randomSeed(), level.pointToCell(new Point(center.x + 1, center.y - 1)));
                level.plant(randomSeed(), level.pointToCell(new Point(center.x - 1, center.y + 1)));
                level.plant(randomSeed(), level.pointToCell(new Point(center.x + 1, center.y + 1)));

                //place 2 plants otherwise
                //left/right
            } else if (width() > height() || (width() == height() && Random.Int(2) == 0)) {
                Painter.drawLine(level, new Point(center.x, top + 2), new Point(center.x, bottom - 2), Terrain.HIGH_GRASS);
                level.plant(randomSeed(), level.pointToCell(new Point(center.x - 1, center.y)));
                level.plant(randomSeed(), level.pointToCell(new Point(center.x + 1, center.y)));

                //top/bottom
            } else {
                Painter.drawLine(level, new Point(left + 2, center.y), new Point(right - 2, center.y), Terrain.HIGH_GRASS);
                level.plant(randomSeed(), level.pointToCell(new Point(center.x, center.y - 1)));
                level.plant(randomSeed(), level.pointToCell(new Point(center.x, center.y + 1)));

            }

            //place just one plant for smaller sized rooms
        } else {
            level.plant(randomSeed(), level.pointToCell(center));
        }

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }
    }

    private static Plant.Seed randomSeed() {
        Plant.Seed result;
        do {
            result = (Plant.Seed) Generator.random(Generator.Category.SEED);
        } while (result instanceof Firebloom.Seed);
        return result;
    }
}
