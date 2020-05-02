package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.Recipe;
import studio.baka.satoripixeldungeon.items.potions.*;
import studio.baka.satoripixeldungeon.plants.Plant;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;

public class ExoticPotion extends Potion {

    public static final HashMap<Class<? extends Potion>, Class<? extends ExoticPotion>> regToExo = new HashMap<>();
    public static final HashMap<Class<? extends ExoticPotion>, Class<? extends Potion>> exoToReg = new HashMap<>();

    static {
        regToExo.put(PotionOfHealing.class, PotionOfShielding.class);
        exoToReg.put(PotionOfShielding.class, PotionOfHealing.class);

        regToExo.put(PotionOfToxicGas.class, PotionOfCorrosiveGas.class);
        exoToReg.put(PotionOfCorrosiveGas.class, PotionOfToxicGas.class);

        regToExo.put(PotionOfStrength.class, PotionOfAdrenalineSurge.class);
        exoToReg.put(PotionOfAdrenalineSurge.class, PotionOfStrength.class);

        regToExo.put(PotionOfFrost.class, PotionOfSnapFreeze.class);
        exoToReg.put(PotionOfSnapFreeze.class, PotionOfFrost.class);

        regToExo.put(PotionOfHaste.class, PotionOfStamina.class);
        exoToReg.put(PotionOfStamina.class, PotionOfHaste.class);

        regToExo.put(PotionOfLiquidFlame.class, PotionOfDragonsBreath.class);
        exoToReg.put(PotionOfDragonsBreath.class, PotionOfLiquidFlame.class);

        regToExo.put(PotionOfInvisibility.class, PotionOfShroudingFog.class);
        exoToReg.put(PotionOfShroudingFog.class, PotionOfInvisibility.class);

        regToExo.put(PotionOfMindVision.class, PotionOfMagicalSight.class);
        exoToReg.put(PotionOfMagicalSight.class, PotionOfMindVision.class);

        regToExo.put(PotionOfLevitation.class, PotionOfStormClouds.class);
        exoToReg.put(PotionOfStormClouds.class, PotionOfLevitation.class);

        regToExo.put(PotionOfExperience.class, PotionOfHolyFuror.class);
        exoToReg.put(PotionOfHolyFuror.class, PotionOfExperience.class);

        regToExo.put(PotionOfPurity.class, PotionOfCleansing.class);
        exoToReg.put(PotionOfCleansing.class, PotionOfPurity.class);

        regToExo.put(PotionOfParalyticGas.class, PotionOfEarthenArmor.class);
        exoToReg.put(PotionOfEarthenArmor.class, PotionOfParalyticGas.class);
    }

    @Override
    public boolean isKnown() {
        return anonymous || (handler != null && handler.isKnown(exoToReg.get(this.getClass())));
    }

    @Override
    public void setKnown() {
        if (!isKnown()) {
            handler.know(exoToReg.get(this.getClass()));
            updateQuickslot();
            Potion p = Dungeon.hero.belongings.getItem(getClass());
            if (p != null) p.setAction();
            p = Dungeon.hero.belongings.getItem(exoToReg.get(this.getClass()));
            if (p != null) p.setAction();
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (handler != null && handler.contains(exoToReg.get(this.getClass()))) {
            image = handler.image(exoToReg.get(this.getClass())) + 16;
            color = handler.label(exoToReg.get(this.getClass()));
        }
    }

    @Override
    //20 gold more than its none-exotic equivalent
    public int price() {
        return (Reflection.newInstance(exoToReg.get(getClass())).price() + 20) * quantity;
    }

    public static class PotionToExotic extends Recipe {

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            int s = 0;
            Potion p = null;

            for (Item i : ingredients) {
                if (i instanceof Plant.Seed) {
                    s++;
                } else if (regToExo.containsKey(i.getClass())) {
                    p = (Potion) i;
                }
            }

            return p != null && s == 2;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 0;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Item result = null;

            for (Item i : ingredients) {
                i.quantity(i.quantity() - 1);
                if (regToExo.containsKey(i.getClass())) {
                    result = Reflection.newInstance(regToExo.get(i.getClass()));
                }
            }
            return result;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                if (regToExo.containsKey(i.getClass())) {
                    return Reflection.newInstance(regToExo.get(i.getClass()));
                }
            }
            return null;

        }
    }
}
