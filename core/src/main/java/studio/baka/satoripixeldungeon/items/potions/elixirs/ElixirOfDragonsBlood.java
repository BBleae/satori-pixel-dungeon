package studio.baka.satoripixeldungeon.items.potions.elixirs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FireImbue;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class ElixirOfDragonsBlood extends Elixir {

    {
        //TODO finish visuals
        image = ItemSpriteSheet.ELIXIR_DRAGON;
    }

    @Override
    public void apply(Hero hero) {
        Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
        Sample.INSTANCE.play(Assets.SND_BURNING);
        hero.sprite.emitter().burst(FlameParticle.FACTORY, 10);
    }

    @Override
    protected int splashColor() {
        return 0xFFFF002A;
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (50 + 40);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{PotionOfDragonsBreath.class, AlchemicalCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = ElixirOfDragonsBlood.class;
            outQuantity = 1;
        }

    }
}
