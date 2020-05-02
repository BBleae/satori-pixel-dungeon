package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.PrismaticGuard;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.PrismaticImage;
import studio.baka.satoripixeldungeon.effects.Speck;
import com.watabou.noosa.audio.Sample;

public class ScrollOfPrismaticImage extends ExoticScroll {

    {
        initials = 3;
    }

    @Override
    public void doRead() {

        boolean found = false;
        for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (m instanceof PrismaticImage) {
                found = true;
                m.HP = m.HT;
                m.sprite.emitter().burst(Speck.factory(Speck.HEALING), 4);
            }
        }

        if (!found) {
            Buff.affect(curUser, PrismaticGuard.class).set(PrismaticGuard.maxHP(curUser));
        }

        setKnown();

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        readAnimation();
    }
}
