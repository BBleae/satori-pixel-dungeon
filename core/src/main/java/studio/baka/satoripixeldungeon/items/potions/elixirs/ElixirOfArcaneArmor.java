package studio.baka.satoripixeldungeon.items.potions.elixirs;

import studio.baka.satoripixeldungeon.actors.buffs.ArcaneArmor;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.potions.exotic.PotionOfEarthenArmor;
import studio.baka.satoripixeldungeon.items.quest.GooBlob;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ElixirOfArcaneArmor extends Elixir {

    {
        image = ItemSpriteSheet.ELIXIR_ARCANE;
    }

    @Override
    public void apply(Hero hero) {
        Buff.affect(hero, ArcaneArmor.class).set(5 + hero.lvl / 2, 80);
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (50 + 40);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{PotionOfEarthenArmor.class, GooBlob.class};
            inQuantity = new int[]{1, 1};

            cost = 8;

            output = ElixirOfArcaneArmor.class;
            outQuantity = 1;
        }

    }
}
