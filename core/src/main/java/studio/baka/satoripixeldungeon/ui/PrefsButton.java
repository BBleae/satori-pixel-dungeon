package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.windows.WndSettings;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

public class PrefsButton extends Button {

    private Image image;

    @Override
    protected void createChildren() {
        super.createChildren();

        image = Icons.PREFS.get();
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
        parent.add(new WndSettings());
    }
}
