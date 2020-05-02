package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.actors.blobs.Freezing;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Roots;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class PotionOfSnapFreeze extends ExoticPotion {

    {
        initials = 1;
    }

    @Override
    public void shatter(int cell) {

        if (Dungeon.level.heroFOV[cell]) {
            setKnown();

            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);

        for (int offset : PathFinder.NEIGHBOURS9) {
            if (!Dungeon.level.solid[cell + offset]) {

                Freezing.affect(cell + offset, fire);

                Char ch = Actor.findChar(cell + offset);
                if (ch != null) {
                    Buff.affect(ch, Roots.class, 10f);
                }

            }
        }
    }
}
