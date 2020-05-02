package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Enchanting;
import studio.baka.satoripixeldungeon.effects.particles.PurpleParticle;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Stylus extends Item {

    private static final float TIME_TO_INSCRIBE = 2;

    private static final String AC_INSCRIBE = "INSCRIBE";

    {
        image = ItemSpriteSheet.STYLUS;

        stackable = true;

        bones = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_INSCRIBE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_INSCRIBE)) {

            curUser = hero;
            GameScene.selectItem(itemSelector, WndBag.Mode.ARMOR, Messages.get(this, "prompt"));

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

    private void inscribe(Armor armor) {

        if (!armor.isIdentified()) {
            GLog.w(Messages.get(this, "identify"));
            return;
        } else if (armor.cursed || armor.hasCurseGlyph()) {
            GLog.w(Messages.get(this, "cursed"));
            return;
        }

        detach(curUser.belongings.backpack);

        GLog.w(Messages.get(this, "inscribed"));

        armor.inscribe();

        curUser.sprite.operate(curUser.pos);
        curUser.sprite.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10);
        Enchanting.show(curUser, armor);
        Sample.INSTANCE.play(Assets.SND_BURNING);

        curUser.spend(TIME_TO_INSCRIBE);
        curUser.busy();
    }

    @Override
    public int price() {
        return 30 * quantity;
    }

    private final WndBag.Listener itemSelector = item -> {
        if (item != null) {
            Stylus.this.inscribe((Armor) item);
        }
    };
}
