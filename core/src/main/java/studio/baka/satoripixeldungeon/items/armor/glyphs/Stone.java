package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;

public class Stone extends Armor.Glyph {

    private static final ItemSprite.Glowing GREY = new ItemSprite.Glowing(0x222222);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        testing = true;
        float evasion = defender.defenseSkill(attacker);
        float accuracy = attacker.attackSkill(defender);
        testing = false;

        float hitChance;
        if (evasion >= accuracy) {
            hitChance = (accuracy / evasion) / 2f;
        } else {
            hitChance = 1f - (evasion / accuracy) / 2f;
        }

        //75% of dodge chance is applied as damage reduction
        hitChance = (1f + 3f * hitChance) / 4f;

        damage = (int) Math.ceil(damage * hitChance);

        return damage;
    }

    private boolean testing = false;

    public boolean testingEvasion() {
        return testing;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GREY;
    }

}
