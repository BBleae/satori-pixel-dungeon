package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.particles.SparkParticle;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ShamanSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Shaman extends Mob implements Callback {

    private static final float TIME_TO_ZAP = 1f;

    {
        spriteClass = ShamanSprite.class;

        HP = HT = 18;
        defenseSkill = 8;

        EXP = 6;
        maxLvl = 14;

        loot = Generator.Category.SCROLL;
        lootChance = 0.33f;

        properties.add(Property.ELECTRIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(2, 8);
    }

    @Override
    public int attackSkill(Char target) {
        return 11;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    //used so resistances can differentiate between melee and magical attacks
    public static class LightningBolt {
    }

    @Override
    protected boolean doAttack(Char enemy) {

        if (Dungeon.level.distance(pos, enemy.pos) <= 1) {

            return super.doAttack(enemy);

        } else {

            if (sprite != null && sprite.visible) {
                sprite.zap(enemy.pos);
            }

            spend(TIME_TO_ZAP);

            if (hit(this, enemy, true)) {
                int dmg = Random.NormalIntRange(3, 10);
                if (Dungeon.level.water[enemy.pos] && !enemy.flying) {
                    dmg *= 1.5f;
                }
                enemy.damage(dmg, new LightningBolt());

                enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
                enemy.sprite.flash();

                if (enemy == Dungeon.hero) {

                    Camera.main.shake(2, 0.3f);

                    if (!enemy.isAlive()) {
                        Dungeon.fail(getClass());
                        GLog.n(Messages.get(this, "zap_kill"));
                    }
                }
            } else {
                enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
            }

            if (sprite != null && sprite.visible) {
                sprite.zap(enemy.pos);
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void call() {
        next();
    }

}
