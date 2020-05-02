package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.GamesInProgress;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.scenes.InterlevelScene;
import studio.baka.satoripixeldungeon.scenes.RankingsScene;
import studio.baka.satoripixeldungeon.scenes.TitleScene;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.Window;
import com.watabou.noosa.Game;

import java.io.IOException;

public class WndGame extends Window {

	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP		= 2;
	
	private int pos;
	
	public WndGame() {
		
		super();
		
		addButton( new RedButton( Messages.get(this, "settings") ) {
			@Override
			protected void onClick() {
				hide();
				GameScene.show(new WndSettings());
			}
		});

		// Challenges window
		if (Dungeon.challenges > 0) {
			addButton( new RedButton( Messages.get(this, "challenges") ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndChallenges( Dungeon.challenges, false ) );
				}
			} );
		}

		// Restart
		if (Dungeon.hero == null || !Dungeon.hero.isAlive()) {
			
			RedButton btnStart;
			addButton( btnStart = new RedButton( Messages.get(this, "start") ) {
				@Override
				protected void onClick() {
					GamesInProgress.selectedClass = Dungeon.hero.heroClass;
					InterlevelScene.noStory = true;
					GameScene.show(new WndStartGame(GamesInProgress.firstEmpty()));
				}
			} );
			btnStart.textColor(Window.TITLE_COLOR);
			
			addButton( new RedButton( Messages.get(this, "rankings") ) {
				@Override
				protected void onClick() {
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					Game.switchScene( RankingsScene.class );
				}
			} );
		}

		addButtons(
				// Main menu
				new RedButton( Messages.get(this, "menu") ) {
					@Override
					protected void onClick() {
						try {
							Dungeon.saveAll();
						} catch (IOException e) {
							SatoriPixelDungeon.reportException(e);
						}
						Game.switchScene(TitleScene.class);
					}
				},
				// Quit
				new RedButton( Messages.get(this, "exit") ) {
					@Override
					protected void onClick() {
						try {
							Dungeon.saveAll();
						} catch (IOException e) {
							SatoriPixelDungeon.reportException(e);
						}
						Game.instance.finish();
					}
				}
		);

		// Cancel
		addButton( new RedButton( Messages.get(this, "return") ) {
			@Override
			protected void onClick() {
				hide();
			}
		} );
		
		resize( WIDTH, pos );
	}
	
	private void addButton( RedButton btn ) {
		add( btn );
		btn.setRect( 0, pos > 0 ? pos += GAP : 0, WIDTH, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}

	private void addButtons( RedButton btn1, RedButton btn2 ) {
		add( btn1 );
		btn1.setRect( 0, pos > 0 ? pos += GAP : 0, (WIDTH - GAP) / 2, BTN_HEIGHT );
		add( btn2 );
		btn2.setRect( btn1.right() + GAP, btn1.top(), WIDTH - btn1.right() - GAP, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}
}
