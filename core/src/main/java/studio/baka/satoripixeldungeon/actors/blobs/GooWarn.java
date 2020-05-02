package studio.baka.satoripixeldungeon.actors.blobs;


import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.GooSprite;

public class GooWarn extends Blob {

    //cosmetic blob, used to warn noobs that goo's pump up should, infact, be avoided.

    {
        //this one needs to act after the Goo
        actPriority = MOB_PRIORITY - 1;
    }

    protected int pos;

    @Override
    protected void evolve() {

        int cell;

        for (int i = area.left; i < area.right; i++) {
            for (int j = area.top; j < area.bottom; j++) {
                cell = i + j * Dungeon.level.width();
                off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                if (off[cell] > 0) {
                    volume += off[cell];
                }
            }
        }

    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(GooSprite.GooParticle.FACTORY, 0.03f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}

