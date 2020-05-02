package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Trap implements Bundlable {

    //trap colors
    public static final int RED = 0;
    public static final int ORANGE = 1;
    public static final int YELLOW = 2;
    public static final int GREEN = 3;
    public static final int TEAL = 4;
    public static final int VIOLET = 5;
    public static final int WHITE = 6;
    public static final int GREY = 7;
    public static final int BLACK = 8;

    //trap shapes
    public static final int DOTS = 0;
    public static final int WAVES = 1;
    public static final int GRILL = 2;
    public static final int STARS = 3;
    public static final int DIAMOND = 4;
    public static final int CROSSHAIR = 5;
    public static final int LARGE_DOT = 6;

    public String name = Messages.get(this, "name");

    public int color;
    public int shape;

    public int pos;

    public boolean visible;
    public boolean active = true;

    public boolean canBeHidden = true;
    public boolean canBeSearched = true;

    public Trap set(int pos) {
        this.pos = pos;
        return this;
    }

    public Trap reveal() {
        visible = true;
        GameScene.updateMap(pos);
        return this;
    }

    public Trap hide() {
        if (canBeHidden) {
            visible = false;
            GameScene.updateMap(pos);
            return this;
        } else {
            return reveal();
        }
    }

    public void trigger() {
        if (active) {
            if (Dungeon.level.heroFOV[pos]) {
                Sample.INSTANCE.play(Assets.SND_TRAP);
            }
            disarm();
            reveal();
            activate();
        }
    }

    public abstract void activate();

    public void disarm() {
        active = false;
        Dungeon.level.disarmTrap(pos);
    }

    private static final String POS = "pos";
    private static final String VISIBLE = "visible";
    private static final String ACTIVE = "active";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        pos = bundle.getInt(POS);
        visible = bundle.getBoolean(VISIBLE);
        if (bundle.contains(ACTIVE)) {
            active = bundle.getBoolean(ACTIVE);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, pos);
        bundle.put(VISIBLE, visible);
        bundle.put(ACTIVE, active);
    }

    public String desc() {
        return Messages.get(this, "desc");
    }
}
