package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.SpellSprite;
import studio.baka.satoripixeldungeon.items.BrokenSeal.WarriorShield;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Berserk extends Buff {

    private enum State {
        NORMAL, BERSERK, RECOVERING
    }

    private State state = State.NORMAL;

    private static final float LEVEL_RECOVER_START = 2f;
    private float levelRecovery;

    private int lasttime = 0;

    private static final String STATE = "state";
    private static final String LEVEL_RECOVERY = "levelrecovery";
    private static final String LAST_TIME = "lasttime";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STATE, state);
        bundle.put(LAST_TIME, lasttime);
        if (state == State.RECOVERING) bundle.put(LEVEL_RECOVERY, levelRecovery);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        //pre-0.6.5 saves
        if (bundle.contains("exhaustion")) {
            state = State.RECOVERING;
        } else {
            state = bundle.getEnum(STATE, State.class);
        }
        if (bundle.contains(LAST_TIME)) {
            lasttime = bundle.getInt(LAST_TIME);
        } else {
            lasttime = 1;
        }
        if (state == State.RECOVERING) levelRecovery = bundle.getFloat(LEVEL_RECOVERY);
    }

    @Override
    public boolean act() {
        if (berserking()) {
            ShieldBuff buff = target.buff(WarriorShield.class);
            if (target.HP <= 0) {
                if (buff != null && buff.shielding() > 0) {
                    buff.absorbDamage(1 + (int) Math.ceil(target.shielding() * 0.1f));
                } else {
                    //if there is no shield buff, or it is empty, then try to remove from other shielding buffs
                    buff = target.buff(ShieldBuff.class);
                    if (buff != null) buff.absorbDamage(1 + (int) Math.ceil(target.shielding() * 0.1f));
                }
                if (target.shielding() <= 0) {
                    target.die(this);
                    if (!target.isAlive()) Dungeon.fail(this.getClass());
                }
            } else {
                state = State.RECOVERING;
                levelRecovery = LEVEL_RECOVER_START;
                BuffIndicator.refreshHero();
                if (buff != null) buff.absorbDamage(buff.shielding());
            }
        } else if (state == State.NORMAL) {
            lasttime -= 1;
            if (lasttime <= 0) {
                detach();
            }
            BuffIndicator.refreshHero();
        }
        spend(TICK);
        return true;
    }

    public int damageFactor(int dmg) {
		/*
		float bonus = Math.min(1.5f, 1f + (power / 2f));
		return Math.round(dmg * bonus);
		 */
        return dmg;            //no more bonus now
    }

    public boolean berserking() {
        if (target.HP == 0 && state == State.NORMAL) {

            WarriorShield shield = target.buff(WarriorShield.class);
            if (shield != null) {
                state = State.BERSERK;
                BuffIndicator.refreshHero();
                shield.supercharge(shield.maxShield() * 10);

                SpellSprite.show(target, SpellSprite.BERSERK);
                Sample.INSTANCE.play(Assets.SND_CHALLENGE);
                GameScene.flash(0xFF0000);
            }

        }

        return state == State.BERSERK && target.shielding() > 0;
    }

    public void damage() {
        if (state == State.RECOVERING) return;
        lasttime += 2;
        BuffIndicator.refreshHero();
    }

    public void recover(float percent) {
        if (levelRecovery > 0) {
            levelRecovery -= percent;
            BuffIndicator.refreshHero();
            if (levelRecovery <= 0) {
                state = State.NORMAL;
                levelRecovery = 0;
            }
        }
    }

    @Override
    public int icon() {
        //return BuffIndicator.BERSERK;
        if (levelRecovery > 0 || state == State.BERSERK) return BuffIndicator.BERSERK;
        return BuffIndicator.NONE;
    }

    @Override
    public void tintIcon(Image icon) {
        if (state == State.RECOVERING)
            icon.hardlight(1f - (levelRecovery * 0.5f), 1f - (levelRecovery * 0.3f), 1f);
    }

    @Override
    public String toString() {
        switch (state) {
            case NORMAL:
            default:
                return Messages.get(this, "angered");
            case BERSERK:
                return Messages.get(this, "berserk");
            case RECOVERING:
                return Messages.get(this, "recovering");
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "recovering_desc", levelRecovery);
    }
}
