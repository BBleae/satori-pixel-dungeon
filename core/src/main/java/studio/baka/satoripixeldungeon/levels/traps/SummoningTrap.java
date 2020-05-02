package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SummoningTrap extends Trap {

    private static final float DELAY = 2f;

    {
        color = TEAL;
        shape = WAVES;
    }

    @Override
    public void activate() {

        int nMobs = 1;
        if (Random.Int(2) == 0) {
            nMobs++;
            if (Random.Int(2) == 0) {
                nMobs++;
            }
        }

        ArrayList<Integer> candidates = new ArrayList<>();

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                candidates.add(p);
            }
        }

        ArrayList<Integer> respawnPoints = new ArrayList<>();

        while (nMobs > 0 && candidates.size() > 0) {
            int index = Random.index(candidates);

            respawnPoints.add(candidates.remove(index));
            nMobs--;
        }

        ArrayList<Mob> mobs = new ArrayList<>();

        for (Integer point : respawnPoints) {
            Mob mob = Dungeon.level.createMob();
            if (mob != null) {
                mob.state = mob.WANDERING;
                mob.pos = point;
                GameScene.add(mob, DELAY);
                mobs.add(mob);
            }
        }

        //important to process the visuals and pressing of cells last, so spawned mobs have a chance to occupy cells first
        Trap t;
        for (Mob mob : mobs) {
            //manually trigger traps first to avoid sfx spam
            if ((t = Dungeon.level.traps.get(mob.pos)) != null && t.active) {
                t.disarm();
                t.reveal();
                t.activate();
            }
            ScrollOfTeleportation.appear(mob, mob.pos);
            Dungeon.level.occupyCell(mob);
        }

    }
}
