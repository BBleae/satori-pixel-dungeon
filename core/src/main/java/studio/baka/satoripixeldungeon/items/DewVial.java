package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.AlchemyScene;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

import java.util.ArrayList;

public class DewVial extends Item {

    private static final int MAX_VOLUME = 40;

    private static final String AC_DRINK = "DRINK";

    private static final float TIME_TO_DRINK = 1f;

    private static final String TXT_STATUS = "%d/%d";

    {
        image = ItemSpriteSheet.VIAL;

        defaultAction = AC_DRINK;

        unique = true;
    }

    private int volume = 0;

    private static final String VOLUME = "volume";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(VOLUME, volume);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        volume = bundle.getInt(VOLUME);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (volume > 0) {
            actions.add(AC_DRINK);
        }
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_DRINK)) {

            if (volume > 0) {

                float missingHealthPercent = 1f - (hero.HP / (float) hero.HT);

                //trimming off 0.01 drops helps with floating point errors
                int dropsNeeded = (int) Math.ceil((missingHealthPercent / 0.05f) - 0.01f);
                dropsNeeded = (int) GameMath.gate(1, dropsNeeded, volume);

                //20 drops for a full heal normally
                int heal = Math.round(hero.HT * 0.05f * dropsNeeded);

                int effect = Math.min(hero.HT - hero.HP, heal);
                if (effect > 0) {
                    hero.HP += effect;
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1 + dropsNeeded / 5);
                    hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "value", effect));
                }

                volume -= dropsNeeded;

                hero.spend(TIME_TO_DRINK);
                hero.busy();

                Sample.INSTANCE.play(Assets.SND_DRINK);
                hero.sprite.operate(hero.pos);

                updateQuickslot();


            } else {
                GLog.w(Messages.get(this, "empty"));
            }

        }
    }

    public void empty() {
        volume = 0;
        updateQuickslot();
    }

    public void empty(int cost) {
        if (cost < volume) volume -= cost;
        else volume = 0;

        updateQuickslot();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public boolean isEnough() {
        return volume >= 20;
    }

    public boolean isFull() {
        return volume >= MAX_VOLUME;
    }

    public void collectDew(Dewdrop dew) {

        GLog.i(Messages.get(this, "collected"));
        volume += dew.quantity;
        if (volume >= MAX_VOLUME) {
            volume = MAX_VOLUME;
            GLog.p(Messages.get(this, "full"));
        }

        updateQuickslot();
    }

    public void fill() {
        volume = MAX_VOLUME;
        updateQuickslot();
    }

    public void absorbEnergy(int energy) {
        if (energy + volume < MAX_VOLUME) volume += energy;
        else volume = MAX_VOLUME;
        updateQuickslot();
    }

    @Override
    public String status() {
        return Messages.format(TXT_STATUS, volume, MAX_VOLUME);
    }

    public static class fill extends Recipe {

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            return ingredients.get(0) instanceof DewVial;
        }

        private static int lastCost;

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return lastCost = Math.max(1, AlchemyScene.availableEnergy());
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            DewVial existing = (DewVial) ingredients.get(0);

            existing.absorbEnergy(lastCost);

            return existing;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            DewVial sample = new DewVial();
            //sample.identify();

            DewVial existing = (DewVial) ingredients.get(0);

            sample.volume = existing.volume;
            sample.absorbEnergy(AlchemyScene.availableEnergy());
            return sample;
        }
    }

}
