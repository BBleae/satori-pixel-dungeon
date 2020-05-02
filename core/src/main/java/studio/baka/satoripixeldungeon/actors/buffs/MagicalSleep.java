package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;

public class MagicalSleep extends Buff {

    private static final float STEP = 1f;

    @Override
    public boolean attachTo(Char target) {
        if (!target.isImmune(Sleep.class) && super.attachTo(target)) {

            target.paralysed++;

            if (target.alignment == Char.Alignment.ALLY) {
                if (target.HP == target.HT) {
                    if (target instanceof Hero) GLog.i(Messages.get(this, "toohealthy"));
                    detach();
                } else {
                    if (target instanceof Hero) GLog.i(Messages.get(this, "fallasleep"));
                }
            }

            if (target instanceof Mob) {
                ((Mob) target).state = ((Mob) target).SLEEPING;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean act() {
        if (target instanceof Mob && ((Mob) target).state != ((Mob) target).SLEEPING) {
            detach();
            return true;
        }
        if (target.alignment == Char.Alignment.ALLY) {
            target.HP = Math.min(target.HP + 1, target.HT);
            if (target instanceof Hero) ((Hero) target).resting = true;
            if (target.HP == target.HT) {
                if (target instanceof Hero) GLog.p(Messages.get(this, "wakeup"));
                detach();
            }
        }
        spend(STEP);
        return true;
    }

    @Override
    public void detach() {
        if (target.paralysed > 0)
            target.paralysed--;
        if (target instanceof Hero)
            ((Hero) target).resting = false;
        super.detach();
    }

    @Override
    public int icon() {
        return BuffIndicator.MAGIC_SLEEP;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
}