package studio.baka.satoripixeldungeon.scenes;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.windows.WndStory;
import com.watabou.noosa.Game;

public class IntroScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        add(new WndStory(Messages.get(this, "text")) {
            @Override
            public void hide() {
                super.hide();
                Game.switchScene(InterlevelScene.class);
            }
        });

        fadeIn();
    }
}
