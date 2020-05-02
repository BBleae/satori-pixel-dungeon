package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.items.artifacts.ChaliceOfBlood;

public class Regeneration extends Buff {

    {
        //unlike other buffs, this one acts after the hero and takes priority against other effects
        //healing is much more useful if you get some of it off before taking damage
        actPriority = HERO_PRIORITY - 1;
    }

    private static final float REGENERATION_DELAY = 10;

    @Override
    public boolean act() {
        if (target.isAlive()) {

            if (target.HP < regencap() && !((Hero) target).isStarving()) {
                LockedFloor lock = target.buff(LockedFloor.class);
                if (target.HP > 0 && (lock == null || lock.regenOn())) {
                    target.HP += 1;
                    if (target.HP == regencap()) {
                        ((Hero) target).resting = false;
                    }
                }
            }

            spend(getregendelay());

        } else {

            deactivate();

        }

        return true;
    }

    public float getregendelay() {
        ChaliceOfBlood.chaliceRegen regenBuff = Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class);
        float regenfactor = 1f;
        float REGENERATION_DELAY_FIX;
        if (target == Dungeon.hero && Dungeon.hero.heroClass == HeroClass.MAHOU_SHOUJO) {
            REGENERATION_DELAY_FIX = 8;
            regenfactor = 2f;
        } else {
            REGENERATION_DELAY_FIX = REGENERATION_DELAY;
        }

        if (regenBuff != null)
            if (regenBuff.isCursed())
                return (REGENERATION_DELAY_FIX * 1.5f * regenfactor);
            else
                return (REGENERATION_DELAY_FIX - regenBuff.itemLevel() * 0.9f / regenfactor);
        else
            return REGENERATION_DELAY_FIX;
    }

    public int regencap() {
        return target.HT;
    }
}
