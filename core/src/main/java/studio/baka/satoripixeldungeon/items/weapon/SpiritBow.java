package studio.baka.satoripixeldungeon.items.weapon;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Regrowth;
import studio.baka.satoripixeldungeon.actors.buffs.Barkskin;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Roots;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.rings.RingOfFuror;
import studio.baka.satoripixeldungeon.items.rings.RingOfSharpshooting;
import studio.baka.satoripixeldungeon.items.wands.WandOfRegrowth;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.plants.Starflower;
import studio.baka.satoripixeldungeon.scenes.CellSelector;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.sprites.MissileSprite;
import studio.baka.satoripixeldungeon.ui.QuickSlotButton;
import studio.baka.satoripixeldungeon.utils.BArray;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SpiritBow extends Weapon {

    public static final String AC_SHOOT = "SHOOT";
    public static final String AC_CAO = "CAO";

    {
        image = ItemSpriteSheet.SPIRIT_BOW;

        defaultAction = AC_SHOOT;
        usesTargeting = true;

        unique = true;
        bones = false;
    }

    public boolean sniperSpecial = false;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_EQUIP);
        actions.add(AC_SHOOT);
        actions.add(AC_CAO);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {
            curUser = hero;
            curItem = this;
            GameScene.selectCell(shooter);
        }

        if (action.equals(AC_CAO)) {
            curUser = hero;
            curItem = this;
            if (hero.mana >= 20) {
                //GameScene.selectCell( shooter );
                cao(hero.pos);
                hero.mana -= 20;
            } else {
                GLog.w(Messages.get(this, "not_enough_mana"), hero.mana, hero.getMaxmana(), 20);
            }
        }
    }

    @Override
    public String info() {
        String info = desc();

        info += "\n\n" + Messages.get(SpiritBow.class, "stats",
                Math.round(augment.damageFactor(min())),
                Math.round(augment.damageFactor(max())),
                STRReq());

        if (STRReq() > Dungeon.hero.STR()) {
            info += " " + Messages.get(Weapon.class, "too_heavy");
        } else if (Dungeon.hero.STR() > STRReq()) {
            info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
        }

        switch (augment) {
            case SPEED:
                info += "\n\n" + Messages.get(Weapon.class, "faster");
                break;
            case DAMAGE:
                info += "\n\n" + Messages.get(Weapon.class, "stronger");
                break;
            case NONE:
        }

        if (enchantment != null && (cursedKnown || !enchantment.curse())) {
            info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
            info += " " + Messages.get(enchantment, "desc");
        }

        if (cursed && isEquipped(Dungeon.hero)) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
        } else if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed");
        } else if (!isIdentified() && cursedKnown) {
            info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
        }

        info += "\n\n" + Messages.get(MissileWeapon.class, "distance");

        return info;
    }

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);
        //strength req decreases at +1,+3,+6,+10,etc.
        return 10 - (int) (Math.sqrt(8 * lvl + 1) - 1) / 2;
    }

    @Override
    public int min(int lvl) {
        return 1 + (int) (Dungeon.hero.lvl / 2.5f)
                + RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
                + (curseInfusionBonus ? 1 : 0);
    }

    @Override
    public int max(int lvl) {
        return 6 + (int) (Dungeon.hero.lvl / 1.75f)
                + 2 * RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
                + (curseInfusionBonus ? 2 : 0);
    }

    private int targetPos;

    @Override
    public int damageRoll(Char owner) {
        int damage = augment.damageFactor(super.damageRoll(owner));

        if (owner instanceof Hero) {
            int exStr = ((Hero) owner).STR() - STRReq();
            if (exStr > 0) {
                damage += Random.IntRange(0, exStr);
            }
        }

        if (sniperSpecial) {
            switch (augment) {
                case NONE:
                    damage = Math.round(damage * 0.667f);
                    break;
                case SPEED:
                    damage = Math.round(damage * 0.5f);
                    break;
                case DAMAGE:
                    int distance = Dungeon.level.distance(owner.pos, targetPos) - 1;
                    damage = Math.round(damage * (1f + 0.1f * distance));
                    break;
            }
        }

        return damage;
    }

    @Override
    public float speedFactor(Char owner) {
        if (sniperSpecial) {
            switch (augment) {
                case NONE:
                default:
                    return 0f;
                case SPEED:
                    return 1f * RingOfFuror.attackDelayMultiplier(owner);
                case DAMAGE:
                    return 2f * RingOfFuror.attackDelayMultiplier(owner);
            }
        } else {
            return super.speedFactor(owner);
        }
    }

    @Override
    public int level() {
        //need to check if hero is null for loading an upgraded bow from pre-0.7.0
        return (Dungeon.hero == null ? 0 : (int) (Dungeon.hero.lvl / 5f))
                + (curseInfusionBonus ? 1 : 0);
    }

    //for fetching upgrades from a boomerang from pre-0.7.1
    public int spentUpgrades() {
        return super.level() - (curseInfusionBonus ? 1 : 0);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public SpiritArrow knockArrow() {
        return new SpiritArrow();
    }

    public void cao(int cell) {
        if (Dungeon.level.heroFOV[cell]) {
            Splash.at(cell, 0x00FF00, 30);
        }

        ArrayList<Integer> plantCandidates = new ArrayList<>();

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 3);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char ch = Actor.findChar(i);
                if (ch != null) {
                    if (ch.alignment != Dungeon.hero.alignment) {
                        Buff.affect(ch, Roots.class, 5f);
                    }
                } else if (Dungeon.level.map[i] == Terrain.EMPTY ||
                        Dungeon.level.map[i] == Terrain.EMBERS ||
                        Dungeon.level.map[i] == Terrain.EMPTY_DECO ||
                        Dungeon.level.map[i] == Terrain.GRASS ||
                        Dungeon.level.map[i] == Terrain.HIGH_GRASS ||
                        Dungeon.level.map[i] == Terrain.FURROWED_GRASS) {

                    plantCandidates.add(i);
                }
                GameScene.add(Blob.seed(i, 3, Regrowth.class));
            }
        }

        int plants = Random.chances(new float[]{0, 9, 6, 3, 2, 1});

        for (int i = 0; i < plants; i++) {
            Integer plantPos = Random.element(plantCandidates);
            if (plantPos != null) {
                Dungeon.level.plant((Plant.Seed) Generator.random(Generator.Category.SEED), plantPos);
                plantCandidates.remove(plantPos);
            }
        }

        Integer plantPos = Random.element(plantCandidates);
        if (plantPos != null) {
            Plant.Seed plant;
            switch (Random.chances(new float[]{0, 6, 3, 1})) {
                case 1:
                default:
                    plant = new WandOfRegrowth.Dewcatcher.Seed();
                    break;
                case 2:
                    plant = new WandOfRegrowth.Seedpod.Seed();
                    break;
                case 3:
                    plant = new Starflower.Seed();
                    break;
            }
            Dungeon.level.plant(plant, plantPos);
        }
    }


    public class SpiritArrow extends MissileWeapon {

        {
            image = ItemSpriteSheet.SPIRIT_ARROW;
        }

        @Override
        public int damageRoll(Char owner) {
            return SpiritBow.this.damageRoll(owner);
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return SpiritBow.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return SpiritBow.this.proc(attacker, defender, damage);
        }

        @Override
        public float speedFactor(Char user) {
            return SpiritBow.this.speedFactor(user);
        }

        @Override
        public float accuracyFactor(Char owner) {
            if (sniperSpecial && SpiritBow.this.augment == Augment.DAMAGE) {
                return Float.POSITIVE_INFINITY;
            } else {
                return super.accuracyFactor(owner);
            }
        }

        @Override
        public int STRReq(int lvl) {
            return SpiritBow.this.STRReq(lvl);
        }

        @Override
        protected void onThrow(int cell) {
            Char enemy = Actor.findChar(cell);
            if (enemy == null) {
                parent = null;
                Splash.at(cell, 0xCC99FFFF, 1);
            } else if (enemy == curUser) {
                parent = null;
                Splash.at(cell, 0xCC99FFFF, 1);
                Buff.affect(enemy, Barkskin.class).set(level(), 1);
            } else {
                if (!curUser.shoot(enemy, this)) {
                    Splash.at(cell, 0xCC99FFFF, 1);
                    Buff.prolong(enemy, Roots.class, 5f);
                }
                if (sniperSpecial && SpiritBow.this.augment != Augment.SPEED) sniperSpecial = false;
            }
        }

        int flurryCount = -1;

        @Override
        public void cast(final Hero user, final int dst) {
            final int cell = throwPos(user, dst);
            SpiritBow.this.targetPos = cell;
            if (sniperSpecial && SpiritBow.this.augment == Augment.SPEED) {
                if (flurryCount == -1) flurryCount = 3;

                final Char enemy = Actor.findChar(cell);

                if (enemy == null) {
                    user.spendAndNext(castDelay(user, dst));
                    sniperSpecial = false;
                    flurryCount = -1;
                    return;
                }
                QuickSlotButton.target(enemy);

                final boolean last = flurryCount == 1;

                user.busy();

                Sample.INSTANCE.play(Assets.SND_MISS, 0.6f, 0.6f, 1.5f);

                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                        reset(user.sprite,
                                cell,
                                this,
                                () -> {
                                    if (enemy.isAlive()) {
                                        curUser = user;
                                        onThrow(cell);
                                    }

                                    if (last) {
                                        user.spendAndNext(castDelay(user, dst));
                                        sniperSpecial = false;
                                        flurryCount = -1;
                                    }
                                });

                user.sprite.zap(cell, () -> {
                    flurryCount--;
                    if (flurryCount > 0) {
                        cast(user, dst);
                    }
                });

            } else {
                super.cast(user, dst);
            }
        }
    }

    private final CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                if (curUser.mana >= 1) {
                    knockArrow().cast(curUser, target);
                    curUser.mana--;
                } else
                    GLog.w(Messages.get(SpiritBow.class, "not_enough_mana"), curUser.mana, curUser.getMaxmana(), 1);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };
}
