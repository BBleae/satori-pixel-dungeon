package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Electricity;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.LockedFloor;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.*;
import studio.baka.satoripixeldungeon.effects.particles.BlastParticle;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.effects.particles.SmokeParticle;
import studio.baka.satoripixeldungeon.effects.particles.SparkParticle;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.TomeOfMastery;
import studio.baka.satoripixeldungeon.items.artifacts.DriedRose;
import studio.baka.satoripixeldungeon.items.artifacts.LloydsBeacon;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.NewPrisonBossLevel;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.sprites.MissileSprite;
import studio.baka.satoripixeldungeon.sprites.TenguSprite;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import studio.baka.satoripixeldungeon.ui.BossHealthBar;
import studio.baka.satoripixeldungeon.utils.BArray;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.HashSet;

public class NewTengu extends Mob {

    {
        spriteClass = TenguSprite.class;

        HP = HT = 160;
        EXP = 20;
        defenseSkill = 15;

        HUNTING = new Hunting();

        flying = true; //doesn't literally fly, but he is fleet-of-foot enough to avoid hazards

        properties.add(Property.BOSS);

        viewDistance = 12;
    }

    @Override
    protected void onAdd() {
        //when he's removed and re-added to the fight, his time is always set to now.
        spend(-cooldown());
        super.onAdd();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(6, 12);
    }

    @Override
    public int attackSkill(Char target) {
        return 18;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }

    @Override
    public void damage(int dmg, Object src) {
        NewPrisonBossLevel.State state = ((NewPrisonBossLevel) Dungeon.level).state();

        int hpBracket = 20;

        int beforeHitHP = HP;
        super.damage(dmg, src);
        dmg = beforeHitHP - HP;

        //tengu cannot be hit through multiple brackets at a time
        if ((beforeHitHP / hpBracket - HP / hpBracket) >= 2) {
            HP = hpBracket * ((beforeHitHP / hpBracket) - 1);
        }

        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) {
            int multiple = state == NewPrisonBossLevel.State.FIGHT_START ? 1 : 4;
            lock.addTime(dmg * multiple);
        }

        //phase 2 of the fight is over
        if (HP == 0 && state == NewPrisonBossLevel.State.FIGHT_ARENA) {
            //let full attack action complete first
            Actor.add(new Actor() {

                {
                    actPriority = VFX_PRIORITY;
                }

                @Override
                protected boolean act() {
                    Actor.remove(this);
                    ((NewPrisonBossLevel) Dungeon.level).progress();
                    return true;
                }
            });
            return;
        }

        //phase 1 of the fight is over
        if (state == NewPrisonBossLevel.State.FIGHT_START && HP <= HT / 2) {
            HP = (HT / 2);
            yell(Messages.get(this, "interesting"));
            ((NewPrisonBossLevel) Dungeon.level).progress();
            BossHealthBar.bleed(true);

            //if tengu has lost a certain amount of hp, jump
        } else if (beforeHitHP / hpBracket != HP / hpBracket) {
            jump();
        }
    }

    @Override
    public boolean isAlive() {
        return HP > 0 || Dungeon.level.mobs.contains(this); //Tengu has special death rules, see prisonbosslevel.progress()
    }

    @Override
    public void die(Object cause) {

        if (Dungeon.hero.subClass == HeroSubClass.NONE) {
            Dungeon.level.drop(new TomeOfMastery(), pos).sprite.drop();
        }

        GameScene.bossSlain();
        super.die(cause);

        Badges.validateBossSlain();

        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }

        yell(Messages.get(this, "defeated"));
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
    }

    //tengu's attack is always visible
    @Override
    protected boolean doAttack(Char enemy) {
        sprite.attack(enemy.pos);
        spend(attackDelay());
        return false;
    }

    private void jump() {

        //in case tengu hasn't had a chance to act yet
        if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
            fieldOfView = new boolean[Dungeon.level.length()];
            Dungeon.level.updateFieldOfView(this, fieldOfView);
        }

        if (enemy == null) enemy = chooseEnemy();
        if (enemy == null) return;

        int newPos;
        if (Dungeon.level instanceof NewPrisonBossLevel) {
            NewPrisonBossLevel level = (NewPrisonBossLevel) Dungeon.level;

            //if we're in phase 1, want to warp around within the room
            if (level.state() == NewPrisonBossLevel.State.FIGHT_START) {

                level.cleanTenguCell();

                do {
                    newPos = ((NewPrisonBossLevel) Dungeon.level).randomTenguCellPos();
                } while ((level.distance(newPos, enemy.pos) < 3 || Actor.findChar(newPos) != null));

                if (level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

                sprite.move(pos, newPos);
                move(newPos);

                if (level.heroFOV[newPos]) CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
                Sample.INSTANCE.play(Assets.SND_PUFF);

                float fill = 0.9f - 0.5f * ((HP - 80) / 80f);
                level.placeTrapsInTenguCell(fill);

                //otherwise, jump in a larger possible area, as the room is bigger
            } else {

                do {
                    newPos = Random.Int(level.length());
                } while (
                        level.solid[newPos] ||
                                level.distance(newPos, enemy.pos) < 5 ||
                                level.distance(newPos, enemy.pos) > 7 ||
                                level.distance(newPos, pos) < 6 ||
                                Actor.findChar(newPos) != null ||
                                Dungeon.level.heaps.get(newPos) != null);

                if (level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

                sprite.move(pos, newPos);
                move(newPos);

                if (arenaJumps < 4) arenaJumps++;

                if (level.heroFOV[newPos]) CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
                Sample.INSTANCE.play(Assets.SND_PUFF);

            }

            //if we're on another type of level
        } else {
            Level level = Dungeon.level;

            newPos = level.randomRespawnCell();

            if (level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

            sprite.move(pos, newPos);
            move(newPos);

            if (level.heroFOV[newPos]) CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
            Sample.INSTANCE.play(Assets.SND_PUFF);

        }

    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            if (HP <= HT / 2) BossHealthBar.bleed(true);
            if (HP == HT) {
                yell(Messages.get(this, "notice_gotcha", Dungeon.hero.givenName()));
                for (Char ch : Actor.chars()) {
                    if (ch instanceof DriedRose.GhostHero) {
                        GLog.n("\n");
                        ((DriedRose.GhostHero) ch).sayBoss();
                    }
                }
            } else {
                yell(Messages.get(this, "notice_have", Dungeon.hero.givenName()));
            }
        }
    }

    {
        immunities.add(Blindness.class);
    }

    private static final String LAST_ABILITY = "last_ability";
    private static final String ABILITIES_USED = "abilities_used";
    private static final String ARENA_JUMPS = "arena_jumps";
    private static final String ABILITY_COOLDOWN = "ability_cooldown";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LAST_ABILITY, lastAbility);
        bundle.put(ABILITIES_USED, abilitiesUsed);
        bundle.put(ARENA_JUMPS, arenaJumps);
        bundle.put(ABILITY_COOLDOWN, abilityCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        lastAbility = bundle.getInt(LAST_ABILITY);
        abilitiesUsed = bundle.getInt(ABILITIES_USED);
        arenaJumps = bundle.getInt(ARENA_JUMPS);
        abilityCooldown = bundle.getInt(ABILITY_COOLDOWN);

        BossHealthBar.assignBoss(this);
        if (HP <= HT / 2) BossHealthBar.bleed(true);
    }

    //don't bother bundling this, as its purely cosmetic
    private final boolean yelledCoward = false;

    //tengu is always hunting
    private class Hunting extends Mob.Hunting {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {

            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy(enemy) && canAttack(enemy)) {

                if (canUseAbility()) {
                    return useAbility();
                }

                return doAttack(enemy);

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else {
                    chooseEnemy();
                    if (enemy == null) {
                        //if nothing else can be targeted, target hero
                        enemy = Dungeon.hero;
                    }
                    target = enemy.pos;
                }

                //if not charmed, attempt to use an ability, even if the enemy can't be seen
                if (canUseAbility()) {
                    return useAbility();
                }

                spend(TICK);
                return true;

            }
        }
    }

    //*****************************************************************************************
    //***** Tengu abilities. These are expressed in game logic as buffs, blobs, and items *****
    //*****************************************************************************************

    //so that mobs can also use this
    private static Char throwingChar;

    private int lastAbility = -1;
    private int abilitiesUsed = 0;
    private int arenaJumps = 0;

    //starts at 2, so one turn and then first ability
    private int abilityCooldown = 2;

    private static final int BOMB_ABILITY = 0;
    private static final int FIRE_ABILITY = 1;
    private static final int SHOCKER_ABILITY = 2;

    //expects to be called once per turn;
    public boolean canUseAbility() {

        if (HP > HT / 2) return false;

        if (abilitiesUsed >= targetAbilityUses()) {
            return false;
        } else {

            abilityCooldown--;

            if (targetAbilityUses() - abilitiesUsed >= 4) {
                //Very behind in ability uses, use one right away!
                abilityCooldown = 0;

            } else if (targetAbilityUses() - abilitiesUsed >= 3) {
                //moderately behind in uses, use one every other action.
                if (abilityCooldown == -1 || abilityCooldown > 1) abilityCooldown = 1;

            } else {
                //standard delay before ability use, 1-4 turns
                if (abilityCooldown == -1) abilityCooldown = Random.IntRange(1, 4);
            }

            return abilityCooldown == 0;
        }
    }

    private int targetAbilityUses() {
        //1 base ability use, plus 2 uses per jump
        int targetAbilityUses = 1 + 2 * arenaJumps;

        //and ane extra 2 use for jumps 3 and 4
        targetAbilityUses += Math.max(0, arenaJumps - 2);

        return targetAbilityUses;
    }

    public boolean useAbility() {
        boolean abilityUsed = false;
        int abilityToUse = -1;

        while (!abilityUsed) {

            if (abilitiesUsed == 0) {
                abilityToUse = BOMB_ABILITY;
            } else if (abilitiesUsed == 1) {
                abilityToUse = SHOCKER_ABILITY;
            } else {
                abilityToUse = Random.Int(3);
            }

            //If we roll the same ability as last time, 9/10 chance to reroll
            if (abilityToUse != lastAbility || Random.Int(10) == 0) {
                switch (abilityToUse) {
                    case BOMB_ABILITY:
                    default:
                        abilityUsed = throwBomb(NewTengu.this, enemy);
                        //if Tengu cannot use his bomb ability first, use fire instead.
                        if (abilitiesUsed == 0 && !abilityUsed) {
                            abilityToUse = FIRE_ABILITY;
                            abilityUsed = throwFire(NewTengu.this, enemy);
                        }
                        break;
                    case FIRE_ABILITY:
                        abilityUsed = throwFire(NewTengu.this, enemy);
                        break;
                    case SHOCKER_ABILITY:
                        abilityUsed = throwShocker(NewTengu.this, enemy);
                        //if Tengu cannot use his shocker ability second, use fire instead.
                        if (abilitiesUsed == 1 && !abilityUsed) {
                            abilityToUse = FIRE_ABILITY;
                            abilityUsed = throwFire(NewTengu.this, enemy);
                        }
                        break;
                }
            }

        }

        //spend only 1 turn if seriously behind on ability uses
        if (targetAbilityUses() - abilitiesUsed >= 4) {
            spend(TICK);
        } else {
            spend(2 * TICK);
        }

        lastAbility = abilityToUse;
        abilitiesUsed++;
        return lastAbility == FIRE_ABILITY;
    }

    //******************
    //***Bomb Ability***
    //******************

    //returns true if bomb was thrown
    public static boolean throwBomb(final Char thrower, final Char target) {

        int targetCell = -1;

        //Targets closest cell which is adjacent to target, and at least 3 tiles away
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = target.pos + i;
            if (Dungeon.level.distance(cell, thrower.pos) >= 3) {
                if (targetCell == -1 ||
                        Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos)) {
                    targetCell = cell;
                }
            }
        }

        if (targetCell == -1) {
            return false;
        }

        final int finalTargetCell = targetCell;
        throwingChar = thrower;
        final BombAbility.BombItem item = new BombAbility.BombItem();
        thrower.sprite.zap(finalTargetCell);
        ((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
                reset(thrower.sprite,
                        finalTargetCell,
                        item,
                        () -> {
                            item.onThrow(finalTargetCell);
                            thrower.next();
                        });
        return true;
    }

    public static class BombAbility extends Buff {

        public int bombPos;
        private int timer = 3;

        @Override
        public boolean act() {

            PointF p = DungeonTilemap.raisedTileCenterToWorld(bombPos);
            if (timer == 3) {
                FloatingText.show(p.x, p.y, bombPos, "3...", CharSprite.NEUTRAL);
                PathFinder.buildDistanceMap(bombPos, BArray.not(Dungeon.level.solid, null), 2);
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                        GameScene.add(Blob.seed(i, 4, BombBlob.class));
                    }
                }
            } else if (timer == 2) {
                FloatingText.show(p.x, p.y, bombPos, "2...", CharSprite.WARNING);
            } else if (timer == 1) {
                FloatingText.show(p.x, p.y, bombPos, "1...", CharSprite.NEGATIVE);
            } else {
                Heap h = Dungeon.level.heaps.get(bombPos);
                if (h != null) {
                    for (Item i : h.items.toArray(new Item[0])) {
                        if (i instanceof BombItem) {
                            h.remove(i);
                        }
                    }
                }
                detach();
                return true;
            }

            timer--;
            spend(TICK);
            return true;
        }

        private static final String BOMB_POS = "bomb_pos";
        private static final String TIMER = "timer";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOMB_POS, bombPos);
            bundle.put(TIMER, timer);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            bombPos = bundle.getInt(BOMB_POS);
            timer = bundle.getInt(TIMER);
        }

        public static class BombBlob extends Blob {
            {
                actPriority = BUFF_PRIORITY - 1;
                alwaysVisible = true;
            }

            @Override
            protected void evolve() {

                boolean exploded = false;

                int cell;
                for (int i = area.left; i < area.right; i++) {
                    for (int j = area.top; j < area.bottom; j++) {
                        cell = i + j * Dungeon.level.width();
                        off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                        if (off[cell] > 0) {
                            volume += off[cell];
                        }

                        if (cur[cell] > 0 && off[cell] == 0) {

                            Char ch = Actor.findChar(cell);
                            if (ch != null && !(ch instanceof NewTengu)) {
                                int dmg = Random.NormalIntRange(5 + Dungeon.depth, 10 + Dungeon.depth * 2);
                                dmg -= ch.drRoll();

                                if (dmg > 0) {
                                    ch.damage(dmg, Bomb.class);
                                }

                                if (ch == Dungeon.hero && !ch.isAlive()) {
                                    Dungeon.fail(NewTengu.class);
                                }
                            }

                            exploded = true;
                            CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
                        }
                    }
                }

                if (exploded) {
                    Sample.INSTANCE.play(Assets.SND_BLAST);
                }

            }

            @Override
            public void use(BlobEmitter emitter) {
                super.use(emitter);

                emitter.pour(SmokeParticle.FACTORY, 0.25f);
            }

            @Override
            public String tileDesc() {
                return Messages.get(this, "desc");
            }
        }

        public static class BombItem extends Item {

            {
                dropsDownHeap = true;
                unique = true;

                image = ItemSpriteSheet.TENGU_BOMB;
            }

            @Override
            public boolean doPickUp(Hero hero) {
                GLog.w(Messages.get(this, "cant_pickup"));
                return false;
            }

            @Override
            protected void onThrow(int cell) {
                super.onThrow(cell);
                if (throwingChar != null) {
                    Buff.append(throwingChar, BombAbility.class).bombPos = cell;
                    throwingChar = null;
                } else {
                    Buff.append(curUser, BombAbility.class).bombPos = cell;
                }
            }

            @Override
            public Emitter emitter() {
                Emitter emitter = new Emitter();
                emitter.pos(7.5f, 3.5f);
                emitter.fillTarget = false;
                emitter.pour(SmokeParticle.SPEW, 0.05f);
                return emitter;
            }
        }
    }

    //******************
    //***Fire Ability***
    //******************

    public static boolean throwFire(final Char thrower, final Char target) {

        Ballistica aim = new Ballistica(thrower.pos, target.pos, Ballistica.WONT_STOP);

        for (int i = 0; i < PathFinder.CIRCLE8.length; i++) {
            if (aim.sourcePos + PathFinder.CIRCLE8[i] == aim.path.get(1)) {
                thrower.sprite.zap(target.pos);
                Buff.append(thrower, NewTengu.FireAbility.class).direction = i;

                thrower.sprite.emitter().start(Speck.factory(Speck.STEAM), .03f, 10);
                return true;
            }
        }

        return false;
    }

    public static class FireAbility extends Buff {

        public int direction;
        private int[] curCells;

        HashSet<Integer> toCells = new HashSet<>();

        @Override
        public boolean act() {

            if (curCells == null) {
                curCells = new int[1];
                curCells[0] = target.pos;
            }

            toCells.clear();

            for (Integer c : curCells) {
                spreadFromCell(c);
            }

            for (Integer c : curCells) {
                toCells.remove(c);
            }

            if (toCells.isEmpty()) {
                detach();
            } else {
                curCells = new int[toCells.size()];
                int i = 0;
                for (Integer c : toCells) {
                    GameScene.add(Blob.seed(c, 2, FireBlob.class));
                    curCells[i] = c;
                    i++;
                }
            }

            spend(TICK);
            return true;
        }

        private void spreadFromCell(int cell) {
            if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[left(direction)]]) {
                toCells.add(cell + PathFinder.CIRCLE8[left(direction)]);
            }
            if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[direction]]) {
                toCells.add(cell + PathFinder.CIRCLE8[direction]);
            }
            if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[right(direction)]]) {
                toCells.add(cell + PathFinder.CIRCLE8[right(direction)]);
            }
        }

        private int left(int direction) {
            return direction == 0 ? 7 : direction - 1;
        }

        private int right(int direction) {
            return direction == 7 ? 0 : direction + 1;
        }

        private static final String DIRECTION = "direction";
        private static final String CUR_CELLS = "cur_cells";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DIRECTION, direction);
            bundle.put(CUR_CELLS, curCells);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            direction = bundle.getInt(DIRECTION);
            curCells = bundle.getIntArray(CUR_CELLS);
        }

        public static class FireBlob extends Blob {

            {
                actPriority = BUFF_PRIORITY - 1;
                alwaysVisible = true;
            }

            @Override
            protected void evolve() {

                boolean observe = false;
                boolean burned = false;

                int cell;
                for (int i = area.left; i < area.right; i++) {
                    for (int j = area.top; j < area.bottom; j++) {
                        cell = i + j * Dungeon.level.width();
                        off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                        if (off[cell] > 0) {
                            volume += off[cell];
                        }

                        if (cur[cell] > 0 && off[cell] == 0) {

                            Char ch = Actor.findChar(cell);
                            if (ch != null && !ch.isImmune(Fire.class) && !(ch instanceof NewTengu)) {
                                Buff.affect(ch, Burning.class).reignite(ch);
                            }

                            if (Dungeon.level.flamable[cell]) {
                                Dungeon.level.destroy(cell);

                                observe = true;
                                GameScene.updateMap(cell);
                            }

                            burned = true;
                            CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.03f, 10);
                        }
                    }
                }

                if (observe) {
                    Dungeon.observe();
                }

                if (burned) {
                    Sample.INSTANCE.play(Assets.SND_BURNING);
                }
            }

            @Override
            public void use(BlobEmitter emitter) {
                super.use(emitter);

                emitter.pour(Speck.factory(Speck.STEAM), 0.2f);
            }

            @Override
            public String tileDesc() {
                return Messages.get(this, "desc");
            }
        }
    }

    //*********************
    //***Shocker Ability***
    //*********************

    //returns true if shocker was thrown
    public static boolean throwShocker(final Char thrower, final Char target) {

        int targetCell = -1;

        //Targets closest cell which is adjacent to target, and not adjacent to thrower or another shocker
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = target.pos + i;
            if (Dungeon.level.distance(cell, thrower.pos) >= 2) {
                boolean validTarget = true;
                for (ShockerAbility s : thrower.buffs(ShockerAbility.class)) {
                    if (Dungeon.level.distance(cell, s.shockerPos) < 2) {
                        validTarget = false;
                        break;
                    }
                }
                if (validTarget && Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos)) {
                    targetCell = cell;
                }
            }
        }

        if (targetCell == -1) {
            return false;
        }

        final int finalTargetCell = targetCell;
        throwingChar = thrower;
        final ShockerAbility.ShockerItem item = new ShockerAbility.ShockerItem();
        thrower.sprite.zap(finalTargetCell);
        ((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
                reset(thrower.sprite,
                        finalTargetCell,
                        item,
                        () -> {
                            item.onThrow(finalTargetCell);
                            thrower.next();
                        });
        return true;
    }

    public static class ShockerAbility extends Buff {

        public int shockerPos;
        private Boolean shockingOrdinals = null;

        @Override
        public boolean act() {

            if (shockingOrdinals == null) {
                shockingOrdinals = Random.Int(2) == 1;

                spreadblob();
            } else if (shockingOrdinals) {

                target.sprite.parent.add(new Lightning(shockerPos - 1 - Dungeon.level.width(), shockerPos + 1 + Dungeon.level.width(), null));
                target.sprite.parent.add(new Lightning(shockerPos - 1 + Dungeon.level.width(), shockerPos + 1 - Dungeon.level.width(), null));

                if (Dungeon.level.distance(Dungeon.hero.pos, shockerPos) <= 1) {
                    Sample.INSTANCE.play(Assets.SND_LIGHTNING);
                }

                shockingOrdinals = false;
                spreadblob();
            } else {

                target.sprite.parent.add(new Lightning(shockerPos - Dungeon.level.width(), shockerPos + Dungeon.level.width(), null));
                target.sprite.parent.add(new Lightning(shockerPos - 1, shockerPos + 1, null));

                if (Dungeon.level.distance(Dungeon.hero.pos, shockerPos) <= 1) {
                    Sample.INSTANCE.play(Assets.SND_LIGHTNING);
                }

                shockingOrdinals = true;
                spreadblob();
            }

            spend(TICK);
            return true;
        }

        private void spreadblob() {
            GameScene.add(Blob.seed(shockerPos, 1, ShockerBlob.class));
            for (int i = shockingOrdinals ? 0 : 1; i < PathFinder.CIRCLE8.length; i += 2) {
                if (!Dungeon.level.solid[shockerPos + PathFinder.CIRCLE8[i]]) {
                    GameScene.add(Blob.seed(shockerPos + PathFinder.CIRCLE8[i], 2, ShockerBlob.class));
                }
            }
        }

        private static final String SHOCKER_POS = "shocker_pos";
        private static final String SHOCKING_ORDINALS = "shocking_ordinals";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SHOCKER_POS, shockerPos);
            bundle.put(SHOCKING_ORDINALS, shockingOrdinals);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            shockerPos = bundle.getInt(SHOCKER_POS);
            shockingOrdinals = bundle.getBoolean(SHOCKING_ORDINALS);
        }

        public static class ShockerBlob extends Blob {

            {
                actPriority = BUFF_PRIORITY - 1;
                alwaysVisible = true;
            }

            @Override
            protected void evolve() {

                int cell;
                for (int i = area.left; i < area.right; i++) {
                    for (int j = area.top; j < area.bottom; j++) {
                        cell = i + j * Dungeon.level.width();
                        off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                        if (off[cell] > 0) {
                            volume += off[cell];
                        }

                        if (cur[cell] > 0 && off[cell] == 0) {

                            Char ch = Actor.findChar(cell);
                            if (ch != null && !(ch instanceof NewTengu)) {
                                ch.damage(2 + Dungeon.depth, Electricity.class);

                                if (ch == Dungeon.hero && !ch.isAlive()) {
                                    Dungeon.fail(NewTengu.class);
                                }
                            }

                        }
                    }
                }

            }

            @Override
            public void use(BlobEmitter emitter) {
                super.use(emitter);

                emitter.pour(SparkParticle.STATIC, 0.10f);
            }

            @Override
            public String tileDesc() {
                return Messages.get(this, "desc");
            }
        }

        public static class ShockerItem extends Item {

            {
                dropsDownHeap = true;
                unique = true;

                image = ItemSpriteSheet.TENGU_SHOCKER;
            }

            @Override
            public boolean doPickUp(Hero hero) {
                GLog.w(Messages.get(this, "cant_pickup"));
                return false;
            }

            @Override
            protected void onThrow(int cell) {
                super.onThrow(cell);
                if (throwingChar != null) {
                    Buff.append(throwingChar, ShockerAbility.class).shockerPos = cell;
                    throwingChar = null;
                } else {
                    Buff.append(curUser, ShockerAbility.class).shockerPos = cell;
                }
            }

            @Override
            public Emitter emitter() {
                Emitter emitter = new Emitter();
                emitter.pos(5, 5);
                emitter.fillTarget = false;
                emitter.pour(SparkParticle.FACTORY, 0.1f);
                return emitter;
            }
        }

    }
}
