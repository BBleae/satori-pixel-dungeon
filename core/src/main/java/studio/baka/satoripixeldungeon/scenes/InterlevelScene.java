package studio.baka.satoripixeldungeon.scenes;

import studio.baka.satoripixeldungeon.*;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.features.Chasm;
import studio.baka.satoripixeldungeon.levels.rooms.special.SpecialRoom;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.GameLog;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.windows.WndError;
import studio.baka.satoripixeldungeon.windows.WndStory;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.*;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class InterlevelScene extends PixelScene {

    //slow fade on entering a new region
    private static final float SLOW_FADE = 1f; //.33 in, 1.33 steady, .33 out, 2 seconds total
    //norm fade when loading, falling, returning, or descending to a new floor
    private static final float NORM_FADE = 0.67f; //.33 in, .67 steady, .33 out, 1.33 seconds total
    //fast fade when ascending, or descending to a floor you've been on
    private static final float FAST_FADE = 0.50f; //.33 in, .33 steady, .33 out, 1 second total
    //super slow fade while entering level 30, the Author floor, which need longer to generate maze
    private static final float SUPER_SLOW_FADE = 1.50f; //.33 in, 2.34 steady .33 out, 3 seconds total

    private static float fadeTime;

    public enum Mode {
        DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, RESET, NONE
    }

    public static Mode mode;

    public static int returnDepth;
    public static int returnPos;

    public static boolean noStory = false;

    public static boolean fallIntoPit;

    private enum Phase {
        FADE_IN, STATIC, FADE_OUT
    }

    private Phase phase;
    private float timeLeft;

    private RenderedTextBlock message;

    private static Thread thread;
    private static Exception error = null;
    private float waitingTime;

    @Override
    public void create() {
        super.create();

        String loadingAsset;
        int loadingDepth;
        final float scrollSpeed;
        fadeTime = NORM_FADE;
        switch (mode) {
            default:
                loadingDepth = Dungeon.depth;
                scrollSpeed = 0;
                break;
            case CONTINUE:
                loadingDepth = Objects.requireNonNull(GamesInProgress.check(GamesInProgress.curSlot)).depth;
                scrollSpeed = 5;
                break;
            case DESCEND:
                if (Dungeon.hero == null) {
                    loadingDepth = 1;
                    fadeTime = SLOW_FADE;
                } else {
                    loadingDepth = Dungeon.depth + 1;
                    if (!(Statistics.deepestFloor < loadingDepth)) {
                        fadeTime = FAST_FADE;
                    } else if (loadingDepth == 6 || loadingDepth == 11
                            || loadingDepth == 16 || loadingDepth == 22) {
                        fadeTime = SLOW_FADE;
                    } else if (loadingDepth == 30)
                        fadeTime = SUPER_SLOW_FADE;
                }

                if (loadingDepth == 30)
                    scrollSpeed = 100;            //这样进入30层的效果更鬼畜了。
                else if (loadingDepth == 29 && !(Statistics.deepestFloor < loadingDepth))
                    scrollSpeed = -100;            //离开的效果也要同样鬼畜！
                else scrollSpeed = 5;

                break;
            case FALL:
                loadingDepth = Dungeon.depth + 1;
                scrollSpeed = 50;
                break;
            case ASCEND:
                fadeTime = FAST_FADE;
                loadingDepth = Dungeon.depth - 1;
                scrollSpeed = -5;
                break;
            case RETURN:
                loadingDepth = returnDepth;
                scrollSpeed = returnDepth > Dungeon.depth ? 15 : -15;
                break;
        }
        if (loadingDepth <= 5) loadingAsset = Assets.LOADING_SEWERS;
        else if (loadingDepth <= 10) loadingAsset = Assets.LOADING_PRISON;
        else if (loadingDepth <= 15) loadingAsset = Assets.LOADING_CAVES;
        else if (loadingDepth <= 21) loadingAsset = Assets.LOADING_CITY;
        else if (loadingDepth <= 25) loadingAsset = Assets.LOADING_HALLS;
        else if (loadingDepth <= 30) loadingAsset = Assets.LOADING_HALLOW;
        else loadingAsset = Assets.SHADOW;

        //speed up transition when debugging
        if (DeviceCompat.isDebug()) {
            fadeTime /= 2;
        }

        SkinnedBlock bg = new SkinnedBlock(Camera.main.width, Camera.main.height, loadingAsset) {
            @Override
            protected NoosaScript script() {
                return NoosaScriptNoLighting.get();
            }

            @Override
            public void draw() {
                Blending.disable();
                super.draw();
                Blending.enable();
            }

            @Override
            public void update() {
                super.update();
                offset(0, Game.elapsed * scrollSpeed);
            }
        };
        bg.scale(4, 4);
        add(bg);

        Image im = new Image(TextureCache.createGradient(0xAA000000, 0xBB000000, 0xCC000000, 0xDD000000, 0xFF000000)) {
            @Override
            public void update() {
                super.update();
                if (phase == Phase.FADE_IN) aa = Math.max(0, (timeLeft - (fadeTime - 0.333f)));
                else if (phase == Phase.FADE_OUT) aa = Math.max(0, (0.333f - timeLeft));
                else aa = 0;
            }
        };
        im.angle = 90;
        im.scale.x = Camera.main.height / 5f;
        im.scale.y = im.x = Camera.main.width;
        add(im);

        String text = Messages.get(Mode.class, mode.name());
        if (loadingDepth == 1 && !Dungeon.start_descended) {
            text = Messages.get(Hero.class, "start_load");
            Dungeon.start_descended = true;
        }
        if (!Dungeon.haveplayed) text = Messages.get(Hero.class, "before_start");

        message = PixelScene.renderTextBlock(text, 9);
        message.setPos(
                (Camera.main.width - message.width()) / 2,
                (Camera.main.height - message.height()) / 2
        );
        align(message);
        add(message);

        phase = Phase.FADE_IN;
        timeLeft = fadeTime;

        if (thread == null) {
            thread = new Thread(() -> {

                try {

                    if (Dungeon.hero != null) {
                        Dungeon.hero.spendToWhole();
                    }
                    Actor.fixTime();

                    switch (mode) {
                        case DESCEND:
                            descend();
                            break;
                        case ASCEND:
                            ascend();
                            break;
                        case CONTINUE:
                            restore();
                            break;
                        case RESURRECT:
                            resurrect();
                            break;
                        case RETURN:
                            returnTo();
                            break;
                        case FALL:
                            fall();
                            break;
                        case RESET:
                            reset();
                            break;
                    }


                    if ((Dungeon.depth % 5) == 0) {
                        Sample.INSTANCE.load(Assets.SND_BOSS);
                    }

                } catch (Exception e) {

                    error = e;

                }

                if (phase == Phase.STATIC && error == null) {
                    phase = Phase.FADE_OUT;
                    timeLeft = fadeTime;
                }
            });
            thread.start();
        }
        waitingTime = 0f;
    }

    @Override
    public void update() {
        super.update();

        waitingTime += Game.elapsed;

        float p = timeLeft / fadeTime;

        switch (phase) {

            case FADE_IN:
                message.alpha(1 - p);
                if ((timeLeft -= Game.elapsed) <= 0) {
                    if (!thread.isAlive() && error == null) {
                        phase = Phase.FADE_OUT;
                        timeLeft = fadeTime;
                    } else {
                        phase = Phase.STATIC;
                    }
                }
                break;

            case FADE_OUT:
                message.alpha(p);

                if ((timeLeft -= Game.elapsed) <= 0) {
                    Game.switchScene(GameScene.class);
                    thread = null;
                    error = null;
                }
                break;

            case STATIC:
                if (error != null) {
                    String errorMsg;
                    if (error instanceof FileNotFoundException) errorMsg = Messages.get(this, "file_not_found");
                    else if (error instanceof IOException) errorMsg = Messages.get(this, "io_error");
                    else if (error.getMessage() != null &&
                            error.getMessage().equals("old save")) errorMsg = Messages.get(this, "io_error");

                    else throw new RuntimeException("fatal error occured while moving between floors. " +
                                "Seed:" + Dungeon.seed + " depth:" + Dungeon.depth, error);

                    add(new WndError(errorMsg) {
                        public void onBackPressed() {
                            super.onBackPressed();
                            Game.switchScene(StartScene.class);
                        }
                    });
                    thread = null;
                    error = null;
                } else if (thread != null && (int) waitingTime == 20) {
                    waitingTime = 21f;
                    StringBuilder s = new StringBuilder();
                    for (StackTraceElement t : thread.getStackTrace()) {
                        s.append("\n");
                        s.append(t.toString());
                    }
                    SatoriPixelDungeon.reportException(
                            new RuntimeException("waited more than 20 seconds on levelgen. " +
                                    "Seed:" + Dungeon.seed + " depth:" + Dungeon.depth + " trace:" +
                                    s)
                    );
                }
                break;
        }
    }

    private void descend() throws IOException {

        if (Dungeon.hero == null) {
            Mob.clearHeldAllies();
            Dungeon.init();
            if (noStory) {
                Dungeon.chapters.add(WndStory.ID_SEWERS);
                noStory = false;
            }
            GameLog.wipe();
        } else {
            Mob.holdAllies(Dungeon.level);
            Dungeon.saveAll();
        }

        Level level;
        if (Dungeon.depth == -1) {
            //Dungeon.depth++;
            level = Dungeon.newLevel();
        } else if (Dungeon.depth >= Statistics.deepestFloor) {
            level = Dungeon.newLevel();
        } else {
            Dungeon.depth++;
            level = Dungeon.loadLevel(GamesInProgress.curSlot);
        }
        Dungeon.switchLevel(level, level.entrance);
    }

    private void fall() throws IOException {

        Mob.holdAllies(Dungeon.level);

        Buff.affect(Dungeon.hero, Chasm.Falling.class);
        Dungeon.saveAll();

        Level level;
        if (Dungeon.depth >= Statistics.deepestFloor) {
            level = Dungeon.newLevel();
        } else {
            Dungeon.depth++;
            level = Dungeon.loadLevel(GamesInProgress.curSlot);
        }
        Dungeon.switchLevel(level, level.fallCell(fallIntoPit));
    }

    private void ascend() throws IOException {

        Mob.holdAllies(Dungeon.level);

        Dungeon.saveAll();
        Dungeon.depth--;
        Level level = Dungeon.loadLevel(GamesInProgress.curSlot);
        Dungeon.switchLevel(level, level.exit);
    }

    private void returnTo() throws IOException {

        Mob.holdAllies(Dungeon.level);

        Dungeon.saveAll();
        Dungeon.depth = returnDepth;
        Level level = Dungeon.loadLevel(GamesInProgress.curSlot);
        Dungeon.switchLevel(level, returnPos);
    }

    private void restore() throws IOException {

        Mob.clearHeldAllies();

        GameLog.wipe();

        Dungeon.loadGame(GamesInProgress.curSlot);
        if (Dungeon.depth == -1) {
            Dungeon.depth = Statistics.deepestFloor;
            Dungeon.switchLevel(Dungeon.loadLevel(GamesInProgress.curSlot), -1);
        } else {
            Level level = Dungeon.loadLevel(GamesInProgress.curSlot);
            Dungeon.switchLevel(level, Dungeon.hero.pos);
        }
    }

    private void resurrect() {

        Mob.holdAllies(Dungeon.level);

        if (Dungeon.level.locked) {
            Dungeon.hero.resurrect(Dungeon.depth);
            Dungeon.depth--;
            Level level = Dungeon.newLevel();
            Dungeon.switchLevel(level, level.entrance);
        } else {
            Dungeon.hero.resurrect(-1);
            Dungeon.resetLevel();
        }
    }

    private void reset() {

        Mob.holdAllies(Dungeon.level);

        SpecialRoom.resetPitRoom(Dungeon.depth + 1);

        Dungeon.depth--;
        Level level = Dungeon.newLevel();
        Dungeon.switchLevel(level, level.entrance);
    }

    @Override
    protected void onBackPressed() {
        //Do nothing
    }
}
