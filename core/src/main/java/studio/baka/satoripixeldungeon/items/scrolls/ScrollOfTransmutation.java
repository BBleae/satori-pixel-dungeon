package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.EquipableItem;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.artifacts.Artifact;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.potions.brews.Brew;
import studio.baka.satoripixeldungeon.items.potions.elixirs.Elixir;
import studio.baka.satoripixeldungeon.items.potions.exotic.ExoticPotion;
import studio.baka.satoripixeldungeon.items.rings.Ring;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ExoticScroll;
import studio.baka.satoripixeldungeon.items.stones.Runestone;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MeleeWeapon;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.items.weapon.missiles.ThrowingKnife;
import studio.baka.satoripixeldungeon.items.weapon.missiles.darts.Dart;
import studio.baka.satoripixeldungeon.journal.Catalog;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class ScrollOfTransmutation extends InventoryScroll {

    {
        initials = 10;
        mode = WndBag.Mode.TRANMSUTABLE;

        bones = true;
    }

    public static boolean canTransmute(Item item) {
        return item instanceof MeleeWeapon ||
                item instanceof MissileWeapon && !(item instanceof Dart) && !(item instanceof ThrowingKnife) ||
                item instanceof Potion && !(item instanceof Elixir ||
                        item instanceof Brew ||
                        item instanceof AlchemicalCatalyst) ||
                item instanceof Scroll ||
                item instanceof Ring ||
                item instanceof Wand ||
                item instanceof Plant.Seed ||
                item instanceof Runestone ||
                item instanceof Artifact;
    }

    @Override
    protected void onItemSelected(Item item) {

        Item result;

        if (item instanceof MagesStaff) {
            result = changeStaff((MagesStaff) item);
        } else if (item instanceof MeleeWeapon || item instanceof MissileWeapon) {
            result = changeWeapon((Weapon) item);
        } else if (item instanceof Scroll) {
            result = changeScroll((Scroll) item);
        } else if (item instanceof Potion) {
            result = changePotion((Potion) item);
        } else if (item instanceof Ring) {
            result = changeRing((Ring) item);
        } else if (item instanceof Wand) {
            result = changeWand((Wand) item);
        } else if (item instanceof Plant.Seed) {
            result = changeSeed((Plant.Seed) item);
        } else if (item instanceof Runestone) {
            result = changeStone((Runestone) item);
        } else if (item instanceof Artifact) {
            result = changeArtifact((Artifact) item);
        } else {
            result = null;
        }

        if (result == null) {
            //This shouldn't ever trigger
            GLog.n(Messages.get(this, "nothing"));
            curItem.collect(curUser.belongings.backpack);
        } else {
            if (item.isEquipped(Dungeon.hero)) {
                item.cursed = false; //to allow it to be unequipped
                ((EquipableItem) item).doUnequip(Dungeon.hero, false);
                ((EquipableItem) result).doEquip(Dungeon.hero);
            } else {
                item.detach(Dungeon.hero.belongings.backpack);
                if (!result.collect()) {
                    Dungeon.level.drop(result, curUser.pos).sprite.drop();
                }
            }
            if (result.isIdentified()) {
                Catalog.setSeen(result.getClass());
            }
            //TODO visuals
            GLog.p(Messages.get(this, "morph"));
        }

    }

    private MagesStaff changeStaff(MagesStaff staff) {
        Class<? extends Wand> wandClass = staff.wandClass();

        if (wandClass == null) {
            return null;
        } else {
            Wand n;
            do {
                n = (Wand) Generator.random(Generator.Category.WAND);
            } while (Challenges.isItemBlocked(n) || n.getClass() == wandClass);
            n.level(0);
            n.identify();
            staff.imbueWand(n, null);
        }

        return staff;
    }

    private Weapon changeWeapon(Weapon w) {

        Weapon n;
        Generator.Category c;
        if (w instanceof MeleeWeapon) {
            c = Generator.wepTiers[((MeleeWeapon) w).tier - 1];
        } else {
            c = Generator.misTiers[((MissileWeapon) w).tier - 1];
        }

        do {
            n = (Weapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
        } while (Challenges.isItemBlocked(n) || n.getClass() == w.getClass());

        int level = w.level();
        if (w.curseInfusionBonus) level--;
        if (level > 0) {
            n.upgrade(level);
        } else if (level < 0) {
            n.degrade(-level);
        }

        n.enchantment = w.enchantment;
        n.curseInfusionBonus = w.curseInfusionBonus;
        n.levelKnown = w.levelKnown;
        n.cursedKnown = w.cursedKnown;
        n.cursed = w.cursed;
        n.augment = w.augment;

        return n;

    }

    private Ring changeRing(Ring r) {
        Ring n;
        do {
            n = (Ring) Generator.random(Generator.Category.RING);
        } while (Challenges.isItemBlocked(n) || n.getClass() == r.getClass());

        n.level(0);

        int level = r.level();
        if (level > 0) {
            n.upgrade(level);
        } else if (level < 0) {
            n.degrade(-level);
        }

        n.levelKnown = r.levelKnown;
        n.cursedKnown = r.cursedKnown;
        n.cursed = r.cursed;

        return n;
    }

    private Artifact changeArtifact(Artifact a) {
        Artifact n = Generator.randomArtifact();

        if (n != null && !Challenges.isItemBlocked(n)) {
            n.cursedKnown = a.cursedKnown;
            n.cursed = a.cursed;
            n.levelKnown = a.levelKnown;
            n.transferUpgrade(a.visiblyUpgraded());
            return n;
        }

        return null;
    }

    private Wand changeWand(Wand w) {

        Wand n;
        do {
            n = (Wand) Generator.random(Generator.Category.WAND);
        } while (Challenges.isItemBlocked(n) || n.getClass() == w.getClass());

        n.level(0);
        int level = w.level();
        if (w.curseInfusionBonus) level--;
        n.upgrade(level);

        n.levelKnown = w.levelKnown;
        n.cursedKnown = w.cursedKnown;
        n.cursed = w.cursed;
        n.curseInfusionBonus = w.curseInfusionBonus;

        return n;
    }

    private Plant.Seed changeSeed(Plant.Seed s) {

        Plant.Seed n;

        do {
            n = (Plant.Seed) Generator.random(Generator.Category.SEED);
        } while (n.getClass() == s.getClass());

        return n;
    }

    private Runestone changeStone(Runestone r) {

        Runestone n;

        do {
            n = (Runestone) Generator.random(Generator.Category.STONE);
        } while (n.getClass() == r.getClass());

        return n;
    }

    private Scroll changeScroll(Scroll s) {
        if (s instanceof ExoticScroll) {
            return Reflection.newInstance(ExoticScroll.exoToReg.get(s.getClass()));
        } else {
            return Reflection.newInstance(ExoticScroll.regToExo.get(s.getClass()));
        }
    }

    private Potion changePotion(Potion p) {
        if (p instanceof ExoticPotion) {
            return Reflection.newInstance(ExoticPotion.exoToReg.get(p.getClass()));
        } else {
            return Reflection.newInstance(ExoticPotion.regToExo.get(p.getClass()));
        }
    }

    @Override
    public void empoweredRead() {
        //does nothing, this shouldn't happen
    }

    @Override
    public int price() {
        return isKnown() ? 50 * quantity : super.price();
    }
}
