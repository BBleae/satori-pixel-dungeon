package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Objects;

public class AttackIndicator extends Tag {

    private static final float ENABLED = 1.0f;
    private static final float DISABLED = 0.3f;

    private static float delay;

    private static AttackIndicator instance;

    private CharSprite sprite = null;

    private static Mob lastTarget;
    private final ArrayList<Mob> candidates = new ArrayList<>();

    public AttackIndicator() {
        super(DangerIndicator.COLOR);

        instance = this;
        lastTarget = null;

        setSize(24, 24);
        visible(false);
        enable(false);
    }

    @Override
    protected void createChildren() {
        super.createChildren();
    }

    @Override
    protected synchronized void layout() {
        super.layout();

        if (sprite != null) {
            sprite.x = x + (width - sprite.width()) / 2;
            sprite.y = y + (height - sprite.height()) / 2;
            PixelScene.align(sprite);
        }
    }

    @Override
    public synchronized void update() {
        super.update();

        if (!bg.visible) {
            enable(false);
            if (delay > 0f) delay -= Game.elapsed;
            if (delay <= 0f) active = false;
        } else {
            delay = 0.75f;
            active = true;

            if (Dungeon.hero.isAlive()) {

                enable(Dungeon.hero.ready);

            } else {
                visible(false);
                enable(false);
            }
        }
    }

    private synchronized void checkEnemies() {

        candidates.clear();
        int v = Dungeon.hero.visibleEnemies();
        for (int i = 0; i < v; i++) {
            Mob mob = Dungeon.hero.visibleEnemy(i);
            if (Dungeon.hero.canAttack(mob)) {
                candidates.add(mob);
            }
        }

        if (!candidates.contains(lastTarget)) {
            if (candidates.isEmpty()) {
                lastTarget = null;
            } else {
                active = true;
                lastTarget = Random.element(candidates);
                updateImage();
                flash();
            }
        } else {
            if (!bg.visible) {
                active = true;
                flash();
            }
        }

        visible(lastTarget != null);
        enable(bg.visible);
    }

    private synchronized void updateImage() {

        if (sprite != null) {
            sprite.killAndErase();
            sprite = null;
        }

        sprite = Reflection.newInstance(lastTarget.spriteClass);
        active = true;
        Objects.requireNonNull(sprite).linkVisuals(lastTarget);
        sprite.idle();
        sprite.paused = true;
        add(sprite);

        sprite.x = x + (width - sprite.width()) / 2 + 1;
        sprite.y = y + (height - sprite.height()) / 2;
        PixelScene.align(sprite);
    }

    private boolean enabled = true;

    private synchronized void enable(boolean value) {
        enabled = value;
        if (sprite != null) {
            sprite.alpha(value ? ENABLED : DISABLED);
        }
    }

    private synchronized void visible(boolean value) {
        bg.visible = value;
        if (sprite != null) {
            sprite.visible = value;
        }
    }

    @Override
    protected void onClick() {
        if (enabled) {
            if (Dungeon.hero.handle(lastTarget.pos)) {
                Dungeon.hero.next();
            }
        }
    }

    public static void target(Char target) {
        lastTarget = (Mob) target;
        instance.updateImage();

        TargetHealthIndicator.instance.target(target);
    }

    public static void updateState() {
        instance.checkEnemies();
    }
}
