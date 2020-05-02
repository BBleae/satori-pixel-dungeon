package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Sheep;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class FlockTrap extends Trap {

    {
        color = WHITE;
        shape = WAVES;
    }


    @Override
    public void activate() {
        //use an actor as we want to put this on a slight delay so all chars get a chance to act this turn first.
        Actor.add(new Actor() {

            {
                actPriority = BUFF_PRIORITY;
            }

            protected boolean act() {
                PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    Trap t;
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                        if (Dungeon.level.insideMap(i)
                                && Actor.findChar(i) == null
                                && !(Dungeon.level.pit[i])) {
                            Sheep sheep = new Sheep();
                            sheep.lifespan = Random.NormalIntRange(4, 8);
                            sheep.pos = i;
                            GameScene.add(sheep);
                            CellEmitter.get(i).burst(Speck.factory(Speck.WOOL), 4);
                            //before the tile is pressed, directly trigger traps to avoid sfx spam
                            if ((t = Dungeon.level.traps.get(i)) != null && t.active) {
                                t.disarm();
                                t.reveal();
                                t.activate();
                            }
                            Dungeon.level.occupyCell(sheep);

                        }
                    }
                }
                Sample.INSTANCE.play(Assets.SND_PUFF);
                Actor.remove(this);
                return true;
            }
        });

    }

}
