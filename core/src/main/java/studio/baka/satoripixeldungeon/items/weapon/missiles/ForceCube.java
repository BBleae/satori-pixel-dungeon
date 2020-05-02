package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.wands.WandOfBlastWave;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ForceCube extends MissileWeapon {

    {
        image = ItemSpriteSheet.FORCE_CUBE;

        tier = 5;
        baseUses = 5;

        sticky = false;
    }

    @Override
    protected void onThrow(int cell) {
        Dungeon.level.pressCell(cell);

        ArrayList<Char> targets = new ArrayList<>();
        if (Actor.findChar(cell) != null) targets.add(Actor.findChar(cell));

        for (int i : PathFinder.NEIGHBOURS8) {
            Dungeon.level.pressCell(cell);
            if (Actor.findChar(cell + i) != null) targets.add(Actor.findChar(cell + i));
        }

        for (Char target : targets) {
            curUser.shoot(target, this);
            if (target == Dungeon.hero && !target.isAlive()) {
                Dungeon.fail(getClass());
                GLog.n(Messages.get(this, "ondeath"));
            }
        }

        rangedHit(null, cell);

        WandOfBlastWave.BlastWave.blast(cell);
        Sample.INSTANCE.play(Assets.SND_BLAST);
    }
}
