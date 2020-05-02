package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfInvisibility extends Potion {

    {
        initials = 4;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();
        Buff.affect(hero, Invisibility.class, Invisibility.DURATION);
        GLog.i(Messages.get(this, "invisible"));
        Sample.INSTANCE.play(Assets.SND_MELD);
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }

}
