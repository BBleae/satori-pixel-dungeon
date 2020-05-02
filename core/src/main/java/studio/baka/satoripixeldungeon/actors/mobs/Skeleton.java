package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.weapon.melee.MeleeWeapon;
import studio.baka.satoripixeldungeon.levels.features.Chasm;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.SkeletonSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Skeleton extends Mob {

    {
        spriteClass = SkeletonSprite.class;

        HP = HT = 25;
        defenseSkill = 9;

        EXP = 5;
        maxLvl = 10;

        loot = Generator.Category.WEAPON;
        lootChance = 0.125f;

        properties.add(Property.UNDEAD);
        properties.add(Property.INORGANIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(2, 10);
    }

    @Override
    public void die(Object cause) {

        super.die(cause);

        if (cause == Chasm.class) return;

        boolean heroKilled = false;
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            Char ch = findChar(pos + PathFinder.NEIGHBOURS8[i]);
            if (ch != null && ch.isAlive()) {
                int damage = Random.NormalIntRange(6, 12);
                damage = Math.max(0, damage - (ch.drRoll() + ch.drRoll()));
                ch.damage(damage, this);
                if (ch == Dungeon.hero && !ch.isAlive()) {
                    heroKilled = true;
                }
            }
        }

        if (Dungeon.level.heroFOV[pos]) {
            Sample.INSTANCE.play(Assets.SND_BONES);
        }

        if (heroKilled) {
            Dungeon.fail(getClass());
            GLog.n(Messages.get(this, "explosion_kill"));
        }
    }

    @Override
    protected Item createLoot() {
        MeleeWeapon loot;
        do {
            loot = Generator.randomWeapon();
            //50% chance of re-rolling tier 4 or 5 melee weapons
        } while (loot.tier >= 4 && Random.Int(2) == 0);
        loot.level(0);
        return loot;
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }

}
