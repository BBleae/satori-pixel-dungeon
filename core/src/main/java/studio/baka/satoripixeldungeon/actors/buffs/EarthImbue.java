package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.EarthParticle;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

//pre-0.7.0, otherwise unused
public class EarthImbue extends FlavourBuff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    public static final float DURATION = 30f;

    public void proc(Char enemy) {
        Buff.affect(enemy, Cripple.class, 2);
        CellEmitter.bottom(enemy.pos).start(EarthParticle.FACTORY, 0.05f, 8);
    }

    @Override
    public int icon() {
        return BuffIndicator.ROOTS;
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