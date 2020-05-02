package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.CorrosiveGas;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Corruption;
import studio.baka.satoripixeldungeon.actors.buffs.PrismaticGuard;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.armor.glyphs.AntiMagic;
import studio.baka.satoripixeldungeon.items.armor.glyphs.Brimstone;
import studio.baka.satoripixeldungeon.levels.features.Chasm;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.PrismaticSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class PrismaticImage extends NPC {

    {
        spriteClass = PrismaticSprite.class;

        HP = HT = 8;
        defenseSkill = 1;

        alignment = Alignment.ALLY;
        intelligentAlly = true;
        state = HUNTING;

        WANDERING = new Wandering();

        //before other mobs
        actPriority = MOB_PRIORITY + 1;
    }

    private Hero hero;
    private int heroID;
    public int armTier;

    private int deathTimer = -1;

    @Override
    protected boolean act() {

        if (!isAlive()) {
            deathTimer--;

            if (deathTimer > 0) {
                sprite.alpha((deathTimer + 3) / 8f);
                spend(TICK);
            } else {
                destroy();
                sprite.die();
            }
            return true;
        }

        if (deathTimer != -1) {
            if (paralysed == 0) sprite.remove(CharSprite.State.PARALYSED);
            deathTimer = -1;
            sprite.resetColor();
        }

        if (hero == null) {
            hero = (Hero) Actor.findById(heroID);
            if (hero == null) {
                destroy();
                sprite.die();
                return true;
            }
        }

        if (hero.tier() != armTier) {
            armTier = hero.tier();
            ((PrismaticSprite) sprite).updateArmor(armTier);
        }

        return super.act();
    }

    @Override
    public void die(Object cause) {
        if (deathTimer == -1) {
            if (cause == Chasm.class) {
                super.die(cause);
            } else {
                deathTimer = 5;
                sprite.add(CharSprite.State.PARALYSED);
            }
        }
    }

    private static final String HEROID = "hero_id";
    private static final String TIMER = "timer";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEROID, heroID);
        bundle.put(TIMER, deathTimer);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        heroID = bundle.getInt(HEROID);
        deathTimer = bundle.getInt(TIMER);
    }

    public void duplicate(Hero hero, int HP) {
        this.hero = hero;
        heroID = this.hero.id();
        this.HP = HP;
        HT = PrismaticGuard.maxHP(hero);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1 + hero.lvl / 8, 4 + hero.lvl / 2);
    }

    @Override
    public int attackSkill(Char target) {
        return hero.attackSkill(target);
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (hero != null) {
            int baseEvasion = 4 + hero.lvl;
            int heroEvasion = hero.defenseSkill(enemy);

            //if the hero has more/less evasion, 50% of it is applied
            return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
        } else {
            return 0;
        }
    }

    @Override
    public int drRoll() {
        if (hero != null) {
            return hero.drRoll();
        } else {
            return 0;
        }
    }

    @Override
    public int defenseProcess(Char enemy, int damage) {
        damage = super.defenseProcess(enemy, damage);
        if (hero.belongings.armor != null) {
            return hero.belongings.armor.proc(enemy, this, damage);
        } else {
            return damage;
        }
    }

    @Override
    public void damage(int dmg, Object src) {

        //TODO improve this when I have proper damage source logic
        if (hero.belongings.armor != null && hero.belongings.armor.hasGlyph(AntiMagic.class, this)
                && AntiMagic.RESISTS.contains(src.getClass())) {
            dmg -= AntiMagic.drRoll(hero.belongings.armor.level());
        }

        super.damage(dmg, src);
    }

    @Override
    public float speed() {
        if (hero.belongings.armor != null) {
            return hero.belongings.armor.speedFactor(this, super.speed());
        }
        return super.speed();
    }

    @Override
    public int attackProc(Char enemy, int damage) {

        if (enemy instanceof Mob) {
            ((Mob) enemy).aggro(this);
        }

        return super.attackProc(enemy, damage);
    }

    @Override
    public CharSprite sprite() {
        CharSprite s = super.sprite();

        hero = (Hero) Actor.findById(heroID);
        if (hero != null) {
            armTier = hero.tier();
        }
        ((PrismaticSprite) s).updateArmor(armTier);
        return s;
    }

    @Override
    public boolean isImmune(@SuppressWarnings("rawtypes") Class effect) {
        if (effect == Burning.class
                && hero != null
                && hero.belongings.armor != null
                && hero.belongings.armor.hasGlyph(Brimstone.class, this)) {
            return true;
        }
        return super.isImmune(effect);
    }

    {
        immunities.add(ToxicGas.class);
        immunities.add(CorrosiveGas.class);
        immunities.add(Burning.class);
        immunities.add(Corruption.class);
    }

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (!enemyInFOV) {
                Buff.affect(hero, PrismaticGuard.class).set(HP);
                destroy();
                CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
                sprite.die();
                Sample.INSTANCE.play(Assets.SND_TELEPORT);
                return true;
            } else {
                return super.act(true, justAlerted);
            }
        }

    }

}
