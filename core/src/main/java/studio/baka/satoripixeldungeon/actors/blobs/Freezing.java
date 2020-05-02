package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Chill;
import studio.baka.satoripixeldungeon.actors.buffs.Frost;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.SnowParticle;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.messages.Messages;
import com.watabou.utils.Random;

public class Freezing extends Blob {

    @Override
    protected void evolve() {

        int cell;

        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);

        for (int i = area.left - 1; i <= area.right; i++) {
            for (int j = area.top - 1; j <= area.bottom; j++) {
                cell = i + j * Dungeon.level.width();
                if (cur[cell] > 0) {

                    if (fire != null && fire.volume > 0 && fire.cur[cell] > 0) {
                        fire.clear(cell);
                        off[cell] = cur[cell] = 0;
                        continue;
                    }

                    Freezing.freeze(cell);

                    off[cell] = cur[cell] - 1;
                    volume += off[cell];
                } else {
                    off[cell] = 0;
                }
            }
        }
    }

    public static void freeze(int cell) {
        Char ch = Actor.findChar(cell);
        if (ch != null && !ch.isImmune(Freezing.class)) {
            if (ch.buff(Frost.class) != null) {
                Buff.affect(ch, Frost.class, 2f);
            } else {
                Buff.affect(ch, Chill.class, Dungeon.level.water[cell] ? 5f : 3f);
                Chill chill = ch.buff(Chill.class);
                if (chill != null && chill.cooldown() >= 10f) {
                    Buff.affect(ch, Frost.class, 5f);
                }
            }
        }

        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null) heap.freeze();
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(SnowParticle.FACTORY, 0.05f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }

    //legacy functionality from before this was a proper blob. Returns true if this cell is visible
    public static boolean affect(int cell, Fire fire) {

        Char ch = Actor.findChar(cell);
        if (ch != null) {
            if (Dungeon.level.water[ch.pos]) {
                Buff.prolong(ch, Frost.class, Frost.duration(ch) * Random.Float(5f, 7.5f));
            } else {
                Buff.prolong(ch, Frost.class, Frost.duration(ch) * Random.Float(1.0f, 1.5f));
            }
        }

        if (fire != null) {
            fire.clear(cell);
        }

        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null) {
            heap.freeze();
        }

        if (Dungeon.level.heroFOV[cell]) {
            CellEmitter.get(cell).start(SnowParticle.FACTORY, 0.2f, 6);
            return true;
        } else {
            return false;
        }
    }
}
