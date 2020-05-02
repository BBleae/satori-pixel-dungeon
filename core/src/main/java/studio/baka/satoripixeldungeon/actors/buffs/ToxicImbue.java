package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class ToxicImbue extends Buff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    public static final float DURATION = 50f;

    protected float left;

    private static final String LEFT = "left";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, left);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        left = bundle.getFloat(LEFT);
    }

    public void set(float duration) {
        this.left = duration;
    }

    @Override
    public boolean act() {
        GameScene.add(Blob.seed(target.pos, 50, ToxicGas.class));

        spend(TICK);
        left -= TICK;
        if (left <= 0) {
            detach();
        } else if (left < 5) {
            BuffIndicator.refreshHero();
        }

        return true;
    }

    @Override
    public int icon() {
        return BuffIndicator.IMMUNITY;
    }

    @Override
    public void tintIcon(Image icon) {
        FlavourBuff.greyIcon(icon, 5f, left);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(left));
    }

    {
        immunities.add(ToxicGas.class);
        immunities.add(Poison.class);
    }
}
