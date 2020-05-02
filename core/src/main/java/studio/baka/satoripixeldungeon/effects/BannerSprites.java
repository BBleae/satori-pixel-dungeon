package studio.baka.satoripixeldungeon.effects;

import studio.baka.satoripixeldungeon.Assets;
import com.watabou.noosa.Image;

public class BannerSprites {

    public enum Type {
        PIXEL_DUNGEON,
        BOSS_SLAIN,
        GAME_OVER,
        SELECT_YOUR_HERO,
        PIXEL_DUNGEON_SIGNS
    }

    public static Image get(Type type) {
        Image icon = new Image(Assets.BANNERS);
        switch (type) {
            case PIXEL_DUNGEON:
                icon.frame(icon.texture.uvRect(0, 0, 132, 90));
                break;
            case BOSS_SLAIN:
                icon.frame(icon.texture.uvRect(0, 90, 128, 125));
                break;
            case GAME_OVER:
                icon.frame(icon.texture.uvRect(0, 125, 128, 160));
                break;
            case SELECT_YOUR_HERO:
                icon.frame(icon.texture.uvRect(0, 160, 128, 181));
                break;
            case PIXEL_DUNGEON_SIGNS:
                icon.frame(icon.texture.uvRect(133, 0, 255, 90));
                break;
        }
        return icon;
    }
}
