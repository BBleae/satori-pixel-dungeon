package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Blacksmith;
import studio.baka.satoripixeldungeon.levels.painters.CavesPainter;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.traps.*;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CavesLevel extends RegularLevel {

    {
        color1 = 0x534f3e;
        color2 = 0xb9d661;

        viewDistance = Math.min(6, viewDistance);
    }

    @Override
    protected ArrayList<Room> initRooms() {
        return Blacksmith.Quest.spawn(super.initRooms());
    }

    @Override
    protected int standardRooms() {
        //6 to 9, average 7.333
        return 6 + Random.chances(new float[]{2, 3, 3, 1});
    }

    @Override
    protected int specialRooms() {
        //1 to 3, average 2.2
        return 1 + Random.chances(new float[]{2, 4, 4});
    }

    @Override
    protected Painter painter() {
        return new CavesPainter()
                .setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 6)
                .setGrass(feeling == Feeling.GRASS ? 0.65f : 0.15f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_CAVES;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_CAVES;
    }

    @Override
    protected Class<?>[] trapClasses() {
        return new Class[]{BurningTrap.class, PoisonDartTrap.class, FrostTrap.class, StormTrap.class, CorrosionTrap.class,
                GrippingTrap.class, ExplosiveTrap.class, RockfallTrap.class, GuardianTrap.class,
                ConfusionTrap.class, SummoningTrap.class, WarpingTrap.class,
                PitfallTrap.class};
    }

    @Override
    protected float[] trapChances() {
        return new float[]{8, 8, 8, 8, 8,
                4, 4, 4, 4,
                2, 2, 2,
                1};
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.GRASS:
                return Messages.get(CavesLevel.class, "grass_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(CavesLevel.class, "high_grass_name");
            case Terrain.WATER:
                return Messages.get(CavesLevel.class, "water_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.ENTRANCE:
                return Messages.get(CavesLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(CavesLevel.class, "exit_desc");
            case Terrain.HIGH_GRASS:
                return Messages.get(CavesLevel.class, "high_grass_desc");
            case Terrain.WALL_DECO:
                return Messages.get(CavesLevel.class, "wall_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(CavesLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addCavesVisuals(this, visuals);
        return visuals;
    }

    public static void addCavesVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL_DECO) {
                group.add(new Vein(i));
            }
        }
    }

    private static class Vein extends Group {

        private final int pos;

        private float delay;

        public Vein(int pos) {
            super();

            this.pos = pos;

            delay = Random.Float(2);
        }

        @Override
        public void update() {

            if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {

                super.update();

                if ((delay -= Game.elapsed) <= 0) {

                    //pickaxe can remove the ore, should remove the sparkling too.
                    if (Dungeon.level.map[pos] != Terrain.WALL_DECO) {
                        kill();
                        return;
                    }

                    delay = Random.Float();

                    PointF p = DungeonTilemap.tileToWorld(pos);
                    ((Sparkle) recycle(Sparkle.class)).reset(
                            p.x + Random.Float(DungeonTilemap.SIZE),
                            p.y + Random.Float(DungeonTilemap.SIZE));
                }
            }
        }
    }

    public static final class Sparkle extends PixelParticle {

        public void reset(float x, float y) {
            revive();

            this.x = x;
            this.y = y;

            left = lifespan = 0.5f;
        }

        @Override
        public void update() {
            super.update();

            float p = left / lifespan;
            size((am = p < 0.5f ? p * 2 : (1 - p) * 2) * 2);
        }
    }
}