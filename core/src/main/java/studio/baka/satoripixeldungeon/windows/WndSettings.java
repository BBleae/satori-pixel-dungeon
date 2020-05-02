package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.ui.CheckBox;
import studio.baka.satoripixeldungeon.ui.OptionSlider;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Toolbar;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;

public class WndSettings extends WndTabbed {

	private static final int WIDTH		    = 112;
	private static final int HEIGHT         = 138;
	private static final int SLIDER_HEIGHT	= 24;
	private static final int BTN_HEIGHT	    = 18;
	private static final int GAP_TINY 		= 2;
	private static final int GAP_SML 		= 6;
	private static final int GAP_LRG 		= 18;

	private final DisplayTab display;
	private final UITab ui;
	private final AudioTab audio;

	private static int last_index = 0;

	public WndSettings() {
		super();

		display = new DisplayTab();
		add( display );

		ui = new UITab();
		add( ui );

		audio = new AudioTab();
		add( audio );

		add( new LabeledTab(Messages.get(this, "display")){
			@Override
			protected void select(boolean value) {
				super.select(value);
				display.visible = display.active = value;
				if (value) last_index = 0;
			}
		});

		add( new LabeledTab(Messages.get(this, "ui")){
			@Override
			protected void select(boolean value) {
				super.select(value);
				ui.visible = ui.active = value;
				if (value) last_index = 1;
			}
		});

		add( new LabeledTab(Messages.get(this, "audio")){
			@Override
			protected void select(boolean value) {
				super.select(value);
				audio.visible = audio.active = value;
				if (value) last_index = 2;
			}
		});

		resize(WIDTH, HEIGHT);

		layoutTabs();

		select(last_index);

	}

	private static class DisplayTab extends Group {

		public DisplayTab() {
			super();

			OptionSlider scale = new OptionSlider(Messages.get(this, "scale"),
					(int)Math.ceil(2* Game.density)+ "X",
					PixelScene.maxDefaultZoom + "X",
					(int)Math.ceil(2* Game.density),
					PixelScene.maxDefaultZoom ) {
				@Override
				protected void onChange() {
					if (getSelectedValue() != SPDSettings.scale()) {
						SPDSettings.scale(getSelectedValue());
						SatoriPixelDungeon.seamlessResetScene();
					}
				}
			};
			if ((int)Math.ceil(2* Game.density) < PixelScene.maxDefaultZoom) {
				scale.setSelectedValue(PixelScene.defaultZoom);
				scale.setRect(0, 0, WIDTH, SLIDER_HEIGHT);
				add(scale);
			}

			CheckBox chkSaver = new CheckBox( Messages.get(this, "saver") ) {
				@Override
				protected void onClick() {
					super.onClick();
					if (checked()) {
						checked(!checked());
						SatoriPixelDungeon.scene().add(new WndOptions(
								Messages.get(DisplayTab.class, "saver"),
								Messages.get(DisplayTab.class, "saver_desc"),
								Messages.get(DisplayTab.class, "okay"),
								Messages.get(DisplayTab.class, "cancel")) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									checked(!checked());
									SPDSettings.powerSaver(checked());
								}
							}
						});
					} else {
						SPDSettings.powerSaver(checked());
					}
				}
			};
			if (PixelScene.maxScreenZoom >= 2) {
				chkSaver.setRect(0, scale.bottom() + GAP_TINY, WIDTH, BTN_HEIGHT);
				chkSaver.checked(SPDSettings.powerSaver());
				add(chkSaver);
			}

			RedButton btnOrientation = new RedButton( SPDSettings.landscape() ?
					Messages.get(this, "portrait")
					: Messages.get(this, "landscape") ) {
				@Override
				protected void onClick() {
					SPDSettings.landscape(!SPDSettings.landscape());
				}
			};
			btnOrientation.setRect(0, chkSaver.bottom() + GAP_TINY, WIDTH, BTN_HEIGHT);
			add( btnOrientation );


			OptionSlider brightness = new OptionSlider(Messages.get(this, "brightness"),
					Messages.get(this, "dark"), Messages.get(this, "bright"), -2, 2) {
				@Override
				protected void onChange() {
					SPDSettings.brightness(getSelectedValue());
				}
			};
			brightness.setSelectedValue(SPDSettings.brightness());
			brightness.setRect(0, btnOrientation.bottom() + GAP_LRG, WIDTH, SLIDER_HEIGHT);
			add(brightness);

			OptionSlider tileGrid = new OptionSlider(Messages.get(this, "visual_grid"),
					Messages.get(this, "off"), Messages.get(this, "high"), -1, 3) {
				@Override
				protected void onChange() {
					SPDSettings.visualGrid(getSelectedValue());
				}
			};
			tileGrid.setSelectedValue(SPDSettings.visualGrid());
			tileGrid.setRect(0, brightness.bottom() + GAP_TINY, WIDTH, SLIDER_HEIGHT);
			add(tileGrid);


		}
	}

	private static class UITab extends Group {

		public UITab(){
			super();

			RenderedTextBlock barDesc = PixelScene.renderTextBlock(Messages.get(this, "mode"), 9);
			barDesc.setPos((WIDTH-barDesc.width())/2f, GAP_TINY);
			PixelScene.align(barDesc);
			add(barDesc);

			RedButton btnSplit = new RedButton(Messages.get(this, "split")){
				@Override
				protected void onClick() {
					SPDSettings.toolbarMode(Toolbar.Mode.SPLIT.name());
					Toolbar.updateLayout();
				}
			};
			btnSplit.setRect( 0, barDesc.bottom() + GAP_TINY, 36, 16);
			add(btnSplit);

			RedButton btnGrouped = new RedButton(Messages.get(this, "group")){
				@Override
				protected void onClick() {
					SPDSettings.toolbarMode(Toolbar.Mode.GROUP.name());
					Toolbar.updateLayout();
				}
			};
			btnGrouped.setRect( btnSplit.right()+GAP_TINY, btnSplit.top(), 36, 16);
			add(btnGrouped);

			RedButton btnCentered = new RedButton(Messages.get(this, "center")){
				@Override
				protected void onClick() {
					SPDSettings.toolbarMode(Toolbar.Mode.CENTER.name());
					Toolbar.updateLayout();
				}
			};
			btnCentered.setRect(btnGrouped.right()+GAP_TINY, btnSplit.top(), 36, 16);
			add(btnCentered);

			CheckBox chkFlipToolbar = new CheckBox(Messages.get(this, "flip_toolbar")){
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.flipToolbar(checked());
					Toolbar.updateLayout();
				}
			};
			chkFlipToolbar.setRect(0, btnGrouped.bottom() + GAP_TINY, WIDTH, BTN_HEIGHT);
			chkFlipToolbar.checked(SPDSettings.flipToolbar());
			add(chkFlipToolbar);

			final CheckBox chkFlipTags = new CheckBox(Messages.get(this, "flip_indicators")){
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.flipTags(checked());
					GameScene.layoutTags();
				}
			};
			chkFlipTags.setRect(0, chkFlipToolbar.bottom() + GAP_TINY, WIDTH, BTN_HEIGHT);
			chkFlipTags.checked(SPDSettings.flipTags());
			add(chkFlipTags);

			OptionSlider slots = new OptionSlider(Messages.get(this, "quickslots"), "0", "4", 0, 4) {
				@Override
				protected void onChange() {
					SPDSettings.quickSlots(getSelectedValue());
					Toolbar.updateLayout();
				}
			};
			slots.setSelectedValue(SPDSettings.quickSlots());
			slots.setRect(0, chkFlipTags.bottom() + GAP_TINY, WIDTH, SLIDER_HEIGHT);
			add(slots);

			CheckBox chkImmersive = new CheckBox( Messages.get(this, "nav_bar") ) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.fullscreen(checked());
				}
			};
			chkImmersive.setRect( 0, slots.bottom() + GAP_SML, WIDTH, BTN_HEIGHT );
			chkImmersive.checked(SPDSettings.fullscreen());
			chkImmersive.enable(DeviceCompat.supportsFullScreen());
			add(chkImmersive);

			CheckBox chkFont = new CheckBox(Messages.get(this, "system_font")){
				@Override
				protected void onClick() {
					super.onClick();
					SatoriPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback() {
						@Override
						public void beforeCreate() {
							SPDSettings.systemFont(checked());
						}

						@Override
						public void afterCreate() {
							//do nothing
						}
					});
				}
			};
			chkFont.setRect(0, chkImmersive.bottom() + GAP_TINY, WIDTH, BTN_HEIGHT);
			chkFont.checked(SPDSettings.systemFont());
			add(chkFont);
		}

	}

	private class AudioTab extends Group {

		public AudioTab() {
			OptionSlider musicVol = new OptionSlider(Messages.get(this, "music_vol"), "0", "10", 0, 10) {
				@Override
				protected void onChange() {
					SPDSettings.musicVol(getSelectedValue());
				}
			};
			musicVol.setSelectedValue(SPDSettings.musicVol());
			musicVol.setRect(0, 0, WIDTH, SLIDER_HEIGHT);
			add(musicVol);

			CheckBox musicMute = new CheckBox(Messages.get(this, "music_mute")){
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.music(!checked());
				}
			};
			musicMute.setRect(0, musicVol.bottom() + GAP_TINY, WIDTH, BTN_HEIGHT);
			musicMute.checked(!SPDSettings.music());
			add(musicMute);


			OptionSlider SFXVol = new OptionSlider(Messages.get(this, "sfx_vol"), "0", "10", 0, 10) {
				@Override
				protected void onChange() {
					SPDSettings.SFXVol(getSelectedValue());
				}
			};
			SFXVol.setSelectedValue(SPDSettings.SFXVol());
			SFXVol.setRect(0, musicMute.bottom() + GAP_LRG, WIDTH, SLIDER_HEIGHT);
			add(SFXVol);

			CheckBox btnSound = new CheckBox( Messages.get(this, "sfx_mute") ) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.soundFx(!checked());
					Sample.INSTANCE.play( Assets.SND_CLICK );
				}
			};
			btnSound.setRect(0, SFXVol.bottom() + GAP_TINY, WIDTH, BTN_HEIGHT);
			btnSound.checked(!SPDSettings.soundFx());
			add( btnSound );

			resize( WIDTH, (int)btnSound.bottom());
		}

	}
}
