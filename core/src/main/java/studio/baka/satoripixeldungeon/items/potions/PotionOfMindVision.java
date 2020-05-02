package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.MindVision;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;

public class PotionOfMindVision extends Potion {

    {
        initials = 7;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();
        Buff.affect(hero, MindVision.class, MindVision.DURATION);
        Dungeon.observe();

        if (Dungeon.level.mobs.size() > 0) {
            GLog.i(Messages.get(this, "see_mobs"));
        } else {
            GLog.i(Messages.get(this, "see_none"));
        }
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
