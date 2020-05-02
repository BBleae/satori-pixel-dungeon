package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.Vertigo;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.Speck;
import com.watabou.noosa.audio.Sample;

public class ScrollOfConfusion extends ExoticScroll {

    {
        initials = 5;
    }

    @Override
    public void doRead() {
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                Buff.prolong(mob, Vertigo.class, 10f);
                Buff.prolong(mob, Blindness.class, 10f);
            }
        }

        setKnown();

        curUser.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        readAnimation();
    }

}
