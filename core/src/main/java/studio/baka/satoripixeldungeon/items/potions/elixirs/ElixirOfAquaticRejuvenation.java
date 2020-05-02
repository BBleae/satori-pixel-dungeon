package studio.baka.satoripixeldungeon.items.potions.elixirs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FlavourBuff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.items.quest.GooBlob;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class ElixirOfAquaticRejuvenation extends Elixir {

    {
        //TODO finish visuals
        image = ItemSpriteSheet.ELIXIR_AQUA;
    }

    @Override
    public void apply(Hero hero) {
        Buff.affect(hero, AquaHealing.class).set(Math.round(hero.HT * 1.5f));
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (30 + 50);
    }

    public static class AquaHealing extends Buff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        private int left;

        public void set(int amount) {
            if (amount > left) left = amount;
        }

        @Override
        public boolean act() {

            if (Dungeon.level.water[target.pos] && target.HP < target.HT) {
                float healAmt = GameMath.gate(1, target.HT / 50f, left);
                healAmt = Math.min(healAmt, target.HT - target.HP);
                if (Random.Float() < (healAmt % 1)) {
                    healAmt = (float) Math.ceil(healAmt);
                } else {
                    healAmt = (float) Math.floor(healAmt);
                }
                target.HP += healAmt;
                left -= healAmt;
                target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
            }

            if (left <= 0) {
                detach();
            } else {
                spend(TICK);
                if (left <= target.HT / 4f) {
                    BuffIndicator.refreshHero();
                }
            }
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.HEALING;
        }

        @Override
        public void tintIcon(Image icon) {
            FlavourBuff.greyIcon(icon, target.HT / 4f, left);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", left);
        }

        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            left = bundle.getInt(LEFT);

        }
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{PotionOfHealing.class, GooBlob.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = ElixirOfAquaticRejuvenation.class;
            outQuantity = 1;
        }

    }

}
