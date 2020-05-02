package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.scenes.TitleScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

public class ExitButton extends Button {

    protected Image image;

    public ExitButton() {
        super();

        width = 20;
        height = 20;
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        image = Icons.EXIT.get();
        add(image);
    }

    @Override
    protected void layout() {
        super.layout();

        image.x = x + (width - image.width) / 2f;
        image.y = y + (height - image.height) / 2f;
        PixelScene.align(image);
    }

    @Override
    protected void onPointerDown() {
        image.brightness(1.5f);
        Sample.INSTANCE.play(Assets.SND_CLICK);
    }

    @Override
    protected void onPointerUp() {
        image.resetColor();
    }

    @Override
    protected void onClick() {
        if (Game.scene() instanceof TitleScene) {
            Game.instance.finish();
        } else {
            SatoriPixelDungeon.switchNoFade(TitleScene.class);
        }
    }
}
