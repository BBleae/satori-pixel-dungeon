package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Roots;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.particles.WebParticle;
import studio.baka.satoripixeldungeon.messages.Messages;

public class Web extends Blob {

    @Override
    protected void evolve() {

        int cell;

        for (int i = area.left; i < area.right; i++) {
            for (int j = area.top; j < area.bottom; j++) {
                cell = i + j * Dungeon.level.width();
                off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                if (off[cell] > 0) {

                    volume += off[cell];

                    Char ch = Actor.findChar(cell);
                    if (ch != null && !ch.isImmune(this.getClass())) {
                        Buff.prolong(ch, Roots.class, TICK);
                    }
                }
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);

        emitter.pour(WebParticle.FACTORY, 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
