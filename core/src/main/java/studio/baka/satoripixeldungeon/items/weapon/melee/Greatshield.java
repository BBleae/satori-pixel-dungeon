package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Greatshield extends MeleeWeapon {

    {
        image = ItemSpriteSheet.GREATSHIELD;

        tier = 5;
    }

    @Override
    public int max(int lvl) {
        return Math.round(2.5f * (tier + 1)) +     //15 base, down from 30
                lvl * (tier - 2);                   //+3 per level, down from +6
    }

    @Override
    public int defenseFactor(Char owner) {
        return 10 + 3 * level();    //10 extra defence, plus 3 per level;
    }

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats_desc", 10 + 3 * level());
        } else {
            return Messages.get(this, "typical_stats_desc", 10);
        }
    }
}
