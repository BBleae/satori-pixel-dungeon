package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Light;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.particles.Emitter;

import java.util.ArrayList;

public class Torch extends Item {

    public static final String AC_LIGHT = "LIGHT";

    public static final float TIME_TO_LIGHT = 1;

    {
        image = ItemSpriteSheet.TORCH;

        stackable = true;

        defaultAction = AC_LIGHT;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_LIGHT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_LIGHT)) {

            hero.spend(TIME_TO_LIGHT);
            hero.busy();

            hero.sprite.operate(hero.pos);

            detach(hero.belongings.backpack);

            Buff.affect(hero, Light.class, Light.DURATION);

            Emitter emitter = hero.sprite.centerEmitter();
            emitter.start(FlameParticle.FACTORY, 0.2f, 3);

        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int price() {
        return 10 * quantity;
    }

}
