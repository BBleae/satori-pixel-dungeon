package studio.baka.satoripixeldungeon.ui.changelist;

import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.windows.WndTitledMessage;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;

public class ChangesWindow extends WndTitledMessage {

    public ChangesWindow(Image icon, String title, String message) {
        super(icon, title, message);

        PointerArea blocker = new PointerArea(0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height) {
            @Override
            protected void onClick(PointerEvent event) {
                hide();
            }
        };
        blocker.camera = PixelScene.uiCamera;
        add(blocker);

    }

}
