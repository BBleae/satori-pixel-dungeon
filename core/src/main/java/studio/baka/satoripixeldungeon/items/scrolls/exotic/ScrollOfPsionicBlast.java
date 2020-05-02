package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.Weakness;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfPsionicBlast extends ExoticScroll {

    {
        initials = 4;
    }

    @Override
    public void doRead() {

        GameScene.flash(0xFFFFFF);

        Sample.INSTANCE.play(Assets.SND_BLAST);
        Invisibility.dispel();

        int targets = 0;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                targets++;
                mob.damage(Math.round(mob.HT / 2f + mob.HP / 2f), this);
                if (mob.isAlive()) {
                    Buff.prolong(mob, Blindness.class, 10);
                }
            }
        }

        curUser.damage(Math.max(0, Math.round(curUser.HT * (0.5f * (float) Math.pow(0.9, targets)))), this);
        if (curUser.isAlive()) {
            Buff.prolong(curUser, Blindness.class, 10);
            Buff.prolong(curUser, Weakness.class, 100);
            Dungeon.observe();
            readAnimation();
        } else {
            Dungeon.fail(getClass());
            GLog.n(Messages.get(this, "ondeath"));
        }

        setKnown();


    }
}
