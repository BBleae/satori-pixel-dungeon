package studio.baka.satoripixeldungeon.items.potions.elixirs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Healing;
import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Bee;
import studio.baka.satoripixeldungeon.items.Honeypot;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class ElixirOfHoneyedHealing extends Elixir {

    {
        image = ItemSpriteSheet.ELIXIR_HONEY;
    }

    @Override
    public void apply(Hero hero) {
        Buff.affect(hero, Healing.class).setHeal((int) (0.8f * hero.HT + 14), 0.25f, 0);
        PotionOfHealing.cure(hero);
        Buff.affect(hero, Hunger.class).satisfy(Hunger.STARVING / 5f);
    }

    @Override
    public void shatter(int cell) {
        if (Dungeon.level.heroFOV[cell]) {
            Sample.INSTANCE.play(Assets.SND_SHATTER);
            splash(cell);
        }

        Char ch = Actor.findChar(cell);
        if (ch != null) {
            Buff.affect(ch, Healing.class).setHeal((int) (0.8f * ch.HT + 14), 0.25f, 0);
            PotionOfHealing.cure(ch);
            if (ch instanceof Bee && ch.alignment != curUser.alignment) {
                ch.alignment = Char.Alignment.ALLY;
                ((Bee) ch).setPotInfo(-1, null);

            }
        }
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (30 + 5);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{PotionOfHealing.class, Honeypot.ShatteredPot.class};
            inQuantity = new int[]{1, 1};

            cost = 4;

            output = ElixirOfHoneyedHealing.class;
            outQuantity = 1;
        }

    }
}
