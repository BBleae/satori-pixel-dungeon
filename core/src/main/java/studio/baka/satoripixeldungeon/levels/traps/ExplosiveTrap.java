package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.items.bombs.Bomb;

public class ExplosiveTrap extends Trap {

    {
        color = ORANGE;
        shape = DIAMOND;
    }

    @Override
    public void activate() {
        new Bomb().explode(pos);
    }

}
