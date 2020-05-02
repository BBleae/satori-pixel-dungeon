package studio.baka.satoripixeldungeon.items.artifacts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class MasterThievesArmband extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_ARMBAND;

        levelCap = 10;

        charge = 0;
    }

    private int exp = 0;

    @Override
    protected ArtifactBuff passiveBuff() {
        return new Thievery();
    }

    @Override
    public void charge(Hero target) {
        if (charge < chargeCap) {
            charge += 10;
            updateQuickslot();
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (cursed) {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            } else {
                desc += "\n\n" + Messages.get(this, "desc_worn");
            }
        }


        return desc;
    }

    public class Thievery extends ArtifactBuff {
        public void collect(int gold) {
            if (!cursed) {
                charge += gold / 2;
            }
        }

        @Override
        public void detach() {
            charge *= 0.95;
            super.detach();
        }

        @Override
        public boolean act() {
            if (cursed) {

                if (Dungeon.gold > 0 && Random.Int(6) == 0) {
                    Dungeon.gold--;
                }

                spend(TICK);
                return true;
            } else {
                return super.act();
            }
        }

        public boolean steal(int value) {
            if (value <= charge) {
                charge -= value;
                exp += value;
            } else {
                float chance = stealChance(value);
                if (Random.Float() > chance)
                    return false;
                else {
                    if (chance <= 1)
                        charge = 0;
                    else
                        //removes the charge it took you to reach 100%
                        charge -= charge / chance;
                    exp += value;
                }
            }
            while (exp >= (250 + 50 * level()) && level() < levelCap) {
                exp -= (250 + 50 * level());
                upgrade();
            }
            return true;
        }

        public float stealChance(int value) {
            //get lvl*50 gold or lvl*3.33% item value of free charge, whichever is less.
            int chargeBonus = Math.min(level() * 50, (value * level()) / 30);
            return (((float) charge + chargeBonus) / value);
        }
    }
}
