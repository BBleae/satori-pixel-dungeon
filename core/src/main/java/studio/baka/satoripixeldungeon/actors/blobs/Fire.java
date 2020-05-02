package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.scenes.GameScene;

public class Fire extends Blob {

    @Override
    protected void evolve() {

        boolean[] flamable = Dungeon.level.flamable;
        int cell;
        int fire;

        Freezing freeze = (Freezing) Dungeon.level.blobs.get(Freezing.class);

        boolean observe = false;

        for (int i = area.left - 1; i <= area.right; i++) {
            for (int j = area.top - 1; j <= area.bottom; j++) {
                cell = i + j * Dungeon.level.width();
                if (cur[cell] > 0) {

                    if (freeze != null && freeze.volume > 0 && freeze.cur[cell] > 0) {
                        freeze.clear(cell);
                        off[cell] = cur[cell] = 0;
                        continue;
                    }

                    burn(cell);

                    fire = cur[cell] - 1;
                    if (fire <= 0 && flamable[cell]) {

                        Dungeon.level.destroy(cell);

                        observe = true;
                        GameScene.updateMap(cell);

                    }

                } else if (freeze == null || freeze.volume <= 0 || freeze.cur[cell] <= 0) {

                    if (flamable[cell]
                            && (cur[cell - 1] > 0
                            || cur[cell + 1] > 0
                            || cur[cell - Dungeon.level.width()] > 0
                            || cur[cell + Dungeon.level.width()] > 0)) {
                        fire = 4;
                        burn(cell);
                        area.union(i, j);
                    } else {
                        fire = 0;
                    }

                } else {
                    fire = 0;
                }

                volume += (off[cell] = fire);
            }
        }

        if (observe) {
            Dungeon.observe();
        }
    }

    public static void burn(int pos) {
        Char ch = Actor.findChar(pos);
        if (ch != null && !ch.isImmune(Fire.class)) {
            Buff.affect(ch, Burning.class).reignite(ch);
        }

        Heap heap = Dungeon.level.heaps.get(pos);
        if (heap != null) {
            heap.burn();
        }

        Plant plant = Dungeon.level.plants.get(pos);
        if (plant != null) {
            plant.wither();
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(FlameParticle.FACTORY, 0.03f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
