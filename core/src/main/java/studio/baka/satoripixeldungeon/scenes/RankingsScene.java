package studio.baka.satoripixeldungeon.scenes;

import studio.baka.satoripixeldungeon.Rankings;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.effects.Flare;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.*;
import studio.baka.satoripixeldungeon.windows.WndError;
import studio.baka.satoripixeldungeon.windows.WndRanking;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.GameMath;

public class RankingsScene extends PixelScene {

    private static final float ROW_HEIGHT_MAX = 20;
    private static final float ROW_HEIGHT_MIN = 12;

    private static final float MAX_ROW_WIDTH = 160;

    private static final float GAP = 4;

    @Override
    public void create() {

        super.create();

        //Music.INSTANCE.play( Assets.THEME, true );

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;

        Archs archs = new Archs();
        archs.setSize(w, h);
        add(archs);

        Rankings.INSTANCE.load();

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
        title.hardlight(Window.TITLE_COLOR);
        title.setPos(
                (w - title.width()) / 2f,
                (20 - title.height()) / 2f
        );
        align(title);
        add(title);

        if (Rankings.INSTANCE.records.size() > 0) {

            //attempts to give each record as much space as possible, ideally as much space as portrait mode
            float rowHeight = GameMath.gate(ROW_HEIGHT_MIN, (uiCamera.height - 26) / Rankings.INSTANCE.records.size(), ROW_HEIGHT_MAX);

            float left = (w - Math.min(MAX_ROW_WIDTH, w)) / 2 + GAP;
            float top = (h - rowHeight * Rankings.INSTANCE.records.size()) / 2;

            int pos = 0;

            for (Rankings.Record rec : Rankings.INSTANCE.records) {
                Record row = new Record(pos, pos == Rankings.INSTANCE.lastRecord, rec);
                float offset =
                        rowHeight <= 14 ?
                                pos % 2 == 1 ?
                                        5 :
                                        -5
                                : 0;
                row.setRect(left + offset, top + pos * rowHeight, w - left * 2, rowHeight);
                add(row);

                pos++;
            }

            if (Rankings.INSTANCE.totalNumber >= Rankings.TABLE_SIZE) {

                RenderedTextBlock label = PixelScene.renderTextBlock(8);
                label.hardlight(0xCCCCCC);
                label.setHightlighting(true, Window.SHPX_COLOR);
                label.text(Messages.get(this, "total") + " _" + Rankings.INSTANCE.wonNumber + "_/" + Rankings.INSTANCE.totalNumber);
                add(label);

                label.setPos(
                        (w - label.width()) / 2,
                        h - label.height() - 2 * GAP
                );
                align(label);

            }

        } else {

            RenderedTextBlock noRec = PixelScene.renderTextBlock(Messages.get(this, "no_games"), 8);
            noRec.hardlight(0xCCCCCC);
            noRec.setPos(
                    (w - noRec.width()) / 2,
                    (h - noRec.height()) / 2
            );
            align(noRec);
            add(noRec);

        }

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        fadeIn();
    }

    @Override
    protected void onBackPressed() {
        SatoriPixelDungeon.switchNoFade(TitleScene.class);
    }

    public static class Record extends Button {

        private static final float GAP = 4;

        private static final int[] TEXT_WIN = {0xFFFF88, 0xB2B25F};
        private static final int[] TEXT_LOSE = {0xDDDDDD, 0x888888};
        private static final int FLARE_WIN = 0x888866;
        private static final int FLARE_LOSE = 0x666666;

        private final Rankings.Record rec;

        protected ItemSprite shield;
        private Flare flare;
        private BitmapText position;
        private RenderedTextBlock desc;
        private Image steps;
        private BitmapText depth;
        private Image classIcon;
        private BitmapText level;

        public Record(int pos, boolean latest, Rankings.Record rec) {
            super();

            this.rec = rec;

            if (latest) {
                flare = new Flare(6, 24);
                flare.angularSpeed = 90;
                flare.color(rec.win ? FLARE_WIN : FLARE_LOSE);
                addToBack(flare);
            }

            if (pos != Rankings.TABLE_SIZE - 1) {
                position.text(Integer.toString(pos + 1));
            } else
                position.text(" ");
            position.measure();

            desc.text(Messages.titleCase(rec.desc()));

            //desc.measure();

            int odd = pos % 2;

            if (rec.win) {
                shield.view(ItemSpriteSheet.AMULET, null);
                position.hardlight(TEXT_WIN[odd]);
                desc.hardlight(TEXT_WIN[odd]);
                depth.hardlight(TEXT_WIN[odd]);
                level.hardlight(TEXT_WIN[odd]);
            } else {
                position.hardlight(TEXT_LOSE[odd]);
                desc.hardlight(TEXT_LOSE[odd]);
                depth.hardlight(TEXT_LOSE[odd]);
                level.hardlight(TEXT_LOSE[odd]);

                if (rec.depth != 0) {
                    depth.text(Integer.toString(rec.depth));
                    depth.measure();
                    steps.copy(Icons.DEPTH.get());

                    add(steps);
                    add(depth);
                }

            }

            if (rec.herolevel != 0) {
                level.text(Integer.toString(rec.herolevel));
                level.measure();
                add(level);
            }

            classIcon.copy(Icons.get(rec.heroClass));
            if (rec.heroClass == HeroClass.ROGUE) {
                //cloak of shadows needs to be brightened a bit
                classIcon.brightness(2f);
            }
        }

        @Override
        protected void createChildren() {

            super.createChildren();

            shield = new ItemSprite(ItemSpriteSheet.TOMB, null);
            add(shield);

            position = new BitmapText(PixelScene.pixelFont);
            add(position);

            desc = renderTextBlock(7);
            add(desc);

            depth = new BitmapText(PixelScene.pixelFont);

            steps = new Image();

            classIcon = new Image();
            add(classIcon);

            level = new BitmapText(PixelScene.pixelFont);
        }

        @Override
        protected void layout() {

            super.layout();

            shield.x = x;
            shield.y = y + (height - shield.height) / 2f;
            align(shield);

            position.x = shield.x + (shield.width - position.width()) / 2f;
            position.y = shield.y + (shield.height - position.height()) / 2f + 1;
            align(position);

            if (flare != null) {
                flare.point(shield.center());
            }

            classIcon.x = x + width - 16 + (16 - classIcon.width()) / 2f;
            classIcon.y = shield.y + (16 - classIcon.height()) / 2f;
            align(classIcon);

            level.x = classIcon.x + (classIcon.width - level.width()) / 2f;
            level.y = classIcon.y + (classIcon.height - level.height()) / 2f + 1;
            align(level);

            steps.x = x + width - 32 + (16 - steps.width()) / 2f;
            steps.y = shield.y + (16 - steps.height()) / 2f;
            align(steps);

            depth.x = steps.x + (steps.width - depth.width()) / 2f;
            depth.y = steps.y + (steps.height - depth.height()) / 2f + 1;
            align(depth);

            desc.maxWidth((int) (steps.x - (shield.x + shield.width + GAP)));
            desc.setPos(shield.x + shield.width + GAP, shield.y + (shield.height - desc.height()) / 2f + 1);
            align(desc);
        }

        @Override
        protected void onClick() {
            if (rec.gameData != null) {
                parent.add(new WndRanking(rec));
            } else {
                parent.add(new WndError(Messages.get(RankingsScene.class, "no_info")));
            }
        }
    }
}
