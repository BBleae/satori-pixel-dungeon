package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Light;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.PurpleParticle;
import studio.baka.satoripixeldungeon.items.Dewdrop;
import studio.baka.satoripixeldungeon.items.wands.WandOfDisintegration;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Grim;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.EyeSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Eye extends Mob {

    {
        spriteClass = EyeSprite.class;

        HP = HT = 100;
        defenseSkill = 20;
        viewDistance = Light.DISTANCE;

        EXP = 13;
        maxLvl = 25;

        flying = true;

        HUNTING = new Hunting();

        loot = new Dewdrop();
        lootChance = 0.5f;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20, 30);
    }

    @Override
    public int attackSkill(Char target) {
        return 30;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }

    private Ballistica beam;
    private int beamTarget = -1;
    private int beamCooldown;
    public boolean beamCharged;

    @Override
    protected boolean canAttack(Char enemy) {

        if (beamCooldown == 0) {
            Ballistica aim = new Ballistica(pos, enemy.pos, Ballistica.STOP_TERRAIN);

            if (enemy.invisible == 0 && !isCharmedBy(enemy) && fieldOfView[enemy.pos] && aim.subPath(1, aim.dist).contains(enemy.pos)) {
                beam = aim;
                beamTarget = aim.collisionPos;
                return true;
            } else
                //if the beam is charged, it has to attack, will aim at previous location of target.
                return beamCharged;
        } else
            return super.canAttack(enemy);
    }

    @Override
    protected boolean act() {
        if (beamCharged && state != HUNTING) {
            beamCharged = false;
        }
        if (beam == null && beamTarget != -1) {
            beam = new Ballistica(pos, beamTarget, Ballistica.STOP_TERRAIN);
            sprite.turnTo(pos, beamTarget);
        }
        if (beamCooldown > 0)
            beamCooldown--;
        return super.act();
    }

    @Override
    protected boolean doAttack(Char enemy) {

        if (beamCooldown > 0) {
            return super.doAttack(enemy);
        } else if (!beamCharged) {
            ((EyeSprite) sprite).charge(enemy.pos);
            spend(attackDelay() * 2f);
            beamCharged = true;
            return true;
        } else {

            spend(attackDelay());

            beam = new Ballistica(pos, beamTarget, Ballistica.STOP_TERRAIN);
            if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[beam.collisionPos]) {
                sprite.zap(beam.collisionPos);
                return false;
            } else {
                deathGaze();
                return true;
            }
        }

    }

    @Override
    public void damage(int dmg, Object src) {
        if (beamCharged) dmg /= 4;
        super.damage(dmg, src);
    }

    //used so resistances can differentiate between melee and magical attacks
    public static class DeathGaze {
    }

    public void deathGaze() {
        if (!beamCharged || beamCooldown > 0 || beam == null)
            return;

        beamCharged = false;
        beamCooldown = Random.IntRange(3, 6);

        boolean terrainAffected = false;

        for (int pos : beam.subPath(1, beam.dist)) {

            if (Dungeon.level.flamable[pos]) {

                Dungeon.level.destroy(pos);
                GameScene.updateMap(pos);
                terrainAffected = true;

            }

            Char ch = Actor.findChar(pos);
            if (ch == null) {
                continue;
            }

            if (hit(this, ch, true)) {
                ch.damage(Random.NormalIntRange(30, 50), new DeathGaze());

                if (Dungeon.level.heroFOV[pos]) {
                    ch.sprite.flash();
                    CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
                }

                if (!ch.isAlive() && ch == Dungeon.hero) {
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "deathgaze_kill"));
                }
            } else {
                ch.sprite.showStatus(CharSprite.NEUTRAL, ch.defenseVerb());
            }
        }

        if (terrainAffected) {
            Dungeon.observe();
        }

        beam = null;
        beamTarget = -1;
    }

    private static final String BEAM_TARGET = "beamTarget";
    private static final String BEAM_COOLDOWN = "beamCooldown";
    private static final String BEAM_CHARGED = "beamCharged";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BEAM_TARGET, beamTarget);
        bundle.put(BEAM_COOLDOWN, beamCooldown);
        bundle.put(BEAM_CHARGED, beamCharged);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(BEAM_TARGET))
            beamTarget = bundle.getInt(BEAM_TARGET);
        beamCooldown = bundle.getInt(BEAM_COOLDOWN);
        beamCharged = bundle.getBoolean(BEAM_CHARGED);
    }

    {
        resistances.add(WandOfDisintegration.class);
        resistances.add(Grim.class);
    }

    {
        immunities.add(Terror.class);
    }

    private class Hunting extends Mob.Hunting {
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            //even if enemy isn't seen, attack them if the beam is charged
            if (beamCharged && enemy != null && canAttack(enemy)) {
                enemySeen = enemyInFOV;
                return doAttack(enemy);
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }
}
