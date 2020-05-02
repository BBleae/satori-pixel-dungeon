package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.Flare;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfTerror extends Scroll {

    {
        initials = 9;
    }

    @Override
    public void doRead() {

        new Flare(5, 32).color(0xFF0000, true).show(curUser.sprite, 2f);
        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        int count = 0;
        Mob affected = null;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                Buff.affect(mob, Terror.class, 20f).object = curUser.id();

                if (mob.buff(Terror.class) != null) {
                    count++;
                    affected = mob;
                }
            }
        }

        switch (count) {
            case 0:
                GLog.i(Messages.get(this, "none"));
                break;
            case 1:
                GLog.i(Messages.get(this, "one", affected.name));
                break;
            default:
                GLog.i(Messages.get(this, "many"));
        }
        setKnown();

        readAnimation();
    }

    @Override
    public void empoweredRead() {
        doRead();
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                Terror t = mob.buff(Terror.class);
                if (t != null) {
                    Buff.prolong(mob, Terror.class, 15f);
                    Buff.affect(mob, Paralysis.class, 5f);
                }
            }
        }
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
