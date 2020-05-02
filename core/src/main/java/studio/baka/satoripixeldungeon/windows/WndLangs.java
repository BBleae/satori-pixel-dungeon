package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Chrome;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.messages.Languages;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class WndLangs extends Window {

	public WndLangs(){
		super();

		final ArrayList<Languages> langs = new ArrayList<>(Arrays.asList(Languages.values()));
		langs.remove(Languages.ENGLISH);
		Languages nativeLang = Languages.matchLocale(Locale.getDefault());
		langs.remove(nativeLang);
		//move the native language to the top.
		langs.add(0, nativeLang);

		final Languages currLang = Messages.lang();

		//language buttons layout
		int y = 0;
		for (int i = 0; i < langs.size(); i++){
			final int langIndex = i;
			RedButton btn = new RedButton(Messages.titleCase(langs.get(i).nativeName())){
				@Override
				protected void onClick() {
					super.onClick();
					Messages.setup(langs.get(langIndex));
					SatoriPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback() {
						@Override
						public void beforeCreate() {
							SPDSettings.language(langs.get(langIndex));
							Game.platform.resetGenerators();
						}
						@Override
						public void afterCreate() {
							//do nothing
						}
					});
				}
			};
			if (currLang == langs.get(i) && langs.get(i)!= Languages.ENGLISH){
				btn.textColor(TITLE_COLOR);
			} else {
				switch (langs.get(i).status()) {
					case INCOMPLETE:
						btn.textColor(0x888888);
						break;
					case UNREVIEWED:
						btn.textColor(0xBBBBBB);
						break;
				}
			}
			int BTN_HEIGHT = 12;
			int BTN_WIDTH = 50;
			btn.setSize(BTN_WIDTH, BTN_HEIGHT);
			if (SPDSettings.landscape() && i % 2 == 1){
				btn.setPos(BTN_WIDTH +1, y-(BTN_HEIGHT + 1));
			} else {
				btn.setPos(0, y);
				y += BTN_HEIGHT;
				if (SPDSettings.landscape()) y++;
			}

			add(btn);
		}
		int MIN_HEIGHT = 110;
		y = Math.max(MIN_HEIGHT, y);
		int WIDTH_L = 171;
		int WIDTH_P = 120;
		resize(SPDSettings.landscape() ? WIDTH_L : WIDTH_P, y);

		int textLeft = width - 65;
		int textWidth = width - textLeft;

		ColorBlock separator = new ColorBlock(1, y, 0xFF000000);
		separator.x = textLeft - 2.5f;
		add(separator);

		//language info layout.
		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.titleCase(currLang.nativeName()) , 9 );
		title.setPos( textLeft + (textWidth - title.width())/2f, 2 );
		title.hardlight(TITLE_COLOR);
		PixelScene.align(title);
		add(title);

		if (currLang == Languages.CHINESE){

			RenderedTextBlock info = PixelScene.renderTextBlock(6);
			info.text("这是开发者所用的母语，建议使用这个语言。请放心，我已经把其他语言移除掉了，因为懒得翻译。", width - textLeft);
			info.setPos(textLeft, title.bottom() + 4);
			add(info);

		} else {

			RenderedTextBlock info = PixelScene.renderTextBlock(6);
			switch (currLang.status()) {
				case REVIEWED:
					info.text(Messages.get(this, "completed"), width - textLeft);
					break;
				case UNREVIEWED:
					info.text(Messages.get(this, "unreviewed"), width - textLeft);
					break;
				case INCOMPLETE:
					info.text(Messages.get(this, "unfinished"), width - textLeft);
					break;
			}
			info.setPos(textLeft, title.bottom() + 4);
			add(info);

			RedButton creditsBtn = new RedButton(Messages.titleCase(Messages.get(this, "credits"))){
				@Override
				protected void onClick() {
					super.onClick();
					StringBuilder creds = new StringBuilder();
					StringBuilder creds2 = new StringBuilder();
					String[] reviewers = currLang.reviewers();
					String[] translators = currLang.translators();
					
					boolean wide = false;
					if (SPDSettings.landscape() && (reviewers.length + translators.length) > 10){
						wide = true;
					}
					
					int i;
					if (reviewers.length > 0){
						creds.append(Messages.titleCase(Messages.get(WndLangs.class, "reviewers"))).append("\n");
						creds2.append("");
						for ( i = 0; i < reviewers.length; i++){
							if (wide && i % 2 == 1){
								creds2.append("-").append(reviewers[i]).append("\n");
							} else {
								creds.append("-").append(reviewers[i]).append("\n");
							}
						}
						creds.append("\n");
						creds2.append("\n");
						if (i % 2 == 1) creds2.append("\n");
					}

					if (reviewers.length > 0 || translators.length > 0){
						creds.append(Messages.titleCase(Messages.get(WndLangs.class, "translators"))).append("\n");
						creds2.append("\n");
						//reviewers are also translators
						for ( i = 0; i < reviewers.length; i++){
							if (wide && i % 2 == 1){
								creds2.append("-").append(reviewers[i]).append("\n");
							} else {
								creds.append("-").append(reviewers[i]).append("\n");
							}
						}
						for (int j = 0; j < translators.length; j++){
							if (wide && (j + i) % 2 == 1){
								creds2.append("-").append(translators[j]).append("\n");
							} else {
								creds.append("-").append(translators[j]).append("\n");
							}
						}
					}
					
					creds = new StringBuilder(creds.substring(0, creds.length() - 1));

					Window credits = new Window( 0, 0, 0, Objects.requireNonNull(Chrome.get(Chrome.Type.TOAST)));
					
					int w = wide? 135 : 65;

					RenderedTextBlock title = PixelScene.renderTextBlock(6);
					title.text(Messages.titleCase(Messages.get(WndLangs.class, "credits")) , w);
					title.hardlight(SHPX_COLOR);
					title.setPos((w - title.width())/2, 0);
					credits.add(title);

					RenderedTextBlock text = PixelScene.renderTextBlock(5);
					text.setHightlighting(false);
					text.text(creds.toString(), 65);
					text.setPos(0, title.bottom() + 2);
					credits.add(text);
					
					if (wide){
						RenderedTextBlock rightColumn = PixelScene.renderTextBlock(5);
						rightColumn.setHightlighting(false);
						rightColumn.text(creds2.toString(), 65);
						rightColumn.setPos(70, title.bottom() + 8.5f);
						credits.add(rightColumn);
					}

					credits.resize(w, (int)text.bottom());
					parent.add(credits);
				}
			};
			creditsBtn.setSize(creditsBtn.reqWidth() + 2, 16);
			creditsBtn.setPos(textLeft + (textWidth - creditsBtn.width()) / 2f, y - 18);
			add(creditsBtn);

			RenderedTextBlock transifex_text = PixelScene.renderTextBlock(6);
			transifex_text.text(Messages.get(this, "transifex"), width - textLeft);
			transifex_text.setPos(textLeft, creditsBtn.top() - 2 - transifex_text.height());
			add(transifex_text);

		}

	}
	
	@Override
	public void hide() {
		super.hide();
		//resets generators because there's no need to retain chars for languages not selected
		SatoriPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback() {
			@Override
			public void beforeCreate() {
				Game.platform.resetGenerators();
			}
			@Override
			public void afterCreate() {
				//do nothing
			}
		});
	}
}
