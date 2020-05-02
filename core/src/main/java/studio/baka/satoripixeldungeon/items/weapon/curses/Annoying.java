package studio.baka.satoripixeldungeon.items.weapon.curses;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Annoying extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        if (Random.Int(20) == 0) {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                mob.beckon(attacker.pos);
            }
            attacker.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
            Sample.INSTANCE.play(Assets.SND_MIMIC);
            Invisibility.dispel();
            GLog.n(Messages.get(this, "msg_" + (Random.Int(5) + 1)));
        }

        return damage;
    }

    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

}