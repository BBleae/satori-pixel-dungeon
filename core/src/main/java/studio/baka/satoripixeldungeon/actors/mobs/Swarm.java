package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Corruption;
import studio.baka.satoripixeldungeon.actors.buffs.Poison;
import studio.baka.satoripixeldungeon.effects.Pushing;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.SwarmSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Swarm extends Mob {

    {
        spriteClass = SwarmSprite.class;

        HP = HT = 50;
        defenseSkill = 5;

        EXP = 3;
        maxLvl = 9;

        flying = true;

        loot = new PotionOfHealing();
        lootChance = 0.1667f; //by default, see rollToDropLoot()
    }

    private static final float SPLIT_DELAY = 1f;

    int generation = 0;

    private static final String GENERATION = "generation";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(GENERATION, generation);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        generation = bundle.getInt(GENERATION);
        if (generation > 0) EXP = 0;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {

        if (HP >= damage + 2) {
            ArrayList<Integer> candidates = new ArrayList<>();
            boolean[] solid = Dungeon.level.solid;

            int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
            for (int n : neighbours) {
                if (!solid[n] && Actor.findChar(n) == null) {
                    candidates.add(n);
                }
            }

            if (candidates.size() > 0) {

                Swarm clone = split();
                clone.HP = (HP - damage) / 2;
                clone.pos = Random.element(candidates);
                clone.state = clone.HUNTING;

                Dungeon.level.occupyCell(clone);

                GameScene.add(clone, SPLIT_DELAY);
                Actor.addDelayed(new Pushing(clone, pos, clone.pos), -1);

                HP -= clone.HP;
            }
        }

        return super.defenseProc(enemy, damage);
    }

    @Override
    public int attackSkill(Char target) {
        return 10;
    }

    private Swarm split() {
        Swarm clone = new Swarm();
        clone.generation = generation + 1;
        clone.EXP = 0;
        if (buff(Burning.class) != null) {
            Buff.affect(clone, Burning.class).reignite(clone);
        }
        if (buff(Poison.class) != null) {
            Buff.affect(clone, Poison.class).set(2);
        }
        if (buff(Corruption.class) != null) {
            Buff.affect(clone, Corruption.class);
        }
        return clone;
    }

    @Override
    public void rollToDropLoot() {
        lootChance = 1f / (6 * (generation + 1));
        lootChance *= (5f - Dungeon.LimitedDrops.SWARM_HP.count) / 5f;
        super.rollToDropLoot();
    }

    @Override
    protected Item createLoot() {
        Dungeon.LimitedDrops.SWARM_HP.count++;
        return super.createLoot();
    }
}
