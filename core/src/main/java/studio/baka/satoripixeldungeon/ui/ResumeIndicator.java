package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Image;

public class ResumeIndicator extends Tag {

    private Image icon;

    public ResumeIndicator() {
        super(0xCDD5C0);

        setSize(24, 24);

        visible = false;

    }

    @Override
    protected void createChildren() {
        super.createChildren();

        icon = Icons.get(Icons.ARROW);
        add(icon);
    }

    @Override
    protected void layout() {
        super.layout();

        icon.x = x + 1 + (width - icon.width) / 2f;
        icon.y = y + (height - icon.height) / 2f;
        PixelScene.align(icon);
    }

    @Override
    protected void onClick() {
        Dungeon.hero.resume();
    }

    @Override
    public void update() {
        if (!Dungeon.hero.isAlive())
            visible = false;
        else if (visible == (Dungeon.hero.lastAction == null)) {
            visible = Dungeon.hero.lastAction != null;
            if (visible)
                flash();
        }
        super.update();
    }
}
