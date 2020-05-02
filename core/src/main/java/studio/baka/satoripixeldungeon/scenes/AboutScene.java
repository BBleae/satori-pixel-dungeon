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

    private static final String TTL_SHPX = "Shattered Pixel Dungeon";

    private static final String TXT_SHPX = "Design, Code, & Graphics: Evan";


    private static final String LNK_SHPX = "ShatteredPixel.com";

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

        RenderedTextBlock shpxtitle = renderTextBlock(TTL_SPD, 8);
        shpxtitle.hardlight(Window.SHPX_COLOR);
        add(shpxtitle);

        shpxtitle.setPos(
                (colWidth - shpxtitle.width()) / 2,
                spd.y + spd.height + 5
        );
        align(shpxtitle);

        RenderedTextBlock shpxtext = renderTextBlock(TXT_SPD, 8);
        shpxtext.maxWidth((int) Math.min(colWidth, 120));
        add(shpxtext);

        shpxtext.setPos((colWidth - shpxtext.width()) / 2, shpxtitle.bottom() + 12);
        align(shpxtext);

        RenderedTextBlock shpxlink = renderTextBlock(LNK_SPD, 8);
        shpxlink.maxWidth(shpxtext.maxWidth());
        shpxlink.hardlight(Window.SHPX_COLOR);
        add(shpxlink);

        shpxlink.setPos((colWidth - shpxlink.width()) / 2, shpxtext.bottom() + 6);
        align(shpxlink);

        PointerArea shpxhotArea = new PointerArea(shpxlink.left(), shpxlink.top(), shpxlink.width(), shpxlink.height()) {
            @Override
            protected void onClick(PointerEvent event) {
                //DeviceCompat.openURI( "https://" + LNK_SHPX );
            }
        };
        add(shpxhotArea);

        Image shpx = Icons.SHPX.get();
        shpx.x = wataOffset + (colWidth - shpx.width()) / 2;
        shpx.y = SPDSettings.landscape() ?
                colTop :
                shpxlink.top() + shpx.height + 20;
        align(shpx);
        add(shpx);

        new Flare(7, 64).color(0x112233, true).show(shpx, 0).angularSpeed = +20;

        RenderedTextBlock wataTitle = renderTextBlock(TTL_SHPX, 8);
        wataTitle.hardlight(Window.TITLE_COLOR);
        add(wataTitle);

        wataTitle.setPos(
                wataOffset + (colWidth - wataTitle.width()) / 2,
                shpx.y + shpx.height + 11
        );
        align(wataTitle);

        RenderedTextBlock wataText = renderTextBlock(TXT_SHPX, 8);
        wataText.maxWidth((int) Math.min(colWidth, 120));
        wataText.setHightlighting(false); //underscore in cube_code
        add(wataText);

        wataText.setPos(wataOffset + (colWidth - wataText.width()) / 2, wataTitle.bottom() + 12);
        align(wataText);

        RenderedTextBlock wataLink = renderTextBlock(LNK_SHPX, 8);
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
