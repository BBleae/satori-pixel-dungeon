package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.Statue;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.StatueSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class GuardianTrap extends Trap {

    {
        color = RED;
        shape = STARS;
    }

    @Override
    public void activate() {

        for (Mob mob : Dungeon.level.mobs) {
            mob.beckon(pos);
        }

        if (Dungeon.level.heroFOV[pos]) {
            GLog.w(Messages.get(this, "alarm"));
            CellEmitter.center(pos).start(Speck.factory(Speck.SCREAM), 0.3f, 3);
        }

        Sample.INSTANCE.play(Assets.SND_ALERT);

        for (int i = 0; i < (Dungeon.depth - 5) / 5; i++) {
            Guardian guardian = new Guardian();
            guardian.state = guardian.WANDERING;
            guardian.pos = Dungeon.level.randomRespawnCell();
            GameScene.add(guardian);
            guardian.beckon(Dungeon.hero.pos);
        }

    }

    public static class Guardian extends Statue {

        {
            spriteClass = GuardianSprite.class;

            EXP = 0;
            state = WANDERING;
        }

        public Guardian() {
            super();

            weapon.enchant(null);
            weapon.degrade(weapon.level());
        }

        @Override
        public void beckon(int cell) {
            //Beckon works on these ones, unlike their superclass.
            notice();

            if (state != HUNTING) {
                state = WANDERING;
            }
            target = cell;
        }

    }

    public static class GuardianSprite extends StatueSprite {

        public GuardianSprite() {
            super();
            tint(0, 0, 1, 0.2f);
        }

        @Override
        public void resetColor() {
            super.resetColor();
            tint(0, 0, 1, 0.2f);
        }
    }
}
