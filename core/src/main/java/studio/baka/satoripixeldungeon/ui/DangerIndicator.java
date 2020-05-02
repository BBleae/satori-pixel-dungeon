package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;

public class DangerIndicator extends Tag {

    public static final int COLOR = 0xFF4C4C;

    private BitmapText number;
    private Image icon;

    private int enemyIndex = 0;

    private int lastNumber = -1;

    public DangerIndicator() {
        super(0xFF4C4C);

        setSize(24, 16);

        visible = false;
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        number = new BitmapText(PixelScene.pixelFont);
        add(number);

        icon = Icons.SKULL.get();
        add(icon);
    }

    @Override
    protected void layout() {
        super.layout();

        icon.x = right() - 10;
        icon.y = y + (height - icon.height) / 2;

        placeNumber();
    }

    private void placeNumber() {
        number.x = right() - 11 - number.width();
        number.y = y + (height - number.baseLine()) / 2f;
        PixelScene.align(number);
    }

    @Override
    public void update() {

        if (Dungeon.hero.isAlive()) {
            int v = Dungeon.hero.visibleEnemies();
            if (v != lastNumber) {
                lastNumber = v;
                if (visible = lastNumber > 0) {
                    number.text(Integer.toString(lastNumber));
                    number.measure();
                    placeNumber();

                    flash();
                }
            }
        } else {
            visible = false;
        }

        super.update();
    }

    @Override
    protected void onClick() {
        if (Dungeon.hero.visibleEnemies() > 0) {

            Mob target = Dungeon.hero.visibleEnemy(enemyIndex++);

            TargetHealthIndicator.instance.target(target == TargetHealthIndicator.instance.target() ? null : target);

            if (Dungeon.hero.curAction == null) {
                Camera.main.panTo(target.sprite.center(), 5f);
            }
        }
    }
}
