package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.wands.WandOfBlastWave;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Repulsion extends Armor.Glyph {

    private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        int level = Math.max(0, armor.level());

        if (Random.Int(level + 5) >= 4) {
            int oppositeHero = attacker.pos + (attacker.pos - defender.pos);
            Ballistica trajectory = new Ballistica(attacker.pos, oppositeHero, Ballistica.MAGIC_BOLT);
            WandOfBlastWave.throwChar(attacker, trajectory, 2);
        }

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return WHITE;
    }
}
