package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.particles.SparkParticle;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Electricity extends Blob {

    {
        //acts after mobs, to give them a chance to resist paralysis
        actPriority = MOB_PRIORITY - 1;
    }

    private boolean[] water;

    @Override
    protected void evolve() {

        water = Dungeon.level.water;
        int cell;

        //spread first..
        for (int i = area.left - 1; i <= area.right; i++) {
            for (int j = area.top - 1; j <= area.bottom; j++) {
                cell = i + j * Dungeon.level.width();

                if (cur[cell] > 0) {
                    spreadFromCell(cell, cur[cell]);
                }
            }
        }

        //..then decrement/shock
        for (int i = area.left - 1; i <= area.right; i++) {
            for (int j = area.top - 1; j <= area.bottom; j++) {
                cell = i + j * Dungeon.level.width();
                if (cur[cell] > 0) {
                    Char ch = Actor.findChar(cell);
                    if (ch != null && !ch.isImmune(this.getClass())) {
                        Buff.prolong(ch, Paralysis.class, 1f);
                        if (cur[cell] % 2 == 1) {
                            ch.damage(Math.round(Random.Float(2 + Dungeon.depth / 5f)), this);
                            if (!ch.isAlive() && ch == Dungeon.hero) {
                                Dungeon.fail(getClass());
                                GLog.n(Messages.get(this, "ondeath"));
                            }
                        }
                    }

                    Heap h = Dungeon.level.heaps.get(cell);
                    if (h != null) {
                        Item toShock = h.peek();
                        if (toShock instanceof Wand) {
                            ((Wand) toShock).gainCharge(0.333f);
                        } else if (toShock instanceof MagesStaff) {
                            ((MagesStaff) toShock).gainCharge(0.333f);
                        }
                    }

                    off[cell] = cur[cell] - 1;
                    volume += off[cell];
                } else {
                    off[cell] = 0;
                }
            }
        }

    }

    private void spreadFromCell(int cell, int power) {
        if (cur[cell] == 0) {
            area.union(cell % Dungeon.level.width(), cell / Dungeon.level.width());
        }
        cur[cell] = Math.max(cur[cell], power);

        for (int c : PathFinder.NEIGHBOURS4) {
            if (water[cell + c] && cur[cell + c] < power) {
                spreadFromCell(cell + c, power);
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(SparkParticle.FACTORY, 0.05f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }

}
