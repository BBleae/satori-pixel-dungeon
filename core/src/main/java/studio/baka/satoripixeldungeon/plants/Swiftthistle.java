package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FlavourBuff;
import studio.baka.satoripixeldungeon.actors.buffs.Haste;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Swiftthistle extends Plant {

    {
        image = 2;
    }

    @Override
    public void activate(Char ch) {
        if (ch == Dungeon.hero) {
            Buff.affect(ch, TimeBubble.class).reset();
            if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Haste.class, 1f);
            }
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_SWIFTTHISTLE;

            plantClass = Swiftthistle.class;
        }
    }

    //FIXME lots of copypasta from time freeze here

    public static class TimeBubble extends Buff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        private float left;
        ArrayList<Integer> presses = new ArrayList<>();

        @Override
        public int icon() {
            return BuffIndicator.SLOW;
        }

        @Override
        public void tintIcon(Image icon) {
            FlavourBuff.greyIcon(icon, 5f, left);
        }

        public void reset() {
            left = 7f;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(left));
        }

        public void processTime(float time) {
            left -= time;

            BuffIndicator.refreshHero();

            if (left <= 0) {
                detach();
            }

        }

        public void setDelayedPress(int cell) {
            if (!presses.contains(cell))
                presses.add(cell);
        }

        private void triggerPresses() {
            for (int cell : presses)
                Dungeon.level.pressCell(cell);

            presses = new ArrayList<>();
        }

        @Override
        public boolean attachTo(Char target) {
            if (Dungeon.level != null)
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
                    mob.sprite.add(CharSprite.State.PARALYSED);
            GameScene.freezeEmitters = true;
            return super.attachTo(target);
        }

        @Override
        public void detach() {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
                if (mob.paralysed <= 0) mob.sprite.remove(CharSprite.State.PARALYSED);
            GameScene.freezeEmitters = false;

            super.detach();
            triggerPresses();
            target.next();
        }

        private static final String PRESSES = "presses";
        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            int[] values = new int[presses.size()];
            for (int i = 0; i < values.length; i++)
                values[i] = presses.get(i);
            bundle.put(PRESSES, values);

            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            int[] values = bundle.getIntArray(PRESSES);
            for (int value : values)
                presses.add(value);

            left = bundle.getFloat(LEFT);
        }

    }
}
