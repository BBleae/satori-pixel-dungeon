package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.items.artifacts.CloakOfShadows;
import studio.baka.satoripixeldungeon.items.artifacts.TimekeepersHourglass;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Swiftthistle;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Invisibility extends FlavourBuff {

    public static final float DURATION = 20f;

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.invisible++;
            if (target instanceof Hero && ((Hero) target).subClass == HeroSubClass.ASSASSIN) {
                Buff.affect(target, Preparation.class);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        if (target.invisible > 0)
            target.invisible--;
        super.detach();
    }

    @Override
    public int icon() {
        return BuffIndicator.INVISIBLE;
    }

    @Override
    public void tintIcon(Image icon) {
        greyIcon(icon, 5f, cooldown());
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.INVISIBLE);
        else if (target.invisible == 0) target.sprite.remove(CharSprite.State.INVISIBLE);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    public static void dispel() {
        Invisibility buff = Dungeon.hero.buff(Invisibility.class);
        if (buff != null) {
            buff.detach();
        }
        CloakOfShadows.cloakStealth cloakBuff = Dungeon.hero.buff(CloakOfShadows.cloakStealth.class);
        if (cloakBuff != null) {
            cloakBuff.dispel();
        }

        //these aren't forms of invisibilty, but do dispel at the same time as it.
        TimekeepersHourglass.timeFreeze timeFreeze = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
        if (timeFreeze != null) {
            timeFreeze.detach();
        }

        Preparation prep = Dungeon.hero.buff(Preparation.class);
        if (prep != null) {
            prep.detach();
        }

        Swiftthistle.TimeBubble bubble = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
        if (bubble != null) {
            bubble.detach();
        }
    }
}
