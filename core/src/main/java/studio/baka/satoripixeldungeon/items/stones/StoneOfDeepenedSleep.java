package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.MagicalSleep;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class StoneOfDeepenedSleep extends Runestone {

    {
        image = ItemSpriteSheet.STONE_SLEEP;
    }

    @Override
    protected void activate(int cell) {

        for (int i : PathFinder.NEIGHBOURS9) {

            CellEmitter.get(cell + i).start(Speck.factory(Speck.NOTE), 0.1f, 2);

            if (Actor.findChar(cell + i) != null) {

                Char c = Actor.findChar(cell + i);

                if ((c instanceof Mob && ((Mob) c).state == ((Mob) c).SLEEPING)) {

                    Buff.affect(c, MagicalSleep.class);

                }

            }
        }

        Sample.INSTANCE.play(Assets.SND_LULLABY);

    }
}
