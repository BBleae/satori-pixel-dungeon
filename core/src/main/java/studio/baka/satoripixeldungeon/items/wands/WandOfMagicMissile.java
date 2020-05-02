package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Recharging;
import studio.baka.satoripixeldungeon.effects.SpellSprite;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class WandOfMagicMissile extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_MAGIC_MISSILE;
    }

    public int min(int lvl) {
        return 2 + lvl;
    }

    public int max(int lvl) {
        return 8 + 2 * lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {

        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {

            processSoulMark(ch, chargesPerCast());
            ch.damage(damageRoll(), this);

            ch.sprite.burst(0xFFFFFFFF, level() / 2 + 2);

        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        Buff.prolong(attacker, Recharging.class, 1 + staff.level() / 2f);
        SpellSprite.show(attacker, SpellSprite.CHARGE);

    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        Buff.prolong(attacker, Recharging.class, 1 + staff.level() / 2f);
        SpellSprite.show(attacker, SpellSprite.CHARGE);

    }

    protected int initialCharges() {
        return 3;
    }

}
