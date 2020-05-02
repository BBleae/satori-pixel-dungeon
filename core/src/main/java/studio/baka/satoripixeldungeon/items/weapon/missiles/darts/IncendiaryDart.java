package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class IncendiaryDart extends TippedDart {

    {
        image = ItemSpriteSheet.INCENDIARY_DART;
    }

    @Override
    protected void onThrow(int cell) {
        Char enemy = Actor.findChar(cell);
        if ((enemy == null || enemy == curUser) && Dungeon.level.flamable[cell]) {
            GameScene.add(Blob.seed(cell, 4, Fire.class));
            Dungeon.level.drop(new Dart(), cell).sprite.drop();
        } else {
            super.onThrow(cell);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.affect(defender, Burning.class).reignite(defender);
        return super.proc(attacker, defender, damage);
    }

}
