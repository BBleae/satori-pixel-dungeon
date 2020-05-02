package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Flare;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.Surprise;
import studio.baka.satoripixeldungeon.effects.Wound;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.artifacts.DriedRose;
import studio.baka.satoripixeldungeon.items.artifacts.TimekeepersHourglass;
import studio.baka.satoripixeldungeon.items.rings.Ring;
import studio.baka.satoripixeldungeon.items.rings.RingOfWealth;
import studio.baka.satoripixeldungeon.items.stones.StoneOfAggression;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Lucky;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.features.Chasm;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Swiftthistle;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

public abstract class Mob extends Char {

    {
        name = Messages.get(this, "name");
        actPriority = MOB_PRIORITY;

        alignment = Alignment.ENEMY;
    }

    private static final String TXT_DIED = "You hear something died in the distance";

    protected static final String TXT_NOTICE1 = "?!";
    protected static final String TXT_RAGE = "#$%^";
    protected static final String TXT_EXP = "%+dEXP";

    public AiState SLEEPING = new Sleeping();
    public AiState HUNTING = new Hunting();
    public AiState WANDERING = new Wandering();
    public AiState FLEEING = new Fleeing();
    public AiState PASSIVE = new Passive();
    public AiState state = SLEEPING;

    public Class<? extends CharSprite> spriteClass;

    protected int target = -1;

    protected int defenseSkill = 0;

    public int EXP = 1;
    public int maxLvl = Hero.MAX_LEVEL;

    protected Char enemy;
    protected boolean enemySeen;
    protected boolean alerted = false;

    protected static final float TIME_TO_WAKE_UP = 1f;

    private static final String STATE = "state";
    private static final String SEEN = "seen";
    private static final String TARGET = "target";

    @Override
    public void storeInBundle(Bundle bundle) {

        super.storeInBundle(bundle);

        if (state == SLEEPING) {
            bundle.put(STATE, Sleeping.TAG);
        } else if (state == WANDERING) {
            bundle.put(STATE, Wandering.TAG);
        } else if (state == HUNTING) {
            bundle.put(STATE, Hunting.TAG);
        } else if (state == FLEEING) {
            bundle.put(STATE, Fleeing.TAG);
        } else if (state == PASSIVE) {
            bundle.put(STATE, Passive.TAG);
        }
        bundle.put(SEEN, enemySeen);
        bundle.put(TARGET, target);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {

        super.restoreFromBundle(bundle);

        String state = bundle.getString(STATE);
        switch (state) {
            case Sleeping.TAG:
                this.state = SLEEPING;
                break;
            case Wandering.TAG:
                this.state = WANDERING;
                break;
            case Hunting.TAG:
                this.state = HUNTING;
                break;
            case Fleeing.TAG:
                this.state = FLEEING;
                break;
            case Passive.TAG:
                this.state = PASSIVE;
                break;
        }

        enemySeen = bundle.getBoolean(SEEN);

        target = bundle.getInt(TARGET);
    }

    public CharSprite sprite() {
        return Reflection.newInstance(spriteClass);
    }

    @Override
    protected boolean act() {

        super.act();

        boolean justAlerted = alerted;
        alerted = false;

        if (justAlerted) {
            sprite.showAlert();
        } else {
            sprite.hideAlert();
            sprite.hideLost();
        }

        if (paralysed > 0) {
            enemySeen = false;
            spend(TICK);
            return true;
        }

        enemy = chooseEnemy();

        boolean enemyInFOV = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;

        return state.act(enemyInFOV, justAlerted);
    }

    //FIXME this is sort of a band-aid correction for allies needing more intelligent behaviour
    protected boolean intelligentAlly = false;

    protected Char chooseEnemy() {

        Terror terror = buff(Terror.class);
        if (terror != null) {
            Char source = (Char) Actor.findById(terror.object);
            if (source != null) {
                return source;
            }
        }

        //if we are an enemy, and have no target or current target isn't affected by aggression
        //then auto-prioritize a target that is affected by aggression, even another enemy
        if (alignment == Alignment.ENEMY
                && (enemy == null || enemy.buff(StoneOfAggression.Aggression.class) == null)) {
            for (Char ch : Actor.chars()) {
                if (ch != this && fieldOfView[ch.pos] &&
                        ch.buff(StoneOfAggression.Aggression.class) != null) {
                    return ch;
                }
            }
        }

        //find a new enemy if..
        boolean newEnemy = false;
        //we have no enemy, or the current one is dead/missing
        if (enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING)
            newEnemy = true;
            //We are an ally, and current enemy is another ally.
        else if (alignment == Alignment.ALLY && enemy.alignment == Alignment.ALLY)
            newEnemy = true;
            //We are amoked and current enemy is the hero
        else if (buff(Amok.class) != null && enemy == Dungeon.hero)
            newEnemy = true;
            //We are charmed and current enemy is what charmed us
        else if (buff(Charm.class) != null && buff(Charm.class).object == enemy.id())
            newEnemy = true;

        if (newEnemy) {

            HashSet<Char> enemies = new HashSet<>();

            //if the mob is amoked...
            if (buff(Amok.class) != null) {
                //try to find an enemy mob to attack first.
                for (Mob mob : Dungeon.level.mobs)
                    if (mob.alignment == Alignment.ENEMY && mob != this && fieldOfView[mob.pos])
                        enemies.add(mob);

                if (enemies.isEmpty()) {
                    //try to find ally mobs to attack second.
                    for (Mob mob : Dungeon.level.mobs)
                        if (mob.alignment == Alignment.ALLY && mob != this && fieldOfView[mob.pos])
                            enemies.add(mob);

                    if (enemies.isEmpty()) {
                        //try to find the hero third
                        if (fieldOfView[Dungeon.hero.pos]) {
                            enemies.add(Dungeon.hero);
                        }
                    }
                }

                //if the mob is an ally...
            } else if (alignment == Alignment.ALLY) {
                //look for hostile mobs to attack
                for (Mob mob : Dungeon.level.mobs)
                    if (mob.alignment == Alignment.ENEMY && fieldOfView[mob.pos])
                        //intelligent allies do not target mobs which are passive, wandering, or asleep
                        if (!intelligentAlly ||
                                (mob.state != mob.SLEEPING && mob.state != mob.PASSIVE && mob.state != mob.WANDERING)) {
                            enemies.add(mob);
                        }

                //if the mob is an enemy...
            } else if (alignment == Alignment.ENEMY) {
                //look for ally mobs to attack
                for (Mob mob : Dungeon.level.mobs)
                    if (mob.alignment == Alignment.ALLY && fieldOfView[mob.pos])
                        enemies.add(mob);

                //and look for the hero
                if (fieldOfView[Dungeon.hero.pos]) {
                    enemies.add(Dungeon.hero);
                }

            }

            Charm charm = buff(Charm.class);
            if (charm != null) {
                Char source = (Char) Actor.findById(charm.object);
                if (source != null && enemies.contains(source) && enemies.size() > 1) {
                    enemies.remove(source);
                }
            }

            //neutral characters in particular do not choose enemies.
            if (enemies.isEmpty()) {
                return null;
            } else {
                //go after the closest potential enemy, preferring the hero if two are equidistant
                Char closest = null;
                for (Char curr : enemies) {
                    if (closest == null
                            || Dungeon.level.distance(pos, curr.pos) < Dungeon.level.distance(pos, closest.pos)
                            || Dungeon.level.distance(pos, curr.pos) == Dungeon.level.distance(pos, closest.pos) && curr == Dungeon.hero) {
                        closest = curr;
                    }
                }
                return closest;
            }

        } else
            return enemy;
    }

    @Override
    public void add(Buff buff) {
        super.add(buff);
        if (buff instanceof Amok || buff instanceof Corruption) {
            state = HUNTING;
        } else if (buff instanceof Terror) {
            state = FLEEING;
        } else if (buff instanceof Sleep) {
            state = SLEEPING;
            postpone(Sleep.SWS);
        }
    }

    @Override
    public void remove(Buff buff) {
        super.remove(buff);
        if (buff instanceof Terror) {
            sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "rage"));
            state = HUNTING;
        }
    }

    protected boolean canAttack(Char enemy) {
        return Dungeon.level.adjacent(pos, enemy.pos);
    }

    protected boolean getCloser(int target) {

        if (rooted || target == pos) {
            return false;
        }

        int step = -1;

        if (Dungeon.level.adjacent(pos, target)) {

            path = null;

            if (Actor.findChar(target) == null && Dungeon.level.passable[target]) {
                step = target;
            }

        } else {

            boolean newPath = false;
            //scrap the current path if it's empty, no longer connects to the current location
            //or if it's extremely inefficient and checking again may result in a much better path
            if (path == null || path.isEmpty()
                    || !Dungeon.level.adjacent(pos, path.getFirst())
                    || path.size() > 2 * Dungeon.level.distance(pos, target))
                newPath = true;
            else if (path.getLast() != target) {
                //if the new target is adjacent to the end of the path, adjust for that
                //rather than scrapping the whole path.
                if (Dungeon.level.adjacent(target, path.getLast())) {
                    int last = path.removeLast();

                    if (path.isEmpty()) {

                        //shorten for a closer one
                        if (Dungeon.level.adjacent(target, pos)) {
                            path.add(target);
                            //extend the path for a further target
                        } else {
                            path.add(last);
                            path.add(target);
                        }

                    } else {
                        //if the new target is simply 1 earlier in the path shorten the path
                        if (path.getLast() != target) {
                            if (Dungeon.level.adjacent(target, path.getLast())) {
                                path.add(target);

                                //if the new target is further away, need to extend the path
                            } else {
                                path.add(last);
                                path.add(target);
                            }
                        }
                    }

                } else {
                    newPath = true;
                }

            }


            if (!newPath) {
                //looks ahead for path validity, up to length-1 or 4, but always at least 1.
                int lookAhead = (int) GameMath.gate(1, path.size() - 1, 4);
                for (int i = 0; i < lookAhead; i++) {
                    int cell = path.get(i);
                    if (!Dungeon.level.passable[cell] || (fieldOfView[cell] && Actor.findChar(cell) != null)) {
                        newPath = true;
                        break;
                    }
                }
            }

            if (newPath) {
                path = Dungeon.findPath(this, pos, target,
                        Dungeon.level.passable,
                        fieldOfView);
            }

            //if hunting something, don't follow a path that is extremely inefficient
            //FIXME this is fairly brittle, primarily it assumes that hunting mobs can't see through
            // permanent terrain, such that if their path is inefficient it's always because
            // of a temporary blockage, and therefore waiting for it to clear is the best option.
            if (path == null ||
                    (state == HUNTING && path.size() > Math.max(9, 2 * Dungeon.level.distance(pos, target)))) {
                return false;
            }

            step = path.removeFirst();
        }
        if (step != -1) {
            move(step);
            return true;
        } else {
            return false;
        }
    }

    protected boolean getFurther(int target) {
        if (rooted || target == pos) {
            return false;
        }

        int step = Dungeon.flee(this, pos, target,
                Dungeon.level.passable,
                fieldOfView);
        if (step != -1) {
            move(step);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateSpriteState() {
        super.updateSpriteState();
        if (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null
                || Dungeon.hero.buff(Swiftthistle.TimeBubble.class) != null)
            sprite.add(CharSprite.State.PARALYSED);
    }

    protected float attackDelay() {
        float delay = 1f;
        if (buff(Adrenaline.class) != null) delay /= 1.5f;
        return delay;
    }

    protected boolean doAttack(Char enemy) {

        boolean visible = Dungeon.level.heroFOV[pos];

        if (visible) {
            sprite.attack(enemy.pos);
        } else {
            attack(enemy);
        }

        spend(attackDelay());

        return !visible;
    }

    @Override
    public void onAttackComplete() {
        attack(enemy);
        super.onAttackComplete();
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (buff(Weakness.class) != null) {
            damage *= 0.67f;
        }
        return damage;
    }

    @Override
    public int defenseSkill(Char enemy) {
        boolean seen = (enemySeen && enemy.invisible == 0);
        if (enemy == Dungeon.hero && !Dungeon.hero.canSurpriseAttack()) seen = true;
        if (seen
                && paralysed == 0
                && !(alignment == Alignment.ALLY && enemy == Dungeon.hero)) {
            return this.defenseSkill;
        } else {
            return 0;
        }
    }

    protected boolean hitWithRanged = false;

    @Override
    public int defenseProcess(Char enemy, int damage) {

        if (enemy instanceof Hero && ((Hero) enemy).belongings.weapon instanceof MissileWeapon) {
            hitWithRanged = true;
        }

        if ((!enemySeen || enemy.invisible > 0)
                && enemy == Dungeon.hero && Objects.requireNonNull(Dungeon.hero).canSurpriseAttack()) {
            Statistics.sneakAttacks++;
            Badges.validateRogueUnlock();
            if (Objects.requireNonNull(enemy).buff(Preparation.class) != null) {
                Wound.hit(this);
            } else {
                Surprise.hit(this);
            }
        }

        //if attacked by something else than current target, and that thing is closer, switch targets
        if (this.enemy == null
                || (enemy != this.enemy && (Dungeon.level.distance(pos, Objects.requireNonNull(enemy).pos) < Dungeon.level.distance(pos, this.enemy.pos)))) {
            aggro(enemy);
            target = Objects.requireNonNull(enemy).pos;
        }

        if (buff(SoulMark.class) != null) {
            int restoration = Math.min(damage, HP);

            //physical damage that doesn't come from the hero is less effective
            if (enemy != Dungeon.hero) {
                restoration = Math.round(restoration * 0.4f);
            }

            Buff.affect(Dungeon.hero, Hunger.class).satisfy(restoration);
            Dungeon.hero.HP = (int) Math.ceil(Math.min(Dungeon.hero.HT, Dungeon.hero.HP + (restoration * 0.4f)));
            Dungeon.hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
        }

        return damage;
    }

    public boolean surprisedBy(Char enemy) {
        return !enemySeen && enemy == Dungeon.hero;
    }

    public void aggro(Char ch) {
        enemy = ch;
        if (state != PASSIVE) {
            state = HUNTING;
        }
    }

    public boolean isTargeting(Char ch) {
        return enemy == ch;
    }

    @Override
    public void damage(int dmg, Object src) {

        if (state == SLEEPING) {
            state = WANDERING;
        }
        if (state != HUNTING) {
            alerted = true;
        }

        super.damage(dmg, src);
    }


    @Override
    public void destroy() {

        super.destroy();

        Dungeon.level.mobs.remove(this);

        if (Dungeon.hero.isAlive()) {

            if (alignment == Alignment.ENEMY) {
                Statistics.enemiesSlain++;
                Badges.validateMonstersSlain();
                Statistics.qualifiedForNoKilling = false;

                int exp = Dungeon.hero.lvl <= maxLvl ? EXP : 0;
                if (exp > 0) {
                    Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "exp", exp));
                }
                Dungeon.hero.earnExp(exp, getClass());
            }
        }
    }

    @Override
    public void die(Object cause) {

        if (hitWithRanged) {
            Statistics.thrownAssists++;
            Badges.validateHuntressUnlock();
        }

        if (cause == Chasm.class) {
            //50% chance to round up, 50% to round down
            if (EXP % 2 == 1) EXP += Random.Int(2);
            EXP /= 2;
        }

        if (alignment == Alignment.ENEMY) {
            rollToDropLoot();
        }

        if (Dungeon.hero.isAlive() && !Dungeon.level.heroFOV[pos]) {
            GLog.i(Messages.get(this, "died"));
        }

        super.die(cause);
    }

    public void rollToDropLoot() {
        if (Dungeon.hero.lvl > maxLvl + 2) return;

        float lootChance = this.lootChance;
        lootChance *= RingOfWealth.dropChanceMultiplier(Dungeon.hero);

        if (Random.Float() < lootChance) {
            Item loot = createLoot();
            if (loot != null) {
                Dungeon.level.drop(loot, pos).sprite.drop();
            }
        }

        //ring of wealth logic
        if (Ring.getBonus(Dungeon.hero, RingOfWealth.Wealth.class) > 0) {
            int rolls = 1;
            if (properties.contains(Property.BOSS)) rolls = 15;
            else if (properties.contains(Property.MINIBOSS)) rolls = 5;
            ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(Dungeon.hero, rolls);
            if (bonus != null && !bonus.isEmpty()) {
                for (Item b : bonus) Dungeon.level.drop(b, pos).sprite.drop();
                if (RingOfWealth.latestDropWasRare) {
                    new Flare(8, 48).color(0xAA00FF, true).show(sprite, 3f);
                    RingOfWealth.latestDropWasRare = false;
                } else {
                    new Flare(8, 24).color(0xFFFFFF, true).show(sprite, 3f);
                }
            }
        }

        //lucky enchant logic
        if (Dungeon.hero.lvl <= maxLvl && buff(Lucky.LuckProc.class) != null) {
            new Flare(8, 24).color(0x00FF00, true).show(sprite, 3f);
            Dungeon.level.drop(Lucky.genLoot(), pos).sprite.drop();
        }
    }

    protected Object loot = null;
    protected float lootChance = 0;

    @SuppressWarnings("unchecked")
    protected Item createLoot() {
        Item item;
        if (loot instanceof Generator.Category) {

            item = Generator.random((Generator.Category) loot);

        } else if (loot instanceof Class<?>) {

            item = Generator.random((Class<? extends Item>) loot);

        } else {

            item = (Item) loot;

        }
        return item;
    }

    public boolean reset() {
        return false;
    }

    public void beckon(int cell) {

        notice();

        if (state != HUNTING) {
            state = WANDERING;
        }
        target = cell;
    }

    public String description() {
        return Messages.get(this, "desc");
    }

    public void notice() {
        sprite.showAlert();
    }

    public void yell(String str) {
        GLog.n("%s: \"%s\" ", Messages.titleCase(name), str);
    }

    //returns true when a mob sees the hero, and is currently targeting them.
    public boolean focusingHero() {
        return enemySeen && (target == Dungeon.hero.pos);
    }

    public interface AiState {
        boolean act(boolean enemyInFOV, boolean justAlerted);
    }

    protected class Sleeping implements AiState {

        public static final String TAG = "SLEEPING";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV && Random.Float(distance(enemy) + enemy.stealth() + (enemy.flying ? 2 : 0)) < 1) {

                enemySeen = true;

                notice();
                state = HUNTING;
                target = enemy.pos;

                if (Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
                    for (Mob mob : Dungeon.level.mobs) {
                        if (Dungeon.level.distance(pos, mob.pos) <= 8 && mob.state != mob.HUNTING) {
                            mob.beckon(target);
                        }
                    }
                }

                spend(TIME_TO_WAKE_UP);

            } else {

                enemySeen = false;

                spend(TICK);

            }
            return true;
        }
    }

    protected class Wandering implements AiState {

        public static final String TAG = "WANDERING";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV && (justAlerted || Random.Float(distance(enemy) / 2f + enemy.stealth()) < 1)) {

                enemySeen = true;

                notice();
                alerted = true;
                state = HUNTING;
                target = enemy.pos;

                if (Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
                    for (Mob mob : Dungeon.level.mobs) {
                        if (Dungeon.level.distance(pos, mob.pos) <= 8 && mob.state != mob.HUNTING) {
                            mob.beckon(target);
                        }
                    }
                }

            } else {

                enemySeen = false;

                int oldPos = pos;
                if (target != -1 && getCloser(target)) {
                    spend(1 / speed());
                    return moveSprite(oldPos, pos);
                } else {
                    target = Dungeon.level.randomDestination();
                    spend(TICK);
                }

            }
            return true;
        }
    }

    protected class Hunting implements AiState {

        public static final String TAG = "HUNTING";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy(enemy) && canAttack(enemy)) {

                return doAttack(enemy);

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else if (enemy == null) {
                    state = WANDERING;
                    target = Dungeon.level.randomDestination();
                    return true;
                }

                int oldPos = pos;
                if (target != -1 && getCloser(target)) {

                    spend(1 / speed());
                    return moveSprite(oldPos, pos);

                } else {
                    spend(TICK);
                    if (!enemyInFOV) {
                        sprite.showLost();
                        state = WANDERING;
                        target = Dungeon.level.randomDestination();
                    }
                    return true;
                }
            }
        }
    }

    protected class Fleeing implements AiState {

        public static final String TAG = "FLEEING";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;
            //loses target when 0-dist rolls a 6 or greater.
            if (enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level.distance(pos, target)) >= 6) {
                target = -1;

                //if enemy isn't in FOV, keep running from their previous position.
            } else if (enemyInFOV) {
                target = enemy.pos;
            }

            int oldPos = pos;
            if (target != -1 && getFurther(target)) {

                spend(1 / speed());
                return moveSprite(oldPos, pos);

            } else {

                spend(TICK);
                nowhereToRun();

                return true;
            }
        }

        protected void nowhereToRun() {
        }
    }

    protected class Passive implements AiState {

        public static final String TAG = "PASSIVE";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = false;
            spend(TICK);
            return true;
        }
    }


    private static final ArrayList<Mob> heldAllies = new ArrayList<>();

    public static void holdAllies(Level level) {
        heldAllies.clear();
        for (Mob mob : level.mobs.toArray(new Mob[0])) {
            //preserve the ghost no matter where they are
            if (mob instanceof DriedRose.GhostHero) {
                ((DriedRose.GhostHero) mob).clearDefensingPos();
                level.mobs.remove(mob);
                heldAllies.add(mob);

                //preserve intelligent allies if they are near the hero
            } else if (mob.alignment == Alignment.ALLY
                    && mob.intelligentAlly
                    && Dungeon.level.distance(Dungeon.hero.pos, mob.pos) <= 3) {
                level.mobs.remove(mob);
                heldAllies.add(mob);
            }
        }
    }

    public static void restoreAllies(Level level, int pos) {
        if (!heldAllies.isEmpty()) {

            ArrayList<Integer> candidatePositions = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.solid[i + pos] && level.findMob(i + pos) == null) {
                    candidatePositions.add(i + pos);
                }
            }
            Collections.shuffle(candidatePositions);

            for (Mob ally : heldAllies) {
                level.mobs.add(ally);
                ally.state = ally.WANDERING;

                if (!candidatePositions.isEmpty()) {
                    ally.pos = candidatePositions.remove(0);
                } else {
                    ally.pos = pos;
                }

            }
        }
        heldAllies.clear();
    }

    public static void clearHeldAllies() {
        heldAllies.clear();
    }
}

