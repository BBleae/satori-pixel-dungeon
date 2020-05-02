package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FlavourBuff;
import studio.baka.satoripixeldungeon.actors.buffs.Sleep;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class SleepDart extends TippedDart {

    {
        image = ItemSpriteSheet.SLEEP_DART;
    }

    @Override
    public int proc(Char attacker, final Char defender, int damage) {

        //need to delay this so damage from the dart doesn't break the sleep
        new FlavourBuff() {
            {
                actPriority = VFX_PRIO;
            }

            public boolean act() {
                Buff.affect(defender, Sleep.class);
                return super.act();
            }
        }.attachTo(defender);

        return super.proc(attacker, defender, damage);
    }
}
