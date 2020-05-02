package studio.baka.satoripixeldungeon.actors;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Electricity;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.items.BrokenSeal;
import studio.baka.satoripixeldungeon.items.armor.glyphs.AntiMagic;
import studio.baka.satoripixeldungeon.items.armor.glyphs.Brimstone;
import studio.baka.satoripixeldungeon.items.armor.glyphs.Potential;
import studio.baka.satoripixeldungeon.items.rings.RingOfElements;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRetribution;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import studio.baka.satoripixeldungeon.items.stones.StoneOfAggression;
import studio.baka.satoripixeldungeon.items.wands.WandOfFireblast;
import studio.baka.satoripixeldungeon.items.wands.WandOfLightning;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Blazing;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Grim;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Shocking;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.items.weapon.missiles.darts.ShockingDart;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.features.Chasm;
import studio.baka.satoripixeldungeon.levels.features.Door;
import studio.baka.satoripixeldungeon.levels.traps.GrimTrap;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("rawtypes")
public abstract class Char extends Actor {

    public int pos = 0;

    public CharSprite sprite;

    public String name = "mob";

    public int HT;
    public int HP;

    protected float baseSpeed = 1;
    protected PathFinder.Path path;

    public int paralysed = 0;
    public boolean rooted = false;
    public boolean flying = false;
    public int invisible = 0;

    //these are relative to the hero
    public enum Alignment {
        ENEMY,
        NEUTRAL,
        ALLY
    }

    public Alignment alignment;

    public int viewDistance = 8;

    public boolean[] fieldOfView = null;

    private final HashSet<Buff> buffs = new HashSet<>();

    @Override
    protected boolean act() {
        if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
            fieldOfView = new boolean[Dungeon.level.length()];
        }
        Dungeon.level.updateFieldOfView(this, fieldOfView);
        return false;
    }

    public boolean canInteract(Hero h) {
        return Dungeon.level.adjacent(pos, h.pos);
    }

    //swaps places by default
    public boolean interact() {

        if (!Dungeon.level.passable[pos] && !Dungeon.hero.flying) {
            return true;
        }

        int curPos = pos;

        moveSprite(pos, Dungeon.hero.pos);
        move(Dungeon.hero.pos);

        Dungeon.hero.sprite.move(Dungeon.hero.pos, curPos);
        Dungeon.hero.move(curPos);

        Dungeon.hero.spend(1 / Dungeon.hero.speed());
        Dungeon.hero.busy();

        return true;
    }

    protected boolean moveSprite(int from, int to) {

        if (sprite.isVisible() && (Dungeon.level.heroFOV[from] || Dungeon.level.heroFOV[to])) {
            sprite.move(from, to);
        } else {
            sprite.turnTo(from, to);
            sprite.place(to);
        }
        return true;
    }

    protected static final String POS = "pos";
    protected static final String TAG_HP = "HP";
    protected static final String TAG_HT = "HT";
    protected static final String TAG_SHLD = "SHLD";
    protected static final String BUFFS = "buffs";

    @Override
    public void storeInBundle(Bundle bundle) {

        super.storeInBundle(bundle);

        bundle.put(POS, pos);
        bundle.put(TAG_HP, HP);
        bundle.put(TAG_HT, HT);
        bundle.put(BUFFS, buffs);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {

        super.restoreFromBundle(bundle);

        pos = bundle.getInt(POS);
        HP = bundle.getInt(TAG_HP);
        HT = bundle.getInt(TAG_HT);

        for (Bundlable b : bundle.getCollection(BUFFS)) {
            if (b != null) {
                ((Buff) b).attachTo(this);
            }
        }

        //pre-0.7.0
        if (bundle.contains("SHLD")) {
            int legacySHLD = bundle.getInt("SHLD");
            //attempt to find the buff that may have given the shield
            ShieldBuff buff = buff(Brimstone.BrimstoneShield.class);
            if (buff != null) legacySHLD -= buff.shielding();
            if (legacySHLD > 0) {
                BrokenSeal.WarriorShield buff2 = buff(BrokenSeal.WarriorShield.class);
                if (buff != null) buff2.supercharge(legacySHLD);
            }
        }
    }

    public boolean attack(Char enemy) {

        if (enemy == null) return false;

        boolean visibleFight = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[enemy.pos];

        if (hit(this, enemy, false)) {

            int dr = enemy.drRoll();

            if (this instanceof Hero) {
                Hero h = (Hero) this;
                if (h.belongings.weapon instanceof MissileWeapon
                        && h.subClass == HeroSubClass.SNIPER
                        && !Dungeon.level.adjacent(h.pos, enemy.pos)) {
                    dr = 0;
                }
            }

            int dmg;
            Preparation prep = buff(Preparation.class);
            if (prep != null) {
                dmg = prep.damageRoll(this, enemy);
            } else {
                dmg = damageRoll();
            }

            int effectiveDamage = enemy.defenseProcess(this, dmg);
            effectiveDamage = Math.max(effectiveDamage - dr, 0);
            effectiveDamage = attackProc(enemy, effectiveDamage);

            if (visibleFight) {
                Sample.INSTANCE.play(Assets.SND_HIT, 1, 1, Random.Float(0.8f, 1.25f));
            }

            // If the enemy is already dead, interrupt the attack.
            // This matters as defence procs can sometimes inflict self-damage, such as armor glyphs.
            if (!enemy.isAlive()) {
                return true;
            }

            //TODO: consider revisiting this and shaking in more cases.
            float shake = 0f;
            if (enemy == Dungeon.hero)
                shake = effectiveDamage / (enemy.HT / 4);

            if (shake > 1f)
                Camera.main.shake(GameMath.gate(1, shake, 5), 0.3f);

            enemy.damage(effectiveDamage, this);

            if (buff(FireImbue.class) != null)
                buff(FireImbue.class).proc(enemy);
            if (buff(EarthImbue.class) != null)
                buff(EarthImbue.class).proc(enemy);
            if (buff(FrostImbue.class) != null)
                buff(FrostImbue.class).proc(enemy);

            enemy.sprite.bloodBurstA(sprite.center(), effectiveDamage);
            enemy.sprite.flash();

            if (!enemy.isAlive() && visibleFight) {
                if (enemy == Dungeon.hero) {

                    if (this == Dungeon.hero) {
                        return true;
                    }

                    Dungeon.fail(getClass());
                    GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", name)));

                } else if (this == Dungeon.hero) {
                    GLog.i(Messages.capitalize(Messages.get(Char.class, "defeat", enemy.name)));
                }
            }

            return true;

        } else {

            if (visibleFight) {
                String defense = enemy.defenseVerb();
                enemy.sprite.showStatus(CharSprite.NEUTRAL, defense);

                Sample.INSTANCE.play(Assets.SND_MISS);
            }

            return false;

        }
    }

    public static boolean hit(Char attacker, Char defender, boolean magic) {
        float acuRoll = Random.Float(attacker.attackSkill(defender));
        float defRoll = Random.Float(defender.defenseSkill(attacker));
        if (attacker.buff(Bless.class) != null) acuRoll *= 1.20f;
        if (defender.buff(Bless.class) != null) defRoll *= 1.20f;
        return (magic ? acuRoll * 2 : acuRoll) >= defRoll;
    }

    public int attackSkill(Char target) {
        return 0;
    }

    public int defenseSkill(Char enemy) {
        return 0;
    }

    public String defenseVerb() {
        return Messages.get(this, "def_verb");
    }

    public int drRoll() {
        return 0;
    }

    public int damageRoll() {
        return 1;
    }

    public int attackProc(Char enemy, int damage) {
        return damage;
    }

    public int defenseProcess(Char enemy, int damage) {
        return damage;
    }

    public float speed() {
        float speed = baseSpeed;
        if (buff(Cripple.class) != null) speed /= 2f;
        if (buff(Stamina.class) != null) speed *= 1.5f;
        if (buff(Adrenaline.class) != null) speed *= 2f;
        if (buff(Haste.class) != null) speed *= 3f;
        return speed;
    }

    //used so that buffs(Shieldbuff.class) isn't called every time unnecessarily
    private int cachedShield = 0;
    public boolean needsShieldUpdate = true;

    public int shielding() {
        if (!needsShieldUpdate) {
            return cachedShield;
        }

        cachedShield = 0;
        for (ShieldBuff s : buffs(ShieldBuff.class)) {
            cachedShield += s.shielding();
        }
        needsShieldUpdate = false;
        return cachedShield;
    }

    public void damage(int dmg, Object src) {

        if (!isAlive() || dmg < 0) {
            return;
        }
        Terror t = buff(Terror.class);
        if (t != null) {
            t.recover();
        }
        Charm c = buff(Charm.class);
        if (c != null) {
            c.recover();
        }
        if (this.buff(Frost.class) != null) {
            Buff.detach(this, Frost.class);
        }
        if (this.buff(MagicalSleep.class) != null) {
            Buff.detach(this, MagicalSleep.class);
        }
        if (this.buff(Doom.class) != null) {
            dmg *= 2;
        }

        Class<?> srcClass = src.getClass();
        if (isImmune(srcClass)) {
            dmg = 0;
        } else {
            dmg = Math.round(dmg * resist(srcClass));
        }

        //TODO improve this when I have proper damage source logic
        if (AntiMagic.RESISTS.contains(src.getClass()) && buff(ArcaneArmor.class) != null) {
            dmg -= Random.NormalIntRange(0, buff(ArcaneArmor.class).level());
            if (dmg < 0) dmg = 0;
        }

        if (buff(Paralysis.class) != null) {
            buff(Paralysis.class).processDamage(dmg);
        }

        int shielded = dmg;
        //FIXME: when I add proper damage properties, should add an IGNORES_SHIELDS property to use here.
        if (!(src instanceof Hunger)) {
            for (ShieldBuff s : buffs(ShieldBuff.class)) {
                dmg = s.absorbDamage(dmg);
                if (dmg == 0) break;
            }
        }
        shielded -= dmg;
        HP -= dmg;

        if (sprite != null) {
            sprite.showStatus(HP > HT / 2 ?
                            CharSprite.WARNING :
                            CharSprite.NEGATIVE,
                    Integer.toString(dmg + shielded));
        }

        if (HP < 0) HP = 0;

        if (!isAlive()) {
            die(src);
        }
    }

    public void destroy() {
        HP = 0;
        Actor.remove(this);
    }

    public void die(Object src) {
        destroy();
        if (src != Chasm.class) sprite.die();
    }

    public boolean isAlive() {
        return HP > 0;
    }

    @Override
    protected void spend(float time) {

        float timeScale = 1f;
        if (buff(Slow.class) != null) {
            timeScale *= 0.5f;
            //slowed and chilled do not stack
        } else if (buff(Chill.class) != null) {
            timeScale *= buff(Chill.class).speedFactor();
        }
        if (buff(Speed.class) != null) {
            timeScale *= 2.0f;
        }

        super.spend(time / timeScale);
    }

    public synchronized HashSet<Buff> buffs() {
        return new HashSet<>(buffs);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Buff> HashSet<T> buffs(Class<T> c) {
        HashSet<T> filtered = new HashSet<>();
        for (Buff b : buffs) {
            if (c.isInstance(b)) {
                filtered.add((T) b);
            }
        }
        return filtered;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Buff> T buff(Class<T> c) {
        for (Buff b : buffs) {
            if (c.isInstance(b)) {
                return (T) b;
            }
        }
        return null;
    }

    public synchronized boolean isCharmedBy(Char ch) {
        int chID = ch.id();
        for (Buff b : buffs) {
            if (b instanceof Charm && ((Charm) b).object == chID) {
                return true;
            }
        }
        return false;
    }

    public synchronized void add(Buff buff) {

        buffs.add(buff);
        Actor.add(buff);

        if (sprite != null && buff.announced)
            switch (buff.type) {
                case POSITIVE:
                    sprite.showStatus(CharSprite.POSITIVE, buff.toString());
                    break;
                case NEGATIVE:
                    sprite.showStatus(CharSprite.NEGATIVE, buff.toString());
                    break;
                case NEUTRAL:
                default:
                    sprite.showStatus(CharSprite.NEUTRAL, buff.toString());
                    break;
            }

    }

    public synchronized void remove(Buff buff) {

        buffs.remove(buff);
        Actor.remove(buff);

    }

    public synchronized void remove(Class<? extends Buff> buffClass) {
        for (Buff buff : buffs(buffClass)) {
            remove(buff);
        }
    }

    @Override
    protected synchronized void onRemove() {
        for (Buff buff : buffs.toArray(new Buff[0])) {
            buff.detach();
        }
    }

    public synchronized void updateSpriteState() {
        for (Buff buff : buffs) {
            buff.fx(true);
        }
    }

    public float stealth() {
        return 0;
    }

    public void move(int step) {

        if (Dungeon.level.adjacent(step, pos) && buff(Vertigo.class) != null) {
            sprite.interruptMotion();
            int newPos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
            if (!(Dungeon.level.passable[newPos] || Dungeon.level.avoid[newPos]) || Actor.findChar(newPos) != null)
                return;
            else {
                sprite.move(pos, newPos);
                step = newPos;
            }
        }

        if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
            Door.leave(pos);
        }

        pos = step;

        if (this != Dungeon.hero) {
            sprite.visible = Dungeon.level.heroFOV[pos];
        }

        Dungeon.level.occupyCell(this);
    }

    public int distance(Char other) {
        return Dungeon.level.distance(pos, other.pos);
    }

    public void onMotionComplete() {
        //Does nothing by default
        //The main actor thread already accounts for motion,
        // so calling next() here isn't necessary (see Actor.process)
    }

    public void onAttackComplete() {
        next();
    }

    public void onOperateComplete() {
        next();
    }

    protected final HashSet<Class> resistances = new HashSet<>();

    //returns percent effectiveness after resistances
    //TODO currently resistances reduce effectiveness by a static 50%, and do not stack.
    @SuppressWarnings("rawtypes")
    public float resist(Class effect) {
        HashSet<Class> resists = new HashSet<>(resistances);
        for (Property p : properties()) {
            resists.addAll(p.resistances());
        }
        for (Buff b : buffs()) {
            resists.addAll(b.resistances());
        }

        float result = 1f;
        for (Class c : resists) {
            //noinspection unchecked
            if (c.isAssignableFrom(effect)) {
                result *= 0.5f;
            }
        }
        return result * RingOfElements.resist(this, effect);
    }

    protected final HashSet<Class> immunities = new HashSet<>();

    public boolean isImmune(Class effect) {
        HashSet<Class> immunes = new HashSet<>(immunities);
        for (Property p : properties()) {
            immunes.addAll(p.immunities());
        }
        for (Buff b : buffs()) {
            immunes.addAll(b.immunities());
        }

        for (Class c : immunes) {
            //noinspection unchecked
            if (c.isAssignableFrom(effect)) {
                return true;
            }
        }
        return false;
    }

    protected HashSet<Property> properties = new HashSet<>();

    public HashSet<Property> properties() {
        return new HashSet<>(properties);
    }

    public enum Property {
        BOSS(new HashSet<>(Arrays.asList(Grim.class, GrimTrap.class, ScrollOfRetribution.class, ScrollOfPsionicBlast.class)),
                new HashSet<>(Arrays.asList(Corruption.class, StoneOfAggression.Aggression.class))),
        MINIBOSS(new HashSet<>(),
                new HashSet<>(Collections.singletonList(Corruption.class))),
        UNDEAD,
        DEMONIC,
        INORGANIC(new HashSet<>(),
                new HashSet<>(Arrays.asList(Bleeding.class, ToxicGas.class, Poison.class))),
        BLOB_IMMUNE(new HashSet<>(),
                new HashSet<>(Collections.singletonList(Blob.class))),
        FIERY(new HashSet<>(Collections.singletonList(WandOfFireblast.class)),
                new HashSet<>(Arrays.asList(Burning.class, Blazing.class))),
        ACIDIC(new HashSet<>(Collections.singletonList(Corrosion.class)),
                new HashSet<>(Collections.singletonList(Ooze.class))),
        ELECTRIC(new HashSet<>(Arrays.asList(WandOfLightning.class, Shocking.class, Potential.class, Electricity.class, ShockingDart.class)),
                new HashSet<>()),
        IMMOVABLE;

        private final HashSet<Class> resistances;
        private final HashSet<Class> immunities;

        Property() {
            this(new HashSet<>(), new HashSet<>());
        }

        Property(HashSet<Class> resistances, HashSet<Class> immunities) {
            this.resistances = resistances;
            this.immunities = immunities;
        }

        public HashSet<Class> resistances() {
            return new HashSet<>(resistances);
        }

        public HashSet<Class> immunities() {
            return new HashSet<>(immunities);
        }
    }
}
