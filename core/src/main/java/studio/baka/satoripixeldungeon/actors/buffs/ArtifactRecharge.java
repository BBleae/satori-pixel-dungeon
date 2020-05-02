package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.hero.Belongings;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.artifacts.Artifact;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

//TODO this may be very powerful, consider balancing
public class ArtifactRecharge extends Buff {

    {
        type = buffType.POSITIVE;
    }

    private int left;

    @Override
    public boolean act() {

        if (target instanceof Hero) {
            Belongings b = ((Hero) target).belongings;

            if (b.misc1 instanceof Artifact) {
                ((Artifact) b.misc1).charge((Hero) target);
            }
            if (b.misc2 instanceof Artifact) {
                ((Artifact) b.misc2).charge((Hero) target);
            }
            if (b.misc3 instanceof Artifact) {
                ((Artifact) b.misc3).charge((Hero) target);
            }
        }

        left--;
        if (left <= 0) {
            detach();
        } else {
            spend(TICK);
        }

        return true;
    }

    public void set(int amount) {
        left = amount;
    }

    public void prolong(int amount) {
        left += amount;
    }

    @Override
    public int icon() {
        return BuffIndicator.RECHARGING;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0, 1f, 0);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(left + 1));
    }

    private static final String LEFT = "left";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        left = bundle.getInt(LEFT);
    }
}
