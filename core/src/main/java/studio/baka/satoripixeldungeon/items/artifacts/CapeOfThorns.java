package studio.baka.satoripixeldungeon.items.artifacts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class CapeOfThorns extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_CAPE;

        levelCap = 10;

        charge = 0;
        chargeCap = 100;
        cooldown = 0;

        defaultAction = "NONE"; //so it can be quickslotted
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new Thorns();
    }

    @Override
    public void charge(Hero target) {
        if (cooldown == 0) {
            charge += 4;
            updateQuickslot();
        }
        if (charge >= chargeCap) {
            target.buff(Thorns.class).proc(0, null, null);
        }
    }

    @Override
    public String desc() {
        String desc = Messages.get(this, "desc");
        if (isEquipped(Dungeon.hero)) {
            desc += "\n\n";
            if (cooldown == 0)
                desc += Messages.get(this, "desc_inactive");
            else
                desc += Messages.get(this, "desc_active");
        }

        return desc;
    }

    public class Thorns extends ArtifactBuff {

        @Override
        public boolean act() {
            if (cooldown > 0) {
                cooldown--;
                if (cooldown == 0) {
                    BuffIndicator.refreshHero();
                    GLog.w(Messages.get(this, "inert"));
                }
                updateQuickslot();
            }
            spend(TICK);
            return true;
        }

        public int proc(int damage, Char attacker, Char defender) {
            if (cooldown == 0) {
                charge += damage * (0.5 + level() * 0.05);
                if (charge >= chargeCap) {
                    charge = 0;
                    cooldown = 10 + level();
                    GLog.p(Messages.get(this, "radiating"));
                    BuffIndicator.refreshHero();
                }
            }

            if (cooldown != 0) {
                int deflected = Random.NormalIntRange(0, damage);
                damage -= deflected;

                if (attacker != null && Dungeon.level.adjacent(attacker.pos, defender.pos)) {
                    attacker.damage(deflected, this);
                }

                exp += deflected;

                if (exp >= (level() + 1) * 5 && level() < levelCap) {
                    exp -= (level() + 1) * 5;
                    upgrade();
                    GLog.p(Messages.get(this, "levelup"));
                }

            }
            updateQuickslot();
            return damage;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(cooldown));
        }

        @Override
        public int icon() {
            if (cooldown == 0)
                return BuffIndicator.NONE;
            else
                return BuffIndicator.THORNS;
        }

        @Override
        public void detach() {
            cooldown = 0;
            charge = 0;
            super.detach();
        }

    }


}
