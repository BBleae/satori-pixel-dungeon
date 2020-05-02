package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;

public class Dissolve extends FlavourBuff implements Hero.Doom {

    {
        type = buffType.NEGATIVE;
        announced = false;
    }

    @Override
    public int icon() {
        return BuffIndicator.NONE;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public boolean act() {
        if (target.isAlive() && target == Dungeon.hero) {
            if (Dungeon.level.map[target.pos] == Terrain.WATER && target.buff(Levitation.class) == null)
                if (Dungeon.hero.heroClass == HeroClass.MAHOU_SHOUJO) {
                    target.sprite.showStatus(CharSprite.NEGATIVE, this.toString());
                    target.damage(1, this);
                }
            spend(TICK);
        } else {
            detach();
        }
        return true;
    }

    @Override
    public void onDeath() {
        Dungeon.fail(getClass());
        GLog.n(Messages.get(this, "ondeath"));
    }
}
