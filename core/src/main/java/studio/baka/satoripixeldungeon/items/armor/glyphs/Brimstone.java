package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.ShieldBuff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundle;

public class Brimstone extends Armor.Glyph {

    private static final ItemSprite.Glowing ORANGE = new ItemSprite.Glowing(0xFF4400);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        //no proc effect, see Hero.isImmune and GhostHero.isImmune
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return ORANGE;
    }

    //pre-0.7.4 saves
    public static class BrimstoneShield extends ShieldBuff {

        {
            type = buffType.POSITIVE;
        }

        @Override
        public boolean act() {
            Hero hero = (Hero) target;

            if (hero.belongings.armor == null || !hero.belongings.armor.hasGlyph(Brimstone.class, hero)) {
                detach();
                return true;
            }

            if (shielding() > 0) {
                decShield();

                //shield decays at a rate of 1 per turn.
                spend(TICK);
            } else {
                detach();
            }

            return true;
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            //pre-0.7.0
            if (bundle.contains("added")) {
                setShield(bundle.getInt("added"));
            }
        }
    }

}
