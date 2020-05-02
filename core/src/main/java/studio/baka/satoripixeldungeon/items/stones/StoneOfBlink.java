package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class StoneOfBlink extends Runestone {

    {
        image = ItemSpriteSheet.STONE_BLINK;
    }

    private static Ballistica throwPath;

    @Override
    public int throwPos(Hero user, int dst) {
        throwPath = new Ballistica(user.pos, dst, Ballistica.PROJECTILE);
        return throwPath.collisionPos;
    }

    @Override
    protected void onThrow(int cell) {
        if (Actor.findChar(cell) != null && throwPath.dist >= 1) {
            cell = throwPath.path.get(throwPath.dist - 1);
        }
        throwPath = null;
        super.onThrow(cell);
    }

    @Override
    protected void activate(int cell) {
        ScrollOfTeleportation.teleportToLocation(curUser, cell);
    }
}
