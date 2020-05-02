package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Foresight;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.effects.SpellSprite;
import com.watabou.noosa.audio.Sample;

public class ScrollOfForesight extends ExoticScroll {

    {
        initials = 2;
    }

    @Override
    public void doRead() {
        SpellSprite.show(curUser, SpellSprite.MAP);
        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        Buff.affect(curUser, Foresight.class, 600f);

        setKnown();

        readAnimation();
    }

}
