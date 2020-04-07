/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ReagentOfPellouxite;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MahoStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

import java.util.ArrayList;

public class v0_7_X_Changes {
	private static ChangeButton itembutton(int id, String title, String text) {
		int l,t,w,h;
		l = id % ItemSpriteSheet.WIDTH * 16;
		t = id / ItemSpriteSheet.WIDTH * 16;
		w = h = 16;
		return new ChangeButton(new Image(Assets.ITEMS, l,t,w,h), title,text);
	}
	
	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
		add_v1_7_3_X_Changes(changeInfos);
		add_v0_7_5_Changes(changeInfos);
	}

	public static void add_v1_7_3_X_Changes( ArrayList<ChangeInfo> changeInfos ){
		ChangeInfo changes = new ChangeInfo("SPD v1.7.3.X",true,"");
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);

		changes = new ChangeInfo("SPD v1.7.3.8", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"_-_Ver Beta\n_-_完成了物品快捷栏的重写。"));

		//changes.addButton( itembutton(new MahoStaff().image,"test","testing") );

		changes = new ChangeInfo("SPD v1.7.3.7", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"_-_稍微削弱了妖梦。\n_-_增加了物品栏大小。\n_-_加入了真正意义上的剧情。"));

		changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16),"BUG修复",
				"_-_修复了部分文本错误~\n_-_DM300的自爆不会在进入最后阶段时立刻发生了。\n_-_修复了鵺的防御用灵力问题。\n_-_把国王不死的可能删除了。"));

		changes.addButton( itembutton(new MahoStaff().image,"芙兰削弱","_-_芙兰朵露的所有法杖都不能自动回复充能。") );

		changes = new ChangeInfo("SPD v1.7.3.6", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"_-_修复了部分文本错误~\n_-_更改了APK包名。强化了一些Boss"));

		changes.addButton( new ChangeButton(new Image(Assets.WARRIOR, 12, 14, 12, 14), "恋恋强化",
				"_-_抑制·超我的恋技能完全改变。"));

		changes.addButton( new ChangeButton(new Image(Assets.MAGE, 12, 0, 12, 14),"鵺强化",
				"_-_鵺现在可以主动诅咒或消除诅咒，但是仅限于主法杖。_-_鵺的灵力使用在她的被动技能上，包括攻击和防御。" ));

		changes = new ChangeInfo("SPD v1.7.3.5", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"_-_修复了部分文本错误~\n_-_在生命条下方增加了灵力条。"));

		changes.addButton( itembutton(new ReagentOfPellouxite().image,"芙兰强化","_-_芙兰朵露开局25瓶生石灰。") );

		changes.addButton( new ChangeButton(new Image(Assets.ROGUE,12,0,12,14),"紫削弱",
				"_-_紫开局力量下调两点,但折扇的初始力量需求同样下调两点。"));

		changes = new ChangeInfo("SPD v1.7.3.4", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"_-_修复了部分文本错误~\n将各个卷轴的符石拆解所得数量调为3"));

		changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16),"BUG修复",
				"_-_改回了jar+apk双签名方法。\n_-_修复了蓄血圣杯的致命性错误。\n_-_芙兰朵露不会再被水汽伤到。" ));


		changes = new ChangeInfo("SPD v1.7.3.3", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"修复了部分文本错误~\n_-_恢复了蓄血圣杯的伤害显示。\n_-_删除了过重护甲的错误显示信息。\n_-_删除了过重武器的错误显示信息。\n_-_增加了魔法少女服的文本显示。\n_-_提升了露水瓶的容量上限。"));

		changes.addButton( new ChangeButton(new Image(Assets.WARRIOR, 12, 14, 12, 14), "恋恋强化",
				"_-_恋恋开局的石子增加到了4个，恋恋的刀现在可无视boss层发动传送。\n_-_恋恋的核动力特甲造成的麻痹时间增加到5回合。"));

		changes.addButton( new ChangeButton(new Image(Assets.ROGUE, 12, 0, 12, 14), "紫强化",
				"紫的最终护甲：护身隙间的能力强化了，现在其特殊技能的距离限制增加到100格，且其技能对敌怪加的buff更为强力。"));

		changes.addButton( new ChangeButton(new Image(Assets.MAHOU_SHOUJO, 12, 0, 12, 14), "芙兰强化",
				"芙兰朵露的最终护甲：魔法少女服的吸血能力加强了。"));

		changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 12, 14),"BUG修复",
				"_-_改用了旧版签名方法，以支持更多版本的Android系统。\n_-_修复了26层的错误。\n_-_修正了露水瓶的祝福用量。" ));

		changes = new ChangeInfo("SPD v1.7.3.2", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(new Image(Assets.MAHOU_SHOUJO, 12, 0, 12, 14), "魔法少女正式加入",
				"这下算是正式写完了芙兰朵露。芙兰朵露的定位是一个前期弱而后期强的角色。\n目前在正式版本解锁芙兰朵露的条件是在一次游戏中使用四次祝福过的复活十字架。\n真是非常挑战性的条件呢~"));

		changes.addButton( new ChangeButton(new Image(Assets.ROGUE, 12,0,12,14),"八云紫的空间碎片",
				"将八云紫的\"路牌\"改成了\"空间碎片\"，现在投掷不会消耗了，并用牺牲了攻击力的代价加入了非常厉害的传送能力。"));

		changes.addButton( new ChangeButton(new Image(Assets.ROGUE, 12,0,12,14),"八云紫的空间碎片",
				"给八云紫的\"护身隙间\"加入了使用灵力充能的功能。消耗10灵力"));

		changes = new ChangeInfo("SPD v1.7.3.1", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"完成了这个更新列表的改造喵~"));

		changes.addButton( new ChangeButton(new Image(Assets.WARRIOR, 12,14,12,14),"恋恋削弱啦！",
				"恋恋的刀现在的攻击力和等级是二次关系，不是指数关系了"));
	}
	
	public static void add_v0_7_5_Changes( ArrayList<ChangeInfo> changeInfos ){
		ChangeInfo changes = new ChangeInfo("SHPD v0.7.5", true, "");
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes = new ChangeInfo("", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes = new ChangeInfo("v0.7.5e", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(new Image(Assets.SNAKE, 12, 0, 12, 11), "Snake adjustments",
				"Snakes are doing a good job of filling their role as an enemy that demands being surprise attacked, but they are a bit annoying if the player gets unlucky and has to surprise them multiple times.\n\n" +
				"I'm tweaking them so that they are much more likely to die from a single surprise hit, but their danger otherwise should be very similar:\n" +
				"_-_ Snake health reduced to 4 from 6\n" +
				"_-_ Snake evasion increased by 25%\n\n" +
				"Snakes now also have an item drop! They will occasionally drop a random seed."));
		
		changes.addButton( new ChangeButton(new Image(Assets.TENGU, 0, 0, 14, 16), "Tengu Adjustments",
				"Tengu is in a much better place balance-wise since the changes in 0.7.5b, but he's still ruining the rogue's day a bit too often.\n\n" +
				"I'm buffing invisibility versus Tengu again, so that it completely avoids his regular attacks, but doesn't totally trivialize him:\n" +
				"_-_ Tengu now cannot attack invisible heroes\n" +
				"_-_ Tengu can now use his 3rd phase abilities against heroes he cannot see\n" +
				"_-_ VFX for Tengu's abilities now triggers even if the player can't see them"));
		
		changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
				"Fixed (existed prior to 0.7.5):\n" +
				"_-_ Small amounts of stuttering when the hero moves\n" +
				"_-_ Rare layout issues with buttons in item windows\n" +
				"_-_ Bolts from wand of lightning not spreading in many cases where they should\n" +
				"_-_ Various rare crash bugs"));
		
		changes.addButton( new ChangeButton(Icons.get(Icons.LANGS), Messages.get(ChangesScene.class, "language"),
				"Updated Translations"));
		
		changes = new ChangeInfo("v0.7.5c&d", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"_-_ Made additional tweaks to camera movement speed when following hero, should be slightly faster in most cases."));
		
		changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
				"Fixed (caused by 0.7.5):\n" +
				"_-_ Various visual bugs on floor 10\n" +
				"_-_ Text being highlighted when it shouldn't in specific cases\n" +
				"_-_ Letters failing to render in various specific cases\n" +
				"_-_ Camera moving slower than intended when zoomed in\n" +
				"_-_ Camera jittering at low framerates\n" +
				"_-_ Various rare crash bugs\n\n" +
				"Fixed (existed prior to 0.7.5):\n" +
				"_-_ Thrown weapons sticking to corrupted characters when they shouldn't"));
		
		changes.addButton( new ChangeButton(Icons.get(Icons.LANGS), Messages.get(ChangesScene.class, "language"),
				"Updated Translations"));
		
		changes = new ChangeInfo("v0.7.5a&b", false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.LIBGDX), "LibGDX Text Rendering!",
				"The game's text renderer is now using LibGDX freetype. This looks almost identical to the existing text but is slightly crisper, platform-independent, and much more efficient!\n\n" +
				"Text rendering was the last bit of android-dependant code, so the game's core code modules (~98% of its code) are now being compiled as general code and not android-specific code!\n\n" +
				"Also updated translations"));
		
		changes.addButton( new ChangeButton(new Image(Assets.TENGU, 0, 0, 14, 16), "Enemy Balance Adjustments",
				"Tengu has been adjusted to be a bit less difficult for melee characters, in particular for the rogue:\n" +
				"_-_ Tengu blink distance on phase 3 reduced by 1 tile\n" +
				"_-_ Tengu accuracy reduced by 10%\n" +
				"_-_ Tengu accuracy versus invisible characters reduced by 50%\n\n" +
				"Additionally, some minor balance changes have been made to regular enemies:\n" +
				"_-_ Snake damage down to 1-4 from 1-5\n" +
				"_-_ Crab damage down to 1-7 from 1-8\n" +
				"_-_ Slime damage down to 2-5 from 3-5\n" +
				"_-_ Necromancer Skeleton HP on summon up to 20/25 from 15/25"));
		
		changes.addButton( new ChangeButton(new WandOfCorruption(),
				"The nerfs to the wand of corruption in 0.7.5 had basically no effect on its winrate when upgraded, so I'm taking a different approach and buffing its base power but reducing its upgraded power. I'm also putting more emphasis on debuffs helping corruption chances:\n\n" +
				"_-_ Corruption resistance reduction from minor debuffs up to 25% from 12.5% (was 20% prior to 0.7.5)\n" +
				"_-_ Corruption resistance reduction from major debuffs up to 50% from 25% (was 33% prior to 0.7.5)\n" +
				"_-_ Corruption power adjusted to 3+lvl/2 from 2+lvl\n\n" +
				"_-_ Wraith corruption resistance reduced slightly, to put them into line with other uncommon enemies."));
		
		changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
				"Fixed (caused by 0.7.5):\n" +
				"_-_ Necromancers incorrectly only summoning skeletons at melee range\n" +
				"_-_ Rare cases where doors would appear incorrectly on floor 5\n" +
				"_-_ Doors not opening when they should in some cases\n" +
				"_-_ Necromancers rarely healing skeletons after they die\n" +
				"_-_ Various rare crash bugs\n\n" +
				"Fixed (existed prior to 0.7.5):\n" +
				"_-_ Black texture errors on older android devices\n" +
				"_-_ Scenes not fading in when they should in certain cases"));
		
		changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
				"_-_ Released October 2nd, 2019\n" +
				"_-_ 76 days after Shattered v0.7.4" +
				"\n" +
				"Dev commentary will be added here in the future."));
		
		changes.addButton( new ChangeButton(new Image(Assets.SNAKE, 12, 0, 12, 11), "Sewer Enemies",
				"Two new enemies have been added to the sewers!\n\n" +
				"_- Snakes_ are an evasive enemy which mainly shows up on early floors, they help teach the importance of surprise attacks.\n" +
				"_- Slimes_ primarily appear on floor 4, and are an enemy type which rewards defense over damage.\n\n" +
				"Goo's level has also received significant changes. It now uses a new unique level layout pattern, and Goo itself always spawns in a new unique room type.\n\n" +
				"I have also made slight balance changes to the Goo fight itself. 1x1 pillars have been mostly removed from Goo's arena to reduce surprise-attack spam, but Goo's damage has been reduced by 20% to compensate."));
		
		changes.addButton( new ChangeButton(new Image(Assets.TENGU, 0, 0, 14, 16), "Prison Enemies",
				"_Necromancers_ have been added to the prison! These powerful enemies fight by summoning and buffing undead.\n\n" +
				"The _Tengu_ boss fight has been totally reworked! The fight still takes place over 3 stages, and has a similar core theme, but I have totally ditched the tedious maze and chasing mechanics from stages 2&3, and have given Tengu several new abilities. Watch your step!\n\n" +
				"As a part of this rework, Tengu's stats have also been adjusted:\n" +
				"_-_ HP up to 160 from 120\n" +
				"_-_ Evasion reduced by 25%\n" +
				"_-_ Damage reduced by 30%"));
		
		changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
		changes.hardlight( CharSprite.WARNING );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(new Image(Assets.RAT, 0, 15, 16, 15), "Enemy Changes",
				"_-_ Significantly improved the consistency of enemy spawns (large numbers of the same enemy and large enemy groups should be less common)\n\n" +
				"_-_ Adjusted enemy spawn chances on floors 1-10 to make rooms for new enemies\n\n" +
				"_-_ Skeletons no longer rarely appear on floor 4\n\n" +
				"_-_ Guards no longer drop healing potions, they are now dropped by necromancers\n" +
				"_-_ Guards now grant 7 exp, up from 6\n\n" +
				"_-_ Albino rats now grant 2 exp, up from 1\n" +
				"_-_ Albino rats now drop mystery meat"));
		
		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"_-_ The game camera now smoothly follows the hero while they are moving, instead of snapping to their location.\n\n" +
				"_-_ Standardized word use when attacks miss to reduce confusion. Enemies now always 'block' or 'dodge'.\n\n" +
				"_-_ Various improvements to wording on the supporter menu for Google Play users.\n\n" +
				"_-_ Various internal code improvements"));
		
		changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
				"Fixed:\n" +
				"_-_ Various stability issues caused by the LibGDX conversion\n" +
				"_-_ Area-based effects behaving oddly in rare cases\n" +
				"_-_ Thieves not escaping when they should in many cases\n" +
				"_-_ A rare crash bug involving boomerangs\n" +
				"_-_ Sai and gauntlets giving 1 more defense than what their descriptions stated\n" +
				"_-_ Players rarely opening containers/doors from a distance"));
		
		changes.addButton( new ChangeButton(Icons.get(Icons.LANGS), Messages.get(ChangesScene.class, "language"),
				"Added new Language: Japanese!\n\n" +
				"Updated Translations and Translator Credits!"));
		
		changes = new ChangeInfo(Messages.get(ChangesScene.class, "buffs"), false, null);
		changes.hardlight( CharSprite.POSITIVE );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_BLAST_WAVE, null), "Wand Buffs",
				"Blast wave is an excellent sidearm wand, but not as good when invested in. I'm making the wand a bit stronger and less risky to hopefully make it more worthy of upgrades.\n" +
				"_-_ Increased AOE damage from 67% to 100%, AOE knockback force is unchanged\n" +
				"_-_ AOE no longer damages hero/allies, but still knocks them back\n\n" +
				"Corrosion is a very powerful wand in the right hands, but is currently a bit too hard to use right. I'm lightly buffing it to make its power a bit more accessible.\n" +
				"_-_ Corrosion gas starting damage increased to 2+lvl from 1+lvl"));
		
		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.RING_AMETHYST, null), "Ring Buffs",
				"Based on their performance, I'm giving a light buff to ring of energy, and a more significant buff to ring of wealth:\n\n" +
				"_-_ Ring of energy charge boost increased to 30% per level, from 25%\n\n" +
				"_-_ Ring of wealth exclusive drops are 20% more common\n" +
				"_-_ Rare ring of wealth exclusive drops are now 33% more common"));
		
		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.LONGSWORD, new ItemSprite.Glowing( 0x440066 )), "Glyph/Enchant Buffs",
				"I'm giving some significant buffs to underperforming rare enchants/glyphs:\n\n" +
				"_-_ Proc chance for corruption enchant increased by ~25% at all levels\n\n" +
				"_-_ Proc chance for glyph of affection increased by ~50% at +0, scaling to ~10% at +10"));
		
		changes = new ChangeInfo(Messages.get(ChangesScene.class, "nerfs"), false, null);
		changes.hardlight( CharSprite.NEGATIVE );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(new WandOfLivingEarth(),
				"I'm continuing to adjust the wand of living earth to make it less able to stand on its own as a run-winning item. It should excel at providing defensive power, but shouldn't also give good offense.\n\n" +
				"_-_ Guardian average damage decreased by 33%\n" +
				"_-_ Base wand damage up to 4-6 from 3-6\n" +
				"_-_ Wand damage scaling down to 0-2 from 1-2"));
		
		changes.addButton( new ChangeButton(new WandOfCorruption(),
				"Corruption is performing extremely well when invested in, so I'm adjusting debuff influence on corruption chance to make it more difficult to corrupt enemies.\n\n" +
				"_-_ Corruption resistance reduction from minor debuffs reduced to 12.5% from 20%\n" +
				"_-_ Corruption resistance reduction from major debuffs reduced to 25% from 33%"));
		
	}
}
