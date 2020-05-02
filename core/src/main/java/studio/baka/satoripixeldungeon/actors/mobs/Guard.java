package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.effects.Chains;
import studio.baka.satoripixeldungeon.effects.Pushing;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.GuardSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Guard extends Mob {

    //they can only use their chains once
    private boolean chainsUsed = false;

    {
        spriteClass = GuardSprite.class;

        HP = HT = 40;
        defenseSkill = 10;

        EXP = 7;
        maxLvl = 14;

        loot = Generator.Category.ARMOR;
        lootChance = 0.1667f;

        properties.add(Property.UNDEAD);

        HUNTING = new Hunting();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(4, 12);
    }

    private boolean chain(int target) {
        if (chainsUsed || enemy.properties().contains(Property.IMMOVABLE))
            return false;

        Ballistica chain = new Ballistica(pos, target, Ballistica.PROJECTILE);

        if (chain.collisionPos != enemy.pos
                || chain.path.size() < 2
                || Dungeon.level.pit[chain.path.get(1)])
            return false;
        else {
            int newPos = -1;
            for (int i : chain.subPath(1, chain.dist)) {
                if (!Dungeon.level.solid[i] && Actor.findChar(i) == null) {
                    newPos = i;
                    break;
                }
            }

            if (newPos == -1) {
                return false;
            } else {
                final int newPosFinal = newPos;
                this.target = newPos;
                yell(Messages.get(this, "scorpion"));
                sprite.parent.add(new Chains(sprite.center(), enemy.sprite.center(), () -> {
                    Actor.addDelayed(new Pushing(enemy, enemy.pos, newPosFinal, () -> {
                        enemy.pos = newPosFinal;
                        Dungeon.level.occupyCell(enemy);
                        Cripple.prolong(enemy, Cripple.class, 4f);
                        if (enemy == Dungeon.hero) {
                            Dungeon.hero.interrupt();
                            Dungeon.observe();
                            GameScene.updateFog();
                        }
                    }), -1);
                    next();
                }));
            }
        }
        chainsUsed = true;
        return true;
    }

    @Override
    public int attackSkill(Char target) {
        return 14;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

    @Override
    protected Item createLoot() {
        Armor loot;
        do {
            loot = Generator.randomArmor();
            //50% chance of re-rolling tier 4 or 5 items
        } while (loot.tier >= 4 && Random.Int(2) == 0);
        loot.level(0);
        return loot;
    }

    private final String CHAINSUSED = "chainsused";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHAINSUSED, chainsUsed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        chainsUsed = bundle.getBoolean(CHAINSUSED);
    }

    private class Hunting extends Mob.Hunting {
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;

            if (!chainsUsed
                    && enemyInFOV
                    && !isCharmedBy(enemy)
                    && !canAttack(enemy)
                    && Dungeon.level.distance(pos, enemy.pos) < 5
                    && Random.Int(3) == 0

                    && chain(enemy.pos)) {
                return false;
            } else {
                return super.act(enemyInFOV, justAlerted);
            }

        }
    }
}
