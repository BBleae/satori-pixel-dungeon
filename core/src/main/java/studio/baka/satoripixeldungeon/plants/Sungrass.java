package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FlavourBuff;
import studio.baka.satoripixeldungeon.actors.buffs.Healing;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.particles.ShaftParticle;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Sungrass extends Plant {

    {
        image = 3;
    }

    @Override
    public void activate(Char ch) {

        if (ch == Dungeon.hero) {
            if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Healing.class).setHeal(ch.HT, 0, 1);
            } else {
                Buff.affect(ch, Health.class).boost(ch.HT);
            }
        }

        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3);
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_SUNGRASS;

            plantClass = Sungrass.class;

            bones = true;
        }
    }

    public static class Health extends Buff {

        private static final float STEP = 1f;

        private int pos;
        private float partialHeal;
        private int level;

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public boolean act() {
            if (target.pos != pos) {
                detach();
            }

            //for the hero, full heal takes ~50/93/111/120 turns at levels 1/10/20/30
            partialHeal += (40 + target.HT) / 150f;

            if (partialHeal > 1) {
                target.HP += (int) partialHeal;
                level -= (int) partialHeal;
                partialHeal -= (int) partialHeal;
                target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);

                if (target.HP >= target.HT) {
                    target.HP = target.HT;
                    if (target instanceof Hero) {
                        ((Hero) target).resting = false;
                    }
                }
            }

            if (level <= 0) {
                detach();
            } else {
                BuffIndicator.refreshHero();
            }
            spend(STEP);
            return true;
        }

        public void boost(int amount) {
            level += amount;
            pos = target.pos;
        }

        @Override
        public int icon() {
            return BuffIndicator.HERB_HEALING;
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
            return Messages.get(this, "desc", level);
        }

        private static final String POS = "pos";
        private static final String PARTIAL = "partial_heal";
        private static final String LEVEL = "level";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
            bundle.put(PARTIAL, partialHeal);
            bundle.put(LEVEL, level);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
            partialHeal = bundle.getFloat(PARTIAL);
            level = bundle.getInt(LEVEL);

        }
    }
}
