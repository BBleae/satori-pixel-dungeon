package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.actors.mobs.*;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashMap;

//TODO need to consider other balance adjustments here. Might want to put more emphasis into debuffs rather than less
@SuppressWarnings("unchecked")
public class WandOfCorruption extends Wand {

    {
        image = ItemSpriteSheet.WAND_CORRUPTION;
    }

    //Note that some debuffs here have a 0% chance to be applied.
    // This is because the wand of corruption considers them to be a certain level of harmful
    // for the purposes of reducing resistance, but does not actually apply them itself

    private static final float MINOR_DEBUFF_WEAKEN = 7 / 8f;
    private static final HashMap<Class<? extends Buff>, Float> MINOR_DEBUFFS = new HashMap<>();

    static {
        MINOR_DEBUFFS.put(Weakness.class, 2f);
        MINOR_DEBUFFS.put(Cripple.class, 1f);
        MINOR_DEBUFFS.put(Blindness.class, 1f);
        MINOR_DEBUFFS.put(Terror.class, 1f);

        MINOR_DEBUFFS.put(Chill.class, 0f);
        MINOR_DEBUFFS.put(Ooze.class, 0f);
        MINOR_DEBUFFS.put(Roots.class, 0f);
        MINOR_DEBUFFS.put(Vertigo.class, 0f);
        MINOR_DEBUFFS.put(Drowsy.class, 0f);
        MINOR_DEBUFFS.put(Bleeding.class, 0f);
        MINOR_DEBUFFS.put(Burning.class, 0f);
        MINOR_DEBUFFS.put(Poison.class, 0f);
    }

    private static final float MAJOR_DEBUFF_WEAKEN = 4 / 5f;
    private static final HashMap<Class<? extends Buff>, Float> MAJOR_DEBUFFS = new HashMap<>();

    static {
        MAJOR_DEBUFFS.put(Amok.class, 3f);
        MAJOR_DEBUFFS.put(Slow.class, 2f);
        MAJOR_DEBUFFS.put(Paralysis.class, 1f);

        MAJOR_DEBUFFS.put(Charm.class, 0f);
        MAJOR_DEBUFFS.put(MagicalSleep.class, 0f);
        MAJOR_DEBUFFS.put(SoulMark.class, 0f);
        MAJOR_DEBUFFS.put(Corrosion.class, 0f);
        MAJOR_DEBUFFS.put(Frost.class, 0f);
        MAJOR_DEBUFFS.put(Doom.class, 0f);
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Char ch = Actor.findChar(bolt.collisionPos);

        if (ch != null) {

            if (!(ch instanceof Mob)) {
                return;
            }

            Mob enemy = (Mob) ch;

            float corruptingPower = 3 + level() / 2;

            //base enemy resistance is usually based on their exp, but in special cases it is based on other criteria
            float enemyResist = 1 + enemy.EXP;
            if (ch instanceof Mimic || ch instanceof Statue) {
                enemyResist = 1 + Dungeon.depth;
            } else if (ch instanceof Piranha || ch instanceof Bee) {
                enemyResist = 1 + Dungeon.depth / 2f;
            } else if (ch instanceof Wraith) {
                //divide by 3 as wraiths are always at full HP and are therefore ~3x harder to corrupt
                enemyResist = (1f + Dungeon.depth / 3f) / 3f;
            } else if (ch instanceof Yog.BurningFist || ch instanceof Yog.RottingFist) {
                enemyResist = 1 + 30;
            } else if (ch instanceof Yog.Larva || ch instanceof King.Undead) {
                enemyResist = 1 + 5;
            } else if (ch instanceof Swarm) {
                //child swarms don't give exp, so we force this here.
                enemyResist = 1 + 3;
            }

            //100% health: 3x resist   75%: 2.1x resist   50%: 1.5x resist   25%: 1.1x resist
            enemyResist *= 1 + 2 * Math.pow(enemy.HP / (float) enemy.HT, 2);

            //debuffs placed on the enemy reduce their resistance
            for (Buff buff : enemy.buffs()) {
                if (MAJOR_DEBUFFS.containsKey(buff.getClass())) enemyResist *= MAJOR_DEBUFF_WEAKEN;
                else if (MINOR_DEBUFFS.containsKey(buff.getClass())) enemyResist *= MINOR_DEBUFF_WEAKEN;
                else if (buff.type == Buff.buffType.NEGATIVE) enemyResist *= MINOR_DEBUFF_WEAKEN;
            }

            //cannot re-corrupt or doom an enemy, so give them a major debuff instead
            if (enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null) {
                corruptingPower = enemyResist - 0.001f;
            }

            if (corruptingPower > enemyResist) {
                corruptEnemy(enemy);
            } else {
                float debuffChance = corruptingPower / enemyResist;
                if (Random.Float() < debuffChance) {
                    debuffEnemy(enemy, MAJOR_DEBUFFS);
                } else {
                    debuffEnemy(enemy, MINOR_DEBUFFS);
                }
            }

            processSoulMark(ch, chargesPerCast());

        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    private void debuffEnemy(Mob enemy, HashMap<Class<? extends Buff>, Float> category) {

        //do not consider buffs which are already assigned, or that the enemy is immune to.
        HashMap<Class<? extends Buff>, Float> debuffs = new HashMap<>(category);
        for (Buff existing : enemy.buffs()) {
            if (debuffs.containsKey(existing.getClass())) {
                debuffs.put(existing.getClass(), 0f);
            }
        }
        for (Class<? extends Buff> toAssign : debuffs.keySet()) {
            if (debuffs.get(toAssign) > 0 && enemy.isImmune(toAssign)) {
                debuffs.put(toAssign, 0f);
            }
        }

        //all buffs with a > 0 chance are flavor buffs
        Class<? extends FlavourBuff> debuffCls = (Class<? extends FlavourBuff>) Random.chances(debuffs);

        if (debuffCls != null) {
            Buff.append(enemy, debuffCls, 6 + level() * 3);
        } else {
            //if no debuff can be applied (all are present), then go up one tier
            if (category == MINOR_DEBUFFS) debuffEnemy(enemy, MAJOR_DEBUFFS);
            else if (category == MAJOR_DEBUFFS) corruptEnemy(enemy);
        }
    }

    private void corruptEnemy(Mob enemy) {
        //cannot re-corrupt or doom an enemy, so give them a major debuff instead
        if (enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null) {
            GLog.w(Messages.get(this, "already_corrupted"));
            return;
        }

        if (!enemy.isImmune(Corruption.class)) {
            enemy.HP = enemy.HT;
            for (Buff buff : enemy.buffs()) {
                if (buff.type == Buff.buffType.NEGATIVE
                        && !(buff instanceof SoulMark)) {
                    buff.detach();
                } else if (buff instanceof PinCushion) {
                    buff.detach();
                }
            }
            if (enemy.alignment == Char.Alignment.ENEMY) {
                enemy.rollToDropLoot();
            }

            Buff.affect(enemy, Corruption.class);

            Statistics.enemiesSlain++;
            Badges.validateMonstersSlain();
            Statistics.qualifiedForNoKilling = false;
            if (enemy.EXP > 0 && curUser.lvl <= enemy.maxLvl) {
                curUser.sprite.showStatus(CharSprite.POSITIVE, Messages.get(enemy, "exp", enemy.EXP));
                curUser.earnExp(enemy.EXP, enemy.getClass());
            } else {
                curUser.earnExp(0, enemy.getClass());
            }
        } else {
            Buff.affect(enemy, Doom.class);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        if (Random.Int(level() + 4) >= 3) {
            Buff.prolong(defender, Amok.class, 4 + level() * 2);
        }
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        if (Random.Int(level() + 4) >= 3) {
            Buff.prolong(defender, Amok.class, 4 + level() * 2);
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.SHADOW,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0);
        particle.am = 0.6f;
        particle.setLifespan(2f);
        particle.speed.set(0, 5);
        particle.setSize(0.5f, 2f);
        particle.shuffleXY(1f);
    }

}
