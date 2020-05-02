package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ReagentOfPellouxite extends Item {

    public static final float TIME_TO_LIGHT = 1;

    {
        image = ItemSpriteSheet.REAGENTOFPELLOUXITE;

        stackable = true;

        defaultAction = AC_THROW;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        return super.actions(hero);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell]) {

            super.onThrow(cell);

        } else {

            Dungeon.level.pressCell(cell);
            shatter(cell);

        }
    }

    public void shatter(int cell) {

        if (Dungeon.level.heroFOV[cell]) {
            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        for (int offset : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.water[cell + offset]) {
                Level.set(cell + offset, Terrain.EMPTY);
                DEM(cell + offset);
                //GameScene.add(Blob.seed(cell + offset, 1, Fire.class));
                GameScene.updateMap(cell + offset);
            }
        }
    }

    protected void splash(int cell) {

        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
        if (fire != null)
            fire.clear(cell);

        DEM(cell);
    }

    protected void DEM(int cell) {
        final int color = splashColor();
        Char ch = Actor.findChar(cell);
        if (ch != null) {
            Buff.affect(ch, Burning.class).reignite(ch, 8f);
            Buff.affect(ch, Blindness.class, 8f);
            Splash.at(ch.sprite.center(), color, 5);
        } else {
            Splash.at(cell, color, 5);
        }
    }

    protected int splashColor() {
        return ItemSprite.pick(image, 5, 9);
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
