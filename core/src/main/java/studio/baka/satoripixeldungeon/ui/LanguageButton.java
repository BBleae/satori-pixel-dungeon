package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.windows.WndLangs;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

public class LanguageButton extends Button {

    private Image image;

    @Override
    protected void createChildren() {
        super.createChildren();

        image = Icons.get(Icons.LANGS);
        add(image);
        updateIcon();
    }

    private boolean flashing;
    private float time = 0;

    @Override
    public void update() {
        super.update();

        if (flashing) {
            image.am = (float) Math.abs(Math.cos((time += Game.elapsed)));
            if (time >= Math.PI) {
                time = 0;
            }
        }

    }

    private void updateIcon() {
        image.resetColor();
        flashing = false;
        switch (Messages.lang().status()) {
            case INCOMPLETE:
                image.tint(1, 0, 0, .5f);
                flashing = true;
                break;
            case UNREVIEWED:
                image.tint(1, .5f, 0, .5f);
                break;
        }
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
        updateIcon();
    }

    @Override
    protected void onClick() {
        parent.add(new WndLangs());
    }

}
