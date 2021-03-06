package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Author;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.effects.particles.SparkParticle;
import studio.baka.satoripixeldungeon.items.Amulet;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.levels.features.MazeBalanca;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Arrays;

//import studio.baka.satoripixeldungeon.levels.features.MazeBalanca;
//import studio.baka.satoripixeldungeon.levels.rooms.MazeBalancaRoom;

public class HallowBossLevel extends Level {

    {
        color1 = 0x6a723d;
        color2 = 0x88924c;
    }

    //keep track of that need to be removed as the level is changed. We dump 'em back into the level at the end.
    //private ArrayList<Item> storedItems = new ArrayList<>();

    private int stairs = -1;
    private boolean enteredArena = false;
    private boolean keyDropped = false;

    private static final int WIDTH = 4 * 6 + 1;
    private static final int HEIGHT = 4 * 6 + 1;

    private static int[] map_bk, map_maze1, map_maze2, map_maze3;

    public enum stat {
        not_started, maze1, maze2, maze3, ended
    }

    public stat levelstat = stat.not_started;

    @Override
    public String tilesTex() {
        return Assets.TILES_HALLOW;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_HALLOW;
    }

    private static final String STAIRS = "stairs";
    private static final String ENTERED = "entered";
    private static final String DROPPED = "droppped";
    private static final String STAT = "stat";
    private static final String MAP = "map_";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STAIRS, stairs);
        bundle.put(ENTERED, enteredArena);
        bundle.put(DROPPED, keyDropped);
        bundle.put(STAT, levelstat);

        bundle.put(MAP + "0", map_bk);
        bundle.put(MAP + "1", map_maze1);
        bundle.put(MAP + "2", map_maze2);
        bundle.put(MAP + "3", map_maze3);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        stairs = bundle.getInt(STAIRS);
        enteredArena = bundle.getBoolean(ENTERED);
        keyDropped = bundle.getBoolean(DROPPED);
        levelstat = bundle.getEnum(STAT, stat.class);

        map_bk = bundle.getIntArray(MAP + "0");
        map_maze1 = bundle.getIntArray(MAP + "1");
        map_maze2 = bundle.getIntArray(MAP + "2");
        map_maze3 = bundle.getIntArray(MAP + "3");
    }

    @Override
    protected boolean build() {

        setSize(WIDTH, HEIGHT);

        Arrays.fill(map, Terrain.WALL);
        Painter.fill(this, 1, 1, WIDTH - 2, HEIGHT - 2, Terrain.EMPTY_SP);

        buildFlagMaps();
        cleanWalls();

        entrance = WIDTH / 2 + (HEIGHT / 2) * width;
        map[entrance] = Terrain.ENTRANCE;
        exit = 1 + 2 * width;

        levelstat = stat.not_started;
        map_bk = map.clone();

        MazeBalanca mazeBalanca1 = new MazeBalanca(6, 6);
        map_maze1 = mazeBalanca1.paint_pArray();//17*17=289 map
        MazeBalanca mazeBalanca2 = new MazeBalanca(6, 6);
        map_maze2 = mazeBalanca2.paint_pArray();
        MazeBalanca mazeBalanca3 = new MazeBalanca(6, 6);
        map_maze3 = mazeBalanca3.paint_pArray();

        return true;
    }

    /*
    @Override
    public Mob createMob() {
        return null;
    }
    */
    @Override
    protected void createMobs() {
    }

    public Actor respawner() {
        return null;
    }

    public int randomRespawnCell() {
        int cell;
        do {
            cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
        } while (!passable[cell] || Actor.findChar(cell) != null);
        return cell;
    }

    @Override
    protected void createItems() {
        //drop(new TestItem(), 30+30*width);
    }

    private void doMagic(int cell) {
        set(cell, Terrain.EMPTY_SP);
        CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.1f, 3);
    }

    public void process() {
        switch (levelstat) {
            case not_started:
                change_map_to_maze(1);
                moveplayer_tostartpoint();
                moveauthor();
                levelstat = stat.maze1;
                break;
            case maze1:
                change_map_to_maze(2);
                moveplayer_tostartpoint();
                levelstat = stat.maze2;
                break;
            case maze2:
                change_map_to_maze(3);
                moveplayer_tostartpoint();
                levelstat = stat.maze3;
                break;
            case maze3:
                change_map_to_maze(0);
                levelstat = stat.ended;
                break;
            case ended:
            default:
                killauthor();
                break;
        }
    }

    private void change_map_to_maze(int map_index) {

        switch (map_index) {
            case 3:
                changeMap(map_maze3);
                break;
            case 2:
                changeMap(map_maze2);
                break;
            case 1:
                changeMap(map_maze1);
                break;
            case 0:
            default:
                changeMap(map_bk);
                break;
        }

        cleanMapState();
    }

    private void moveplayer_tostartpoint() {
        //ScrollOfTeleportation.teleportToLocation(Dungeon.hero,getPos(2,2));
        ScrollOfTeleportation.appear(Dungeon.hero, getPos(2, 2));
        occupyCell(Dungeon.hero);
        Dungeon.observe();
        GameScene.updateFog();

        for (Mob m : mobs) {
            //bring the first ally with you
            if (m.alignment == Char.Alignment.ALLY && !m.properties().contains(Char.Property.IMMOVABLE)) {
                m.pos = getPos(1, 1);
                m.sprite.place(m.pos);
                break;
            }
        }
    }

    private void moveauthor() {
        for (Mob m : mobs) {
            //bring the Author to the end of the maze
            if (m instanceof Author) {
                m.pos = getPos(WIDTH - 3, HEIGHT - 3);
                m.sprite.place(m.pos);
                break;
            }
        }
    }

    private void killauthor() {
        for (Mob m : mobs) {
            //bring the Author to the end of the maze
            if (m instanceof Author) {
                m.die(Dungeon.hero);
                break;
            }
        }
    }

    private void changeMap(int[] map) {
        GameScene.flash(0x000000);        //先闪它一下。
        this.map = map.clone();
        buildFlagMaps();
        cleanWalls();

        BArray.setFalse(visited);
        BArray.setFalse(mapped);

        for (Blob blob : blobs.values()) {
            blob.fullyClear();
        }
        addVisuals(); //this also resets existing visuals

        GameScene.resetMap(); //???
        Dungeon.observe();
        GameScene.flash(0x000000);        //闪它一下。
        Sample.INSTANCE.play(Assets.SND_BLAST);
    }

    private void cleanMapState() {
        buildFlagMaps();
        cleanWalls();

        BArray.setFalse(visited);
        BArray.setFalse(mapped);

        for (Blob blob : blobs.values()) {
            blob.fullyClear();
        }
        addVisuals(); //this also resets existing visuals
        traps.clear();
    }

    @Override
    public void occupyCell(Char ch) {

        super.occupyCell(ch);

        if (!enteredArena && ch == Dungeon.hero && ch.pos != entrance) {
            enteredArena = true;
            seal();
            doMagic(entrance);
            GameScene.updateMap();

            Dungeon.observe();

            Author boss = new Author();
            boss.pos = entrance;
			/*
			do {
				boss.pos = Random.Int( length() );
			}
			while (!passable[boss.pos] || !heroFOV[boss.pos]);
			*/
            CellEmitter.get(boss.pos).start(SparkParticle.FACTORY, 0.1f, 3);
            GameScene.add(boss);

            boss.notice();

            stairs = entrance;
            entrance = -1;
        }
    }

    @Override
    public Heap drop(Item item, int cell) {

        if (!keyDropped && item instanceof Amulet) {
            keyDropped = true;
            unseal();

            entrance = stairs;
            set(entrance, Terrain.ENTRANCE);
            GameScene.updateMap(entrance);
        }

        return super.drop(item, cell);
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
        CityLevel.addCityVisuals(this, visuals);
        return visuals;
    }
}
