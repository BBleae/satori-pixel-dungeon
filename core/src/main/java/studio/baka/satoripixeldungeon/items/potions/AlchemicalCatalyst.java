package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.exotic.ExoticPotion;
import studio.baka.satoripixeldungeon.items.stones.Runestone;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AlchemicalCatalyst extends Potion {

    {
        image = ItemSpriteSheet.POTION_CATALYST;

    }

    private static final HashMap<Class<? extends Potion>, Float> potionChances = new HashMap<>();

    static {
        potionChances.put(PotionOfHealing.class, 3f);
        potionChances.put(PotionOfMindVision.class, 2f);
        potionChances.put(PotionOfFrost.class, 2f);
        potionChances.put(PotionOfLiquidFlame.class, 2f);
        potionChances.put(PotionOfToxicGas.class, 2f);
        potionChances.put(PotionOfHaste.class, 2f);
        potionChances.put(PotionOfInvisibility.class, 2f);
        potionChances.put(PotionOfLevitation.class, 2f);
        potionChances.put(PotionOfParalyticGas.class, 2f);
        potionChances.put(PotionOfPurity.class, 2f);
        potionChances.put(PotionOfExperience.class, 1f);
    }

    @Override
    public void apply(Hero hero) {
        Potion p = Reflection.newInstance(Random.chances(potionChances));
        Objects.requireNonNull(p).anonymize();
        p.apply(hero);
    }

    @Override
    public void shatter(int cell) {
        Potion p = Reflection.newInstance(Random.chances(potionChances));
        Objects.requireNonNull(p).anonymize();
        curItem = p;
        p.shatter(cell);
    }

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public int price() {
        return 40 * quantity;
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe {

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            boolean potion = false;
            boolean secondary = false;

            for (Item i : ingredients) {
                if (i instanceof Plant.Seed || i instanceof Runestone) {
                    secondary = true;
                    //if it is a regular or exotic potion
                } else if (ExoticPotion.regToExo.containsKey(i.getClass())
                        || ExoticPotion.regToExo.containsValue(i.getClass())) {
                    potion = true;
                }
            }

            return potion && secondary;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                if (i instanceof Plant.Seed) {
                    return 1;
                } else if (i instanceof Runestone) {
                    return 2;
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
            return new AlchemicalCatalyst();
        }
    }

}
