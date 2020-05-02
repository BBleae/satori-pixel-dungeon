package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfLullaby;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.sprites.SuccubusSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Succubus extends Mob {

    private static final int BLINK_DELAY = 5;

    private int delay = 0;

    {
        spriteClass = SuccubusSprite.class;

        HP = HT = 80;
        defenseSkill = 25;
        viewDistance = Light.DISTANCE;

        EXP = 12;
        maxLvl = 25;

        loot = new ScrollOfLullaby();
        lootChance = 0.05f;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(22, 30);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);

        if (enemy.buff(Charm.class) != null) {
            int shield = (HP - HT) + (5 + damage);
            if (shield > 0) {
                HP = HT;
                Buff.affect(this, Barrier.class).setShield(shield);
            } else {
                HP += 5 + damage;
            }
            sprite.emitter().burst(Speck.factory(Speck.HEALING), 2);
            Sample.INSTANCE.play(Assets.SND_CHARMS);
        } else if (Random.Int(3) == 0) {
            //attack will reduce by 5 turns, so effectively 3-4 turns
            Buff.affect(enemy, Charm.class, Random.IntRange(3, 4) + 5).object = id();
            enemy.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
            Sample.INSTANCE.play(Assets.SND_CHARMS);
        }

        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        if (fieldOfView[target] && Dungeon.level.distance(pos, target) > 2 && delay <= 0) {

            blink(target);
            spend(-1 / speed());
            return true;

        } else {

            delay--;
            return super.getCloser(target);

        }
    }

    private void blink(int target) {

        Ballistica route = new Ballistica(pos, target, Ballistica.PROJECTILE);
        int cell = route.collisionPos;

        //can't occupy the same cell as another char, so move back one.
        if (Actor.findChar(cell) != null && cell != this.pos)
            cell = route.path.get(route.dist - 1);

        if (Dungeon.level.avoid[cell]) {
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int n : PathFinder.NEIGHBOURS8) {
                cell = route.collisionPos + n;
                if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
                    candidates.add(cell);
                }
            }
            if (candidates.size() > 0)
                //noinspection ConstantConditions
                cell = Random.element(candidates);
            else {
                delay = BLINK_DELAY;
                return;
            }
        }

        ScrollOfTeleportation.appear(this, cell);

        delay = BLINK_DELAY;
    }

    @Override
    public int attackSkill(Char target) {
        return 40;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }

    {
        immunities.add(Sleep.class);
        immunities.add(Charm.class);
    }
}
