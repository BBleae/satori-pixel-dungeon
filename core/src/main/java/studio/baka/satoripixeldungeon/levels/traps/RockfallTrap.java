package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.levels.RegularLevel;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.BArray;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RockfallTrap extends Trap {

    {
        color = GREY;
        shape = DIAMOND;

        canBeHidden = false;
    }

    @Override
    public void activate() {

        ArrayList<Integer> rockCells = new ArrayList<>();

        //determines if the trap is actually in the world, or if it is being spawned for its effect
        boolean onGround = Dungeon.level.traps.get(pos) == this;

        if (onGround && Dungeon.level instanceof RegularLevel) {
            Room r = ((RegularLevel) Dungeon.level).room(pos);
            int cell;
            for (Point p : r.getPoints()) {
                cell = Dungeon.level.pointToCell(p);
                if (!Dungeon.level.solid[cell]) {
                    rockCells.add(cell);
                }
            }

            //if we don't have rooms, then just do 5x5
        } else {
            PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    rockCells.add(i);
                }
            }
        }

        boolean seen = false;
        for (int cell : rockCells) {

            if (Dungeon.level.heroFOV[cell]) {
                CellEmitter.get(cell - Dungeon.level.width()).start(Speck.factory(Speck.ROCK), 0.07f, 10);
                seen = true;
            }

            Char ch = Actor.findChar(cell);

            if (ch != null && ch.isAlive()) {
                int damage = Random.NormalIntRange(5 + Dungeon.depth, 10 + Dungeon.depth * 2);
                damage -= ch.drRoll();
                ch.damage(Math.max(damage, 0), this);

                Buff.prolong(ch, Paralysis.class, Paralysis.DURATION);

                if (!ch.isAlive() && ch == Dungeon.hero) {
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "ondeath"));
                }
            }
        }

        if (seen) {
            Camera.main.shake(3, 0.7f);
            Sample.INSTANCE.play(Assets.SND_ROCKS);
        }

    }
}
