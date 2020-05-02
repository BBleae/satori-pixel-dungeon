package studio.baka.satoripixeldungeon.items.armor;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.weapon.missiles.Shuriken;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.sprites.MissileSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

import java.util.HashMap;

public class HuntressArmor extends ClassArmor {


    {
        image = ItemSpriteSheet.ARMOR_HUNTRESS;
    }

    private final HashMap<Callback, Mob> targets = new HashMap<>();

    @Override
    public void doSpecial() {
        if (curUser.mana >= 20) {
            curUser.mana -= 20;
            Item proto = new Shuriken();

            for (Mob mob : Dungeon.level.mobs) {
                if (Dungeon.level.distance(curUser.pos, mob.pos) <= 12
                        && Dungeon.level.heroFOV[mob.pos]
                        && mob.alignment != Char.Alignment.ALLY) {

                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            curUser.attack(targets.get(this));
                            targets.remove(this);
                            if (targets.isEmpty()) {
                                curUser.spendAndNext(curUser.attackDelay());
                            }
                        }
                    };

                    ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                            reset(curUser.pos, mob.pos, proto, callback);

                    targets.put(callback, mob);
                }
            }

            if (targets.size() == 0) {
                GLog.w(Messages.get(this, "no_enemies"));
                return;
            }


            curUser.sprite.zap(curUser.pos);
            curUser.busy();
        } else {
            GLog.w(Messages.get(this, "not_enough_mana"), curUser.mana, curUser.getMaxmana(), 20);
        }
    }

}