package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Random;

public class Drowsy extends Buff {

    {
        type = buffType.NEUTRAL;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.DROWSY;
    }

    public boolean attachTo(Char target) {
        if (!target.isImmune(Sleep.class) && super.attachTo(target)) {
            if (cooldown() == 0)
                spend(Random.Int(3, 6));
            return true;
        }
        return false;
    }

    @Override
    public boolean act() {
        Buff.affect(target, MagicalSleep.class);

        detach();
        return true;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(cooldown() + 1));
    }
}
