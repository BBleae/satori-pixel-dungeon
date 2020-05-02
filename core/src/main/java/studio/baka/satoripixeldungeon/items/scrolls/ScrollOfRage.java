package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Amok;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.mobs.Mimic;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfRage extends Scroll {

    {
        initials = 5;
    }

    @Override
    public void doRead() {

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            mob.beckon(curUser.pos);
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                Buff.prolong(mob, Amok.class, 5f);
            }
        }

        for (Heap heap : Dungeon.level.heaps.valueList()) {
            if (heap.type == Heap.Type.MIMIC) {
                Mimic m = Mimic.spawnAt(heap.pos, heap.items);
                if (m != null) {
                    m.beckon(curUser.pos);
                    heap.destroy();
                }
            }
        }

        GLog.w(Messages.get(this, "roar"));
        setKnown();

        curUser.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
        Sample.INSTANCE.play(Assets.SND_CHALLENGE);
        Invisibility.dispel();

        readAnimation();
    }

    @Override
    public void empoweredRead() {
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                Buff.prolong(mob, Amok.class, 5f);
            }
        }

        setKnown();

        curUser.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        readAnimation();
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
