package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Chrome;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;

public abstract class OptionSlider extends Component {

    private PointerArea pointerArea;

    private RenderedTextBlock title;
    private RenderedTextBlock minTxt;
    private RenderedTextBlock maxTxt;

    //values are expressed internally as ints, but they can easily be interpreted as something else externally.
    private final int minVal;
    private final int maxVal;
    private int selectedVal;

    private NinePatch sliderNode;
    private NinePatch BG;
    private ColorBlock sliderBG;
    private final ColorBlock[] sliderTicks;
    private float tickDist;


    public OptionSlider(String title, String minTxt, String maxTxt, int minVal, int maxVal) {
        super();

        //shouldn't function if this happens.
        if (minVal > maxVal) {
            minVal = maxVal;
            active = false;
        }

        this.title.text(title);
        this.minTxt.text(minTxt);
        this.maxTxt.text(maxTxt);

        this.minVal = minVal;
        this.maxVal = maxVal;

        sliderTicks = new ColorBlock[(maxVal - minVal) + 1];
        for (int i = 0; i < sliderTicks.length; i++) {
            add(sliderTicks[i] = new ColorBlock(1, 11, 0xFF222222));
        }
        add(sliderNode);
    }

    protected abstract void onChange();

    public int getSelectedValue() {
        return selectedVal;
    }

    public void setSelectedValue(int val) {
        this.selectedVal = val;
        sliderNode.x = (int) (x + tickDist * (selectedVal - minVal));
        sliderNode.y = sliderBG.y - 4;
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        add(BG = Chrome.get(Chrome.Type.RED_BUTTON));
        BG.alpha(0.5f);

        add(title = PixelScene.renderTextBlock(9));
        add(this.minTxt = PixelScene.renderTextBlock(6));
        add(this.maxTxt = PixelScene.renderTextBlock(6));

        add(sliderBG = new ColorBlock(1, 1, 0xFF222222));
        sliderNode = Chrome.get(Chrome.Type.RED_BUTTON);
        sliderNode.size(5, 9);

        pointerArea = new PointerArea(0, 0, 0, 0) {
            boolean pressed = false;

            @Override
            protected void onPointerDown(PointerEvent event) {
                pressed = true;
                PointF p = camera().screenToCamera((int) event.current.x, (int) event.current.y);
                sliderNode.x = GameMath.gate(sliderBG.x - 2, p.x, sliderBG.x + sliderBG.width() - 2);
                sliderNode.brightness(1.5f);
            }

            @Override
            protected void onPointerUp(PointerEvent event) {
                if (pressed) {
                    PointF p = camera().screenToCamera((int) event.current.x, (int) event.current.y);
                    sliderNode.x = GameMath.gate(sliderBG.x - 2, p.x, sliderBG.x + sliderBG.width() - 2);
                    sliderNode.resetColor();

                    //sets the selected value
                    selectedVal = minVal + Math.round(sliderNode.x / tickDist);
                    sliderNode.x = (int) (x + tickDist * (selectedVal - minVal));
                    onChange();
                    pressed = false;
                }
            }

            @Override
            protected void onDrag(PointerEvent event) {
                if (pressed) {
                    PointF p = camera().screenToCamera((int) event.current.x, (int) event.current.y);
                    sliderNode.x = GameMath.gate(sliderBG.x - 2, p.x, sliderBG.x + sliderBG.width() - 2);
                }
            }
        };
        add(pointerArea);

    }

    @Override
    protected void layout() {
        title.setPos(
                x + (width - title.width()) / 2,
                y + 2
        );
        PixelScene.align(title);
        sliderBG.y = y + height() - 8;
        sliderBG.x = x + 2;
        sliderBG.size(width - 5, 1);
        tickDist = sliderBG.width() / (maxVal - minVal);
        for (int i = 0; i < sliderTicks.length; i++) {
            sliderTicks[i].y = sliderBG.y - 5;
            sliderTicks[i].x = (int) (x + 2 + (tickDist * i));
        }

        minTxt.setPos(
                x + 1,
                sliderBG.y - 6 - minTxt.height()
        );
        maxTxt.setPos(
                x + width() - maxTxt.width() - 1,
                sliderBG.y - 6 - minTxt.height()
        );

        sliderNode.x = (int) (x + tickDist * (selectedVal - minVal));
        sliderNode.y = sliderBG.y - 4;

        pointerArea.x = x;
        pointerArea.y = y;
        pointerArea.width = width();
        pointerArea.height = height();

        BG.size(width(), height());
        BG.x = x;
        BG.y = y;

    }
}
