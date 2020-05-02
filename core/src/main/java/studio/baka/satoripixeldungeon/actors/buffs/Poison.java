package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.PoisonParticle;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Poison extends Buff implements Hero.Doom {

    protected float left;

    private static final String LEFT = "left";

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

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
        this.left = Math.max(duration, left);
    }

    public void extend(float duration) {
        this.left += duration;
    }

    @Override
    public int icon() {
        return BuffIndicator.POISON;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.6f, 0.2f, 0.6f);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String heroMessage() {
        return Messages.get(this, "heromsg");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(left));
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target) && target.sprite != null) {
            CellEmitter.center(target.pos).burst(PoisonParticle.SPLASH, 5);
            return true;
        } else
            return false;
    }

    @Override
    public boolean act() {
        if (target.isAlive()) {

            target.damage((int) (left / 3) + 1, this);
            spend(TICK);

            if ((left -= TICK) <= 0) {
                detach();
            }

        } else {

            detach();

        }

        return true;
    }

    @Override
    public void onDeath() {
        Badges.validateDeathFromPoison();

        Dungeon.fail(getClass());
        GLog.n(Messages.get(this, "ondeath"));
    }
}
