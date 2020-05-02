package studio.baka.satoripixeldungeon.actors.hero;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.*;
import studio.baka.satoripixeldungeon.items.armor.ClothArmor;
import studio.baka.satoripixeldungeon.items.artifacts.ChaliceOfBlood;
import studio.baka.satoripixeldungeon.items.artifacts.CloakOfShadows;
import studio.baka.satoripixeldungeon.items.bags.PotionBandolier;
import studio.baka.satoripixeldungeon.items.bags.ScrollHolder;
import studio.baka.satoripixeldungeon.items.bags.VelvetPouch;
import studio.baka.satoripixeldungeon.items.food.Food;
import studio.baka.satoripixeldungeon.items.food.SmallRation;
import studio.baka.satoripixeldungeon.items.potions.*;
import studio.baka.satoripixeldungeon.items.rings.RingOfAccuracy;
import studio.baka.satoripixeldungeon.items.rings.RingOfElements;
import studio.baka.satoripixeldungeon.items.rings.RingOfEnergy;
import studio.baka.satoripixeldungeon.items.scrolls.*;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import studio.baka.satoripixeldungeon.items.wands.WandOfMagicMissile;
import studio.baka.satoripixeldungeon.items.weapon.SpiritBow;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.items.weapon.WornShortsword;
import studio.baka.satoripixeldungeon.items.weapon.melee.*;
import studio.baka.satoripixeldungeon.items.weapon.missiles.Shuriken;
import studio.baka.satoripixeldungeon.items.weapon.missiles.ThrowingKnife;
import studio.baka.satoripixeldungeon.items.weapon.missiles.ThrowingStone;
import studio.baka.satoripixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;

public enum HeroClass {

    WARRIOR("warrior", HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR),           //koishi
    MAGE("mage", HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK),                  //nue
    ROGUE("rogue", HeroSubClass.ASSASSIN, HeroSubClass.SNIPER),               //yukari
    HUNTRESS("huntress", HeroSubClass.FREERUNNER, HeroSubClass.WARDEN),               //youmu
    MAHOU_SHOUJO("mahou_shoujo", HeroSubClass.DESTORYER, HeroSubClass.DEVIL);       //flandre

    private final String title;
    private final HeroSubClass[] subClasses;

    HeroClass(String title, HeroSubClass... subClasses) {
        this.title = title;
        this.subClasses = subClasses;
    }

    public void fakeinit(Hero hero) {
        hero.heroClass = this;
        fakeCommon(hero);
    }

    public void initHero(Hero hero) {

        hero.heroClass = this;

        initCommon(hero);

        switch (this) {
            case WARRIOR:
                initWarrior(hero);
                break;

            case MAGE:
                initMage(hero);
                break;

            case ROGUE:
                initRogue(hero);
                break;

            case HUNTRESS:
                initHuntress(hero);
                break;

            case MAHOU_SHOUJO:
                initMahou_Shoujo(hero);
                break;
        }

    }

    private static void fakeCommon(Hero hero) {
        Item i = new ClothArmor().identify();
        if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor) i;
    }

    private static void initCommon(Hero hero) {

        Item i;
		/*
		i = new ClothArmor().identify();
		if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor)i;
		*/
        i = new Food();
        if (!Challenges.isItemBlocked(i)) i.collect();

        if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
            new SmallRation().collect();
        }

        new ScrollOfIdentify().identify();

        if (DeviceCompat.isDebug()) {
            //for testing
            new Shuriken().quantity(10).collect();
            new ScrollOfUpgrade().quantity(10).collect();
            new PotionOfLiquidFlame().collect();
            new PotionOfHealing().quantity(10).collect();
            new TestItem().collect();
            new PotionOfExperience().quantity(20).collect();
            Ankh a = new Ankh();
            a.blessed = true;
            a.quantity(4).collect();
            new ChaliceOfBlood().collect();
            new RingOfAccuracy().collect();
            new RingOfEnergy().collect();
            new Amulet().collect();
            new RingOfElements().collect();
            new ScrollOfIdentify().collect();
            new ScrollOfEnchantment().quantity(20).collect();
            new ThreeDirectionsword().enchant(Weapon.Enchantment.randomUncommon()).collect();
            new PotionOfStrength().quantity(6).collect();
            new PotionOfInvisibility().quantity(15).collect();
            new AmuletFake().collect();
            //new ScrollOfTransmutation().collect();
            new TomeOfMastery().collect();
        }
    }

    public Badges.Badge masteryBadge() {
        switch (this) {
            case WARRIOR:
                return Badges.Badge.MASTERY_WARRIOR;
            case MAGE:
                return Badges.Badge.MASTERY_MAGE;
            case ROGUE:
                return Badges.Badge.MASTERY_ROGUE;
            case HUNTRESS:
                return Badges.Badge.MASTERY_HUNTRESS;
            case MAHOU_SHOUJO:
                return Badges.Badge.MASTERY_MAHOU_SHOUJO;
        }
        return null;
    }

    private static void initWarrior(Hero hero) {
        (hero.belongings.weapon = new WornShortsword()).identify();
        ThrowingStone stones = new ThrowingStone();
        stones.quantity(4).collect();
        Dungeon.quickslot.setSlot(0, stones);

        //new ChaliceOfBlood().identify().collect();
        //new PotionOfExperience().quantity(3).identify().collect();
        //new TomeOfMastery().collect();
        //Ankh ak;
        //for(int i=0;i<4;i++) {ak = new Ankh();ak.blessed=true;ak.collect();}

        if (hero.belongings.armor != null) {
            hero.belongings.armor.affixSeal(new BrokenSeal());
        }

        new PotionBandolier().collect();
        Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
        //new ScrollOfUpgrade().identify().collect();
        new PotionOfHealing().identify();
        new ScrollOfRage().identify();
    }

    private static void initMage(Hero hero) {
        MagesStaff staff;

        staff = new MagesStaff(new WandOfMagicMissile());

        (hero.belongings.weapon = staff).identify();
        hero.belongings.weapon.upgrade(0);
        hero.belongings.weapon.activate(hero);

        Dungeon.quickslot.setSlot(0, staff);

        new ScrollHolder().collect();
        Dungeon.LimitedDrops.SCROLL_HOLDER.drop();

        new ScrollOfUpgrade().identify();
        new PotionOfLiquidFlame().identify();

        //WandOfHumor woh = new WandOfHumor();
        //woh.level(8);
        //woh.collect();
        //woh.identify();
    }

    private static void initRogue(Hero hero) {
        (hero.belongings.weapon = new Dagger()).identify();

        CloakOfShadows cloak = new CloakOfShadows();
        (hero.belongings.misc1 = cloak).identify();
        hero.belongings.misc1.activate(hero);

        ThrowingKnife knives = new ThrowingKnife();
        knives.quantity(3).collect();

        Dungeon.quickslot.setSlot(0, cloak);
        Dungeon.quickslot.setSlot(1, knives);

        new VelvetPouch().collect();
        Dungeon.LimitedDrops.VELVET_POUCH.drop();

        new ScrollOfMagicMapping().identify();
        new PotionOfInvisibility().identify();
    }

    private static void initHuntress(Hero hero) {

        (hero.belongings.weapon = new Gloves()).identify();
        SpiritBow bow = new SpiritBow();
        bow.identify().collect();

        Dungeon.quickslot.setSlot(0, bow);

        new VelvetPouch().collect();
        Dungeon.LimitedDrops.VELVET_POUCH.drop();

        new PotionOfMindVision().identify();
        new ScrollOfLullaby().identify();
    }

    private static void initMahou_Shoujo(Hero hero) {
        MahoStaff staff;
        staff = new MahoStaff();
        (hero.belongings.weapon = staff).identify();
        hero.belongings.weapon.upgrade(0);
        hero.belongings.weapon.activate(hero);
        //new WandOfLightning().identify().collect();

        Dungeon.quickslot.setSlot(0, staff);

        new ScrollHolder().collect();
        Dungeon.LimitedDrops.SCROLL_HOLDER.drop();

        new ScrollOfTerror().identify();
        new PotionOfHealing().identify();
        //new WandOfMagicMissile().upgrade(3).identify().collect();
        //new TomeOfMastery().collect();
        new ReagentOfPellouxite().quantity(25).collect();
    }

    public String title() {
        return Messages.get(HeroClass.class, title);
    }

    public HeroSubClass[] subClasses() {
        return subClasses;
    }

    public String spritesheet() {
        switch (this) {
            case WARRIOR:
            default:
                return Assets.WARRIOR;
            case MAGE:
                return Assets.MAGE;
            case ROGUE:
                return Assets.ROGUE;
            case HUNTRESS:
                return Assets.HUNTRESS;
            case MAHOU_SHOUJO:
                return Assets.MAHOU_SHOUJO;
        }
    }

    public String[] perks() {
        switch (this) {
            case WARRIOR:
            default:
                return new String[]{
                        Messages.get(HeroClass.class, "warrior_perk1"),
                        Messages.get(HeroClass.class, "warrior_perk2"),
                        Messages.get(HeroClass.class, "warrior_perk3"),
                        Messages.get(HeroClass.class, "warrior_perk4"),
                        Messages.get(HeroClass.class, "warrior_perk5"),
                };
            case MAGE:
                return new String[]{
                        Messages.get(HeroClass.class, "mage_perk1"),
                        Messages.get(HeroClass.class, "mage_perk2"),
                        Messages.get(HeroClass.class, "mage_perk3"),
                        Messages.get(HeroClass.class, "mage_perk4"),
                        Messages.get(HeroClass.class, "mage_perk5"),
                };
            case ROGUE:
                return new String[]{
                        Messages.get(HeroClass.class, "rogue_perk1"),
                        Messages.get(HeroClass.class, "rogue_perk2"),
                        Messages.get(HeroClass.class, "rogue_perk3"),
                        Messages.get(HeroClass.class, "rogue_perk4"),
                        Messages.get(HeroClass.class, "rogue_perk5"),
                };
            case HUNTRESS:
                return new String[]{
                        Messages.get(HeroClass.class, "huntress_perk1"),
                        Messages.get(HeroClass.class, "huntress_perk2"),
                        Messages.get(HeroClass.class, "huntress_perk3"),
                        Messages.get(HeroClass.class, "huntress_perk4"),
                        Messages.get(HeroClass.class, "huntress_perk5"),
                };
            case MAHOU_SHOUJO:
                return new String[]{
                        Messages.get(HeroClass.class, "mahou_shoujo_perk1"),
                        Messages.get(HeroClass.class, "mahou_shoujo_perk2"),
                        Messages.get(HeroClass.class, "mahou_shoujo_perk3"),
                        Messages.get(HeroClass.class, "mahou_shoujo_perk4"),
                        Messages.get(HeroClass.class, "mahou_shoujo_perk5"),
                };
        }
    }

    public boolean isUnlocked() {
        //always unlock on debug builds
        if (DeviceCompat.isDebug()) return true;

        switch (this) {
            case WARRIOR:
            default:
                return true;
            case MAGE:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
            case ROGUE:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
            case HUNTRESS:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
            case MAHOU_SHOUJO:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_MAHOU_SHOUJO);
        }
    }

    public String unlockMsg() {
        switch (this) {
            case WARRIOR:
            default:
                return "";
            case MAGE:
                return Messages.get(HeroClass.class, "mage_unlock");
            case ROGUE:
                return Messages.get(HeroClass.class, "rogue_unlock");
            case HUNTRESS:
                return Messages.get(HeroClass.class, "huntress_unlock");
            case MAHOU_SHOUJO:
                return Messages.get(HeroClass.class, "mahou_shoujo_unlock");
        }
    }

    private static final String CLASS = "class";

    public void storeInBundle(Bundle bundle) {
        bundle.put(CLASS, toString());
    }

    public static HeroClass restoreInBundle(Bundle bundle) {
        String value = bundle.getString(CLASS);
        return value.length() > 0 ? valueOf(value) : ROGUE;
    }
}
