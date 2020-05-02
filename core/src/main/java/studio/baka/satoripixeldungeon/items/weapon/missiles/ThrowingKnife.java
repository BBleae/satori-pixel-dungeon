package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class ThrowingKnife extends MissileWeapon {

    {
        image = ItemSpriteSheet.THROWING_KNIFE;

        bones = false;
        tier = 0;
        baseUses = 1;
    }

    @Override
    public int min(int lvl) {
        return 0;
    }

    @Override
    public int max(int lvl) {
        return 0;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_DROP);
        return actions;
    }

    @Override
    protected void onThrow(int cell) {
        Char enemy = Actor.findChar(cell);

        if (enemy != null && !(enemy instanceof Hero)) {
            int postmp = enemy.pos;
            int postmp2 = curUser.pos;
            ScrollOfTeleportation.teleportToLocation(enemy, Dungeon.level.randomRespawnCell());
            ScrollOfTeleportation.teleportToLocation(curUser, postmp);
            ScrollOfTeleportation.teleportToLocation(enemy, postmp2);
        } else {
            ScrollOfTeleportation.teleportToLocation(curUser, cell);
        }

        //new Noisemaker().explode(cell);
        //ThrowingKnife tk = new ThrowingKnife();
        this.quantity(1).collect();
        //super.onThrow(cell);
    }

    @Override
    public String info() {

        return desc();
    }

    @Override
    public int damageRoll(Char owner) {
        return 0;
    }
}
