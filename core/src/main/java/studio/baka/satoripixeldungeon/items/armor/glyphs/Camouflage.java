package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Camouflage extends Armor.Glyph {

    private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x448822);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        //no proc effect, see HighGrass.trample
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GREEN;
    }

    public static class Camo extends Invisibility {

        {
            announced = false;
        }

        private int pos;
        private int left;

        @Override
        public boolean act() {
            left--;
            if (left == 0 || target.pos != pos) {
                detach();
            } else {
                spend(TICK);
            }
            return true;
        }

        public void set(int time) {
            left = time;
            pos = target.pos;
            Sample.INSTANCE.play(Assets.SND_MELD);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(left));
        }

        private static final String POS = "pos";
        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
            left = bundle.getInt(LEFT);
        }
    }

}

