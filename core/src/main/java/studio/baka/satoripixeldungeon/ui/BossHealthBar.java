package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.particles.BloodParticle;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;

public class BossHealthBar extends Component {

    private Image bar;
    private Image hp;

    private static Mob boss;

    private Image skull;
    private Emitter blood;

    private static BossHealthBar instance;
    private static boolean bleeding;

    BossHealthBar() {
        super();
        visible = active = (boss != null);
        instance = this;
    }

    @Override
    protected void createChildren() {
        String asset = Assets.BOSSHP;
        bar = new Image(asset, 0, 0, 64, 16);
        add(bar);

        width = bar.width;
        height = bar.height;

        hp = new Image(asset, 15, 19, 47, 4);
        add(hp);

        skull = new Image(asset, 5, 18, 6, 6);
        add(skull);

        blood = new Emitter();
        blood.pos(skull);
        blood.pour(BloodParticle.FACTORY, 0.3f);
        blood.autoKill = false;
        blood.on = false;
        add(blood);
    }

    @Override
    protected void layout() {
        bar.x = x;
        bar.y = y;

        hp.x = bar.x + 15;
        hp.y = bar.y + 6;

        skull.x = bar.x + 5;
        skull.y = bar.y + 5;
    }

    @Override
    public void update() {
        super.update();
        if (boss != null) {
            if (!boss.isAlive() || !Dungeon.level.mobs.contains(boss)) {
                boss = null;
                visible = active = false;
            } else {
                hp.scale.x = (float) boss.HP / boss.HT;
                if (hp.scale.x < 0.25f) bleed(true);

                if (bleeding != blood.on) {
                    if (bleeding) skull.tint(0xcc0000, 0.6f);
                    else skull.resetColor();
                    blood.on = bleeding;
                }
            }
        }
    }

    public static void assignBoss(Mob boss) {
        BossHealthBar.boss = boss;
        bleed(false);
        if (instance != null) {
            instance.visible = instance.active = true;
        }
    }

    public static boolean isAssigned() {
        return boss != null && boss.isAlive() && Dungeon.level.mobs.contains(boss);
    }

    public static void bleed(boolean value) {
        bleeding = value;
    }

}
