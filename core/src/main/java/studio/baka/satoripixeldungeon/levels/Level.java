package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.*;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.SmokeScreen;
import studio.baka.satoripixeldungeon.actors.blobs.WellWater;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.actors.mobs.Bestiary;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Sheep;
import studio.baka.satoripixeldungeon.effects.particles.FlowParticle;
import studio.baka.satoripixeldungeon.effects.particles.WindParticle;
import studio.baka.satoripixeldungeon.items.*;
import studio.baka.satoripixeldungeon.items.artifacts.DriedRose;
import studio.baka.satoripixeldungeon.items.artifacts.TimekeepersHourglass;
import studio.baka.satoripixeldungeon.items.food.SmallRation;
import studio.baka.satoripixeldungeon.items.potions.PotionOfStrength;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfUpgrade;
import studio.baka.satoripixeldungeon.items.stones.StoneOfEnchantment;
import studio.baka.satoripixeldungeon.items.stones.StoneOfIntuition;
import studio.baka.satoripixeldungeon.items.wands.WandOfWarding;
import studio.baka.satoripixeldungeon.levels.features.Chasm;
import studio.baka.satoripixeldungeon.levels.features.Door;
import studio.baka.satoripixeldungeon.levels.features.HighGrass;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.traps.Trap;
import studio.baka.satoripixeldungeon.mechanics.ShadowCaster;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.plants.Swiftthistle;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.tiles.CustomTilemap;
import studio.baka.satoripixeldungeon.utils.BArray;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.watabou.utils.*;

import java.util.*;

public abstract class Level implements Bundlable {

    public enum Feeling {
        NONE,
        CHASM,
        WATER,
        GRASS,
        DARK
    }

    protected int width;
    protected int height;
    protected int length;

    protected static final float TIME_TO_RESPAWN = 50;

    public int version;

    public int[] map;
    public boolean[] visited;
    public boolean[] mapped;
    public boolean[] discoverable;

    public int viewDistance = Dungeon.isChallenged(Challenges.DARKNESS) ? 2 : 8;

    public boolean[] heroFOV;

    public boolean[] passable;
    public boolean[] losBlocking;
    public boolean[] flamable;
    public boolean[] secret;
    public boolean[] solid;
    public boolean[] avoid;
    public boolean[] water;
    public boolean[] pit;

    public Feeling feeling = Feeling.NONE;

    public int entrance;
    public int exit;

    //when a boss level has become locked.
    public boolean locked = false;

    public HashSet<Mob> mobs;
    public SparseArray<Heap> heaps;
    public HashMap<Class<? extends Blob>, Blob> blobs;
    public SparseArray<Plant> plants;
    public SparseArray<Trap> traps;
    public HashSet<CustomTilemap> customTiles;
    public HashSet<CustomTilemap> customWalls;

    protected ArrayList<Item> itemsToSpawn = new ArrayList<>();

    protected Group visuals;

    public int color1 = 0x004400;
    public int color2 = 0x88CC44;

    private static final String VERSION = "version";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String MAP = "map";
    private static final String VISITED = "visited";
    private static final String MAPPED = "mapped";
    private static final String ENTRANCE = "entrance";
    private static final String EXIT = "exit";
    private static final String LOCKED = "locked";
    private static final String HEAPS = "heaps";
    private static final String PLANTS = "plants";
    private static final String TRAPS = "traps";
    private static final String CUSTOM_TILES = "customTiles";
    private static final String CUSTOM_WALLS = "customWalls";
    private static final String MOBS = "mobs";
    private static final String BLOBS = "blobs";
    private static final String FEELING = "feeling";

    public void create() {

        Random.seed(Dungeon.seedCurDepth());

        if (!(Dungeon.bossLevel() || Dungeon.depth == 21) /*final shop floor*/) {

            if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
                addItemToSpawn(new SmallRation());
            } else {
                addItemToSpawn(Generator.random(Generator.Category.FOOD));
            }

            if (Dungeon.isChallenged(Challenges.DARKNESS)) {
                addItemToSpawn(new Torch());
            }

            if (Dungeon.posNeeded()) {
                addItemToSpawn(new PotionOfStrength());
                Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
            }
            if (Dungeon.souNeeded()) {
                addItemToSpawn(new ScrollOfUpgrade());
                Dungeon.LimitedDrops.UPGRADE_SCROLLS.count++;
            }
            if (Dungeon.asNeeded()) {
                addItemToSpawn(new Stylus());
                Dungeon.LimitedDrops.ARCANE_STYLI.count++;
            }
            //one scroll of transmutation is guaranteed to spawn somewhere on chapter 2-4
            int enchChapter = (int) ((Dungeon.seed / 10) % 3) + 1;
            if (Dungeon.depth / 5 == enchChapter &&
                    Dungeon.seed % 4 + 1 == Dungeon.depth % 5) {
                addItemToSpawn(new StoneOfEnchantment());
            }

            if (Dungeon.depth == ((Dungeon.seed % 3) + 1)) {
                addItemToSpawn(new StoneOfIntuition());
            }

            DriedRose rose = Dungeon.hero.belongings.getItem(DriedRose.class);
            if (rose != null && rose.isIdentified() && !rose.cursed) {
                //aim to drop 1 petal every 2 floors
                int petalsNeeded = (int) Math.ceil((float) ((Dungeon.depth / 2) - rose.droppedPetals) / 3);

                for (int i = 1; i <= petalsNeeded; i++) {
                    //the player may miss a single petal and still max their rose.
                    if (rose.droppedPetals < 11) {
                        addItemToSpawn(new DriedRose.Petal());
                        rose.droppedPetals++;
                    }
                }
            }

            if (Dungeon.depth > 1) {
                switch (Random.Int(10)) {
                    case 0:
                        if (!Dungeon.bossLevel(Dungeon.depth + 1)) {
                            feeling = Feeling.CHASM;
                        }
                        break;
                    case 1:
                        feeling = Feeling.WATER;
                        break;
                    case 2:
                        feeling = Feeling.GRASS;
                        break;
                    case 3:
                        feeling = Feeling.DARK;
                        addItemToSpawn(new Torch());
                        viewDistance = Math.round(viewDistance / 2f);
                        break;
                }
            }
        }

        do {
            width = height = length = 0;

            mobs = new HashSet<>();
            heaps = new SparseArray<>();
            blobs = new HashMap<>();
            plants = new SparseArray<>();
            traps = new SparseArray<>();
            customTiles = new HashSet<>();
            customWalls = new HashSet<>();

        } while (!build());

        buildFlagMaps();
        cleanWalls();

        createMobs();
        createItems();

        Random.seed();
    }

    public int getPos(int x, int y) {
        return y * width + x;
    }

    public void setSize(int w, int h) {

        width = w;
        height = h;
        length = w * h;

        map = new int[length];
        Arrays.fill(map, feeling == Level.Feeling.CHASM ? Terrain.CHASM : Terrain.WALL);

        visited = new boolean[length];
        mapped = new boolean[length];

        heroFOV = new boolean[length];

        passable = new boolean[length];
        losBlocking = new boolean[length];
        flamable = new boolean[length];
        secret = new boolean[length];
        solid = new boolean[length];
        avoid = new boolean[length];
        water = new boolean[length];
        pit = new boolean[length];

        PathFinder.setMapSize(w, h);
    }

    public void reset() {

        mobs.removeIf(mob -> !mob.reset());
        createMobs();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {

        version = bundle.getInt(VERSION);

        //saves from before 0.6.5c are not supported
        if (version < SatoriPixelDungeon.v0_6_5c) {
            throw new RuntimeException("old save");
        }

        setSize(bundle.getInt(WIDTH), bundle.getInt(HEIGHT));

        mobs = new HashSet<>();
        heaps = new SparseArray<>();
        blobs = new HashMap<>();
        plants = new SparseArray<>();
        traps = new SparseArray<>();
        customTiles = new HashSet<>();
        customWalls = new HashSet<>();

        map = bundle.getIntArray(MAP);

        visited = bundle.getBooleanArray(VISITED);
        mapped = bundle.getBooleanArray(MAPPED);

        entrance = bundle.getInt(ENTRANCE);
        exit = bundle.getInt(EXIT);

        locked = bundle.getBoolean(LOCKED);

        Collection<Bundlable> collection = bundle.getCollection(HEAPS);
        for (Bundlable h : collection) {
            Heap heap = (Heap) h;
            if (!heap.isEmpty())
                heaps.put(heap.pos, heap);
        }

        collection = bundle.getCollection(PLANTS);
        for (Bundlable p : collection) {
            Plant plant = (Plant) p;
            plants.put(plant.pos, plant);
        }

        collection = bundle.getCollection(TRAPS);
        for (Bundlable p : collection) {
            Trap trap = (Trap) p;
            traps.put(trap.pos, trap);
        }

        collection = bundle.getCollection(CUSTOM_TILES);
        for (Bundlable p : collection) {
            CustomTilemap vis = (CustomTilemap) p;
            customTiles.add(vis);
        }

        collection = bundle.getCollection(CUSTOM_WALLS);
        for (Bundlable p : collection) {
            CustomTilemap vis = (CustomTilemap) p;
            customWalls.add(vis);
        }

        collection = bundle.getCollection(MOBS);
        for (Bundlable m : collection) {
            Mob mob = (Mob) m;
            if (mob != null) {
                mobs.add(mob);
            }
        }

        collection = bundle.getCollection(BLOBS);
        for (Bundlable b : collection) {
            Blob blob = (Blob) b;
            blobs.put(blob.getClass(), blob);
        }

        feeling = bundle.getEnum(FEELING, Feeling.class);
        if (feeling == Feeling.DARK)
            viewDistance = Math.round(viewDistance / 2f);

        if (bundle.contains("mobs_to_spawn")) {
            //noinspection unchecked
            for (Class<? extends Mob> mob : bundle.getClassArray("mobs_to_spawn")) {
                if (mob != null) mobsToSpawn.add(mob);
            }
        }

        buildFlagMaps();
        cleanWalls();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(VERSION, Game.versionCode);
        bundle.put(WIDTH, width);
        bundle.put(HEIGHT, height);
        bundle.put(MAP, map);
        bundle.put(VISITED, visited);
        bundle.put(MAPPED, mapped);
        bundle.put(ENTRANCE, entrance);
        bundle.put(EXIT, exit);
        bundle.put(LOCKED, locked);
        bundle.put(HEAPS, heaps.valueList());
        bundle.put(PLANTS, plants.valueList());
        bundle.put(TRAPS, traps.valueList());
        bundle.put(CUSTOM_TILES, customTiles);
        bundle.put(CUSTOM_WALLS, customWalls);
        bundle.put(MOBS, mobs);
        bundle.put(BLOBS, blobs.values());
        bundle.put(FEELING, feeling);
        bundle.put("mobs_to_spawn", mobsToSpawn.toArray(new Class[0]));
    }

    public int tunnelTile() {
        return feeling == Feeling.CHASM ? Terrain.EMPTY_SP : Terrain.EMPTY;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int length() {
        return length;
    }

    public String tilesTex() {
        return null;
    }

    public String waterTex() {
        return null;
    }

    abstract protected boolean build();

    private ArrayList<Class<? extends Mob>> mobsToSpawn = new ArrayList<>();

    public Mob createMob() {
        if (mobsToSpawn == null || mobsToSpawn.isEmpty()) {
            mobsToSpawn = Bestiary.getMobRotation(Dungeon.depth);
        }

        return Reflection.newInstance(mobsToSpawn.remove(0));
    }

    abstract protected void createMobs();

    abstract protected void createItems();

    public void seal() {
        if (!locked) {
            locked = true;
            Buff.affect(Dungeon.hero, LockedFloor.class);
        }
    }

    public void unseal() {
        if (locked) {
            locked = false;
        }
    }

    public Group addVisuals() {
        if (visuals == null || visuals.parent == null) {
            visuals = new Group();
        } else {
            visuals.clear();
            visuals.camera = null;
        }
        for (int i = 0; i < length(); i++) {
            if (pit[i]) {
                visuals.add(new WindParticle.Wind(i));
                if (i >= width() && water[i - width()]) {
                    visuals.add(new FlowParticle.Flow(i - width()));
                }
            }
        }
        return visuals;
    }

    public int nMobs() {
        return 0;
    }

    public Mob findMob(int pos) {
        for (Mob mob : mobs) {
            if (mob.pos == pos) {
                return mob;
            }
        }
        return null;
    }

    public Actor respawner() {
        return new Actor() {

            {
                actPriority = BUFF_PRIORITY; //as if it were a buff.
            }

            @Override
            protected boolean act() {
                int count = 0;
                for (Mob mob : mobs.toArray(new Mob[0])) {
                    if (mob.alignment == Char.Alignment.ENEMY) count++;
                }

                if (count < nMobs()) {

                    Mob mob = createMob();
                    mob.state = mob.WANDERING;
                    mob.pos = randomRespawnCell();
                    if (Dungeon.hero.isAlive() && mob.pos != -1 && distance(Dungeon.hero.pos, mob.pos) >= 4) {
                        GameScene.add(mob);
                        if (Statistics.amuletObtained) {
                            mob.beckon(Dungeon.hero.pos);
                        }
                    }
                }
                spend(respawnTime());
                return true;
            }
        };
    }

    public float respawnTime() {
        if (Statistics.amuletObtained) {
            return TIME_TO_RESPAWN / 2f;
        } else if (Dungeon.level.feeling == Feeling.DARK) {
            return 2 * TIME_TO_RESPAWN / 3f;
        } else {
            return TIME_TO_RESPAWN;
        }
    }

    public int randomRespawnCell() {
        int cell;
        do {
            cell = Random.Int(length());
        } while ((Dungeon.level == this && heroFOV[cell])
                || !passable[cell]
                || Actor.findChar(cell) != null);
        return cell;
    }

    public int randomDestination() {
        int cell;
        do {
            cell = Random.Int(length());
        } while (!passable[cell]);
        return cell;
    }

    public void addItemToSpawn(Item item) {
        if (item != null) {
            itemsToSpawn.add(item);
        }
    }

    public Item findPrizeItem() {
        return findPrizeItem(null);
    }

    public Item findPrizeItem(Class<? extends Item> match) {
        if (itemsToSpawn.size() == 0)
            return null;

        if (match == null) {
            Item item = Random.element(itemsToSpawn);
            itemsToSpawn.remove(item);
            return item;
        }

        for (Item item : itemsToSpawn) {
            if (match.isInstance(item)) {
                itemsToSpawn.remove(item);
                return item;
            }
        }

        return null;
    }

    public void buildFlagMaps() {

        for (int i = 0; i < length(); i++) {
            int flags = Terrain.flags[map[i]];
            passable[i] = (flags & Terrain.PASSABLE) != 0;
            losBlocking[i] = (flags & Terrain.LOS_BLOCKING) != 0;
            flamable[i] = (flags & Terrain.FLAMABLE) != 0;
            secret[i] = (flags & Terrain.SECRET) != 0;
            solid[i] = (flags & Terrain.SOLID) != 0;
            avoid[i] = (flags & Terrain.AVOID) != 0;
            water[i] = (flags & Terrain.LIQUID) != 0;
            pit[i] = (flags & Terrain.PIT) != 0;
        }

        SmokeScreen s = (SmokeScreen) blobs.get(SmokeScreen.class);
        if (s != null && s.volume > 0) {
            for (int i = 0; i < length(); i++) {
                losBlocking[i] = losBlocking[i] || s.cur[i] > 0;
            }
        }

        int lastRow = length() - width();
        for (int i = 0; i < width(); i++) {
            passable[i] = avoid[i] = false;
            losBlocking[i] = true;
            passable[lastRow + i] = avoid[lastRow + i] = false;
            losBlocking[lastRow + i] = true;
        }
        for (int i = width(); i < lastRow; i += width()) {
            passable[i] = avoid[i] = false;
            losBlocking[i] = true;
            passable[i + width() - 1] = avoid[i + width() - 1] = false;
            losBlocking[i + width() - 1] = true;
        }
    }

    public void destroy(int pos) {
        set(pos, Terrain.EMBERS);
    }

    protected void cleanWalls() {
        discoverable = new boolean[length()];

        for (int i = 0; i < length(); i++) {

            boolean d = false;

            for (int j = 0; j < PathFinder.NEIGHBOURS9.length; j++) {
                int n = i + PathFinder.NEIGHBOURS9[j];
                if (n >= 0 && n < length() && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
                    d = true;
                    break;
                }
            }

            discoverable[i] = d;
        }
    }

    public static void set(int cell, int terrain) {
        set(cell, terrain, Dungeon.level);
    }

    public static void set(int cell, int terrain, Level level) {
        Painter.set(level, cell, terrain);

        if (terrain != Terrain.TRAP && terrain != Terrain.SECRET_TRAP && terrain != Terrain.INACTIVE_TRAP) {
            level.traps.remove(cell);
        }

        int flags = Terrain.flags[terrain];
        level.passable[cell] = (flags & Terrain.PASSABLE) != 0;
        level.losBlocking[cell] = (flags & Terrain.LOS_BLOCKING) != 0;
        level.flamable[cell] = (flags & Terrain.FLAMABLE) != 0;
        level.secret[cell] = (flags & Terrain.SECRET) != 0;
        level.solid[cell] = (flags & Terrain.SOLID) != 0;
        level.avoid[cell] = (flags & Terrain.AVOID) != 0;
        level.pit[cell] = (flags & Terrain.PIT) != 0;
        level.water[cell] = terrain == Terrain.WATER;

        SmokeScreen s = (SmokeScreen) level.blobs.get(SmokeScreen.class);
        if (s != null && s.volume > 0) {
            level.losBlocking[cell] = level.losBlocking[cell] || s.cur[cell] > 0;
        }
    }

    public Heap drop(Item item, int cell) {

        if (item == null || Challenges.isItemBlocked(item)) {

            //create a dummy heap, give it a dummy sprite, don't add it to the game, and return it.
            //effectively nullifies whatever the logic calling this wants to do, including dropping items.
            Heap heap = new Heap();
            ItemSprite sprite = heap.sprite = new ItemSprite();
            sprite.link(heap);
            return heap;

        }

        Heap heap = heaps.get(cell);
        if (heap == null) {

            heap = new Heap();
            heap.seen = Dungeon.level == this && heroFOV[cell];
            heap.pos = cell;
            heap.drop(item);
            if (map[cell] == Terrain.CHASM || (Dungeon.level != null && pit[cell])) {
                Dungeon.dropToChasm(item);
                GameScene.discard(heap);
            } else {
                heaps.put(cell, heap);
                GameScene.add(heap);
            }

        } else if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) {

            int n;
            do {
                n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (!passable[n] && !avoid[n]);
            return drop(item, n);

        } else {
            heap.drop(item);
        }

        if (Dungeon.level != null) {
            pressCell(cell);
        }

        return heap;
    }

    public Plant plant(Plant.Seed seed, int pos) {

        if (Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
            return null;
        }

        Plant plant = plants.get(pos);
        if (plant != null) {
            plant.wither();
        }

        if (map[pos] == Terrain.HIGH_GRASS ||
                map[pos] == Terrain.FURROWED_GRASS ||
                map[pos] == Terrain.EMPTY ||
                map[pos] == Terrain.EMBERS ||
                map[pos] == Terrain.EMPTY_DECO) {
            set(pos, Terrain.GRASS, this);
            GameScene.updateMap(pos);
        }

        plant = seed.couch(pos, this);
        plants.put(pos, plant);

        GameScene.plantSeed(pos);

        return plant;
    }

    public void uproot(int pos) {
        plants.remove(pos);
        GameScene.updateMap(pos);
    }

    public boolean bossisalive() {
        for (Mob m : mobs) {
            if (m.properties().contains(Char.Property.BOSS)) {
                return true;
            }
        }
        return false;
    }

    public Trap setTrap(Trap trap, int pos) {
        Trap existingTrap = traps.get(pos);
        if (existingTrap != null) {
            traps.remove(pos);
        }
        trap.set(pos);
        traps.put(pos, trap);
        GameScene.updateMap(pos);
        return trap;
    }

    public void disarmTrap(int pos) {
        set(pos, Terrain.INACTIVE_TRAP);
        GameScene.updateMap(pos);
    }

    public void discover(int cell) {
        set(cell, Terrain.discover(map[cell]));
        Trap trap = traps.get(cell);
        if (trap != null)
            trap.reveal();
        GameScene.updateMap(cell);
    }

    public int fallCell(boolean fallIntoPit) {
        int result;
        do {
            result = randomRespawnCell();
        } while (traps.get(result) != null
                || findMob(result) != null
                || heaps.get(result) != null);
        return result;
    }

    public void occupyCell(Char ch) {
        if (!ch.flying) {

            if (pit[ch.pos]) {
                if (ch == Dungeon.hero) {
                    Chasm.heroFall(ch.pos);
                } else if (ch instanceof Mob) {
                    Chasm.mobFall((Mob) ch);
                }
                return;
            }

            //characters which are not the hero or a sheep 'soft' press cells
            pressCell(ch.pos, ch instanceof Hero || ch instanceof Sheep);
        } else {
            if (map[ch.pos] == Terrain.DOOR) {
                Door.enter(ch.pos);
            }
        }
    }

    //public method for forcing the hard press of a cell. e.g. when an item lands on it
    public void pressCell(int cell) {
        pressCell(cell, true);
    }

    //a 'soft' press ignores hidden traps
    //a 'hard' press triggers all things
    private void pressCell(int cell, boolean hard) {

        Trap trap = null;

        switch (map[cell]) {

            case Terrain.SECRET_TRAP:
                if (hard) {
                    trap = traps.get(cell);
                    GLog.i(Messages.get(Level.class, "hidden_trap", trap.name));
                }
                break;

            case Terrain.TRAP:
                trap = traps.get(cell);
                break;

            case Terrain.HIGH_GRASS:
            case Terrain.FURROWED_GRASS:
                HighGrass.trample(this, cell);
                break;

            case Terrain.WELL:
                WellWater.affectCell(cell);
                break;

            case Terrain.DOOR:
                Door.enter(cell);
                break;
        }

        if (trap != null) {

            TimekeepersHourglass.timeFreeze timeFreeze =
                    Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);

            Swiftthistle.TimeBubble bubble =
                    Dungeon.hero.buff(Swiftthistle.TimeBubble.class);

            if (bubble != null) {

                Sample.INSTANCE.play(Assets.SND_TRAP);

                discover(cell);

                bubble.setDelayedPress(cell);

            } else if (timeFreeze != null) {

                Sample.INSTANCE.play(Assets.SND_TRAP);

                discover(cell);

                timeFreeze.setDelayedPress(cell);

            } else {

                if (Dungeon.hero.pos == cell) {
                    Dungeon.hero.interrupt();
                }

                trap.trigger();

            }
        }

        Plant plant = plants.get(cell);
        if (plant != null) {
            plant.trigger();
        }
    }

    public void updateFieldOfView(Char c, boolean[] fieldOfView) {

        int cx = c.pos % width();
        int cy = c.pos / width();

        boolean sighted = c.buff(Blindness.class) == null && c.buff(Shadows.class) == null
                && c.buff(TimekeepersHourglass.timeStasis.class) == null && c.isAlive();
        if (sighted) {
            boolean[] blocking;

            if (c instanceof Hero && ((Hero) c).subClass == HeroSubClass.WARDEN) {
                blocking = Dungeon.level.losBlocking.clone();
                for (int i = 0; i < blocking.length; i++) {
                    if (blocking[i] && (Dungeon.level.map[i] == Terrain.HIGH_GRASS || Dungeon.level.map[i] == Terrain.FURROWED_GRASS)) {
                        blocking[i] = false;
                    }
                }
            } else {
                blocking = Dungeon.level.losBlocking;
            }

            int viewDist = c.viewDistance;
            if (c instanceof Hero && ((Hero) c).subClass == HeroSubClass.SNIPER) viewDist *= 1.5f;

            ShadowCaster.castShadow(cx, cy, fieldOfView, blocking, viewDist);
        } else {
            BArray.setFalse(fieldOfView);
        }

        int sense = 1;
        //Currently only the hero can get mind vision
        if (c.isAlive() && c == Dungeon.hero) {
            for (Buff b : c.buffs(MindVision.class)) {
                sense = Math.max(((MindVision) b).distance, sense);
            }
            if (c.buff(MagicalSight.class) != null) {
                sense = 8;
            }
            if (((Hero) c).subClass == HeroSubClass.SNIPER) {
                sense *= 1.5f;
            }
        }

        //uses rounding
        if (!sighted || sense > 1) {

            int[][] rounding = ShadowCaster.rounding;

            int left, right;
            int pos;
            for (int y = Math.max(0, cy - sense); y <= Math.min(height() - 1, cy + sense); y++) {
                if (rounding[sense][Math.abs(cy - y)] < Math.abs(cy - y)) {
                    left = cx - rounding[sense][Math.abs(cy - y)];
                } else {
                    left = sense;
                    while (rounding[sense][left] < rounding[sense][Math.abs(cy - y)]) {
                        left--;
                    }
                    left = cx - left;
                }
                right = Math.min(width() - 1, cx + cx - left);
                left = Math.max(0, left);
                pos = left + y * width();
                System.arraycopy(discoverable, pos, fieldOfView, pos, right - left + 1);
            }
        }

        //Currently only the hero can get mind vision or awareness
        if (c.isAlive() && c == Dungeon.hero) {
            Dungeon.hero.mindVisionEnemies.clear();
            if (c.buff(MindVision.class) != null) {
                for (Mob mob : mobs) {
                    int p = mob.pos;

                    if (!fieldOfView[p]) {
                        Dungeon.hero.mindVisionEnemies.add(mob);
                    }

                }
            } else if (((Hero) c).heroClass == HeroClass.HUNTRESS) {
                for (Mob mob : mobs) {
                    int p = mob.pos;
                    if (distance(c.pos, p) == 2) {

                        if (!fieldOfView[p]) {
                            Dungeon.hero.mindVisionEnemies.add(mob);
                        }
                    }
                }
            }

            for (Mob m : Dungeon.hero.mindVisionEnemies) {
                for (int i : PathFinder.NEIGHBOURS9) {
                    fieldOfView[m.pos + i] = true;
                }
            }

            if (c.buff(Awareness.class) != null) {
                for (Heap heap : heaps.valueList()) {
                    int p = heap.pos;
                    for (int i : PathFinder.NEIGHBOURS9)
                        fieldOfView[p + i] = true;
                }
            }

            for (Mob ward : mobs) {
                if (ward instanceof WandOfWarding.Ward) {
                    if (ward.fieldOfView == null || ward.fieldOfView.length != length()) {
                        ward.fieldOfView = new boolean[length()];
                        Dungeon.level.updateFieldOfView(ward, ward.fieldOfView);
                    }
                    for (Mob m : mobs) {
                        if (ward.fieldOfView[m.pos] && !fieldOfView[m.pos] &&
                                !Dungeon.hero.mindVisionEnemies.contains(m)) {
                            Dungeon.hero.mindVisionEnemies.add(m);
                        }
                    }
                    BArray.or(fieldOfView, ward.fieldOfView, fieldOfView);
                }
            }
        }

        if (c == Dungeon.hero) {
            for (Heap heap : heaps.valueList())
                if (!heap.seen && fieldOfView[heap.pos])
                    heap.seen = true;
        }

    }

    public int distance(int a, int b) {
        int ax = a % width();
        int ay = a / width();
        int bx = b % width();
        int by = b / width();
        return Math.max(Math.abs(ax - bx), Math.abs(ay - by));
    }

    public boolean adjacent(int a, int b) {
        return distance(a, b) == 1;
    }

    //uses pythagorean theorum for true distance, as if there was no movement grid
    public float trueDistance(int a, int b) {
        int ax = a % width();
        int ay = a / width();
        int bx = b % width();
        int by = b / width();
        return (float) Math.sqrt(Math.pow(Math.abs(ax - bx), 2) + Math.pow(Math.abs(ay - by), 2));
    }

    //returns true if the input is a valid tile within the level
    public boolean insideMap(int tile) {
        //top and bottom row and beyond
        return !((tile < width || tile >= length - width) ||
                //left and right column
                (tile % width == 0 || tile % width == width - 1));
    }

    public Point cellToPoint(int cell) {
        return new Point(cell % width(), cell / width());
    }

    public int pointToCell(Point p) {
        return p.x + p.y * width();
    }

    public String tileName(int tile) {

        switch (tile) {
            case Terrain.CHASM:
                return Messages.get(Level.class, "chasm_name");
            case Terrain.EMPTY:
            case Terrain.EMPTY_SP:
            case Terrain.EMPTY_DECO:
            case Terrain.SECRET_TRAP:
                return Messages.get(Level.class, "floor_name");
            case Terrain.GRASS:
                return Messages.get(Level.class, "grass_name");
            case Terrain.WATER:
                return Messages.get(Level.class, "water_name");
            case Terrain.WALL:
            case Terrain.WALL_DECO:
            case Terrain.SECRET_DOOR:
                return Messages.get(Level.class, "wall_name");
            case Terrain.DOOR:
                return Messages.get(Level.class, "closed_door_name");
            case Terrain.OPEN_DOOR:
                return Messages.get(Level.class, "open_door_name");
            case Terrain.ENTRANCE:
                return Messages.get(Level.class, "entrace_name");
            case Terrain.EXIT:
                return Messages.get(Level.class, "exit_name");
            case Terrain.EMBERS:
                return Messages.get(Level.class, "embers_name");
            case Terrain.FURROWED_GRASS:
                return Messages.get(Level.class, "furrowed_grass_name");
            case Terrain.LOCKED_DOOR:
                return Messages.get(Level.class, "locked_door_name");
            case Terrain.PEDESTAL:
                return Messages.get(Level.class, "pedestal_name");
            case Terrain.BARRICADE:
                return Messages.get(Level.class, "barricade_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(Level.class, "high_grass_name");
            case Terrain.LOCKED_EXIT:
                return Messages.get(Level.class, "locked_exit_name");
            case Terrain.UNLOCKED_EXIT:
                return Messages.get(Level.class, "unlocked_exit_name");
            case Terrain.SIGN:
                return Messages.get(Level.class, "sign_name");
            case Terrain.WELL:
                return Messages.get(Level.class, "well_name");
            case Terrain.EMPTY_WELL:
                return Messages.get(Level.class, "empty_well_name");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(Level.class, "statue_name");
            case Terrain.INACTIVE_TRAP:
                return Messages.get(Level.class, "inactive_trap_name");
            case Terrain.BOOKSHELF:
                return Messages.get(Level.class, "bookshelf_name");
            case Terrain.ALCHEMY:
                return Messages.get(Level.class, "alchemy_name");
            default:
                return Messages.get(Level.class, "default_name");
        }
    }

    public String tileDesc(int tile) {

        switch (tile) {
            case Terrain.CHASM:
                return Messages.get(Level.class, "chasm_desc");
            case Terrain.WATER:
                return Messages.get(Level.class, "water_desc");
            case Terrain.ENTRANCE:
                return Messages.get(Level.class, "entrance_desc");
            case Terrain.EXIT:
            case Terrain.UNLOCKED_EXIT:
                return Messages.get(Level.class, "exit_desc");
            case Terrain.EMBERS:
                return Messages.get(Level.class, "embers_desc");
            case Terrain.HIGH_GRASS:
            case Terrain.FURROWED_GRASS:
                return Messages.get(Level.class, "high_grass_desc");
            case Terrain.LOCKED_DOOR:
                return Messages.get(Level.class, "locked_door_desc");
            case Terrain.LOCKED_EXIT:
                return Messages.get(Level.class, "locked_exit_desc");
            case Terrain.BARRICADE:
                return Messages.get(Level.class, "barricade_desc");
            case Terrain.SIGN:
                return Messages.get(Level.class, "sign_desc");
            case Terrain.INACTIVE_TRAP:
                return Messages.get(Level.class, "inactive_trap_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(Level.class, "statue_desc");
            case Terrain.ALCHEMY:
                return Messages.get(Level.class, "alchemy_desc");
            case Terrain.EMPTY_WELL:
                return Messages.get(Level.class, "empty_well_desc");
            default:
                return "";
        }
    }
}
