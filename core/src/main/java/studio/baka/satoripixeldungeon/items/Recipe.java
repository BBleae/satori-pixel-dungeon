package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.items.artifacts.AlchemistsToolkit;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.items.food.Blandfruit;
import studio.baka.satoripixeldungeon.items.food.MeatPie;
import studio.baka.satoripixeldungeon.items.food.StewedMeat;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.potions.brews.BlizzardBrew;
import studio.baka.satoripixeldungeon.items.potions.brews.CausticBrew;
import studio.baka.satoripixeldungeon.items.potions.brews.InfernalBrew;
import studio.baka.satoripixeldungeon.items.potions.brews.ShockingBrew;
import studio.baka.satoripixeldungeon.items.potions.elixirs.*;
import studio.baka.satoripixeldungeon.items.potions.exotic.ExoticPotion;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ExoticScroll;
import studio.baka.satoripixeldungeon.items.spells.*;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.missiles.darts.Dart;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Recipe {

    public abstract boolean testIngredients(ArrayList<Item> ingredients);

    public abstract int cost(ArrayList<Item> ingredients);

    public abstract Item brew(ArrayList<Item> ingredients);

    public abstract Item sampleOutput(ArrayList<Item> ingredients);

    //subclass for the common situation of a recipe with static inputs and outputs
    public static abstract class SimpleRecipe extends Recipe {

        //*** These elements must be filled in by subclasses
        protected Class<? extends Item>[] inputs; //each class should be unique
        protected int[] inQuantity;

        protected int cost;

        protected Class<? extends Item> output;
        protected int outQuantity;
        //***

        //gets a simple list of items based on inputs
        public ArrayList<Item> getIngredients() {
            ArrayList<Item> result = new ArrayList<>();
            for (int i = 0; i < inputs.length; i++) {
                Item ingredient = Reflection.newInstance(inputs[i]);
                Objects.requireNonNull(ingredient).quantity(inQuantity[i]);
                result.add(ingredient);
            }
            return result;
        }

        @Override
        public final boolean testIngredients(ArrayList<Item> ingredients) {

            int[] needed = inQuantity.clone();

            for (Item ingredient : ingredients) {
                if (!ingredient.isIdentified()) return false;
                for (int i = 0; i < inputs.length; i++) {
                    if (ingredient.getClass() == inputs[i]) {
                        needed[i] -= ingredient.quantity();
                        break;
                    }
                }
            }

            for (int i : needed) {
                if (i > 0) {
                    return false;
                }
            }

            return true;
        }

        public final int cost(ArrayList<Item> ingredients) {
            return cost;
        }

        @Override
        public final Item brew(ArrayList<Item> ingredients) {
            if (!testIngredients(ingredients)) return null;

            int[] needed = inQuantity.clone();

            for (Item ingredient : ingredients) {
                for (int i = 0; i < inputs.length; i++) {
                    if (ingredient.getClass() == inputs[i] && needed[i] > 0) {
                        if (needed[i] <= ingredient.quantity()) {
                            ingredient.quantity(ingredient.quantity() - needed[i]);
                            needed[i] = 0;
                        } else {
                            needed[i] -= ingredient.quantity();
                            ingredient.quantity(0);
                        }
                    }
                }
            }

            //sample output and real output are identical in this case.
            return sampleOutput(null);
        }

        //ingredients are ignored, as output doesn't vary
        public final Item sampleOutput(ArrayList<Item> ingredients) {
            try {
                Item result = Reflection.newInstance(output);
                Objects.requireNonNull(result).quantity(outQuantity);
                return result;
            } catch (Exception e) {
                SatoriPixelDungeon.reportException(e);
                return null;
            }
        }
    }


    //*******
    // Static members
    //*******

    @SuppressWarnings("StaticInitializerReferencesSubClass")
    private static final Recipe[] oneIngredientRecipes = new Recipe[]{
            new AlchemistsToolkit.upgradeKit(),
            new Scroll.ScrollToStone(),
            new StewedMeat.oneMeat(),
            new DewVial.fill()
    };

    @SuppressWarnings("StaticInitializerReferencesSubClass")
    private static final Recipe[] twoIngredientRecipes = new Recipe[]{
            new Blandfruit.CookFruit(),
            new Bomb.EnhanceBomb(),
            new AlchemicalCatalyst.Recipe(),
            new ArcaneCatalyst.Recipe(),
            new ElixirOfArcaneArmor.Recipe(),
            new ElixirOfAquaticRejuvenation.Recipe(),
            new ElixirOfDragonsBlood.Recipe(),
            new ElixirOfIcyTouch.Recipe(),
            new ElixirOfMight.Recipe(),
            new ElixirOfHoneyedHealing.Recipe(),
            new ElixirOfToxicEssence.Recipe(),
            new BlizzardBrew.Recipe(),
            new InfernalBrew.Recipe(),
            new ShockingBrew.Recipe(),
            new CausticBrew.Recipe(),
            new Alchemize.Recipe(),
            new AquaBlast.Recipe(),
            new BeaconOfReturning.Recipe(),
            new CurseInfusion.Recipe(),
            new FeatherFall.Recipe(),
            new MagicalInfusion.Recipe(),
            new MagicalPorter.Recipe(),
            new PhaseShift.Recipe(),
            new ReclaimTrap.Recipe(),
            new Recycle.Recipe(),
            new WildEnergy.Recipe(),
            new StewedMeat.twoMeat()
    };

    @SuppressWarnings("StaticInitializerReferencesSubClass")
    private static final Recipe[] threeIngredientRecipes = new Recipe[]{
            new Potion.SeedToPotion(),
            new ExoticPotion.PotionToExotic(),
            new ExoticScroll.ScrollToExotic(),
            new StewedMeat.threeMeat(),
            new MeatPie.Recipe()
    };

    public static Recipe findRecipe(ArrayList<Item> ingredients) {

        if (ingredients.size() == 1) {
            for (Recipe recipe : oneIngredientRecipes) {
                if (recipe.testIngredients(ingredients)) {
                    return recipe;
                }
            }

        } else if (ingredients.size() == 2) {
            for (Recipe recipe : twoIngredientRecipes) {
                if (recipe.testIngredients(ingredients)) {
                    return recipe;
                }
            }

        } else if (ingredients.size() == 3) {
            for (Recipe recipe : threeIngredientRecipes) {
                if (recipe.testIngredients(ingredients)) {
                    return recipe;
                }
            }
        }

        return null;
    }

    public static boolean usableInRecipe(Item item) {
        return !item.cursed
                && (!(item instanceof EquipableItem) || item instanceof Dart || item instanceof AlchemistsToolkit)
                && !(item instanceof Wand);
    }
}


