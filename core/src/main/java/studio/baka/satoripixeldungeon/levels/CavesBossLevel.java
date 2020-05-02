package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Bones;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.DM300;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.keys.SkeletonKey;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.traps.ToxicTrap;
import studio.baka.satoripixeldungeon.levels.traps.Trap;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.tiles.DungeonTileSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class CavesBossLevel extends Level {

    {
        color1 = 0x534f3e;
        color2 = 0xb9d661;

        viewDistance = Math.min(6, viewDistance);
    }

    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;

    private static final int ROOM_LEFT = WIDTH / 2 - 3;
    private static final int ROOM_RIGHT = WIDTH / 2 + 1;
    private static final int ROOM_TOP = HEIGHT / 2 - 2;
    private static final int ROOM_BOTTOM = HEIGHT / 2 + 2;

    private int arenaDoor;
    private boolean enteredArena = false;
    private boolean keyDropped = false;

    @Override
    public String tilesTex() {
        return Assets.TILES_CAVES;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_CAVES;
    }

    private static final String DOOR = "door";
    private static final String ENTERED = "entered";
    private static final String DROPPED = "droppped";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DOOR, arenaDoor);
        bundle.put(ENTERED, enteredArena);
        bundle.put(DROPPED, keyDropped);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        arenaDoor = bundle.getInt(DOOR);
        enteredArena = bundle.getBoolean(ENTERED);
        keyDropped = bundle.getBoolean(DROPPED);
    }

    @Override
    protected boolean build() {

        setSize(WIDTH, HEIGHT);

        Rect space = new Rect();

        space.set(
                Random.IntRange(2, 6),
                Random.IntRange(2, 6),
                Random.IntRange(width - 6, width - 2),
                Random.IntRange(height - 6, height - 2)
        );

        Painter.fillEllipse(this, space, Terrain.EMPTY);

        exit = space.left + space.width() / 2 + (space.top - 1) * width();

        map[exit] = Terrain.LOCKED_EXIT;

        Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1,
                ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL);
        Painter.fill(this, ROOM_LEFT, ROOM_TOP + 1,
                ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP, Terrain.EMPTY);

        Painter.fill(this, ROOM_LEFT, ROOM_TOP,
                ROOM_RIGHT - ROOM_LEFT + 1, 1, Terrain.EMPTY_DECO);

        arenaDoor = Random.Int(ROOM_LEFT, ROOM_RIGHT) + (ROOM_BOTTOM + 1) * width();
        map[arenaDoor] = Terrain.DOOR;

        entrance = Random.Int(ROOM_LEFT + 1, ROOM_RIGHT - 1) +
                Random.Int(ROOM_TOP + 1, ROOM_BOTTOM - 1) * width();
        map[entrance] = Terrain.ENTRANCE;

        boolean[] patch = Patch.generate(width, height, 0.30f, 6, true);
        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.EMPTY && patch[i]) {
                map[i] = Terrain.WATER;
            }
        }

        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.EMPTY && Random.Int(6) == 0) {
                map[i] = Terrain.INACTIVE_TRAP;
                Trap t = new ToxicTrap().reveal();
                t.active = false;
                setTrap(t, i);
            }
        }

        for (int i = width() + 1; i < length() - width(); i++) {
            if (map[i] == Terrain.EMPTY) {
                int n = 0;
                if (map[i + 1] == Terrain.WALL) {
                    n++;
                }
                if (map[i - 1] == Terrain.WALL) {
                    n++;
                }
                if (map[i + width()] == Terrain.WALL) {
                    n++;
                }
                if (map[i - width()] == Terrain.WALL) {
                    n++;
                }
                if (Random.Int(8) <= n) {
                    map[i] = Terrain.EMPTY_DECO;
                }
            }
        }

        for (int i = 0; i < length() - width(); i++) {
            if (map[i] == Terrain.WALL
                    && DungeonTileSheet.floorTile(map[i + width()])
                    && Random.Int(3) == 0) {
                map[i] = Terrain.WALL_DECO;
            }
        }

        return true;
    }

    @Override
    protected void createMobs() {
    }

    public Actor respawner() {
        return null;
    }

    @Override
    protected void createItems() {
        Item item = Bones.get();
        if (item != null) {
            int pos;
            do {
                pos = Random.IntRange(ROOM_LEFT, ROOM_RIGHT) + Random.IntRange(ROOM_TOP + 1, ROOM_BOTTOM) * width();
            } while (pos == entrance);
            drop(item, pos).setHauntedIfCursed(1f).type = Heap.Type.REMAINS;
        }
    }

    @Override
    public int randomRespawnCell() {
        int cell;
        if (bossisalive()) {
            do {
                cell = Random.Int(0, (width() - 1) * (height() - 1));
            } while (!passable[cell] || Actor.findChar(cell) != null || !heroFOV[cell]);
            return cell;
        } else {
            do {
                cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (!passable[cell] || Actor.findChar(cell) != null);
            return cell;
        }
		/*
		int cell;
		do {
			cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (!passable[cell] || Actor.findChar(cell) != null);
		return cell;
		*/
    }

    @Override
    public void occupyCell(Char ch) {

        super.occupyCell(ch);

        if (!enteredArena && outsideEntraceRoom(ch.pos) && ch == Dungeon.hero) {

            enteredArena = true;
            seal();

            for (Mob m : mobs) {
                //bring the first ally with you
                if (m.alignment == Char.Alignment.ALLY && !m.properties().contains(Char.Property.IMMOVABLE)) {
                    m.pos = Dungeon.hero.pos + (Random.Int(2) == 0 ? +1 : -1);
                    m.sprite.place(m.pos);
                    break;
                }
            }

            DM300 boss = new DM300();
            boss.state = boss.WANDERING;
            do {
                boss.pos = Random.Int(length());
            } while (
                    !passable[boss.pos] ||
                            !outsideEntraceRoom(boss.pos) ||
                            heroFOV[boss.pos]);
            GameScene.add(boss);

            set(arenaDoor, Terrain.WALL);
            GameScene.updateMap(arenaDoor);
            Dungeon.observe();

            CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10);
            Camera.main.shake(3, 0.7f);
            Sample.INSTANCE.play(Assets.SND_ROCKS);
        }
    }

    @Override
    public Heap drop(Item item, int cell) {

        if (!keyDropped && item instanceof SkeletonKey) {

            keyDropped = true;
            unseal();

            CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10);

            set(arenaDoor, Terrain.EMPTY_DECO);
            GameScene.updateMap(arenaDoor);
            Dungeon.observe();
        }

        return super.drop(item, cell);
    }

    private boolean outsideEntraceRoom(int cell) {
        int cx = cell % width();
        int cy = cell / width();
        return cx < ROOM_LEFT - 1 || cx > ROOM_RIGHT + 1 || cy < ROOM_TOP - 1 || cy > ROOM_BOTTOM + 1;
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
        CavesLevel.addCavesVisuals(this, visuals);
        return visuals;
    }
}
