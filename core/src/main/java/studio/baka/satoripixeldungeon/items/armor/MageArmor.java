package studio.baka.satoripixeldungeon.items.armor;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Roots;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class MageArmor extends ClassArmor {

    {
        image = ItemSpriteSheet.ARMOR_MAGE;
    }

    @Override
    public void doSpecial() {
        if (curUser.mana >= 20) {
            curUser.mana -= 20;

            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (Dungeon.level.heroFOV[mob.pos]
                        && mob.alignment != Char.Alignment.ALLY) {
                    Buff.affect(mob, Burning.class).reignite(mob);
                    Buff.prolong(mob, Roots.class, 3);
                }
            }

            curUser.spend(Actor.TICK);
            curUser.sprite.operate(curUser.pos);
            curUser.busy();

            curUser.sprite.centerEmitter().start(ElmoParticle.FACTORY, 0.15f, 4);
            Sample.INSTANCE.play(Assets.SND_READ);
        } else {
            GLog.w(Messages.get(this, "not_enough_mana"), curUser.mana, curUser.getMaxmana(), 20);
        }
    }

}