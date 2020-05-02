package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Barkskin;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FlavourBuff;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.EarthParticle;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Earthroot extends Plant {

    {
        image = 8;
    }

    @Override
    public void activate(Char ch) {

        if (ch == Dungeon.hero) {
            if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Barkskin.class).set(Dungeon.hero.lvl + 5, 5);
            } else {
                Buff.affect(ch, Armor.class).level(ch.HT);
            }
        }

        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.bottom(pos).start(EarthParticle.FACTORY, 0.05f, 8);
            Camera.main.shake(1, 0.4f);
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_EARTHROOT;

            plantClass = Earthroot.class;

            bones = true;
        }
    }

    public static class Armor extends Buff {

        private static final float STEP = 1f;

        private int pos;
        private int level;

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public boolean attachTo(Char target) {
            pos = target.pos;
            return super.attachTo(target);
        }

        @Override
        public boolean act() {
            if (target.pos != pos) {
                detach();
            }
            spend(STEP);
            return true;
        }

        private static int blocking() {
            return (Dungeon.depth + 5) / 2;
        }

        public int absorb(int damage) {
            int block = Math.min(damage, blocking());
            if (level <= block) {
                detach();
                return damage - block;
            } else {
                level -= block;
                BuffIndicator.refreshHero();
                return damage - block;
            }
        }

        public void level(int value) {
            if (level < value) {
                level = value;
                BuffIndicator.refreshHero();
            }
            pos = target.pos;
        }

        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }

        @Override
        public void tintIcon(Image icon) {
            FlavourBuff.greyIcon(icon, target.HT / 4f, level);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", blocking(), level);
        }

        private static final String POS = "pos";
        private static final String LEVEL = "level";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
            bundle.put(LEVEL, level);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
            level = bundle.getInt(LEVEL);
        }
    }
}
