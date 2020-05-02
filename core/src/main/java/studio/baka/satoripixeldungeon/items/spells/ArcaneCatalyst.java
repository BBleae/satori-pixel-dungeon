package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.scrolls.*;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ExoticScroll;
import studio.baka.satoripixeldungeon.items.stones.Runestone;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ArcaneCatalyst extends Spell {

    {
        image = ItemSpriteSheet.SCROLL_CATALYST;
    }

    private static final HashMap<Class<? extends Scroll>, Float> scrollChances = new HashMap<>();

    static {
        scrollChances.put(ScrollOfIdentify.class, 3f);
        scrollChances.put(ScrollOfRemoveCurse.class, 2f);
        scrollChances.put(ScrollOfMagicMapping.class, 2f);
        scrollChances.put(ScrollOfMirrorImage.class, 2f);
        scrollChances.put(ScrollOfRecharging.class, 2f);
        scrollChances.put(ScrollOfLullaby.class, 2f);
        scrollChances.put(ScrollOfRetribution.class, 2f);
        scrollChances.put(ScrollOfRage.class, 2f);
        scrollChances.put(ScrollOfTeleportation.class, 2f);
        scrollChances.put(ScrollOfTerror.class, 2f);
        scrollChances.put(ScrollOfTransmutation.class, 1f);
    }

    @Override
    protected void onCast(Hero hero) {

        detach(curUser.belongings.backpack);
        updateQuickslot();

        Scroll s = Reflection.newInstance(Random.chances(scrollChances));
        Objects.requireNonNull(s).anonymize();
        curItem = s;
        s.doRead();
    }

    @Override
    public int price() {
        return 40 * quantity;
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe {

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            boolean scroll = false;
            boolean secondary = false;

            for (Item i : ingredients) {
                if (i instanceof Plant.Seed || i instanceof Runestone) {
                    secondary = true;
                    //if it is a regular or exotic potion
                } else if (ExoticScroll.regToExo.containsKey(i.getClass())
                        || ExoticScroll.regToExo.containsValue(i.getClass())) {
                    scroll = true;
                }
            }

            return scroll && secondary;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                if (i instanceof Plant.Seed) {
                    return 2;
                } else if (i instanceof Runestone) {
                    return 1;
                }
            }
            return 1;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {

            for (Item i : ingredients) {
                i.quantity(i.quantity() - 1);
            }

            return sampleOutput(null);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            return new ArcaneCatalyst();
        }
    }
}
