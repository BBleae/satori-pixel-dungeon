package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Belongings;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Shopkeeper;
import studio.baka.satoripixeldungeon.items.*;
import studio.baka.satoripixeldungeon.items.armor.LeatherArmor;
import studio.baka.satoripixeldungeon.items.armor.MailArmor;
import studio.baka.satoripixeldungeon.items.armor.PlateArmor;
import studio.baka.satoripixeldungeon.items.armor.ScaleArmor;
import studio.baka.satoripixeldungeon.items.artifacts.TimekeepersHourglass;
import studio.baka.satoripixeldungeon.items.bags.*;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.items.food.SmallRation;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfIdentify;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfMagicMapping;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import studio.baka.satoripixeldungeon.items.stones.Runestone;
import studio.baka.satoripixeldungeon.items.stones.StoneOfAugmentation;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.melee.*;
import studio.baka.satoripixeldungeon.items.weapon.missiles.*;
import studio.baka.satoripixeldungeon.items.weapon.missiles.darts.TippedDart;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.plants.Plant;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShopRoom extends SpecialRoom {

    private ArrayList<Item> itemsToSpawn;

    @Override
    public int minWidth() {
        if (itemsToSpawn == null) itemsToSpawn = generateItems();
        return Math.max(7, (int) (Math.sqrt(itemsToSpawn.size()) + 3));
    }

    @Override
    public int minHeight() {
        if (itemsToSpawn == null) itemsToSpawn = generateItems();
        return Math.max(7, (int) (Math.sqrt(itemsToSpawn.size()) + 3));
    }

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY_SP);

        placeShopkeeper(level);

        placeItems(level);

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }

    }

    protected void placeShopkeeper(Level level) {

        int pos = level.pointToCell(center());

        Mob shopkeeper = new Shopkeeper();
        shopkeeper.pos = pos;
        level.mobs.add(shopkeeper);

    }

    protected void placeItems(Level level) {

        if (itemsToSpawn == null)
            itemsToSpawn = generateItems();

        Point itemPlacement = new Point(entrance());
        if (itemPlacement.y == top) {
            itemPlacement.y++;
        } else if (itemPlacement.y == bottom) {
            itemPlacement.y--;
        } else if (itemPlacement.x == left) {
            itemPlacement.x++;
        } else {
            itemPlacement.x--;
        }

        for (Item item : itemsToSpawn) {

            if (itemPlacement.x == left + 1 && itemPlacement.y != top + 1) {
                itemPlacement.y--;
            } else if (itemPlacement.y == top + 1 && itemPlacement.x != right - 1) {
                itemPlacement.x++;
            } else if (itemPlacement.x == right - 1 && itemPlacement.y != bottom - 1) {
                itemPlacement.y++;
            } else {
                itemPlacement.x--;
            }

            int cell = level.pointToCell(itemPlacement);

            if (level.heaps.get(cell) != null) {
                do {
                    cell = level.pointToCell(random());
                } while (level.heaps.get(cell) != null || level.findMob(cell) != null);
            }

            level.drop(item, cell).type = Heap.Type.FOR_SALE;
        }

    }

    protected static ArrayList<Item> generateItems() {

        ArrayList<Item> itemsToSpawn = new ArrayList<>();

        switch (Dungeon.depth) {
            case 6:
                itemsToSpawn.add((Random.Int(2) == 0 ? new Shortsword().identify() : new HandAxe()).identify());
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new FishingSpear().quantity(2) :
                        new Shuriken().quantity(2));
                itemsToSpawn.add(new LeatherArmor().identify());
                break;

            case 11:
                itemsToSpawn.add((Random.Int(2) == 0 ? new Sword().identify() : new Mace()).identify());
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new ThrowingSpear().quantity(2) :
                        new Bolas().quantity(2));
                itemsToSpawn.add(new MailArmor().identify());
                break;

            case 16:
                itemsToSpawn.add((Random.Int(2) == 0 ? new Longsword().identify() : new BattleAxe()).identify());
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new Javelin().quantity(2) :
                        new Tomahawk().quantity(2));
                itemsToSpawn.add(new ScaleArmor().identify());
                break;

            case 21:
                itemsToSpawn.add(Random.Int(2) == 0 ? new Greatsword().identify() : new WarHammer().identify());
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new Trident().quantity(2) :
                        new ThrowingHammer().quantity(2));
                itemsToSpawn.add(new PlateArmor().identify());
                itemsToSpawn.add(new Torch());
                itemsToSpawn.add(new Torch());
                itemsToSpawn.add(new Torch());
                break;
        }

        itemsToSpawn.add(TippedDart.randomTipped(2));

        itemsToSpawn.add(new MerchantsBeacon());


        itemsToSpawn.add(ChooseBag(Dungeon.hero.belongings));


        itemsToSpawn.add(new PotionOfHealing());
        for (int i = 0; i < 3; i++)
            itemsToSpawn.add(Generator.random(Generator.Category.POTION));

        itemsToSpawn.add(new ScrollOfIdentify());
        itemsToSpawn.add(new ScrollOfRemoveCurse());
        itemsToSpawn.add(new ScrollOfMagicMapping());
        itemsToSpawn.add(Generator.random(Generator.Category.SCROLL));

        for (int i = 0; i < 2; i++)
            itemsToSpawn.add(Random.Int(2) == 0 ?
                    Generator.random(Generator.Category.POTION) :
                    Generator.random(Generator.Category.SCROLL));


        itemsToSpawn.add(new SmallRation());
        itemsToSpawn.add(new SmallRation());

        switch (Random.Int(4)) {
            case 0:
                itemsToSpawn.add(new Bomb());
                break;
            case 1:
            case 2:
                itemsToSpawn.add(new Bomb.DoubleBomb());
                break;
            case 3:
                itemsToSpawn.add(new Honeypot());
                break;
        }

        itemsToSpawn.add(new Ankh());
        itemsToSpawn.add(new StoneOfAugmentation());

        TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
        if (hourglass != null) {
            int bags = 0;
            //creates the given float percent of the remaining bags to be dropped.
            //this way players who get the hourglass late can still max it, usually.
            switch (Dungeon.depth) {
                case 6:
                    bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.20f);
                    break;
                case 11:
                    bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.25f);
                    break;
                case 16:
                    bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.50f);
                    break;
                case 21:
                    bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.80f);
                    break;
            }

            for (int i = 1; i <= bags; i++) {
                itemsToSpawn.add(new TimekeepersHourglass.sandBag());
                hourglass.sandBags++;
            }
        }

        Item rare;
        switch (Random.Int(10)) {
            case 0:
                rare = Generator.random(Generator.Category.WAND);
                rare.level(0);
                break;
            case 1:
                rare = Generator.random(Generator.Category.RING);
                rare.level(0);
                break;
            case 2:
                rare = Generator.random(Generator.Category.ARTIFACT);
                break;
            default:
                rare = new Stylus();
        }
        rare.cursed = false;
        rare.cursedKnown = true;
        itemsToSpawn.add(rare);

        //hard limit is 63 items + 1 shopkeeper, as shops can't be bigger than 8x8=64 internally
        if (itemsToSpawn.size() > 63)
            throw new RuntimeException("Shop attempted to carry more than 63 items!");

        Random.shuffle(itemsToSpawn);
        return itemsToSpawn;
    }

    protected static Bag ChooseBag(Belongings pack) {

        //0=pouch, 1=holder, 2=bandolier, 3=holster
        int[] bagItems = new int[4];

        //count up items in the main bag
        for (Item item : pack.backpack.items) {
            if (item instanceof Plant.Seed || item instanceof Runestone) bagItems[0]++;
            if (item instanceof Scroll) bagItems[1]++;
            if (item instanceof Potion) bagItems[2]++;
            if (item instanceof Wand || item instanceof MissileWeapon) bagItems[3]++;
        }

        //disqualify bags that have already been dropped
        if (Dungeon.LimitedDrops.VELVET_POUCH.dropped()) bagItems[0] = -1;
        if (Dungeon.LimitedDrops.SCROLL_HOLDER.dropped()) bagItems[1] = -1;
        if (Dungeon.LimitedDrops.POTION_BANDOLIER.dropped()) bagItems[2] = -1;
        if (Dungeon.LimitedDrops.MAGICAL_HOLSTER.dropped()) bagItems[3] = -1;

        //find the best bag to drop. This does give a preference to later bags, if counts are equal
        int bestBagIdx = 0;
        for (int i = 1; i <= 3; i++) {
            if (bagItems[bestBagIdx] <= bagItems[i]) {
                bestBagIdx = i;
            }
        }

        //drop it, or return nothing if no bag works
        if (bagItems[bestBagIdx] == -1) return null;
        switch (bestBagIdx) {
            case 0:
            default:
                Dungeon.LimitedDrops.VELVET_POUCH.drop();
                return new VelvetPouch();
            case 1:
                Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
                return new ScrollHolder();
            case 2:
                Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
                return new PotionBandolier();
            case 3:
                Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
                return new MagicalHolster();
        }

    }

}
