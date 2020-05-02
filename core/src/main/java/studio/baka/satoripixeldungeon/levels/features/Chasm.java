package studio.baka.satoripixeldungeon.levels.features;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Bleeding;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.artifacts.TimekeepersHourglass;
import studio.baka.satoripixeldungeon.items.spells.FeatherFall;
import studio.baka.satoripixeldungeon.levels.RegularLevel;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.rooms.special.WeakFloorRoom;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Swiftthistle;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.scenes.InterlevelScene;
import studio.baka.satoripixeldungeon.sprites.MobSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndOptions;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Chasm {

    public static boolean jumpConfirmed = false;

    public static void heroJump(final Hero hero) {
        Game.runOnRenderThread(() -> GameScene.show(
                new WndOptions(Messages.get(Chasm.class, "chasm"),
                        Messages.get(Chasm.class, "jump"),
                        Messages.get(Chasm.class, "yes"),
                        Messages.get(Chasm.class, "no")) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            jumpConfirmed = true;
                            hero.resume();
                        }
                    }
                }
        ));
    }

    public static void heroFall(int pos) {

        jumpConfirmed = false;

        Sample.INSTANCE.play(Assets.SND_FALLING);

        Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
        if (buff != null) buff.detach();
        buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
        if (buff != null) buff.detach();

        if (Dungeon.hero.isAlive()) {
            Dungeon.hero.interrupt();
            InterlevelScene.mode = InterlevelScene.Mode.FALL;
            if (Dungeon.level instanceof RegularLevel) {
                Room room = ((RegularLevel) Dungeon.level).room(pos);
                InterlevelScene.fallIntoPit = room instanceof WeakFloorRoom;
            } else {
                InterlevelScene.fallIntoPit = false;
            }
            Game.switchScene(InterlevelScene.class);
        } else {
            Dungeon.hero.sprite.visible = false;
        }
    }

    public static void heroLand() {

        Hero hero = Dungeon.hero;

        FeatherFall.FeatherBuff b = hero.buff(FeatherFall.FeatherBuff.class);

        if (b != null) {
            //TODO visuals
            b.detach();
            return;
        }

        Camera.main.shake(4, 1f);

        Dungeon.level.occupyCell(hero);
        Buff.prolong(hero, Cripple.class, Cripple.DURATION);

        //The lower the hero's HP, the more bleed and the less upfront damage.
        //Hero has a 50% chance to bleed out at 66% HP, and begins to risk instant-death at 25%
        Buff.affect(hero, FallBleed.class).set(Math.round(hero.HT / (6f + (6f * (hero.HP / (float) hero.HT)))));
        hero.damage(Math.max(hero.HP / 2, Random.NormalIntRange(hero.HP / 2, hero.HT / 4)), (Hero.Doom) () -> {
            Badges.validateDeathFromFalling();

            Dungeon.fail(Chasm.class);
            GLog.n(Messages.get(Chasm.class, "ondeath"));
        });
    }

    public static void mobFall(Mob mob) {
        if (mob.isAlive()) mob.die(Chasm.class);

        ((MobSprite) mob.sprite).fall();
    }

    public static class Falling extends Buff {

        {
            actPriority = VFX_PRIORITY;
        }

        @Override
        public boolean act() {
            heroLand();
            detach();
            return true;
        }
    }

    public static class FallBleed extends Bleeding implements Hero.Doom {

        @Override
        public void onDeath() {
            Badges.validateDeathFromFalling();
        }
    }
}
