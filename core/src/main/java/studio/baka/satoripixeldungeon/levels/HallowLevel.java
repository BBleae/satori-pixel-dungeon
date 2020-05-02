package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.TestItem;
import studio.baka.satoripixeldungeon.levels.painters.HallowPainter;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.traps.*;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class HallowLevel extends RegularLevel {

    {

        viewDistance = Math.max(Dungeon.depth - 25, 2);

        color1 = 0x48763c;
        color2 = 0x59994a;
    }

    @Override
    protected int standardRooms() {
        //8 to 10, average 8.67
        return 8 + Random.chances(new float[]{3, 2, 1});
    }

    @Override
    protected int specialRooms() {
        //2 to 3, average 2.5
        return 2 + Random.chances(new float[]{2, 1});
    }

    @Override
    protected Painter painter() {
        return new HallowPainter()
                .setWater(feeling == Feeling.WATER ? 0.70f : 0.15f, 6)
                .setGrass(feeling == Feeling.GRASS ? 0.65f : 0.10f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    @Override
    public void create() {
        //addItemToSpawn( new Torch() );
        if (Dungeon.depth == 30) addItemToSpawn(new TestItem());
        super.create();
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_HALLOW;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_HALLOW;
    }

    @Override
    protected Class<?>[] trapClasses() {
        return new Class[]{FrostTrap.class, StormTrap.class, CorrosionTrap.class, BlazingTrap.class, DisintegrationTrap.class,
                ExplosiveTrap.class, RockfallTrap.class, FlashingTrap.class, GuardianTrap.class, WeakeningTrap.class,
                SummoningTrap.class, WarpingTrap.class, CursingTrap.class, GrimTrap.class,
                PitfallTrap.class, DisarmingTrap.class, DistortionTrap.class};
    }

    @Override
    protected float[] trapChances() {
        return new float[]{8, 8, 8, 8, 8,
                4, 4, 4, 4, 4,
                2, 2, 2, 2,
                1, 1, 1};
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(HallowLevel.class, "water_name");
            case Terrain.GRASS:
                return Messages.get(HallowLevel.class, "grass_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(HallowLevel.class, "high_grass_name");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(HallowLevel.class, "statue_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(HallowLevel.class, "water_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(HallowLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(HallowLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addHallowVisuals(this, visuals);
        return visuals;
    }

    public static void addHallowVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WATER) {
                group.add(new Stream(i));
            }
        }
    }

    private static class Stream extends Group {

        private final int pos;

        private float delay;

        public Stream(int pos) {
            super();

            this.pos = pos;

            delay = Random.Float(2);
        }

        @Override
        public void update() {

            if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {

                super.update();

                if ((delay -= Game.elapsed) <= 0) {

                    delay = Random.Float(2);

                    PointF p = DungeonTilemap.tileToWorld(pos);
                    ((FireParticle) recycle(FireParticle.class)).reset(
                            p.x + Random.Float(DungeonTilemap.SIZE),
                            p.y + Random.Float(DungeonTilemap.SIZE));
                }
            }
        }

        @Override
        public void draw() {
            Blending.setLightMode();
            super.draw();
            Blending.setNormalMode();
        }
    }

    public static class FireParticle extends PixelParticle.Shrinking {

        public FireParticle() {
            super();

            color(0xEE7722);
            lifespan = 1f;

            acc.set(0, +80);
        }

        public void reset(float x, float y) {
            revive();

            this.x = x;
            this.y = y;

            left = lifespan;

            speed.set(0, -40);
            size = 4;
        }

        @Override
        public void update() {
            super.update();
            float p = left / lifespan;
            am = p > 0.8f ? (1 - p) * 5 : 1;
        }
    }
}
