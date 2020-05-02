package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Lightning;
import studio.baka.satoripixeldungeon.effects.particles.EnergyParticle;
import studio.baka.satoripixeldungeon.effects.particles.SparkParticle;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class StoneOfShock extends Runestone {

    {
        image = ItemSpriteSheet.STONE_SHOCK;
    }

    @Override
    protected void activate(int cell) {

        Sample.INSTANCE.play(Assets.SND_LIGHTNING);

        ArrayList<Lightning.Arc> arcs = new ArrayList<>();
        int hits = 0;

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char n = Actor.findChar(i);
                if (n != null) {
                    arcs.add(new Lightning.Arc(cell, n.sprite.center()));
                    Buff.prolong(n, Paralysis.class, 1f);
                    hits++;
                }
            }
        }

        CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3);

        if (hits > 0) {
            curUser.sprite.parent.addToFront(new Lightning(arcs, null));
            curUser.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);
            Sample.INSTANCE.play(Assets.SND_LIGHTNING);

            curUser.belongings.charge(1f + hits);
        }

    }
}
