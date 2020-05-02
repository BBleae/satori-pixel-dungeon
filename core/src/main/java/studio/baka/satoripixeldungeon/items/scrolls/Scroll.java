package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.MagicImmune;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.ItemStatusHandler;
import studio.baka.satoripixeldungeon.items.Recipe;
import studio.baka.satoripixeldungeon.items.artifacts.UnstableSpellbook;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ExoticScroll;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import studio.baka.satoripixeldungeon.items.stones.*;
import studio.baka.satoripixeldungeon.journal.Catalog;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.HeroSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Scroll extends Item {

    public static final String AC_READ = "READ";

    protected static final float TIME_TO_READ = 1f;

    protected Integer initials;

    private static final Class<?>[] scrolls = {
            ScrollOfIdentify.class,
            ScrollOfMagicMapping.class,
            ScrollOfRecharging.class,
            ScrollOfRemoveCurse.class,
            ScrollOfTeleportation.class,
            ScrollOfUpgrade.class,
            ScrollOfRage.class,
            ScrollOfTerror.class,
            ScrollOfLullaby.class,
            ScrollOfTransmutation.class,
            ScrollOfRetribution.class,
            ScrollOfMirrorImage.class
    };

    private static final HashMap<String, Integer> runes = new HashMap<String, Integer>() {
        {
            put("KAUNAN", ItemSpriteSheet.SCROLL_KAUNAN);
            put("SOWILO", ItemSpriteSheet.SCROLL_SOWILO);
            put("LAGUZ", ItemSpriteSheet.SCROLL_LAGUZ);
            put("YNGVI", ItemSpriteSheet.SCROLL_YNGVI);
            put("GYFU", ItemSpriteSheet.SCROLL_GYFU);
            put("RAIDO", ItemSpriteSheet.SCROLL_RAIDO);
            put("ISAZ", ItemSpriteSheet.SCROLL_ISAZ);
            put("MANNAZ", ItemSpriteSheet.SCROLL_MANNAZ);
            put("NAUDIZ", ItemSpriteSheet.SCROLL_NAUDIZ);
            put("BERKANAN", ItemSpriteSheet.SCROLL_BERKANAN);
            put("ODAL", ItemSpriteSheet.SCROLL_ODAL);
            put("TIWAZ", ItemSpriteSheet.SCROLL_TIWAZ);
        }
    };

    protected static ItemStatusHandler<Scroll> handler;

    protected String rune;

    {
        stackable = true;
        defaultAction = AC_READ;
    }

    @SuppressWarnings("unchecked")
    public static void initLabels() {
        handler = new ItemStatusHandler<>((Class<? extends Scroll>[]) scrolls, runes);
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        ArrayList<Class<? extends Item>> classes = new ArrayList<>();
        for (Item i : items) {
            if (i instanceof ExoticScroll) {
                if (!classes.contains(ExoticScroll.exoToReg.get(i.getClass()))) {
                    classes.add(ExoticScroll.exoToReg.get(i.getClass()));
                }
            } else if (i instanceof Scroll) {
                if (!classes.contains(i.getClass())) {
                    classes.add(i.getClass());
                }
            }
        }
        handler.saveClassesSelectively(bundle, classes);
    }

    @SuppressWarnings("unchecked")
    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler<>((Class<? extends Scroll>[]) scrolls, runes, bundle);
    }

    public Scroll() {
        super();
        reset();
    }

    //anonymous scrolls are always IDed, do not affect ID status,
    //and their sprite is replaced by a placeholder if they are not known,
    //useful for items that appear in UIs, or which are only spawned for their effects
    protected boolean anonymous = false;

    public void anonymize() {
        if (!isKnown()) image = ItemSpriteSheet.SCROLL_HOLDER;
        anonymous = true;
    }


    @Override
    public void reset() {
        super.reset();
        if (handler != null && handler.contains(this)) {
            image = handler.image(this);
            rune = handler.label(this);
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_READ);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_READ)) {

            if (hero.buff(MagicImmune.class) != null) {
                GLog.w(Messages.get(this, "no_magic"));
            } else if (hero.buff(Blindness.class) != null) {
                GLog.w(Messages.get(this, "blinded"));
            } else if (hero.buff(UnstableSpellbook.bookRecharge.class) != null
                    && hero.buff(UnstableSpellbook.bookRecharge.class).isCursed()
                    && !(this instanceof ScrollOfRemoveCurse || this instanceof ScrollOfAntiMagic)) {
                GLog.n(Messages.get(this, "cursed"));
            } else {
                curUser = hero;
                curItem = detach(hero.belongings.backpack);
                doRead();
            }

        }
    }

    public abstract void doRead();

    //currently unused. Used to be used for unstable spellbook prior to 0.7.0
    public void empoweredRead() {
    }

    protected void readAnimation() {
        curUser.spend(TIME_TO_READ);
        curUser.busy();
        ((HeroSprite) curUser.sprite).read();
    }

    public boolean isKnown() {
        return anonymous || (handler != null && handler.isKnown(this));
    }

    public void setKnown() {
        if (!anonymous) {
            if (!isKnown()) {
                handler.know(this);
                updateQuickslot();
            }

            if (Dungeon.hero.isAlive()) {
                Catalog.setSeen(getClass());
            }
        }
    }

    @Override
    public Item identify() {
        setKnown();
        return super.identify();
    }

    @Override
    public String name() {
        return isKnown() ? name : Messages.get(this, rune);
    }

    @Override
    public String info() {
        return isKnown() ?
                desc() :
                Messages.get(this, "unknown_desc");
    }

    public Integer initials() {
        return isKnown() ? initials : null;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return isKnown();
    }

    public static HashSet<Class<? extends Scroll>> getKnown() {
        return handler.known();
    }

    public static HashSet<Class<? extends Scroll>> getUnknown() {
        return handler.unknown();
    }

    public static boolean allKnown() {
        return handler.known().size() == scrolls.length;
    }

    @Override
    public int price() {
        return 30 * quantity;
    }

    public static class PlaceHolder extends Scroll {

        {
            image = ItemSpriteSheet.SCROLL_HOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return ExoticScroll.regToExo.containsKey(item.getClass())
                    || ExoticScroll.regToExo.containsValue(item.getClass());
        }

        @Override
        public void doRead() {
        }

        @Override
        public String info() {
            return "";
        }
    }

    public static class ScrollToStone extends Recipe {

        private static final HashMap<Class<? extends Scroll>, Class<? extends Runestone>> stones = new HashMap<>();
        private static final HashMap<Class<? extends Scroll>, Integer> amnts = new HashMap<>();

        static {
            stones.put(ScrollOfIdentify.class, StoneOfIntuition.class);
            amnts.put(ScrollOfIdentify.class, 3);

            stones.put(ScrollOfLullaby.class, StoneOfDeepenedSleep.class);
            amnts.put(ScrollOfLullaby.class, 3);

            stones.put(ScrollOfMagicMapping.class, StoneOfClairvoyance.class);
            amnts.put(ScrollOfMagicMapping.class, 3);

            stones.put(ScrollOfMirrorImage.class, StoneOfFlock.class);
            amnts.put(ScrollOfMirrorImage.class, 3);

            stones.put(ScrollOfRetribution.class, StoneOfBlast.class);
            amnts.put(ScrollOfRetribution.class, 3);

            stones.put(ScrollOfRage.class, StoneOfAggression.class);
            amnts.put(ScrollOfRage.class, 3);

            stones.put(ScrollOfRecharging.class, StoneOfShock.class);
            amnts.put(ScrollOfRecharging.class, 3);

            stones.put(ScrollOfRemoveCurse.class, StoneOfDisarming.class);
            amnts.put(ScrollOfRemoveCurse.class, 3);

            stones.put(ScrollOfTeleportation.class, StoneOfBlink.class);
            amnts.put(ScrollOfTeleportation.class, 2);

            stones.put(ScrollOfTerror.class, StoneOfAffection.class);
            amnts.put(ScrollOfTerror.class, 3);

            stones.put(ScrollOfTransmutation.class, StoneOfAugmentation.class);
            amnts.put(ScrollOfTransmutation.class, 3);

            stones.put(ScrollOfUpgrade.class, StoneOfEnchantment.class);
            amnts.put(ScrollOfUpgrade.class, 3);
        }

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            return ingredients.size() == 1
                    && ingredients.get(0).isIdentified()
                    && ingredients.get(0) instanceof Scroll
                    && stones.containsKey(ingredients.get(0).getClass());
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 0;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            if (!testIngredients(ingredients)) return null;

            Scroll s = (Scroll) ingredients.get(0);

            s.quantity(s.quantity() - 1);

            return Reflection.newInstance(stones.get(s.getClass())).quantity(amnts.get(s.getClass()));
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            if (!testIngredients(ingredients)) return null;

            Scroll s = (Scroll) ingredients.get(0);
            return Reflection.newInstance(stones.get(s.getClass())).quantity(amnts.get(s.getClass()));
        }
    }
}
