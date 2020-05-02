package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import com.watabou.noosa.Image;

public enum Icons {

    //button icons
    CHECKED,
    UNCHECKED,
    INFO,
    CHALLENGE_OFF,
    CHALLENGE_ON,
    PREFS,
    LANGS,
    EXIT,
    CLOSE,
    ARROW,

    //ingame UI icons
    SKULL,
    BUSY,
    COMPASS,
    SLEEP,
    ALERT,
    LOST,
    TARGET,
    BACKPACK,
    SEED_POUCH,
    SCROLL_HOLDER,
    POTION_BANDOLIER,
    WAND_HOLSTER,

    //hero & rankings icons
    DEPTH,
    WARRIOR,
    MAGE,
    ROGUE,
    HUNTRESS,
    MAHOU_SHOUJO,

    //main menu icons
    ENTER,
    GOLD,
    RANKINGS,
    BADGES,
    CHANGES,
    SHPX,

    //misc icons
    LIBGDX,
    WATA,
    WARNING;

    public Image get() {
        return get(this);
    }

    public static Image get(Icons type) {
        Image icon = new Image(Assets.ICONS);
        switch (type) {
            case CHECKED:
                icon.frame(icon.texture.uvRect(0, 0, 12, 12));
                break;
            case UNCHECKED:
                icon.frame(icon.texture.uvRect(16, 0, 28, 12));
                break;
            case INFO:
                icon.frame(icon.texture.uvRect(32, 0, 46, 14));
                break;
            case CHALLENGE_ON:
                icon.frame(icon.texture.uvRect(48, 0, 62, 12));
                break;
            case CHALLENGE_OFF:
                icon.frame(icon.texture.uvRect(64, 0, 78, 12));
                break;
            case PREFS:
                icon.frame(icon.texture.uvRect(80, 0, 92, 12));
                break;
            case LANGS:
                icon.frame(icon.texture.uvRect(96, 0, 108, 9));
                break;
            case EXIT:
                icon.frame(icon.texture.uvRect(112, 0, 125, 9));
                break;
            case CLOSE:
                icon.frame(icon.texture.uvRect(0, 16, 11, 27));
                break;
            case ARROW:
                icon.frame(icon.texture.uvRect(16, 16, 27, 27));
                break;

            case SKULL:
                icon.frame(icon.texture.uvRect(0, 32, 8, 40));
                break;
            case BUSY:
                icon.frame(icon.texture.uvRect(8, 32, 16, 40));
                break;
            case COMPASS:
                icon.frame(icon.texture.uvRect(0, 40, 7, 45));
                break;
            case SLEEP:
                icon.frame(icon.texture.uvRect(16, 32, 25, 40));
                break;
            case ALERT:
                icon.frame(icon.texture.uvRect(16, 40, 24, 48));
                break;
            case LOST:
                icon.frame(icon.texture.uvRect(24, 40, 32, 48));
                break;
            case TARGET:
                icon.frame(icon.texture.uvRect(32, 32, 48, 48));
                break;
            case BACKPACK:
                icon.frame(icon.texture.uvRect(48, 32, 58, 42));
                break;
            case SCROLL_HOLDER:
                icon.frame(icon.texture.uvRect(58, 32, 68, 42));
                break;
            case SEED_POUCH:
                icon.frame(icon.texture.uvRect(68, 32, 78, 42));
                break;
            case WAND_HOLSTER:
                icon.frame(icon.texture.uvRect(78, 32, 88, 42));
                break;
            case POTION_BANDOLIER:
                icon.frame(icon.texture.uvRect(88, 32, 98, 42));
                break;

            case DEPTH:
                icon.frame(icon.texture.uvRect(0, 48, 13, 64));
                break;
            case WARRIOR:
                icon.frame(icon.texture.uvRect(16, 48, 25, 63));
                break;
            case MAGE:
                icon.frame(icon.texture.uvRect(32, 48, 47, 62));
                break;
            case ROGUE:
                icon.frame(icon.texture.uvRect(48, 48, 57, 63));
                break;
            case HUNTRESS:
                icon.frame(icon.texture.uvRect(64, 48, 80, 64));
                break;
            case MAHOU_SHOUJO:
                icon.frame(icon.texture.uvRect(80, 48, 96, 64));

            case ENTER:
                icon.frame(icon.texture.uvRect(0, 64, 17, 81));
                break;
            case RANKINGS:
                icon.frame(icon.texture.uvRect(17, 64, 34, 81));
                break;
            case BADGES:
                icon.frame(icon.texture.uvRect(34, 64, 51, 81));
                break;
            case CHANGES:
                icon.frame(icon.texture.uvRect(51, 64, 68, 79));
                break;
            case SHPX:
                icon.frame(icon.texture.uvRect(68, 64, 84, 80));
                break;
            case GOLD:
                icon.frame(icon.texture.uvRect(85, 64, 102, 80));
                break;

            case LIBGDX:
                icon.frame(icon.texture.uvRect(0, 81, 16, 94));
                break;
            case WATA:
                icon.frame(icon.texture.uvRect(17, 81, 34, 95));
                break;
            case WARNING:
                icon.frame(icon.texture.uvRect(34, 81, 48, 95));
                break;
        }
        return icon;
    }

    public static Image get(HeroClass cl) {
        switch (cl) {
            case WARRIOR:
                return get(WARRIOR);
            case MAGE:
            default:
                return get(MAGE);
            case ROGUE:
                return get(ROGUE);
            case HUNTRESS:
                return get(HUNTRESS);
            case MAHOU_SHOUJO:
                return get(MAHOU_SHOUJO);
        }
    }
}
