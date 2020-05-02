package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Flashbang extends Bomb {

    {
        image = ItemSpriteSheet.FLASHBANG;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        Level l = Dungeon.level;
        for (Char ch : Actor.chars()) {
            if (ch.fieldOfView != null && ch.fieldOfView[cell]) {
                int power = 16 - 4 * l.distance(ch.pos, cell);
                if (power > 0) {
                    Buff.prolong(ch, Blindness.class, power);
                    Buff.prolong(ch, Cripple.class, power);
                }
                if (ch == Dungeon.hero) {
                    GameScene.flash(0xFFFFFF);
                }
            }
        }

    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (20 + 40);
    }
}
