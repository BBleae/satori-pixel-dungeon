package studio.baka.satoripixeldungeon.items.potions.brews;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Ooze;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.items.potions.PotionOfToxicGas;
import studio.baka.satoripixeldungeon.items.quest.GooBlob;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class CausticBrew extends Brew {

    {
        //TODO finish visuals
        image = ItemSpriteSheet.BREW_CAUSTIC;
    }

    @Override
    public void shatter(int cell) {

        if (Dungeon.level.heroFOV[cell]) {
            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 3);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Splash.at(i, 0x000000, 5);
                Char ch = Actor.findChar(i);

                if (ch != null) {
                    Buff.affect(ch, Ooze.class).set(20f);
                }
            }
        }
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (30 + 50);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{PotionOfToxicGas.class, GooBlob.class};
            inQuantity = new int[]{1, 1};

            cost = 4;

            output = CausticBrew.class;
            outQuantity = 1;
        }

    }
}
