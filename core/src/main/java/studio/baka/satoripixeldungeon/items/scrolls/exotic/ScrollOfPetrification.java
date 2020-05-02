package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.Flare;
import com.watabou.noosa.audio.Sample;

public class ScrollOfPetrification extends ExoticScroll {

    {
        initials = 9;
    }

    @Override
    public void doRead() {
        new Flare(5, 32).color(0xFF0000, true).show(curUser.sprite, 2f);
        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                Buff.affect(mob, Paralysis.class, Paralysis.DURATION);
            }
        }

        setKnown();

        readAnimation();
    }
}
