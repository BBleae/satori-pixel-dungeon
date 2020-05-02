package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Kunai extends MissileWeapon {

    {
        image = ItemSpriteSheet.KUNAI;

        tier = 3;
        baseUses = 5;
    }

    private Char enemy;

    @Override
    protected void onThrow(int cell) {
        enemy = Actor.findChar(cell);
        super.onThrow(cell);
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero) owner;
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                //deals 60% toward max to max on surprise, instead of min to max.
                int diff = max() - min();
                int damage = augment.damageFactor(Random.NormalIntRange(
                        min() + Math.round(diff * 0.6f),
                        max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Random.IntRange(0, exStr);
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }

}
