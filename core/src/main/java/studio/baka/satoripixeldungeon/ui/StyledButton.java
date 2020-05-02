package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Chrome;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

import java.util.Objects;

//simple button which support a background chrome, text, and an icon.
public class StyledButton extends Button {

    protected NinePatch bg;
    protected RenderedTextBlock text;
    protected Image icon;

    public StyledButton(Chrome.Type type, String label) {
        this(type, label, 9);
    }

    public StyledButton(Chrome.Type type, String label, int size) {
        super();

        bg = Chrome.get(type);
        addToBack(Objects.requireNonNull(bg));

        text = PixelScene.renderTextBlock(size);
        text.text(label);
        add(text);
    }

    @Override
    protected void layout() {

        super.layout();

        bg.x = x;
        bg.y = y;
        bg.size(width, height);

        float componentWidth = 0;

        if (icon != null) componentWidth += icon.width() + 2;

        if (text != null && !text.text().equals("")) {
            componentWidth += text.width() + 2;

            text.setPos(
                    x + (width() + componentWidth) / 2f - text.width() - 1,
                    y + (height() - text.height()) / 2f
            );
            PixelScene.align(text);

        }

        if (icon != null) {

            icon.x = x + (width() - componentWidth) / 2f + 1;
            icon.y = y + (height() - icon.height()) / 2f;
            PixelScene.align(icon);
        }

    }

    @Override
    protected void onPointerDown() {
        bg.brightness(1.2f);
        Sample.INSTANCE.play(Assets.SND_CLICK);
    }

    @Override
    protected void onPointerUp() {
        bg.resetColor();
    }

    public void enable(boolean value) {
        active = value;
        text.alpha(value ? 1.0f : 0.3f);
    }

    public void text(String value) {
        text.text(value);
        layout();
    }

    public void textColor(int value) {
        text.hardlight(value);
    }

    public void icon(Image icon) {
        if (this.icon != null) {
            remove(this.icon);
        }
        this.icon = icon;
        if (this.icon != null) {
            add(this.icon);
            layout();
        }
    }

    public Image icon() {
        return icon;
    }

    public float reqWidth() {
        float reqWidth = 0;
        if (icon != null) {
            reqWidth += icon.width() + 2;
        }
        if (text != null && !text.text().equals("")) {
            reqWidth += text.width() + 2;
        }
        return reqWidth;
    }

    public float reqHeight() {
        float reqHeight = 0;
        if (icon != null) {
            reqHeight = Math.max(icon.height() + 4, reqHeight);
        }
        if (text != null && !text.text().equals("")) {
            reqHeight = Math.max(text.height() + 4, reqHeight);
        }
        return reqHeight;
    }
}
