package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Dagger extends MeleeWeapon {

    {
        image = ItemSpriteSheet.DAGGER;

        tier = 1;

        bones = false;
    }

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);
        //strength req decreases at +1,+3,+6,+10,etc.
        return (6 + tier * 2) - (int) (Math.sqrt(8 * lvl + 1) - 1) / 2;
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //8 base, down from 10
                lvl * (tier + 3);   //scaling changed
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero) owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                //deals 75% toward max to max on surprise, instead of min to max.
                int diff = max() - min();
                int damage = augment.damageFactor(Random.NormalIntRange(
                        min() + Math.round(diff * 0.75f),
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
