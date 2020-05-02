package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.particles.SnowParticle;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class FrostImbue extends FlavourBuff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    public static final float DURATION = 50f;

    public void proc(Char enemy) {
        Buff.affect(enemy, Chill.class, 2f);
        enemy.sprite.emitter().burst(SnowParticle.FACTORY, 2);
    }

    @Override
    public int icon() {
        return BuffIndicator.FROST;
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

    {
        immunities.add(Frost.class);
        immunities.add(Chill.class);
    }
}
