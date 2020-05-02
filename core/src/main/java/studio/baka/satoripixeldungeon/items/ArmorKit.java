package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.armor.ClassArmor;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.HeroSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ArmorKit extends Item {

    private static final String TXT_UPGRADED = "you applied the armor kit to upgrade your %s";

    private static final float TIME_TO_UPGRADE = 2;

    private static final String AC_APPLY = "APPLY";

    {
        image = ItemSpriteSheet.KIT;

        unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_APPLY);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_APPLY)) {

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

    private void upgrade(Armor armor) {

        detach(curUser.belongings.backpack);

        curUser.sprite.centerEmitter().start(Speck.factory(Speck.KIT), 0.05f, 10);
        curUser.spend(TIME_TO_UPGRADE);
        curUser.busy();

        GLog.w(Messages.get(this, "upgraded", armor.name()));

        ClassArmor classArmor = ClassArmor.upgrade(curUser, armor);
        if (curUser.belongings.armor == armor) {

            curUser.belongings.armor = classArmor;
            ((HeroSprite) curUser.sprite).updateArmor();
            classArmor.activate(curUser);

        } else {

            armor.detach(curUser.belongings.backpack);
            classArmor.collect(curUser.belongings.backpack);

        }

        curUser.sprite.operate(curUser.pos);
        Sample.INSTANCE.play(Assets.SND_EVOKE);
    }

    private final WndBag.Listener itemSelector = item -> {
        if (item != null) {
            ArmorKit.this.upgrade((Armor) item);
        }
    };
}
