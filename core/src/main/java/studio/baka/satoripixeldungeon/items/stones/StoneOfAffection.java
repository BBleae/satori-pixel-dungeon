package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Charm;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class StoneOfAffection extends Runestone {

    {
        image = ItemSpriteSheet.STONE_AFFECTION;
    }

    @Override
    protected void activate(int cell) {

        for (int i : PathFinder.NEIGHBOURS9) {

            CellEmitter.center(cell + i).start(Speck.factory(Speck.HEART), 0.2f, 5);


            Char ch = Actor.findChar(cell + i);

            if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                Buff.prolong(ch, Charm.class, 10f).object = curUser.id();
            }
        }

        Sample.INSTANCE.play(Assets.SND_CHARMS);

    }

}
