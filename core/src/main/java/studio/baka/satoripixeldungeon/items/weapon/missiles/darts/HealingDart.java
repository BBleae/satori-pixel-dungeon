package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Healing;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class HealingDart extends TippedDart {

    {
        image = ItemSpriteSheet.HEALING_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        //heals 30 hp at base, scaling with enemy HT
        Buff.affect(defender, Healing.class).setHeal((int) (0.5f * defender.HT + 30), 0.25f, 0);
        PotionOfHealing.cure(defender);

        if (attacker.alignment == defender.alignment) {
            return 0;
        }

        return super.proc(attacker, defender, damage);
    }

}
