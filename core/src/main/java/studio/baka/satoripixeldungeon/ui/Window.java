package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Chrome;
import studio.baka.satoripixeldungeon.effects.ShadowBox;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.*;
import com.watabou.utils.Signal;

public class Window extends Group implements Signal.Listener<KeyEvent> {

    protected int width;
    protected int height;

    protected int yOffset;

    protected PointerArea blocker;
    protected ShadowBox shadow;
    protected NinePatch chrome;

    public static final int TITLE_COLOR = 0xFFFF44;
    public static final int SHPX_COLOR = 0x33BB33;

    public Window() {
        this(0, 0, 0, Chrome.get(Chrome.Type.WINDOW));
    }

    public Window(int width, int height) {
        this(width, height, 0, Chrome.get(Chrome.Type.WINDOW));
    }

    public Window(int width, int height, NinePatch chrome) {
        this(width, height, 0, chrome);
    }

    public Window(int width, int height, int yOffset, NinePatch chrome) {
        super();

        this.yOffset = yOffset;

        blocker = new PointerArea(0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height) {
            @Override
            protected void onClick(PointerEvent event) {
                if (Window.this.parent != null && !Window.this.chrome.overlapsScreenPoint(
                        (int) event.current.x,
                        (int) event.current.y)) {

                    onBackPressed();
                }
            }
        };
        blocker.camera = PixelScene.uiCamera;
        add(blocker);

        this.chrome = chrome;

        this.width = width;
        this.height = height;

        shadow = new ShadowBox();
        shadow.am = 0.5f;
        shadow.camera = PixelScene.uiCamera.visible ?
                PixelScene.uiCamera : Camera.main;
        add(shadow);

        chrome.x = -chrome.marginLeft();
        chrome.y = -chrome.marginTop();
        chrome.size(
                width - chrome.x + chrome.marginRight(),
                height - chrome.y + chrome.marginBottom());
        add(chrome);

        camera = new Camera(0, 0,
                (int) chrome.width,
                (int) chrome.height,
                PixelScene.defaultZoom);
        camera.x = (int) (Game.width - camera.width * camera.zoom) / 2;
        camera.y = (int) (Game.height - camera.height * camera.zoom) / 2;
        camera.y -= yOffset * camera.zoom;
        camera.scroll.set(chrome.x, chrome.y);
        Camera.add(camera);

        shadow.boxRect(
                camera.x / camera.zoom,
                camera.y / camera.zoom,
                chrome.width(), chrome.height);

        KeyEvent.addKeyListener(this);
    }

    public void resize(int w, int h) {
        this.width = w;
        this.height = h;

        chrome.size(
                width + chrome.marginHor(),
                height + chrome.marginVer());

        camera.resize((int) chrome.width, (int) chrome.height);
        camera.x = (int) (Game.width - camera.screenWidth()) / 2;
        camera.y = (int) (Game.height - camera.screenHeight()) / 2;
        camera.y += yOffset * camera.zoom;

        shadow.boxRect(camera.x / camera.zoom, camera.y / camera.zoom, chrome.width(), chrome.height);
    }

    public void offset(int yOffset) {
        camera.y -= this.yOffset * camera.zoom;
        this.yOffset = yOffset;
        camera.y += yOffset * camera.zoom;

        shadow.boxRect(camera.x / camera.zoom, camera.y / camera.zoom, chrome.width(), chrome.height);
    }

    public void hide() {
        if (parent != null) {
            parent.erase(this);
        }
        destroy();
    }

    @Override
    public void destroy() {
        super.destroy();

        Camera.remove(camera);
        KeyEvent.removeKeyListener(this);
    }

    @Override
    public boolean onSignal(KeyEvent event) {
        if (event.pressed) {
            switch (event.code) {
                case KeyEvent.BACK:
                    onBackPressed();
                    return true;
                case KeyEvent.MENU:
                    onMenuPressed();
                    return true;
            }
        }

        return false;
    }

    public void onBackPressed() {
        hide();
    }

    public void onMenuPressed() {
    }
}
