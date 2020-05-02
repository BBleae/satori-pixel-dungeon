package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Paralysis extends FlavourBuff {

    public static final float DURATION = 10f;

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.paralysed++;
            return true;
        } else {
            return false;
        }
    }

    public void processDamage(int damage) {
        if (target == null) return;
        ParalysisResist resist = target.buff(ParalysisResist.class);
        if (resist == null) {
            resist = Buff.affect(target, ParalysisResist.class);
        }
        resist.damage += damage;
        if (Random.NormalIntRange(0, resist.damage) >= Random.NormalIntRange(0, target.HP)) {
            if (Dungeon.level.heroFOV[target.pos]) {
                target.sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "out"));
            }
            detach();
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (target.paralysed > 0)
            target.paralysed--;
    }

    @Override
    public int icon() {
        return BuffIndicator.PARALYSIS;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.PARALYSED);
        else target.sprite.remove(CharSprite.State.PARALYSED);
    }

    @Override
    public String heroMessage() {
        return Messages.get(this, "heromsg");
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    public static float duration(Char ch) {
        return DURATION;
    }

    public static class ParalysisResist extends Buff {

        {
            type = buffType.POSITIVE;
        }

        private int damage;

        @Override
        public boolean act() {
            if (target.buff(Paralysis.class) == null) {
                damage -= Math.ceil(damage / 10f);
                if (damage >= 0) detach();
            }
            spend(TICK);
            return true;
        }

        private static final String DAMAGE = "damage";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            damage = bundle.getInt(DAMAGE);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            bundle.put(DAMAGE, damage);
        }
    }
}
