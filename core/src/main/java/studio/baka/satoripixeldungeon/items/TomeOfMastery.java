package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.SpellSprite;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndChooseWay;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class TomeOfMastery extends Item {

    public static final float TIME_TO_READ = 10;

    public static final String AC_READ = "READ";

    {
        stackable = false;
        image = ItemSpriteSheet.MASTERY;

        unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_READ);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_READ)) {

            curUser = hero;

            HeroSubClass way1 = null;
            HeroSubClass way2 = null;
            switch (hero.heroClass) {
                case WARRIOR:
                    way1 = HeroSubClass.GLADIATOR;
                    way2 = HeroSubClass.BERSERKER;
                    break;
                case MAGE:
                    way1 = HeroSubClass.BATTLEMAGE;
                    way2 = HeroSubClass.WARLOCK;
                    break;
                case ROGUE:
                    way1 = HeroSubClass.SNIPER;
                    way2 = HeroSubClass.ASSASSIN;
                    break;
                case HUNTRESS:
                    way1 = HeroSubClass.FREERUNNER;
                    way2 = HeroSubClass.WARDEN;
                    break;
                case MAHOU_SHOUJO:
                    way1 = HeroSubClass.DEVIL;
                    way2 = HeroSubClass.DESTORYER;
            }
            GameScene.show(new WndChooseWay(this, way1, way2));

        }
    }

    @Override
    public boolean doPickUp(Hero hero) {
        Badges.validateMastery();
        return super.doPickUp(hero);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public void choose(HeroSubClass way) {

        detach(curUser.belongings.backpack);

        curUser.spend(TomeOfMastery.TIME_TO_READ);
        curUser.busy();

        curUser.subClass = way;

        curUser.sprite.operate(curUser.pos);
        Sample.INSTANCE.play(Assets.SND_MASTERY);

        SpellSprite.show(curUser, SpellSprite.MASTERY);
        curUser.sprite.emitter().burst(Speck.factory(Speck.MASTERY), 12);
        GLog.w(Messages.get(this, "way", way.title()));

    }
}
