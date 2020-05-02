package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Healing;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.features.MazeBalanca;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.scenes.InterlevelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class TestItem extends Item {

    public static final String AC_DOWN = "DOWN";
    public static final String AC_TEST = "TEST";

    public static final float TIME_TO_LIGHT = 1;

    {
        image = ItemSpriteSheet.TORCH;

        stackable = true;

        defaultAction = AC_THROW;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_DOWN);
        actions.add(AC_TEST);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_DOWN)) {
            if (Dungeon.depth < 30) {
                InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
                Game.switchScene(InterlevelScene.class);
            } else GLog.n("\nYou cannot get lower than level 30");
        }
        if (action.equals(AC_TEST)) {
            MazeBalanca mazeBalanca = new MazeBalanca(8, 8);
            int[] test_map = mazeBalanca.paint_pArray();//17*17=289 map
        }

    }


    @Override
    protected void onThrow(int cell) {
        Buff.affect(curUser, Healing.class).setHeal(999, 0.1f, 0);
        Char target = Actor.findChar(cell);
        if (target != null) target.damage(99, this);
        else {
            Level.set(cell, Terrain.EXIT);
            GameScene.updateMap(cell);
        }
        this.quantity(1).collect();
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
