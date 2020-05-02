package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.scenes.AmuletScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

import java.io.IOException;
import java.util.ArrayList;

public class Amulet extends Item {

    private static final String AC_END = "END";

    {
        image = ItemSpriteSheet.AMULET;

        unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_END);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_END)) {
            showAmuletScene(false);
        }
    }

    @Override
    public boolean doPickUp(Hero hero) {
        if (super.doPickUp(hero)) {

            if (!Statistics.amuletObtained) {
                Statistics.amuletObtained = true;
                Badges.validateVictory();
                hero.spend(-TIME_TO_PICK_UP);

                //add a delayed actor here so pickup behaviour can fully process.
                Actor.addDelayed(new Actor() {
                    @Override
                    protected boolean act() {
                        Actor.remove(this);
                        showAmuletScene(true);
                        return false;
                    }
                }, -5);
            }

            return true;
        } else {
            return false;
        }
    }

    private void showAmuletScene(boolean showText) {
        try {
            Dungeon.saveAll();
            AmuletScene.noText = !showText;
            Game.switchScene(AmuletScene.class);
        } catch (IOException e) {
            SatoriPixelDungeon.reportException(e);
        }
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

}
