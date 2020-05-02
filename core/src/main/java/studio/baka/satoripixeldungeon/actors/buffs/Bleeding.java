package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;

import static com.watabou.utils.Random.NormalFloat;

public class Bleeding extends Buff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    protected float level;

    private static final String LEVEL = "level";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getFloat(LEVEL);
    }

    public void set(float level) {
        this.level = Math.max(this.level, level);
    }

    @Override
    public int icon() {
        return BuffIndicator.BLEEDING;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public boolean act() {
        if (target.isAlive()) {

            level = NormalFloat(level / 2f, level);
            int dmg = Math.round(level);

            if (dmg > 0) {

                target.damage(dmg, this);
                if (target.sprite.visible) {
                    Splash.at(target.sprite.center(), -PointF.PI / 2, PointF.PI / 6,
                            target.sprite.blood(), Math.min(10 * dmg / target.HT, 10));
                }

                if (target == Dungeon.hero && !target.isAlive()) {
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "ondeath"));
                }

                spend(TICK);
            } else {
                detach();
            }

        } else {

            detach();

        }

        return true;
    }

    @Override
    public String heroMessage() {
        return Messages.get(this, "heromsg");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Math.round(level));
    }
}
