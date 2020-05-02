package studio.baka.satoripixeldungeon.scenes;

import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.effects.Flare;
import studio.baka.satoripixeldungeon.ui.*;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;

public class AboutScene extends PixelScene {

    private static final String TTL_SPD = "Satori's Pixel Dungeon v1.7.3.X";

    private static final String TXT_SPD =
            "Code & Graphics: Tomnycui\n" +
                    "Companion & Instructor: BBleae\n" +
                    "Graphics: 万里 & Missing Power\n献给：balanca";

    private static final String LNK_SPD = "https://gitee.com/\nsatori-pixel-dungeon-team";

    private static final String TITLE_ORIGIN = "Shattered Pixel Dungeon";

    private static final String DESC_ORIGIN = "Design, Code, & Graphics: Evan";

    private static final String LINK_ORIGIN = "ShatteredPixel.com";

    @Override
    public void create() {
        super.create();

        final float colWidth = Camera.main.width / (SPDSettings.landscape() ? 2 : 1);
        final float colTop = (Camera.main.height / 2) - (SPDSettings.landscape() ? 30 : 90);
        final float wataOffset = SPDSettings.landscape() ? colWidth : 0;

        Image spd = Icons.WATA.get();
        spd.x = (colWidth - spd.width()) / 2;
        spd.y = colTop;
        align(spd);
        add(spd);

        new Flare(7, 64).color(0x225511, true).show(spd, 0).angularSpeed = +20;

        RenderedTextBlock titleOrigin = renderTextBlock(TTL_SPD, 8);
        titleOrigin.hardlight(Window.SHPX_COLOR);
        add(titleOrigin);

        titleOrigin.setPos(
                (colWidth - titleOrigin.width()) / 2,
                spd.y + spd.height + 5
        );
        align(titleOrigin);

        RenderedTextBlock descOrigin = renderTextBlock(TXT_SPD, 8);
        descOrigin.maxWidth((int) Math.min(colWidth, 120));
        add(descOrigin);

        descOrigin.setPos((colWidth - descOrigin.width()) / 2, titleOrigin.bottom() + 12);
        align(descOrigin);

        RenderedTextBlock linkOrigin = renderTextBlock(LNK_SPD, 8);
        linkOrigin.maxWidth(descOrigin.maxWidth());
        linkOrigin.hardlight(Window.SHPX_COLOR);
        add(linkOrigin);

        linkOrigin.setPos((colWidth - linkOrigin.width()) / 2, descOrigin.bottom() + 6);
        align(linkOrigin);

        PointerArea hotAreaOrigin = new PointerArea(linkOrigin.left(), linkOrigin.top(), linkOrigin.width(), linkOrigin.height()) {
            @Override
            protected void onClick(PointerEvent event) {
            }
        };
        add(hotAreaOrigin);

        Image iconOrigin = Icons.SHPX.get();
        iconOrigin.x = wataOffset + (colWidth - iconOrigin.width()) / 2;
        iconOrigin.y = SPDSettings.landscape() ?
                colTop :
                linkOrigin.top() + iconOrigin.height + 20;
        align(iconOrigin);
        add(iconOrigin);

        new Flare(7, 64).color(0x112233, true).show(iconOrigin, 0).angularSpeed = +20;

        RenderedTextBlock wataTitle = renderTextBlock(TITLE_ORIGIN, 8);
        wataTitle.hardlight(Window.TITLE_COLOR);
        add(wataTitle);

        wataTitle.setPos(
                wataOffset + (colWidth - wataTitle.width()) / 2,
                iconOrigin.y + iconOrigin.height + 11
        );
        align(wataTitle);

        RenderedTextBlock wataText = renderTextBlock(DESC_ORIGIN, 8);
        wataText.maxWidth((int) Math.min(colWidth, 120));
        wataText.setHightlighting(false); //underscore in cube_code
        add(wataText);

        wataText.setPos(wataOffset + (colWidth - wataText.width()) / 2, wataTitle.bottom() + 12);
        align(wataText);

        RenderedTextBlock wataLink = renderTextBlock(LINK_ORIGIN, 8);
        wataLink.maxWidth((int) Math.min(colWidth, 120));
        wataLink.hardlight(Window.TITLE_COLOR);
        add(wataLink);

        wataLink.setPos(wataOffset + (colWidth - wataLink.width()) / 2, wataText.bottom() + 6);
        align(wataLink);

        PointerArea hotArea = new PointerArea(wataLink.left(), wataLink.top(), wataLink.width(), wataLink.height()) {
            @Override
            protected void onClick(PointerEvent event) {
                //DeviceCompat.openURI( "https://" + LNK_WATA );
            }
        };
        add(hotArea);


        Archs archs = new Archs();
        archs.setSize(Camera.main.width, Camera.main.height);
        addToBack(archs);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        fadeIn();
    }

    @Override
    protected void onBackPressed() {
        SatoriPixelDungeon.switchNoFade(TitleScene.class);
    }
}
