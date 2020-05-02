package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.ArtifactRecharge;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.effects.SpellSprite;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRecharging;
import com.watabou.noosa.audio.Sample;

public class ScrollOfMysticalEnergy extends ExoticScroll {

    {
        initials = 6;
    }

    @Override
    public void doRead() {

        //append buff
        Buff.affect(curUser, ArtifactRecharge.class).set(30);

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        SpellSprite.show(curUser, SpellSprite.CHARGE);
        setKnown();
        ScrollOfRecharging.charge(curUser);

        readAnimation();
    }

}
