package studio.baka.satoripixeldungeon.scenes;

import studio.baka.satoripixeldungeon.*;
import studio.baka.satoripixeldungeon.effects.BannerSprites;
import studio.baka.satoripixeldungeon.effects.Fireball;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.*;
import studio.baka.satoripixeldungeon.windows.WndOptions;
import studio.baka.satoripixeldungeon.windows.WndStartGame;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.DeviceCompat;

public class TitleScene extends PixelScene {
    //public final static String gameversion = "1.7.3.X";

    @Override
    public void create() {

        super.create();

        Music.INSTANCE.play(Assets.THEMEOFSATORI, true);

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;

        Archs archs = new Archs();
        archs.setSize(w, h);
        add(archs);

        Image title = BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON);
        add(title);

        float topRegion = Math.max(title.height, h * 0.45f);

        title.x = (w - title.width()) / 2f;
        if (SPDSettings.landscape()) {
            title.y = (topRegion - title.height()) / 2f;
        } else {
            title.y = 20 + (topRegion - title.height() - 20) / 2f;
        }

        align(title);

        placeTorch(title.x + 22, title.y + 46);
        placeTorch(title.x + title.width - 22, title.y + 46);

        Image signs = new Image(BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON_SIGNS)) {
            private float time = 0;

            @Override
            public void update() {
                super.update();
                am = Math.max(0f, (float) Math.sin(time += Game.elapsed));
                if (time >= 1.5f * Math.PI) time = 0;
            }

            @Override
            public void draw() {
                Blending.setLightMode();
                super.draw();
                Blending.setNormalMode();
            }
        };
        signs.x = title.x + (title.width() - signs.width()) / 2f;
        signs.y = title.y;
        add(signs);

        TitleButton btnPlay = new TitleButton(Messages.get(this, "enter")) {
            @Override
            protected void onClick() {
                if (GamesInProgress.checkAll().size() == 0) {
                    TitleScene.this.add(new WndStartGame(1));
                } else {
                    SatoriPixelDungeon.switchNoFade(StartScene.class);
                }
            }

            @Override
            protected boolean onLongClick() {
                //making it easier to start runs quickly while debugging
                if (DeviceCompat.isDebug()) {
                    TitleScene.this.add(new WndStartGame(1));
                    return true;
                }
                return super.onLongClick();
            }
        };
        btnPlay.icon(Icons.get(Icons.ENTER));
        add(btnPlay);

        TitleButton btnSupport = new TitleButton(Messages.get(this, "support")) {
            @Override
            protected void onClick() {
                WndOptions wnd = new WndOptions(Messages.get(TitleScene.class, "support"),
                        Messages.get(TitleScene.class, "patreon_body")) {
					/*
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							//DeviceCompat.openURI("https://www.patreon.com/ShatteredPixel");
						} else {
							hide();
						}
					}
					*/
                };
                parent.add(wnd);
            }
			/*
			@Override
			protected boolean onLongClick() {
				//making it easier to start runs quickly while debugging

				if (!Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE)) {
					Badges.displayBadge(Badges.Badge.UNLOCK_ROGUE);
					return true;
				}
				if (!Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS)) {
					Badges.displayBadge(Badges.Badge.UNLOCK_HUNTRESS);
					return true;
				}
				if (!Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE)) {
					Badges.displayBadge(Badges.Badge.UNLOCK_MAGE);
					return true;
				}
				if (!Badges.isUnlocked(Badges.Badge.UNLOCK_MAHO_SHOUJO)) {
					Badges.displayBadge(Badges.Badge.UNLOCK_MAHO_SHOUJO);
					return true;
				}
				return super.onLongClick();
			}
			*/
        };
        btnSupport.icon(Icons.get(Icons.GOLD));
        add(btnSupport);

        TitleButton btnRankings = new TitleButton(Messages.get(this, "rankings")) {
            @Override
            protected void onClick() {
                SatoriPixelDungeon.switchNoFade(RankingsScene.class);
            }
        };
        btnRankings.icon(Icons.get(Icons.RANKINGS));
        add(btnRankings);

        TitleButton btnBadges = new TitleButton(Messages.get(this, "badges")) {
            @Override
            protected void onClick() {
                SatoriPixelDungeon.switchNoFade(BadgesScene.class);
            }
        };
        btnBadges.icon(Icons.get(Icons.BADGES));
        add(btnBadges);

        TitleButton btnChanges = new TitleButton(Messages.get(this, "changes")) {
            @Override
            protected void onClick() {
                ChangesScene.changesSelected = 0;
                SatoriPixelDungeon.switchNoFade(ChangesScene.class);
            }
        };
        btnChanges.icon(Icons.get(Icons.CHANGES));
        add(btnChanges);

        TitleButton btnAbout = new TitleButton(Messages.get(this, "about")) {
            @Override
            protected void onClick() {
                SatoriPixelDungeon.switchNoFade(AboutScene.class);
            }
        };
        btnAbout.icon(Icons.get(Icons.SHPX));
        add(btnAbout);

        final int BTN_HEIGHT = 21;
        int GAP = (int) (h - topRegion - (SPDSettings.landscape() ? 3 : 4) * BTN_HEIGHT) / 3;
        GAP /= SPDSettings.landscape() ? 3 : 4;
        GAP = Math.max(GAP, 2);

        if (SPDSettings.landscape()) {
            btnPlay.setRect(title.x - 50, topRegion + GAP, ((title.width() + 100) / 2) - 1, BTN_HEIGHT);
            align(btnPlay);
            btnSupport.setRect(btnPlay.right() + 2, btnPlay.top(), btnPlay.width(), BTN_HEIGHT);
            btnRankings.setRect(btnPlay.left() + (btnPlay.width() * .33f) + 1, btnPlay.bottom() + GAP, (btnPlay.width() * .67f) - 1, BTN_HEIGHT);
            btnBadges.setRect(btnRankings.right() + 2, btnRankings.top(), btnRankings.width(), BTN_HEIGHT);
            btnChanges.setRect(btnRankings.left(), btnRankings.bottom() + GAP, btnRankings.width(), BTN_HEIGHT);
            btnAbout.setRect(btnChanges.right() + 2, btnChanges.top(), btnRankings.width(), BTN_HEIGHT);
        } else {
            btnPlay.setRect(title.x, topRegion + GAP, title.width(), BTN_HEIGHT);
            align(btnPlay);
            btnRankings.setRect(btnPlay.left(), btnPlay.bottom() + GAP, (btnPlay.width() / 2) - 1, BTN_HEIGHT);
            btnBadges.setRect(btnRankings.right() + 2, btnRankings.top(), btnRankings.width(), BTN_HEIGHT);
            btnChanges.setRect(btnRankings.left(), btnRankings.bottom() + GAP, btnRankings.width(), BTN_HEIGHT);
            btnAbout.setRect(btnChanges.right() + 2, btnChanges.top(), btnChanges.width(), BTN_HEIGHT);
            btnSupport.setRect(btnPlay.left(), btnAbout.bottom() + GAP, btnPlay.width(), BTN_HEIGHT);
        }

        BitmapText version = new BitmapText("v" + Game.version, pixelFont);
        version.measure();
        version.hardlight(0x888888);
        version.x = w - version.width() - 4;
        version.y = h - version.height() - 2;
        add(version);

        int pos = 2;

        PrefsButton btnPrefs = new PrefsButton();
        btnPrefs.setRect(pos, 0, 16, 20);
        add(btnPrefs);

        pos += btnPrefs.width();

        LanguageButton btnLang = new LanguageButton();
        btnLang.setRect(pos, 0, 16, 20);
        add(btnLang);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(w - btnExit.width(), 0);
        add(btnExit);

        fadeIn();
    }

    private void placeTorch(float x, float y) {
        Fireball fb = new Fireball();
        fb.setPos(x, y);
        add(fb);
    }

    private static class TitleButton extends StyledButton {

        public TitleButton(String label) {
            this(label, 9);
        }

        public TitleButton(String label, int size) {
            super(Chrome.Type.GREY_BUTTON_TR, label, size);
        }

    }
}
