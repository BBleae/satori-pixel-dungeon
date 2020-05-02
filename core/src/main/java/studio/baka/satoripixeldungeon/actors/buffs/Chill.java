package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;

import java.text.DecimalFormat;

public class Chill extends FlavourBuff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        //can't chill what's frozen!
        if (target.buff(Frost.class) != null) return false;

        if (super.attachTo(target)) {
            Buff.detach(target, Burning.class);
            return true;
        } else {
            return false;
        }
    }

    //reduces speed by 10% for every turn remaining, capping at 50%
    public float speedFactor() {
        return Math.max(0.5f, 1 - cooldown() * 0.1f);
    }

    @Override
    public int icon() {
        return BuffIndicator.FROST;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.CHILLED);
        else target.sprite.remove(CharSprite.State.CHILLED);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(), new DecimalFormat("#.##").format((1f - speedFactor()) * 100f));
    }
}
