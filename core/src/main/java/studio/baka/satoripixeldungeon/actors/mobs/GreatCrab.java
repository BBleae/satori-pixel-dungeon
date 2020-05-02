package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Ghost;
import studio.baka.satoripixeldungeon.items.food.MysteryMeat;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.GreatCrabSprite;
import studio.baka.satoripixeldungeon.utils.GLog;

public class GreatCrab extends Crab {

    {
        spriteClass = GreatCrabSprite.class;

        HP = HT = 25;
        defenseSkill = 0; //see damage()
        baseSpeed = 1f;

        EXP = 6;

        state = WANDERING;

        properties.add(Property.MINIBOSS);
    }

    private int moving = 0;

    @Override
    protected boolean getCloser(int target) {
        //this is used so that the crab remains slower, but still detects the player at the expected rate.
        moving++;
        if (moving < 3) {
            return super.getCloser(target);
        } else {
            moving = 0;
            return true;
        }

    }

    @Override
    public void damage(int dmg, Object src) {
        //crab blocks all attacks originating from its current enemy if it sees them.
        //All direct damage is negated, no exceptions. environmental effects go through as normal.
        if ((enemySeen && state != SLEEPING && paralysed == 0)
                && ((src instanceof Wand && enemy == Dungeon.hero)
                || (src instanceof Char && enemy == src))) {
            GLog.n(Messages.get(this, "noticed"));
            sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "blocked"));
        } else {
            super.damage(dmg, src);
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        Ghost.Quest.process();

        Dungeon.level.drop(new MysteryMeat(), pos);
        Dungeon.level.drop(new MysteryMeat(), pos).sprite.drop();
    }
}
