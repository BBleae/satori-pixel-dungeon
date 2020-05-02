package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.items.potions.exotic.PotionOfStormClouds;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class AquaBlast extends TargetedSpell {

    {
        image = ItemSpriteSheet.AQUA_BLAST;
    }

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        int cell = bolt.collisionPos;

        Splash.at(cell, 0x00AAFF, 10);

        for (int i : PathFinder.NEIGHBOURS9) {
            if (i == 0 || Random.Int(5) != 0) {
                int terr = Dungeon.level.map[cell + i];
                if (terr == Terrain.EMPTY || terr == Terrain.GRASS ||
                        terr == Terrain.EMBERS || terr == Terrain.EMPTY_SP ||
                        terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS ||
                        terr == Terrain.EMPTY_DECO) {
                    Level.set(cell + i, Terrain.WATER);
                    GameScene.updateMap(cell + i);
                }
            }
        }

        Char target = Actor.findChar(cell);

        if (target != null && target != hero) {
            //just enough to skip their current turn
            Buff.affect(target, Paralysis.class, 0f);
        }
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((60 + 40) / 12f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            //noinspection unchecked
            inputs = new Class[]{PotionOfStormClouds.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 4;

            output = AquaBlast.class;
            outQuantity = 12;
        }

    }
}
