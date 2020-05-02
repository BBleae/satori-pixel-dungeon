package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Electricity;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Ghost;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.rings.Ring;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.GnollTricksterSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Cup extends Gnoll {

    {
        spriteClass = GnollTricksterSprite.class;

        HP = HT = 100;
        defenseSkill = 25;

        EXP = 15;
        maxLvl = 30;

        state = WANDERING;

        //at half quantity, see createLoot()
        loot = Generator.Category.RING;
        lootChance = 0.125f;

        properties.add(Property.MINIBOSS);
    }

    private int combo = 0;

    @Override
    public int attackSkill(Char target) {
        return 36;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        Ballistica attack = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
        return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        //The gnoll's attacks get more severe the more the player lets it hit them
        combo++;
        int effect = Random.Int(4) + combo;

        if (effect > 2) {

            if (effect >= 6 && enemy.buff(Burning.class) == null) {

                if (Dungeon.level.flamable[enemy.pos])
                    GameScene.add(Blob.seed(enemy.pos, 4, Electricity.class));
                Buff.affect(enemy, Paralysis.class);

            } else
                Buff.affect(enemy, Paralysis.class);

        }
        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        combo = 0; //if he's moving, he isn't attacking, reset combo.
        if (state == HUNTING) {
            return enemySeen && getFurther(target);
        } else {
            return super.getCloser(target);
        }
    }

    @Override
    protected Item createLoot() {
        Ring drop = (Ring) super.createLoot();
        drop.quantity(1);
        return drop;
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        Ghost.Quest.process();
    }

    private static final String COMBO = "combo";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COMBO, combo);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        combo = bundle.getInt(COMBO);
    }

}
