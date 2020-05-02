package studio.baka.satoripixeldungeon.items.artifacts;


import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.LockedFloor;
import studio.baka.satoripixeldungeon.actors.buffs.Preparation;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class CloakOfShadows extends Artifact {

    private static final int MANA_NEED = 10;

    {
        image = ItemSpriteSheet.ARTIFACT_CLOAK;

        exp = 0;
        levelCap = 10;

        charge = Math.min(level() + 3, 10);
        partialCharge = 0;
        chargeCap = Math.min(level() + 3, 10);

        defaultAction = AC_STEALTH;

        unique = true;
        bones = false;
    }

    private boolean stealthed = false;

    public static final String AC_STEALTH = "STEALTH";
    public static final String AC_CHARGE = "charge";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && !cursed && (charge > 0 || stealthed))
            actions.add(AC_STEALTH);
        if (isEquipped(hero) && !cursed && (charge < chargeCap))
            actions.add(AC_CHARGE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_STEALTH)) {

            if (!stealthed) {
                if (!isEquipped(hero)) GLog.i(Messages.get(Artifact.class, "need_to_equip"));
                else if (cursed) GLog.i(Messages.get(this, "cursed"));
                else if (charge <= 0) GLog.i(Messages.get(this, "no_charge"));
                else {
                    stealthed = true;
                    hero.spend(1f);
                    hero.busy();
                    Sample.INSTANCE.play(Assets.SND_MELD);
                    activeBuff = activeBuff();
                    activeBuff.attachTo(hero);
                    if (hero.sprite.parent != null) {
                        hero.sprite.parent.add(new AlphaTweener(hero.sprite, 0.4f, 0.4f));
                    } else {
                        hero.sprite.alpha(0.4f);
                    }
                    hero.sprite.operate(hero.pos);
                }
            } else {
                stealthed = false;
                activeBuff.detach();
                activeBuff = null;
                hero.spend(1f);
                hero.sprite.operate(hero.pos);
            }
        }

        if (action.equals(AC_CHARGE)) {
            if (!stealthed) {
                if (!isEquipped(hero)) GLog.i(Messages.get(Artifact.class, "need_to_equip"));
                else if (cursed) GLog.i(Messages.get(this, "cursed"));
                else if (charge >= chargeCap) GLog.i(Messages.get(this, "no_more_charge"));
                else {
                    if (hero.mana >= MANA_NEED) {
                        hero.spend(1f);
                        hero.busy();
                        charge++;
                        hero.sprite.operate(hero.pos);
                        hero.mana -= MANA_NEED;
                    } else {
                        GLog.i(Messages.get(this, "not_enough_mana"), hero.mana, hero.getMaxmana(), MANA_NEED);
                    }
                }
            } else {
                GLog.i(Messages.get(this, "no_chargable"));
            }
        }
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        if (stealthed) {
            activeBuff = activeBuff();
            activeBuff.attachTo(ch);
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            stealthed = false;
            return true;
        } else
            return false;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new cloakRecharge();
    }

    @Override
    protected ArtifactBuff activeBuff() {
        return new cloakStealth();
    }

    @Override
    public void charge(Hero target) {
        if (charge < chargeCap) {
            partialCharge += 0.25f;
            if (partialCharge >= 1) {
                partialCharge--;
                charge++;
                updateQuickslot();
            }
        }
    }

    @Override
    public Item upgrade() {
        chargeCap = Math.min(chargeCap + 1, 10);
        return super.upgrade();
    }

    private static final String STEALTHED = "stealthed";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STEALTHED, stealthed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        stealthed = bundle.getBoolean(STEALTHED);
    }

    @Override
    public int price() {
        return 0;
    }

    public class cloakRecharge extends ArtifactBuff {
        @Override
        public boolean act() {
            if (charge < chargeCap) {
                LockedFloor lock = target.buff(LockedFloor.class);
                if (!stealthed && (lock == null || lock.regenOn())) {
                    float missing = (chargeCap - charge);
                    if (level() > 7) missing += 5 * (level() - 7) / 3f;
                    float turnsToCharge = (45 - missing);
                    partialCharge += (1f / turnsToCharge);
                }

                if (partialCharge >= 1) {
                    charge++;
                    partialCharge -= 1;
                    if (charge == chargeCap) {
                        partialCharge = 0;
                    }

                }
            } else
                partialCharge = 0;

            if (cooldown > 0)
                cooldown--;

            updateQuickslot();

            spend(TICK);

            return true;
        }

    }

    public class cloakStealth extends ArtifactBuff {

        {
            type = buffType.POSITIVE;
        }

        int turnsToCost = 0;

        @Override
        public int icon() {
            return BuffIndicator.INVISIBLE;
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                target.invisible++;
                if (target instanceof Hero && ((Hero) target).subClass == HeroSubClass.ASSASSIN) {
                    Buff.affect(target, Preparation.class);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean act() {
            turnsToCost--;

            if (turnsToCost <= 0) {
                charge--;
                if (charge < 0) {
                    charge = 0;
                    detach();
                    GLog.w(Messages.get(this, "no_charge"));
                    ((Hero) target).interrupt();
                } else {
                    //target hero level is 1 + 2*cloak level
                    int lvlDiffFromTarget = ((Hero) target).lvl - (1 + level() * 2);
                    //plus an extra one for each level after 6
                    if (level() >= 7) {
                        lvlDiffFromTarget -= level() - 6;
                    }
                    if (lvlDiffFromTarget >= 0) {
                        exp += Math.round(10f * Math.pow(1.1f, lvlDiffFromTarget));
                    } else {
                        exp += Math.round(10f * Math.pow(0.75f, -lvlDiffFromTarget));
                    }

                    if (exp >= (level() + 1) * 50 && level() < levelCap) {
                        upgrade();
                        exp -= level() * 50;
                        GLog.p(Messages.get(this, "levelup"));

                    }
                    turnsToCost = 5;
                }
                updateQuickslot();
            }

            spend(TICK);

            return true;
        }

        public void dispel() {
            updateQuickslot();
            detach();
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.INVISIBLE);
            else if (target.invisible == 0) target.sprite.remove(CharSprite.State.INVISIBLE);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }

        @Override
        public void detach() {
            if (target.invisible > 0)
                target.invisible--;
            stealthed = false;

            updateQuickslot();
            super.detach();
        }

        private static final String TURNSTOCOST = "turnsToCost";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            bundle.put(TURNSTOCOST, turnsToCost);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            turnsToCost = bundle.getInt(TURNSTOCOST);
        }
    }
}
