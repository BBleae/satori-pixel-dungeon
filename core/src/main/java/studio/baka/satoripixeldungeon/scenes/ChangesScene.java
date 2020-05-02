package studio.baka.satoripixeldungeon.scenes;

import studio.baka.satoripixeldungeon.Chrome;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.*;
import studio.baka.satoripixeldungeon.ui.changelist.ChangeInfo;
import studio.baka.satoripixeldungeon.ui.changelist.v0_7_X_Changes;
import com.watabou.noosa.Camera;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class ChangesScene extends PixelScene {

    public static int changesSelected = 0;

    @Override
    public void create() {
        super.create();

        int w = Camera.main.width;
        int h = Camera.main.height;

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
        title.hardlight(Window.TITLE_COLOR);
        title.setPos(
                (w - title.width()) / 2f,
                (20 - title.height()) / 2f
        );
        align(title);
        add(title);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        NinePatch panel = Chrome.get(Chrome.Type.TOAST);

        int pw = 135 + panel.marginLeft() + panel.marginRight() - 2;
        int ph = h - 35;

        panel.size(pw, ph);
        panel.x = (w - pw) / 2f;
        panel.y = title.bottom() + 4;
        align(panel);
        add(panel);

        final ArrayList<ChangeInfo> changeInfos = new ArrayList<>();

        v0_7_X_Changes.addAllChanges(changeInfos);

        ScrollPane list = new ScrollPane(new Component()) {

            @Override
            public void onClick(float x, float y) {
                for (ChangeInfo info : changeInfos) {
                    if (info.onClick(x, y)) {
                        return;
                    }
                }
            }

        };
        add(list);

        Component content = list.content();
        content.clear();

        float posY = 0;
        float nextPosY = 0;
        boolean second = false;
        for (ChangeInfo info : changeInfos) {
            if (info.major) {
                posY = nextPosY;
                second = false;
                info.setRect(0, posY, panel.innerWidth(), 0);
                content.add(info);
                posY = nextPosY = info.bottom();
            } else {
                if (!second) {
                    second = true;
                    info.setRect(0, posY, panel.innerWidth() / 2f, 0);
                    content.add(info);
                    nextPosY = info.bottom();
                } else {
                    second = false;
                    info.setRect(panel.innerWidth() / 2f, posY, panel.innerWidth() / 2f, 0);
                    content.add(info);
                    nextPosY = Math.max(info.bottom(), nextPosY);
                    posY = nextPosY;
                }
            }
        }

        content.setSize(panel.innerWidth(), (int) Math.ceil(posY));

        list.setRect(
                panel.x + panel.marginLeft(),
                panel.y + panel.marginTop() - 1,
                panel.innerWidth(),
                panel.innerHeight() + 2);
        list.scrollTo(0, 0);

        Archs archs = new Archs();
        archs.setSize(Camera.main.width, Camera.main.height);
        addToBack(archs);

        fadeIn();
    }

    @Override
    protected void onBackPressed() {
        SatoriPixelDungeon.switchNoFade(TitleScene.class);
    }

}
