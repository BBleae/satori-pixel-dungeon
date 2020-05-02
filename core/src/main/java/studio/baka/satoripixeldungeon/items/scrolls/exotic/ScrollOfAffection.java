

package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Charm;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.Speck;
import com.watabou.noosa.audio.Sample;

public class ScrollOfAffection extends ExoticScroll {

    {
        initials = 1;
    }

    @Override
    public void doRead() {

        curUser.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
        Sample.INSTANCE.play(Assets.SND_CHARMS);
        Invisibility.dispel();

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                Buff.affect(mob, Charm.class, 20f).object = curUser.id();
                mob.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
            }
        }

        //GLog.i( Messages.get(this, "sooth") );

        setKnown();

        readAnimation();

    }

}
