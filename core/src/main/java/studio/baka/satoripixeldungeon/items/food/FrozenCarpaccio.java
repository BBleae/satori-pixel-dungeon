package studio.baka.satoripixeldungeon.items.food;

import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class FrozenCarpaccio extends Food {

    {
        image = ItemSpriteSheet.CARPACCIO;
        energy = Hunger.HUNGRY / 2f;
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        effect(hero);
    }

    public int price() {
        return 10 * quantity;
    }

    public static void effect(Hero hero) {
        switch (Random.Int(5)) {
            case 0:
                GLog.i(Messages.get(FrozenCarpaccio.class, "invis"));
                Buff.affect(hero, Invisibility.class, Invisibility.DURATION);
                break;
            case 1:
                GLog.i(Messages.get(FrozenCarpaccio.class, "hard"));
                Buff.affect(hero, Barkskin.class).set(hero.HT / 4, 1);
                break;
            case 2:
                GLog.i(Messages.get(FrozenCarpaccio.class, "refresh"));
                Buff.detach(hero, Poison.class);
                Buff.detach(hero, Cripple.class);
                Buff.detach(hero, Weakness.class);
                Buff.detach(hero, Bleeding.class);
                Buff.detach(hero, Drowsy.class);
                Buff.detach(hero, Slow.class);
                Buff.detach(hero, Vertigo.class);
                break;
            case 3:
                GLog.i(Messages.get(FrozenCarpaccio.class, "better"));
                if (hero.HP < hero.HT) {
                    hero.HP = Math.min(hero.HP + hero.HT / 4, hero.HT);
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                }
                break;
        }
    }

    public static Food cook(MysteryMeat ingredient) {
        FrozenCarpaccio result = new FrozenCarpaccio();
        result.quantity = ingredient.quantity();
        return result;
    }
}
