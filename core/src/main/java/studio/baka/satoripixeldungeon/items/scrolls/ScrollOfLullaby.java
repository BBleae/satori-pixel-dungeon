package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Drowsy;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfLullaby extends Scroll {

    {
        initials = 1;
    }

    @Override
    public void doRead() {

        curUser.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
        Sample.INSTANCE.play(Assets.SND_LULLABY);
        Invisibility.dispel();

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                Buff.affect(mob, Drowsy.class);
                mob.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
            }
        }

        Buff.affect(curUser, Drowsy.class);

        GLog.i(Messages.get(this, "sooth"));

        setKnown();

        readAnimation();
    }

    @Override
    public void empoweredRead() {
        doRead();
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                Buff drowsy = mob.buff(Drowsy.class);
                if (drowsy != null) drowsy.act();
            }
        }
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
