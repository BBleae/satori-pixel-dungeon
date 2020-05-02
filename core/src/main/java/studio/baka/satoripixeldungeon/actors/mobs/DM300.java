package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.LockedFloor;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import studio.baka.satoripixeldungeon.items.artifacts.DriedRose;
import studio.baka.satoripixeldungeon.items.artifacts.LloydsBeacon;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.items.bombs.ShrapnelBomb;
import studio.baka.satoripixeldungeon.items.keys.SkeletonKey;
import studio.baka.satoripixeldungeon.items.quest.MetalShard;
import studio.baka.satoripixeldungeon.items.wands.WandOfBlastWave;
import studio.baka.satoripixeldungeon.items.wands.WandOfFireblast;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.DM300Sprite;
import studio.baka.satoripixeldungeon.ui.BossHealthBar;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class DM300 extends Mob {

    {
        spriteClass = DM300Sprite.class;

        HP = HT = 200;
        EXP = 30;
        defenseSkill = 18;


        properties.add(Property.BOSS);
        properties.add(Property.INORGANIC);
    }

    private ACMODE curmode = ACMODE.MODE1;

    private enum ACMODE {
        MODE1, MODE2, MODE3, MODE_BK
    }

    private void modechange() {
        if (curmode != ACMODE.MODE_BK) {
            if (HP <= HT / 2 && curmode == ACMODE.MODE1) {
                curmode = ACMODE.MODE2;
                if (Dungeon.level.heroFOV[pos] && Dungeon.hero.isAlive()) {
                    GLog.n(Messages.get(this, "mode2"));
                }
            }
            if (HP <= HT / 4 && curmode == ACMODE.MODE2) {
                curmode = ACMODE.MODE3;
                if (Dungeon.level.heroFOV[pos] && Dungeon.hero.isAlive()) {
                    GLog.n(Messages.get(this, "mode3"));
                }
            }
        }
    }

    @Override
    public int damageRoll() {
        if (curmode == ACMODE.MODE_BK) {
            return 2 * Random.NormalIntRange(20, 25);
        }
        return Random.NormalIntRange(20, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 28;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }


    /*准备更改DM300的行为模式，加入远程攻击动作。此处的act也被部分改写，但是没有完成，改写内容来自于毒飞镖陷阱。*/

    protected boolean canTarget(Char ch) {
        return ch != this;
    }

    private void fangpao(int mode) {
        Char target = Actor.findChar(pos);

        if (target != null && !canTarget(target)) {
            target = null;
        }

        //find the closest char that can be aimed at
        if (target == null) {
            for (Char ch : Actor.chars()) {
                Ballistica bolt = new Ballistica(pos, ch.pos, Ballistica.PROJECTILE);
                if (canTarget(ch) && bolt.collisionPos == ch.pos &&
                        (target == null || Dungeon.level.trueDistance(pos, ch.pos) < Dungeon.level.trueDistance(pos, target.pos))) {
                    target = ch;
                }
            }
        }

        if (target != null) {
            //new Bomb().explode(target.pos);
            //Sample.INSTANCE.play( Assets.SND_BLAST );
            //CellEmitter.center(target.pos).burst(BlastParticle.FACTORY, 30);
            GLog.w(Messages.get(this, "fangpao"));

            final Ballistica shot = new Ballistica(this.pos, target.pos + PathFinder.NEIGHBOURS9[Random.Int(9)], Ballistica.PROJECTILE);
            Sample.INSTANCE.play(Assets.SND_ZAP);

            switch (mode) {
                case 0:
                default:
                    break;
                case 1:
                    ((WandOfBlastWave) (new WandOfBlastWave().upgrade(2))).zap2(shot);
                    //target.damage((damageRoll()/2),this);
                    break;
                case 2:
                    ((WandOfBlastWave) (new WandOfBlastWave().upgrade(2))).zap2(shot);
                    new Bomb().explode(target.pos, damageRoll() / 2, true);
                    break;
                case 3:
                    ((WandOfBlastWave) (new WandOfBlastWave().upgrade(1))).zap2(shot);
                    ((WandOfFireblast) (new WandOfFireblast().upgrade(10))).zap2(shot);
                    new Bomb().explode(target.pos, damageRoll(), true);
                    break;
            }
            if (target == Dungeon.hero && !target.isAlive()) {
                Dungeon.fail(DM300.class);
            }
        }
    }

    @Override
    public boolean act() {
        modechange();

        GameScene.add(Blob.seed(pos, 30, ToxicGas.class));

        return super.act();
    }

    private int atkmode = 0;

    @Override
    protected boolean canAttack(Char enemy) {
        switch (curmode) {
            case MODE1:
            default:
                atkmode = 0;
                return super.canAttack(enemy);
            case MODE2:
                if (Random.Int(0, 9) == 1) {
                    atkmode = 1;
                    return true;
                } else return super.canAttack(enemy);
            case MODE3:
                if (Random.Int(0, 6) == 1) {
                    atkmode = 2;
                    return true;
                } else return super.canAttack(enemy);
            case MODE_BK:
                if (Random.Int(0, 3) == 1) {
                    atkmode = 3;
                    return true;
                } else return super.canAttack(enemy);
        }
    }

    @Override
    public boolean attack(Char enemy) {
        switch (atkmode) {
            case 0:
            default:
                return super.attack(enemy);
            case 1:
            case 2:
            case 3:
                fangpao(atkmode);
                break;
        }
        return true;
/*
		if (!Dungeon.level.adjacent( pos, enemy.pos )) {
			spend( attackDelay() );
		} else {
			return super.attack( enemy );
		}*/
    }

    @Override
    public void move(int step) {
        super.move(step);

        if (Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && HP < HT) {
            if (curmode == ACMODE.MODE1) HP += Random.Int(1, (HT - HP) / 2);
            if (curmode == ACMODE.MODE2) HP += Random.Int(1, (HT - HP) / 5);
            if (curmode == ACMODE.MODE3) HP += Random.Int(1, HT - HP);
            //if (curmode == ACMODE.MODE_BK)HP += Random.Int(1,(HT - HP) / 4);
            sprite.emitter().burst(ElmoParticle.FACTORY, 5);

            if (Dungeon.level.heroFOV[step] && Dungeon.hero.isAlive()) {
                GLog.n(Messages.get(this, "repair"));
            }
        }

        int[] cells = {
                step - 1, step + 1, step - Dungeon.level.width(), step + Dungeon.level.width(),
                step - 1 - Dungeon.level.width(),
                step - 1 + Dungeon.level.width(),
                step + 1 - Dungeon.level.width(),
                step + 1 + Dungeon.level.width()
        };
        int cell = cells[Random.Int(cells.length)];

        if (Dungeon.level.heroFOV[cell]) {
            CellEmitter.get(cell).start(Speck.factory(Speck.ROCK), 0.07f, 10);
            Camera.main.shake(3, 0.7f);
            Sample.INSTANCE.play(Assets.SND_ROCKS);

            if (Dungeon.level.water[cell]) {
                GameScene.ripple(cell);
            } else if (Dungeon.level.map[cell] == Terrain.EMPTY) {
                Level.set(cell, Terrain.EMPTY_DECO);
                GameScene.updateMap(cell);
            }
        }

        Char ch = Actor.findChar(cell);
        if (ch != null && ch != this) {
            Buff.prolong(ch, Paralysis.class, 2);
        }
    }

    private boolean isprotected = false;

    @Override
    public void damage(int dmg, Object src) {
        if (HP - dmg <= 0) {
            if (curmode != ACMODE.MODE_BK) {
                curmode = ACMODE.MODE_BK;
                HP = 20 + dmg;
                isprotected = true;
                GLog.n(Messages.get(this, "mode_bk"));
            }
            if (isprotected) {
                HP += dmg;
                isprotected = false;
            }
            //if (curmode == ACMODE.MODE_BK) curmode = ACMODE.MODE1;
        }
        super.damage(dmg, src);
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null && !isImmune(src.getClass())) lock.addTime(dmg * 1.5f);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        GameScene.bossSlain();
        new ShrapnelBomb().explode(pos);

        Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();

        //60% chance of 2 shards, 30% chance of 3, 10% chance for 4. Average of 2.5
        int shards = Random.chances(new float[]{0, 0, 6, 3, 1});
        for (int i = 0; i < shards; i++) {
            int ofs;
            do {
                ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (!Dungeon.level.passable[pos + ofs]);
            Dungeon.level.drop(new MetalShard(), pos + ofs).sprite.drop(pos);
        }

        Badges.validateBossSlain();

        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }

        yell(Messages.get(this, "defeated"));
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            yell(Messages.get(this, "notice"));
            GLog.n(Messages.get(this, "mode1"));
            for (Char ch : Actor.chars()) {
                if (ch instanceof DriedRose.GhostHero) {
                    GLog.n("\n");
                    ((DriedRose.GhostHero) ch).sayBoss();
                }
            }
        }
    }

    {
        immunities.add(ToxicGas.class);
        immunities.add(Terror.class);
    }

    private static final String MODE = "MODE";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MODE, curmode);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        BossHealthBar.assignBoss(this);
        curmode = bundle.getEnum(MODE, ACMODE.class);
    }
}
