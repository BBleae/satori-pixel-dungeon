package studio.baka.satoripixeldungeon.items.armor.curses;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Stench extends Armor.Glyph {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        if (Random.Int(8) == 0) {

            GameScene.add(Blob.seed(defender.pos, 250, ToxicGas.class));

        }

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    @Override
    public boolean curse() {
        return true;
    }
}
