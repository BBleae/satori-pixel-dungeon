package studio.baka.satoripixeldungeon.ui;

import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;

public class SimpleButton extends Component {

    private Image image;

    public SimpleButton(Image image) {
        super();

        this.image.copy(image);
        width = image.width;
        height = image.height;
    }

    @Override
    protected void createChildren() {
        image = new Image();
        add(image);

        add(new PointerArea(image) {
            @Override
            protected void onPointerDown(PointerEvent event) {
                image.brightness(1.2f);
            }

            @Override
            protected void onPointerUp(PointerEvent event) {
                image.brightness(1.0f);
            }

            @Override
            protected void onClick(PointerEvent event) {
                SimpleButton.this.onClick();
            }
        });
    }

    @Override
    protected void layout() {
        image.x = x;
        image.y = y;
    }

    protected void onClick() {
    }
}
