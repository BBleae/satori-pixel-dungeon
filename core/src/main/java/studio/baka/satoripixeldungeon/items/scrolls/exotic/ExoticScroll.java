package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.Recipe;
import studio.baka.satoripixeldungeon.items.scrolls.*;
import studio.baka.satoripixeldungeon.items.stones.Runestone;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public abstract class ExoticScroll extends Scroll {


    public static final HashMap<Class<? extends Scroll>, Class<? extends ExoticScroll>> regToExo = new HashMap<>();
    public static final HashMap<Class<? extends ExoticScroll>, Class<? extends Scroll>> exoToReg = new HashMap<>();

    static {
        regToExo.put(ScrollOfIdentify.class, ScrollOfDivination.class);
        exoToReg.put(ScrollOfDivination.class, ScrollOfIdentify.class);

        regToExo.put(ScrollOfUpgrade.class, ScrollOfEnchantment.class);
        exoToReg.put(ScrollOfEnchantment.class, ScrollOfUpgrade.class);

        regToExo.put(ScrollOfTerror.class, ScrollOfPetrification.class);
        exoToReg.put(ScrollOfPetrification.class, ScrollOfTerror.class);

        regToExo.put(ScrollOfRemoveCurse.class, ScrollOfAntiMagic.class);
        exoToReg.put(ScrollOfAntiMagic.class, ScrollOfRemoveCurse.class);

        regToExo.put(ScrollOfLullaby.class, ScrollOfAffection.class);
        exoToReg.put(ScrollOfAffection.class, ScrollOfLullaby.class);

        regToExo.put(ScrollOfRage.class, ScrollOfConfusion.class);
        exoToReg.put(ScrollOfConfusion.class, ScrollOfRage.class);

        regToExo.put(ScrollOfTerror.class, ScrollOfPetrification.class);
        exoToReg.put(ScrollOfPetrification.class, ScrollOfTerror.class);

        regToExo.put(ScrollOfRecharging.class, ScrollOfMysticalEnergy.class);
        exoToReg.put(ScrollOfMysticalEnergy.class, ScrollOfRecharging.class);

        regToExo.put(ScrollOfMagicMapping.class, ScrollOfForesight.class);
        exoToReg.put(ScrollOfForesight.class, ScrollOfMagicMapping.class);

        regToExo.put(ScrollOfTeleportation.class, ScrollOfPassage.class);
        exoToReg.put(ScrollOfPassage.class, ScrollOfTeleportation.class);

        regToExo.put(ScrollOfRetribution.class, ScrollOfPsionicBlast.class);
        exoToReg.put(ScrollOfPsionicBlast.class, ScrollOfRetribution.class);

        regToExo.put(ScrollOfMirrorImage.class, ScrollOfPrismaticImage.class);
        exoToReg.put(ScrollOfPrismaticImage.class, ScrollOfMirrorImage.class);

        regToExo.put(ScrollOfTransmutation.class, ScrollOfPolymorph.class);
        exoToReg.put(ScrollOfPolymorph.class, ScrollOfTransmutation.class);
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
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (handler != null && handler.contains(exoToReg.get(this.getClass()))) {
            image = handler.image(exoToReg.get(this.getClass())) + 16;
            rune = handler.label(exoToReg.get(this.getClass()));
        }
    }

    @Override
    public void empoweredRead() {

    }

    @Override
    //20 gold more than its none-exotic equivalent
    public int price() {
        return (Objects.requireNonNull(Reflection.newInstance(exoToReg.get(getClass()))).price() + 20) * quantity;
    }

    public static class ScrollToExotic extends Recipe {

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            int r = 0;
            Scroll s = null;

            for (Item i : ingredients) {
                if (i instanceof Runestone) {
                    r++;
                } else if (regToExo.containsKey(i.getClass())) {
                    s = (Scroll) i;
                }
            }

            return s != null && r == 2;
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
