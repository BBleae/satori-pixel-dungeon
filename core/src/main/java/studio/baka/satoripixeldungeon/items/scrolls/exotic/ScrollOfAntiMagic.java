package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.MagicImmune;
import studio.baka.satoripixeldungeon.effects.Flare;

public class ScrollOfAntiMagic extends ExoticScroll {

    {
        initials = 7;
    }

    @Override
    public void doRead() {

        Invisibility.dispel();

        Buff.affect(curUser, MagicImmune.class, 20f);
        new Flare(5, 32).color(0xFF0000, true).show(curUser.sprite, 2f);

        setKnown();

        readAnimation();
    }
}
