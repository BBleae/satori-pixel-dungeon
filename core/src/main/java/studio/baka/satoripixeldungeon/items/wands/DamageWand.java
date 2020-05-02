package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.messages.Messages;
import com.watabou.utils.Random;

//for wands that directly damage a target
//wands with AOE effects count here (e.g. fireblast), but wands with indrect damage do not (e.g. venom, transfusion)
public abstract class DamageWand extends Wand {

    public int min() {
        return min(level());
    }

    public abstract int min(int lvl);

    public int max() {
        return max(level());
    }

    public abstract int max(int lvl);

    public int damageRoll() {
        return Random.NormalIntRange(min(), max());
    }

    public int damageRoll(int lvl) {
        return Random.NormalIntRange(min(lvl), max(lvl));
    }

    @Override
    public String statsDesc() {
        if (levelKnown)
            return Messages.get(this, "stats_desc", min(), max());
        else
            return Messages.get(this, "stats_desc", min(0), max(0));
    }
}
