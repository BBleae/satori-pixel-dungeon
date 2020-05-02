package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Corrosion;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public class CorrosiveGas extends Blob {

    //FIXME should have strength per-cell
    private int strength = 0;

    @Override
    protected void evolve() {
        super.evolve();

        if (volume == 0) {
            strength = 0;
        } else {
            Char ch;
            int cell;

            for (int i = area.left; i < area.right; i++) {
                for (int j = area.top; j < area.bottom; j++) {
                    cell = i + j * Dungeon.level.width();
                    if (cur[cell] > 0 && (ch = Actor.findChar(cell)) != null) {
                        if (!ch.isImmune(this.getClass()))
                            Buff.affect(ch, Corrosion.class).set(2f, strength);
                    }
                }
            }
        }
    }

    public CorrosiveGas setStrength(int str) {
        if (str > strength) {
            strength = str;
        }
        return this;
    }

    private static final String STRENGTH = "strength";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        strength = bundle.getInt(STRENGTH);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STRENGTH, strength);
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);

        emitter.pour(Speck.factory(Speck.CORROSION), 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
