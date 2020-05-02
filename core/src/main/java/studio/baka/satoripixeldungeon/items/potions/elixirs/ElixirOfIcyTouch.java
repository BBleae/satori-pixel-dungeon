package studio.baka.satoripixeldungeon.items.potions.elixirs;

import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FrostImbue;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.particles.SnowParticle;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.items.potions.exotic.PotionOfSnapFreeze;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ElixirOfIcyTouch extends Elixir {

    {
        //TODO finish visuals
        image = ItemSpriteSheet.ELIXIR_ICY;
    }

    @Override
    public void apply(Hero hero) {
        Buff.affect(hero, FrostImbue.class, FrostImbue.DURATION);
        hero.sprite.emitter().burst(SnowParticle.FACTORY, 5);
    }

    @Override
    protected int splashColor() {
        return 0xFF18C3E6;
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (50 + 40);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            //noinspection unchecked
            inputs = new Class[]{PotionOfSnapFreeze.class, AlchemicalCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = ElixirOfIcyTouch.class;
            outQuantity = 1;
        }

    }
}
