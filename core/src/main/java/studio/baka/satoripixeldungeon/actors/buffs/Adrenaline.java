package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Adrenaline extends FlavourBuff {

    {
        type = buffType.POSITIVE;

        announced = true;
    }

    public static final float DURATION = 10f;

    @Override
    public int icon() {
        return BuffIndicator.AMOK;
    }

    @Override
    public void tintIcon(Image icon) {
        greyIcon(icon, 5f, cooldown());
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

}
