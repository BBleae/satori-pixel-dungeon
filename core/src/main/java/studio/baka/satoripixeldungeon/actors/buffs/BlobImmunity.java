package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.blobs.*;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class BlobImmunity extends FlavourBuff {

    {
        type = buffType.POSITIVE;
    }

    public static final float DURATION = 20f;

    @Override
    public int icon() {
        return BuffIndicator.IMMUNITY;
    }

    @Override
    public void tintIcon(Image icon) {
        greyIcon(icon, 5f, cooldown());
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    {
        //all harmful blobs
        immunities.add(Blizzard.class);
        immunities.add(ConfusionGas.class);
        immunities.add(CorrosiveGas.class);
        immunities.add(Electricity.class);
        immunities.add(Fire.class);
        immunities.add(Freezing.class);
        immunities.add(Inferno.class);
        immunities.add(ParalyticGas.class);
        immunities.add(Regrowth.class);
        immunities.add(SmokeScreen.class);
        immunities.add(StenchGas.class);
        immunities.add(StormCloud.class);
        immunities.add(ToxicGas.class);
        immunities.add(Web.class);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }
}
