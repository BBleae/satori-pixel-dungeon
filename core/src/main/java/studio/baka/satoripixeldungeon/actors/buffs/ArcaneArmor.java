package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

//A magical version of barkskin, essentially
public class ArcaneArmor extends Buff {

    {
        type = buffType.POSITIVE;
    }

    private int level = 0;
    private int interval = 1;

    @Override
    public boolean act() {
        if (target.isAlive()) {

            spend(interval);
            if (--level <= 0) {
                detach();
            }

        } else {

            detach();

        }

        return true;
    }

    public int level() {
        return level;
    }

    public void set(int value, int time) {
        //decide whether to override, preferring high value + low interval
        if (Math.sqrt(interval) * level < Math.sqrt(time) * value) {
            level = value;
            interval = time;
            spend(time - cooldown() - 1);
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.ARMOR;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.tint(0.5f, 0, 1, 0.5f);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", level, dispTurns(cooldown() + 1));
    }

    private static final String LEVEL = "level";
    private static final String INTERVAL = "interval";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(INTERVAL, interval);
        bundle.put(LEVEL, level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        interval = bundle.getInt(INTERVAL);
        level = bundle.getInt(LEVEL);
    }
}
