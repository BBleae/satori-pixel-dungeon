package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Shuriken extends MissileWeapon {

    {
        image = ItemSpriteSheet.SHURIKEN;

        tier = 2;
        baseUses = 5;
    }

    @Override
    public int max(int lvl) {
        return 4 * tier +                      //8 base, down from 10
                (tier == 1 ? 2 * lvl : tier * lvl); //scaling unchanged
    }

    @Override
    public float speedFactor(Char owner) {
        if (owner instanceof Hero && ((Hero) owner).justMoved) return 0;
        else return super.speedFactor(owner);
    }
}
