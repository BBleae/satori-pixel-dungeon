package studio.baka.satoripixeldungeon.items.armor;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Bleeding;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Healing;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

import java.util.HashMap;

public class MahoArmor extends ClassArmor {


    {
        image = ItemSpriteSheet.ARMOR_MAHO;
    }

    private final HashMap<Callback, Mob> targets = new HashMap<>();

    @Override
    public void doSpecial() {
        if (curUser.mana >= 20) {
            curUser.mana -= 20;

            int healcount = 0;

            for (Mob mob : Dungeon.level.mobs) {
                if (Dungeon.level.distance(curUser.pos, mob.pos) <= 12
                        && Dungeon.level.heroFOV[mob.pos]
                        && mob.alignment != Char.Alignment.ALLY) {
                    Buff.affect(mob, Bleeding.class).set(curUser.lvl / 5);
                    mob.damage(curUser.lvl, this);
                    healcount++;
                }
            }

            if (targets.size() == 0) {
                GLog.w(Messages.get(this, "no_enemies"));
                return;
            }

            Buff.affect(curUser, Healing.class).setHeal((int) (healcount * (curUser.HT / 4f)), 1 / (healcount != 0 ? healcount : 1f), 0);

            curUser.spend(Actor.TICK);
            curUser.sprite.zap(curUser.pos);
            curUser.busy();
        } else {
            GLog.w(Messages.get(this, "not_enough_mana"), curUser.mana, curUser.getMaxmana(), 20);
        }
    }

}