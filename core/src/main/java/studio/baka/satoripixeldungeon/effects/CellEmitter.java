package studio.baka.satoripixeldungeon.effects;

import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;

import java.util.Objects;

public class CellEmitter {

    public static Emitter get(int cell) {

        PointF p = DungeonTilemap.tileToWorld(cell);

        Emitter emitter = GameScene.emitter();
        Objects.requireNonNull(emitter).pos(p.x, p.y, DungeonTilemap.SIZE, DungeonTilemap.SIZE);

        return emitter;
    }

    public static Emitter center(int cell) {

        PointF p = DungeonTilemap.tileToWorld(cell);

        Emitter emitter = GameScene.emitter();
        Objects.requireNonNull(emitter).pos(p.x + DungeonTilemap.SIZE / 2, p.y + DungeonTilemap.SIZE / 2);

        return emitter;
    }

    public static Emitter bottom(int cell) {

        PointF p = DungeonTilemap.tileToWorld(cell);

        Emitter emitter = GameScene.emitter();
        Objects.requireNonNull(emitter).pos(p.x, p.y + DungeonTilemap.SIZE, DungeonTilemap.SIZE, 0);

        return emitter;
    }
}
