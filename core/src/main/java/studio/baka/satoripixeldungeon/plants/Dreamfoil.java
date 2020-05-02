package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;

public class Dreamfoil extends Plant {

    {
        image = 7;
    }

    @Override
    public void activate(Char ch) {

        if (ch != null) {
            if (ch instanceof Mob) {
                Buff.affect(ch, MagicalSleep.class);
            } else if (ch instanceof Hero) {
                GLog.i(Messages.get(this, "refreshed"));
                Buff.detach(ch, Poison.class);
                Buff.detach(ch, Cripple.class);
                Buff.detach(ch, Weakness.class);
                Buff.detach(ch, Bleeding.class);
                Buff.detach(ch, Drowsy.class);
                Buff.detach(ch, Slow.class);
                Buff.detach(ch, Vertigo.class);

                if (((Hero) ch).subClass == HeroSubClass.WARDEN) {
                    Buff.affect(ch, BlobImmunity.class, 10f);
                }

            }
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_DREAMFOIL;

            plantClass = Dreamfoil.class;
        }
    }
}