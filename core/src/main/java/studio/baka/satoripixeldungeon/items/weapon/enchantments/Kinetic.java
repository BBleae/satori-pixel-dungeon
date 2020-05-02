package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Kinetic extends Weapon.Enchantment {

    private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF00);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        int conservedDamage = 0;
        if (attacker.buff(ConservedDamage.class) != null) {
            conservedDamage = attacker.buff(ConservedDamage.class).damageBonus();
            attacker.buff(ConservedDamage.class).detach();
        }

        if (damage > defender.HP) {
            int extraDamage = damage - defender.HP;

            Buff.affect(attacker, ConservedDamage.class).setBonus(extraDamage);
        }

        return damage + conservedDamage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return YELLOW;
    }

    public static class ConservedDamage extends Buff {

        @Override
        public int icon() {
            return BuffIndicator.WEAPON;
        }

        @Override
        public void tintIcon(Image icon) {
            if (preservedDamage >= 10) {
                icon.hardlight(1f, 0f, 0f);
            } else if (preservedDamage >= 5) {
                icon.hardlight(1f, 1f - (preservedDamage - 5f) * .2f, 0f);
            } else {
                icon.hardlight(1f, 1f, 1f - preservedDamage * .2f);
            }
        }

        private float preservedDamage;

        public void setBonus(int bonus) {
            preservedDamage = bonus;
        }

        public int damageBonus() {
            return (int) Math.ceil(preservedDamage);
        }

        @Override
        public boolean act() {
            preservedDamage -= Math.max(preservedDamage * .025f, 0.1f);
            if (preservedDamage <= 0) detach();
            else if (preservedDamage <= 10) BuffIndicator.refreshHero();

            spend(TICK);
            return true;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", damageBonus());
        }

        private static final String PRESERVED_DAMAGE = "preserve_damage";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(PRESERVED_DAMAGE, preservedDamage);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (bundle.contains(PRESERVED_DAMAGE)) {
                preservedDamage = bundle.getFloat(PRESERVED_DAMAGE);
            } else {
                preservedDamage = cooldown() / 10;
                spend(cooldown());
            }
        }
    }
}
