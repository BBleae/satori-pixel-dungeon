package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.weapon.melee.MeleeWeapon;
import studio.baka.satoripixeldungeon.sprites.SlimeSprite;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.Objects;

public class Slime extends Mob {

    {
        spriteClass = SlimeSprite.class;

        HP = HT = 20;
        defenseSkill = 5;

        EXP = 4;
        maxLvl = 9;

        lootChance = 0.1f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(2, 5);
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (dmg >= 5) {
            //takes 5/6/7/8/9/10 dmg at 5/7/10/14/19/25 incoming dmg
            dmg = 4 + (int) (Math.sqrt(8 * (dmg - 4) + 1) - 1) / 2;
        }
        super.damage(dmg, src);
    }

    @Override
    protected Item createLoot() {
        Generator.Category c = Generator.Category.WEP_T2;
        MeleeWeapon w = (MeleeWeapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
        Objects.requireNonNull(w).random();
        w.level(0);
        return w;
    }
}
