package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FlavourBuff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.potions.PotionOfLevitation;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class FeatherFall extends Spell {

    {
        image = ItemSpriteSheet.FEATHER_FALL;
    }

    @Override
    protected void onCast(Hero hero) {
        Buff.append(hero, FeatherBuff.class, 30f);
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.SND_READ);
        hero.sprite.emitter().burst(Speck.factory(Speck.JET), 20);

        GLog.p(Messages.get(this, "light"));

        detach(curUser.belongings.backpack);
        updateQuickslot();
        hero.spendAndNext(1f);
    }

    public static class FeatherBuff extends FlavourBuff {
        //does nothing, just waits to be triggered by chasm falling
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((30 + 40) / 2f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            //noinspection unchecked
            inputs = new Class[]{PotionOfLevitation.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = FeatherFall.class;
            outQuantity = 2;
        }

    }
}
