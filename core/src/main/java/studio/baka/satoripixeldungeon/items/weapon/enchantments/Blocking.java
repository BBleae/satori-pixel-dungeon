package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FlavourBuff;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Blocking extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0000FF);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        int level = Math.max(0, weapon.level());

        Buff.prolong(attacker, BlockBuff.class, 2 + level / 2).setBlocking(level + 1);

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLUE;
    }

    public static class BlockBuff extends FlavourBuff {

        private int blocking = 0;

        public void setBlocking(int blocking) {
            this.blocking = blocking;
        }

        public int blockingRoll() {
            return Random.NormalIntRange(0, blocking);
        }

        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.tint(0, 0.5f, 1, 0.5f);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", blocking, dispTurns());
        }

        private static final String BLOCKING = "blocking";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BLOCKING, blocking);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            blocking = bundle.getInt(BLOCKING);
        }

    }
}
