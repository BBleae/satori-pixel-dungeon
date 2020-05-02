package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.artifacts.Artifact;
import studio.baka.satoripixeldungeon.items.artifacts.HornOfPlenty;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Hunger extends Buff implements Hero.Doom {

    private static final float STEP = 10f;

    public static final float HUNGRY = 300f;
    public static final float STARVING = 450f;

    private float level;
    private float partialDamage;

    private static final String LEVEL = "level";
    private static final String PARTIALDAMAGE = "partialDamage";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
        bundle.put(PARTIALDAMAGE, partialDamage);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getFloat(LEVEL);
        partialDamage = bundle.getFloat(PARTIALDAMAGE);
    }

    @Override
    public boolean act() {

        if (Dungeon.level.locked || target.buff(WellFed.class) != null) {
            spend(STEP);
            return true;
        }

        if (target.isAlive() && target instanceof Hero) {

            Hero hero = (Hero) target;

            if (isStarving()) {

                partialDamage += STEP * target.HT / 1000f;

                if (partialDamage > 1) {
                    target.damage((int) partialDamage, this);
                    partialDamage -= (int) partialDamage;
                }

            } else {

                float newLevel = level + STEP;
                boolean statusUpdated = false;
                if (newLevel >= STARVING) {

                    GLog.n(Messages.get(this, "onstarving"));
                    hero.resting = false;
                    hero.damage(1, this);
                    statusUpdated = true;

                    hero.interrupt();

                } else if (newLevel >= HUNGRY && level < HUNGRY) {

                    GLog.w(Messages.get(this, "onhungry"));
                    statusUpdated = true;

                }
                level = newLevel;

                if (statusUpdated) {
                    BuffIndicator.refreshHero();
                }

            }

            spend(target.buff(Shadows.class) == null ? STEP : STEP * 1.5f);
            hero.setHunger(level);

        } else {

            deactivate();

        }

        return true;
    }

    public void satisfy(float energy) {

        Artifact.ArtifactBuff buff = target.buff(HornOfPlenty.hornRecharge.class);
        if (buff != null && buff.isCursed()) {
            energy *= 0.67f;
            GLog.n(Messages.get(this, "cursedhorn"));
        }

        reduceHunger(energy);
    }

    //directly interacts with hunger, no checks.
    public void reduceHunger(float energy) {

        level -= energy;
        if (level < 0) {
            level = 0;
        } else if (level > STARVING) {
            float excess = level - STARVING;
            level = STARVING;
            partialDamage += excess * (target.HT / 1000f);
        }

        BuffIndicator.refreshHero();
    }

    public boolean isStarving() {
        return level >= STARVING;
    }

    public int hunger() {
        return (int) Math.ceil(level);
    }

    @Override
    public int icon() {
        if (level < HUNGRY) {
            return BuffIndicator.NONE;
        } else if (level < STARVING) {
            return BuffIndicator.HUNGER;
        } else {
            return BuffIndicator.STARVATION;
        }
    }

    @Override
    public String toString() {
        if (level < STARVING) {
            return Messages.get(this, "hungry");
        } else {
            return Messages.get(this, "starving");
        }
    }

    @Override
    public String desc() {
        String result;
        if (level < STARVING) {
            result = Messages.get(this, "desc_intro_hungry");
        } else {
            result = Messages.get(this, "desc_intro_starving");
        }

        result += Messages.get(this, "desc");

        return result;
    }

    @Override
    public void onDeath() {

        Badges.validateDeathFromHunger();

        Dungeon.fail(getClass());
        GLog.n(Messages.get(this, "ondeath"));
    }
}
