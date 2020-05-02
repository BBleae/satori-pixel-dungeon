package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.NewTengu;

public class TenguDartTrap extends PoisonDartTrap {

    {
        canBeHidden = true;
        canBeSearched = false;
    }

    @Override
    protected int poisonAmount() {
        return 8; //17 damage total
    }

    @Override
    protected boolean canTarget(Char ch) {
        return !(ch instanceof NewTengu);
    }
}
