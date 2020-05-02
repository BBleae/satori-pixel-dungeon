package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;

public class Corruption extends Buff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    private float buildToDamage = 0f;

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.alignment = Char.Alignment.ALLY;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean act() {
        buildToDamage += target.HT / 200f;

        int damage = (int) buildToDamage;
        buildToDamage -= damage;

        if (damage > 0)
            target.damage(damage, this);

        spend(TICK);

        return true;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.DARKENED);
        else if (target.invisible == 0) target.sprite.remove(CharSprite.State.DARKENED);
    }

    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
}
