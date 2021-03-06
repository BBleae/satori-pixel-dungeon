package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Amok;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.NPC;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.EarthGuardianSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

public class WandOfLivingEarth extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_LIVING_EARTH;
    }

    @Override
    public int min(int lvl) {
        return 4;
    }

    @Override
    public int max(int lvl) {
        return 6 + 2 * lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Char ch = Actor.findChar(bolt.collisionPos);
        int damage = damageRoll();
        int armorToAdd = damage;

        EarthGuardian guardian = null;
        for (Mob m : Dungeon.level.mobs) {
            if (m instanceof EarthGuardian) {
                guardian = (EarthGuardian) m;
                break;
            }
        }

        RockArmor buff = curUser.buff(RockArmor.class);
        if (ch == null) {
            armorToAdd = 0;
        } else {
            if (buff == null && guardian == null) {
                buff = Buff.affect(curUser, RockArmor.class);
            }
            if (buff != null) {
                buff.addArmor(level(), armorToAdd);
            }
        }

        //shooting at the guardian
        if (guardian != null && guardian == ch) {
            guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + level() / 2);
            guardian.setInfo(curUser, level(), armorToAdd);
            processSoulMark(guardian, chargesPerCast());

            //shooting the guardian at a location
        } else if (guardian == null && buff != null && buff.armor >= buff.armorToGuardian()) {

            //create a new guardian
            guardian = new EarthGuardian();
            guardian.setInfo(curUser, level(), buff.armor);

            //if the collision pos is occupied (likely will be), then spawn the guardian in the
            //adjacent cell which is closes to the user of the wand.
            if (ch != null) {

                ch.sprite.centerEmitter().burst(MagicMissile.EarthParticle.BURST, 5 + level() / 2);

                processSoulMark(ch, chargesPerCast());
                ch.damage(damage, this);

                int closest = -1;
                boolean[] passable = Dungeon.level.passable;

                for (int n : PathFinder.NEIGHBOURS9) {
                    int c = bolt.collisionPos + n;
                    if (passable[c] && Actor.findChar(c) == null
                            && (closest == -1 || (Dungeon.level.trueDistance(c, curUser.pos) < (Dungeon.level.trueDistance(closest, curUser.pos))))) {
                        closest = c;
                    }
                }

                if (closest == -1) {
                    curUser.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + level() / 2);
                    return; //do not spawn guardian or detach buff
                } else {
                    guardian.pos = closest;
                    GameScene.add(guardian, 1);
                    Dungeon.level.occupyCell(guardian);
                }

                if (ch.alignment == Char.Alignment.ENEMY || ch.buff(Amok.class) != null) {
                    guardian.aggro(ch);
                }

            } else {
                guardian.pos = bolt.collisionPos;
                GameScene.add(guardian, 1);
                Dungeon.level.occupyCell(guardian);
            }

            guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + level() / 2);
            buff.detach();

            //shooting at a location/enemy with no guardian being shot
        } else {

            if (ch != null) {

                ch.sprite.centerEmitter().burst(MagicMissile.EarthParticle.BURST, 5 + level() / 2);

                processSoulMark(ch, chargesPerCast());
                ch.damage(damage, this);

                if (guardian == null) {
                    curUser.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + level() / 2);
                } else {
                    guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + level() / 2);
                    guardian.setInfo(curUser, level(), armorToAdd);
                    if (ch.alignment == Char.Alignment.ENEMY || ch.buff(Amok.class) != null) {
                        guardian.aggro(ch);
                    }
                }

            } else {
                Dungeon.level.pressCell(bolt.collisionPos);
            }
        }

    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.EARTH,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        EarthGuardian guardian = null;
        for (Mob m : Dungeon.level.mobs) {
            if (m instanceof EarthGuardian) {
                guardian = (EarthGuardian) m;
                break;
            }
        }

        int armor = Math.round(damage * 0.25f);

        if (guardian != null) {
            guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + level() / 2);
            guardian.setInfo(Dungeon.hero, level(), armor);
        } else {
            attacker.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + level() / 2);
            Buff.affect(attacker, RockArmor.class).addArmor(level(), armor);
        }
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        EarthGuardian guardian = null;
        for (Mob m : Dungeon.level.mobs) {
            if (m instanceof EarthGuardian) {
                guardian = (EarthGuardian) m;
                break;
            }
        }

        int armor = Math.round(damage * 0.25f);

        if (guardian != null) {
            guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + level() / 2);
            guardian.setInfo(Dungeon.hero, level(), armor);
        } else {
            attacker.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + level() / 2);
            Buff.affect(attacker, RockArmor.class).addArmor(level(), armor);
        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        if (Random.Int(10) == 0) {
            particle.color(ColorMath.random(0xFFF568, 0x80791A));
        } else {
            particle.color(ColorMath.random(0x805500, 0x332500));
        }
        particle.am = 1f;
        particle.setLifespan(2f);
        particle.setSize(1f, 2f);
        particle.shuffleXY(0.5f);
        float dst = Random.Float(11f);
        particle.x -= dst;
        particle.y += dst;
    }

    public static class RockArmor extends Buff {

        private int wandLevel;
        private int armor;

        private void addArmor(int wandLevel, int toAdd) {
            this.wandLevel = Math.max(this.wandLevel, wandLevel);
            armor += toAdd;
            armor = Math.min(armor, 2 * armorToGuardian());
        }

        private int armorToGuardian() {
            return 8 + wandLevel * 4;
        }

        public int absorb(int damage) {
            int block = damage - damage / 2;
            if (armor <= block) {
                detach();
                return damage - armor;
            } else {
                armor -= block;
                BuffIndicator.refreshHero();
                return damage - block;
            }
        }

        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", armor, armorToGuardian());
        }

        private static final String WAND_LEVEL = "wand_level";
        private static final String ARMOR = "armor";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(WAND_LEVEL, wandLevel);
            bundle.put(ARMOR, armor);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            wandLevel = bundle.getInt(WAND_LEVEL);
            armor = bundle.getInt(ARMOR);
        }
    }

    public static class EarthGuardian extends NPC {

        {
            spriteClass = EarthGuardianSprite.class;

            alignment = Alignment.ALLY;
            state = HUNTING;
            intelligentAlly = true;
            WANDERING = new Wandering();

            //before other mobs
            actPriority = MOB_PRIORITY + 1;

            HP = HT = 0;
        }

        private int wandLevel = -1;

        private void setInfo(Hero hero, int wandLevel, int healthToAdd) {
            if (wandLevel > this.wandLevel) {
                this.wandLevel = wandLevel;
                HT = 16 + 8 * wandLevel;
            }
            HP = Math.min(HT, HP + healthToAdd);
            //half of hero's evasion
            defenseSkill = (hero.lvl + 4) / 2;
        }

        @Override
        public int attackSkill(Char target) {
            //same as the hero
            return 2 * defenseSkill + 5;
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            if (enemy instanceof Mob) ((Mob) enemy).aggro(this);
            return super.attackProc(enemy, damage);
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(2, 4 + Dungeon.depth / 2);
        }

        @Override
        public int drRoll() {
            if (Dungeon.isChallenged(Challenges.NO_ARMOR)) {
                return Random.NormalIntRange(wandLevel, 2 + wandLevel);
            } else {
                return Random.NormalIntRange(wandLevel, 3 + 3 * wandLevel);
            }
        }

        @Override
        public String description() {
            if (Dungeon.isChallenged(Challenges.NO_ARMOR)) {
                return Messages.get(this, "desc", wandLevel, 2 + wandLevel);
            } else {
                return Messages.get(this, "desc", wandLevel, 3 + 3 * wandLevel);
            }

        }

        private static final String DEFENSE = "defense";
        private static final String WAND_LEVEL = "wand_level";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DEFENSE, defenseSkill);
            bundle.put(WAND_LEVEL, wandLevel);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            defenseSkill = bundle.getInt(DEFENSE);
            wandLevel = bundle.getInt(WAND_LEVEL);
        }

        private class Wandering extends Mob.Wandering {

            @Override
            public boolean act(boolean enemyInFOV, boolean justAlerted) {
                if (!enemyInFOV) {
                    Buff.affect(Dungeon.hero, RockArmor.class).addArmor(wandLevel, HP);
                    Dungeon.hero.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + wandLevel / 2);
                    destroy();
                    sprite.die();
                    return true;
                } else {
                    return super.act(true, justAlerted);
                }
            }

        }

    }
}
