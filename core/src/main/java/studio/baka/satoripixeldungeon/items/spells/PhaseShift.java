package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;

public class PhaseShift extends TargetedSpell {

    {
        image = ItemSpriteSheet.PHASE_SHIFT;
    }

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        final Char ch = Actor.findChar(bolt.collisionPos);

        if (ch == hero) {
            ScrollOfTeleportation.teleportHero(curUser);
        } else if (ch != null) {
            int count = 10;
            int pos;
            do {
                pos = Dungeon.level.randomRespawnCell();
                if (count-- <= 0) {
                    break;
                }
            } while (pos == -1);

            if (pos == -1 || Dungeon.bossLevel()) {

                GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));

            } else if (ch.properties().contains(Char.Property.IMMOVABLE)) {

                GLog.w(Messages.get(this, "tele_fail"));

            } else {

                ch.pos = pos;
                if (ch instanceof Mob && ((Mob) ch).state == ((Mob) ch).HUNTING) {
                    ((Mob) ch).state = ((Mob) ch).WANDERING;
                }
                ch.sprite.place(ch.pos);
                ch.sprite.visible = Dungeon.level.heroFOV[pos];

            }
        }
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((30 + 40) / 8f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{ScrollOfTeleportation.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = PhaseShift.class;
            outQuantity = 8;
        }

    }

}
