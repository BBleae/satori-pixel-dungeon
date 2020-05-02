package studio.baka.satoripixeldungeon.items.potions.elixirs;

import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.ToxicImbue;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.particles.PoisonParticle;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.items.potions.PotionOfToxicGas;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ElixirOfToxicEssence extends Elixir {

    {
        //TODO finish visuals
        image = ItemSpriteSheet.ELIXIR_TOXIC;
    }

    @Override
    public void apply(Hero hero) {
        Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
        hero.sprite.emitter().burst(PoisonParticle.SPLASH, 10);
    }

    @Override
    protected int splashColor() {
        return 0xFF00B34A;
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (30 + 40);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            //noinspection unchecked
            inputs = new Class[]{PotionOfToxicGas.class, AlchemicalCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = ElixirOfToxicEssence.class;
            outQuantity = 1;
        }

    }

}
