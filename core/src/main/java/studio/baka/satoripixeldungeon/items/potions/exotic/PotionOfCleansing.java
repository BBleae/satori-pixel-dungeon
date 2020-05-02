package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Corruption;
import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import com.watabou.noosa.audio.Sample;

import java.util.Objects;

public class PotionOfCleansing extends ExoticPotion {

    {
        initials = 9;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();

        cleanse(hero);
    }

    @Override
    public void shatter(int cell) {
        if (Actor.findChar(cell) == null) {
            super.shatter(cell);
        } else {
            if (Dungeon.level.heroFOV[cell]) {
                Sample.INSTANCE.play(Assets.SND_SHATTER);
                splash(cell);
                setKnown();
            }

            if (Actor.findChar(cell) != null) {
                cleanse(Objects.requireNonNull(Actor.findChar(cell)));
            }
        }
    }

    public static void cleanse(Char ch) {
        for (Buff b : ch.buffs()) {
            if (b.type == Buff.buffType.NEGATIVE && !(b instanceof Corruption)) {
                b.detach();
            }
            if (b instanceof Hunger) {
                ((Hunger) b).satisfy(Hunger.STARVING);
            }
        }
    }
}
