package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Corruption;
import studio.baka.satoripixeldungeon.actors.buffs.PinCushion;
import studio.baka.satoripixeldungeon.actors.buffs.SoulMark;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Corrupting extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x440066);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if (defender.buff(Corruption.class) != null || !(defender instanceof Mob)) return damage;

        int level = Math.max(0, weapon.level());

        // lvl 0 - 20%
        // lvl 1 ~ 22.5%
        // lvl 2 ~ 25%
        if (damage >= defender.HP
                && !defender.isImmune(Corruption.class)
                && Random.Int(level + 30) >= 24) {

            Mob enemy = (Mob) defender;
            Hero hero = (attacker instanceof Hero) ? (Hero) attacker : Dungeon.hero;

            enemy.HP = enemy.HT;
            for (Buff buff : enemy.buffs()) {
                if (buff.type == Buff.buffType.NEGATIVE
                        && !(buff instanceof SoulMark)) {
                    buff.detach();
                } else if (buff instanceof PinCushion) {
                    buff.detach();
                }
            }
            if (enemy.alignment == Char.Alignment.ENEMY) {
                enemy.rollToDropLoot();
            }

            Buff.affect(enemy, Corruption.class);

            Statistics.enemiesSlain++;
            Badges.validateMonstersSlain();
            Statistics.qualifiedForNoKilling = false;
            if (enemy.EXP > 0 && hero.lvl <= enemy.maxLvl) {
                hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(enemy, "exp", enemy.EXP));
                hero.earnExp(enemy.EXP, enemy.getClass());
            } else {
                hero.earnExp(0, enemy.getClass());
            }

            return 0;
        }

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }
}
