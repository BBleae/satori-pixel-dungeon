package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Generator.Category;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.artifacts.Artifact;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.potions.PotionOfStrength;
import studio.baka.satoripixeldungeon.items.rings.Ring;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfUpgrade;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MeleeWeapon;
import studio.baka.satoripixeldungeon.journal.Catalog;
import studio.baka.satoripixeldungeon.journal.Notes.Landmark;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class WaterOfTransmutation extends WellWater {

    @Override
    protected Item affectItem(Item item, int pos) {

        if (item instanceof MagesStaff) {
            item = changeStaff((MagesStaff) item);
        } else if (item instanceof MeleeWeapon) {
            item = changeWeapon((MeleeWeapon) item);
        } else if (item instanceof Scroll) {
            item = changeScroll((Scroll) item);
        } else if (item instanceof Potion) {
            item = changePotion((Potion) item);
        } else if (item instanceof Ring) {
            item = changeRing((Ring) item);
        } else if (item instanceof Wand) {
            item = changeWand((Wand) item);
        } else if (item instanceof Plant.Seed) {
            item = changeSeed((Plant.Seed) item);
        } else if (item instanceof Artifact) {
            item = changeArtifact((Artifact) item);
        } else {
            item = null;
        }

        //incase a never-seen item pops out
        if (item != null && item.isIdentified()) {
            Catalog.setSeen(item.getClass());
        }

        return item;

    }

    @Override
    protected boolean affectHero(Hero hero) {
        return false;
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(Speck.factory(Speck.CHANGE), 0.2f, 0);
    }

    @Override
    protected Landmark record() {
        return Landmark.WELL_OF_TRANSMUTATION;
    }

    private MagesStaff changeStaff(MagesStaff staff) {
        Class<? extends Wand> wandClass = staff.wandClass();

        if (wandClass == null) {
            return null;
        } else {
            Wand n;
            do {
                n = (Wand) Generator.random(Category.WAND);
            } while (Challenges.isItemBlocked(n) || n.getClass() == wandClass);
            n.level(0);
            n.identify();
            staff.imbueWand(n, null);
        }

        return staff;
    }

    private Weapon changeWeapon(MeleeWeapon w) {

        Weapon n;
        Category c = Generator.wepTiers[w.tier - 1];

        do {
            n = (MeleeWeapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
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
            n = (Ring) Generator.random(Category.RING);
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
            n = (Wand) Generator.random(Category.WAND);
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
            n = (Plant.Seed) Generator.random(Category.SEED);
        } while (n.getClass() == s.getClass());

        return n;
    }

    private Scroll changeScroll(Scroll s) {
        if (s instanceof ScrollOfUpgrade) {

            return null;

        } else {

            Scroll n;
            do {
                n = (Scroll) Generator.random(Category.SCROLL);
            } while (n.getClass() == s.getClass());
            return n;
        }
    }

    private Potion changePotion(Potion p) {
        if (p instanceof PotionOfStrength) {

            return null;

        } else {

            Potion n;
            do {
                n = (Potion) Generator.random(Category.POTION);
            } while (n.getClass() == p.getClass());
            return n;
        }
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
