package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Corruption;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Gold;
import studio.baka.satoripixeldungeon.items.Honeypot;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ThiefSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Thief extends Mob {

    public Item item;

    {
        spriteClass = ThiefSprite.class;

        HP = HT = 20;
        defenseSkill = 12;

        EXP = 5;
        maxLvl = 10;

        loot = Random.oneOf(Generator.Category.RING, Generator.Category.ARTIFACT);
        lootChance = 0.01f;

        WANDERING = new Wandering();
        FLEEING = new Fleeing();

        properties.add(Property.UNDEAD);
    }

    private static final String ITEM = "item";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ITEM, item);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        item = (Item) bundle.get(ITEM);
    }

    @Override
    public float speed() {
        if (item != null) return (5 * super.speed()) / 6;
        else return super.speed();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 10);
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay() * 0.5f;
    }

    @Override
    public void rollToDropLoot() {
        if (item != null) {
            Dungeon.level.drop(item, pos).sprite.drop();
            //updates position
            if (item instanceof Honeypot.ShatteredPot) ((Honeypot.ShatteredPot) item).dropPot(this, pos);
            item = null;
        }
        super.rollToDropLoot();
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 3);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);

        if (alignment == Alignment.ENEMY && item == null
                && enemy instanceof Hero && steal((Hero) enemy)) {
            state = FLEEING;
        }

        return damage;
    }

    @Override
    public int defenseProcess(Char enemy, int damage) {
        if (state == FLEEING) {
            Dungeon.level.drop(new Gold(), pos).sprite.drop();
        }

        return super.defenseProcess(enemy, damage);
    }

    protected boolean steal(Hero hero) {

        Item item = hero.belongings.randomUnequipped();

        if (item != null && !item.unique && item.level() < 1) {

            GLog.w(Messages.get(Thief.class, "stole", item.name()));
            if (!item.stackable) {
                Dungeon.quickslot.convertToPlaceholder(item);
            }
            Item.updateQuickslot();

            if (item instanceof Honeypot) {
                this.item = ((Honeypot) item).shatter(this, this.pos);
                item.detach(hero.belongings.backpack);
            } else {
                this.item = item.detach(hero.belongings.backpack);
                if (item instanceof Honeypot.ShatteredPot)
                    ((Honeypot.ShatteredPot) item).pickupPot(this);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public String description() {
        String desc = super.description();

        if (item != null) {
            desc += Messages.get(this, "carries", item.name());
        }

        return desc;
    }

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            super.act(enemyInFOV, justAlerted);

            //if an enemy is just noticed and the thief posses an item, run, don't fight.
            if (state == HUNTING && item != null) {
                state = FLEEING;
            }

            return true;
        }
    }

    private class Fleeing extends Mob.Fleeing {
        @Override
        protected void nowhereToRun() {
            if (buff(Terror.class) == null && buff(Corruption.class) == null) {
                if (enemySeen) {
                    sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Mob.class, "rage"));
                    state = HUNTING;
                } else if (item != null
                        && !Dungeon.level.heroFOV[pos]
                        && Dungeon.level.distance(Dungeon.hero.pos, pos) >= 6) {

                    int count = 32;
                    int newPos;
                    do {
                        newPos = Dungeon.level.randomRespawnCell();
                        if (count-- <= 0) {
                            break;
                        }
                    } while (newPos == -1 || Dungeon.level.heroFOV[newPos] || Dungeon.level.distance(newPos, pos) < (count / 3));

                    if (newPos != -1) {

                        if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);
                        pos = newPos;
                        sprite.place(pos);
                        sprite.visible = Dungeon.level.heroFOV[pos];
                        if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

                    }

                    if (item != null) GLog.n(Messages.get(Thief.class, "escapes", item.name()));
                    item = null;
                    state = WANDERING;
                } else {
                    state = WANDERING;
                }
            } else {
                super.nowhereToRun();
            }
        }
    }
}
