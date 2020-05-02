package studio.baka.satoripixeldungeon.journal;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.*;
import studio.baka.satoripixeldungeon.items.artifacts.*;
import studio.baka.satoripixeldungeon.items.potions.*;
import studio.baka.satoripixeldungeon.items.rings.*;
import studio.baka.satoripixeldungeon.items.scrolls.*;
import studio.baka.satoripixeldungeon.items.wands.*;
import studio.baka.satoripixeldungeon.items.weapon.WornShortsword;
import studio.baka.satoripixeldungeon.items.weapon.melee.*;
import com.watabou.utils.Bundle;

import java.util.*;

public enum Catalog {

    WEAPONS,
    ARMOR,
    WANDS,
    RINGS,
    ARTIFACTS,
    POTIONS,
    SCROLLS;

    private final LinkedHashMap<Class<? extends Item>, Boolean> seen = new LinkedHashMap<>();

    public Collection<Class<? extends Item>> items() {
        return seen.keySet();
    }

    public boolean allSeen() {
        for (Class<? extends Item> item : items()) {
            if (!seen.get(item)) {
                return false;
            }
        }
        return true;
    }

    static {
        WEAPONS.seen.put(WornShortsword.class, false);
        WEAPONS.seen.put(Gloves.class, false);
        WEAPONS.seen.put(Dagger.class, false);
        WEAPONS.seen.put(MagesStaff.class, false);
        //WEAPONS.seen.put( Boomerang.class,                  false);
        WEAPONS.seen.put(Shortsword.class, false);
        WEAPONS.seen.put(HandAxe.class, false);
        WEAPONS.seen.put(Spear.class, false);
        WEAPONS.seen.put(Quarterstaff.class, false);
        WEAPONS.seen.put(Dirk.class, false);
        WEAPONS.seen.put(Sword.class, false);
        WEAPONS.seen.put(Mace.class, false);
        WEAPONS.seen.put(Scimitar.class, false);
        WEAPONS.seen.put(RoundShield.class, false);
        WEAPONS.seen.put(Sai.class, false);
        WEAPONS.seen.put(Whip.class, false);
        WEAPONS.seen.put(Longsword.class, false);
        WEAPONS.seen.put(BattleAxe.class, false);
        WEAPONS.seen.put(Flail.class, false);
        WEAPONS.seen.put(RunicBlade.class, false);
        WEAPONS.seen.put(AssassinsBlade.class, false);
        WEAPONS.seen.put(Crossbow.class, false);
        WEAPONS.seen.put(Greatsword.class, false);
        WEAPONS.seen.put(WarHammer.class, false);
        WEAPONS.seen.put(Glaive.class, false);
        WEAPONS.seen.put(Greataxe.class, false);
        WEAPONS.seen.put(Greatshield.class, false);
        WEAPONS.seen.put(Gauntlet.class, false);

        ARMOR.seen.put(ClothArmor.class, false);
        ARMOR.seen.put(LeatherArmor.class, false);
        ARMOR.seen.put(MailArmor.class, false);
        ARMOR.seen.put(ScaleArmor.class, false);
        ARMOR.seen.put(PlateArmor.class, false);
        ARMOR.seen.put(WarriorArmor.class, false);
        ARMOR.seen.put(MageArmor.class, false);
        ARMOR.seen.put(RogueArmor.class, false);
        ARMOR.seen.put(HuntressArmor.class, false);

        WANDS.seen.put(WandOfMagicMissile.class, false);
        WANDS.seen.put(WandOfLightning.class, false);
        WANDS.seen.put(WandOfDisintegration.class, false);
        WANDS.seen.put(WandOfFireblast.class, false);
        WANDS.seen.put(WandOfCorrosion.class, false);
        WANDS.seen.put(WandOfBlastWave.class, false);
        WANDS.seen.put(WandOfLivingEarth.class, false);
        WANDS.seen.put(WandOfFrost.class, false);
        WANDS.seen.put(WandOfPrismaticLight.class, false);
        WANDS.seen.put(WandOfWarding.class, false);
        WANDS.seen.put(WandOfTransfusion.class, false);
        WANDS.seen.put(WandOfCorruption.class, false);
        WANDS.seen.put(WandOfRegrowth.class, false);

        RINGS.seen.put(RingOfAccuracy.class, false);
        RINGS.seen.put(RingOfEnergy.class, false);
        RINGS.seen.put(RingOfElements.class, false);
        RINGS.seen.put(RingOfEvasion.class, false);
        RINGS.seen.put(RingOfForce.class, false);
        RINGS.seen.put(RingOfFuror.class, false);
        RINGS.seen.put(RingOfHaste.class, false);
        RINGS.seen.put(RingOfMight.class, false);
        RINGS.seen.put(RingOfSharpshooting.class, false);
        RINGS.seen.put(RingOfTenacity.class, false);
        RINGS.seen.put(RingOfWealth.class, false);

        ARTIFACTS.seen.put(AlchemistsToolkit.class, false);
        //ARTIFACTS.seen.put( CapeOfThorns.class,             false);
        ARTIFACTS.seen.put(ChaliceOfBlood.class, false);
        ARTIFACTS.seen.put(CloakOfShadows.class, false);
        ARTIFACTS.seen.put(DriedRose.class, false);
        ARTIFACTS.seen.put(EtherealChains.class, false);
        ARTIFACTS.seen.put(HornOfPlenty.class, false);
        //ARTIFACTS.seen.put( LloydsBeacon.class,             false);
        ARTIFACTS.seen.put(MasterThievesArmband.class, false);
        ARTIFACTS.seen.put(SandalsOfNature.class, false);
        ARTIFACTS.seen.put(TalismanOfForesight.class, false);
        ARTIFACTS.seen.put(TimekeepersHourglass.class, false);
        ARTIFACTS.seen.put(UnstableSpellbook.class, false);

        POTIONS.seen.put(PotionOfHealing.class, false);
        POTIONS.seen.put(PotionOfStrength.class, false);
        POTIONS.seen.put(PotionOfLiquidFlame.class, false);
        POTIONS.seen.put(PotionOfFrost.class, false);
        POTIONS.seen.put(PotionOfToxicGas.class, false);
        POTIONS.seen.put(PotionOfParalyticGas.class, false);
        POTIONS.seen.put(PotionOfPurity.class, false);
        POTIONS.seen.put(PotionOfLevitation.class, false);
        POTIONS.seen.put(PotionOfMindVision.class, false);
        POTIONS.seen.put(PotionOfInvisibility.class, false);
        POTIONS.seen.put(PotionOfExperience.class, false);
        POTIONS.seen.put(PotionOfHaste.class, false);

        SCROLLS.seen.put(ScrollOfIdentify.class, false);
        SCROLLS.seen.put(ScrollOfUpgrade.class, false);
        SCROLLS.seen.put(ScrollOfRemoveCurse.class, false);
        SCROLLS.seen.put(ScrollOfMagicMapping.class, false);
        SCROLLS.seen.put(ScrollOfTeleportation.class, false);
        SCROLLS.seen.put(ScrollOfRecharging.class, false);
        SCROLLS.seen.put(ScrollOfMirrorImage.class, false);
        SCROLLS.seen.put(ScrollOfTerror.class, false);
        SCROLLS.seen.put(ScrollOfLullaby.class, false);
        SCROLLS.seen.put(ScrollOfRage.class, false);
        SCROLLS.seen.put(ScrollOfRetribution.class, false);
        SCROLLS.seen.put(ScrollOfTransmutation.class, false);
    }

    public static LinkedHashMap<Catalog, Badges.Badge> catalogBadges = new LinkedHashMap<>();

    static {
        catalogBadges.put(WEAPONS, Badges.Badge.ALL_WEAPONS_IDENTIFIED);
        catalogBadges.put(ARMOR, Badges.Badge.ALL_ARMOR_IDENTIFIED);
        catalogBadges.put(WANDS, Badges.Badge.ALL_WANDS_IDENTIFIED);
        catalogBadges.put(RINGS, Badges.Badge.ALL_RINGS_IDENTIFIED);
        catalogBadges.put(ARTIFACTS, Badges.Badge.ALL_ARTIFACTS_IDENTIFIED);
        catalogBadges.put(POTIONS, Badges.Badge.ALL_POTIONS_IDENTIFIED);
        catalogBadges.put(SCROLLS, Badges.Badge.ALL_SCROLLS_IDENTIFIED);
    }

    public static boolean isSeen(Class<? extends Item> itemClass) {
        for (Catalog cat : values()) {
            if (cat.seen.containsKey(itemClass)) {
                return cat.seen.get(itemClass);
            }
        }
        return false;
    }

    public static void setSeen(Class<? extends Item> itemClass) {
        for (Catalog cat : values()) {
            if (cat.seen.containsKey(itemClass) && !cat.seen.get(itemClass)) {
                cat.seen.put(itemClass, true);
                Journal.saveNeeded = true;
            }
        }
        Badges.validateItemsIdentified();
    }

    private static final String CATALOGS = "catalogs";

    public static void store(Bundle bundle) {

        Badges.loadGlobal();

        ArrayList<String> seen = new ArrayList<>();

        //if we have identified all items of a set, we use the badge to keep track instead.
        if (!Badges.isUnlocked(Badges.Badge.ALL_ITEMS_IDENTIFIED)) {
            for (Catalog cat : values()) {
                if (!Badges.isUnlocked(catalogBadges.get(cat))) {
                    for (Class<? extends Item> item : cat.items()) {
                        if (cat.seen.get(item)) seen.add(item.getSimpleName());
                    }
                }
            }
        }

        bundle.put(CATALOGS, seen.toArray(new String[0]));

    }

    public static void restore(Bundle bundle) {

        Badges.loadGlobal();

        //logic for if we have all badges
        if (Badges.isUnlocked(Badges.Badge.ALL_ITEMS_IDENTIFIED)) {
            for (Catalog cat : values()) {
                for (Class<? extends Item> item : cat.items()) {
                    cat.seen.put(item, true);
                }
            }
            return;
        }

        //catalog-specific badge logic
        for (Catalog cat : values()) {
            if (Badges.isUnlocked(catalogBadges.get(cat))) {
                for (Class<? extends Item> item : cat.items()) {
                    cat.seen.put(item, true);
                }
            }
        }

        //general save/load
        if (bundle.contains(CATALOGS)) {
            List<String> seen = Arrays.asList(bundle.getStringArray(CATALOGS));

            //TODO should adjust this to tie into the bundling system's class array
            for (Catalog cat : values()) {
                for (Class<? extends Item> item : cat.items()) {
                    if (seen.contains(item.getSimpleName())) {
                        cat.seen.put(item, true);
                    }
                }
            }
        }
    }

}
