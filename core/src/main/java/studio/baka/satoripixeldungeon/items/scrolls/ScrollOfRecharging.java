package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.Recharging;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.SpellSprite;
import studio.baka.satoripixeldungeon.effects.particles.EnergyParticle;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfRecharging extends Scroll {

    public static final float BUFF_DURATION = 30f;

    {
        initials = 6;
    }

    @Override
    public void doRead() {

        Buff.affect(curUser, Recharging.class, BUFF_DURATION);
        charge(curUser);

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        GLog.i(Messages.get(this, "surge"));
        SpellSprite.show(curUser, SpellSprite.CHARGE);
        setKnown();

        readAnimation();
    }

    @Override
    public void empoweredRead() {
        doRead();
        Buff.append(curUser, Recharging.class, BUFF_DURATION / 3f);
    }

    public static void charge(Hero hero) {
        hero.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 15);
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
