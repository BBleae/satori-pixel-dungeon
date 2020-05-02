package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class RoundShield extends MeleeWeapon {

    {
        image = ItemSpriteSheet.ROUND_SHIELD;

        tier = 3;
    }

    @Override
    public int max(int lvl) {
        return 3 * (tier + 1) +    //12 base, down from 20
                lvl * (tier - 1);   //+2 per level, down from +4
    }

    @Override
    public int defenseFactor(Char owner) {
        return 5 + 2 * level();     //5 extra defence, plus 2 per level;
    }

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats_desc", 5 + 2 * level());
        } else {
            return Messages.get(this, "typical_stats_desc", 5);
        }
    }
}