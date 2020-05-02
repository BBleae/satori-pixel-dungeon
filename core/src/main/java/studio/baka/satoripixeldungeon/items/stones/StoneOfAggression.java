package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FlavourBuff;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class StoneOfAggression extends Runestone {

    {
        image = ItemSpriteSheet.STONE_AGGRESSION;
    }

    @Override
    protected void activate(int cell) {

        Char ch = Actor.findChar(cell);

        if (ch != null) {
            if (ch.alignment == Char.Alignment.ENEMY) {
                Buff.prolong(ch, Aggression.class, Aggression.DURATION / 5f);
            } else {
                Buff.prolong(ch, Aggression.class, Aggression.DURATION);
            }
            CellEmitter.center(cell).start(Speck.factory(Speck.SCREAM), 0.3f, 3);
            Sample.INSTANCE.play(Assets.SND_READ);
        } else {
            //Item.onThrow
            Heap heap = Dungeon.level.drop(this, cell);
            if (!heap.isEmpty()) {
                heap.sprite.drop(cell);
            }
        }

    }

    public static class Aggression extends FlavourBuff {

        public static final float DURATION = 20f;

        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
        }

        @Override
        public void detach() {
            //if our target is an enemy, reset the aggro of any enemies targeting it
            if (target.isAlive()) {
                if (target.alignment == Char.Alignment.ENEMY) {
                    for (Mob m : Dungeon.level.mobs) {
                        if (m.alignment == Char.Alignment.ENEMY && m.isTargeting(target)) {
                            m.aggro(null);
                        }
                    }
                }
            }
            super.detach();

        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

    }

}
