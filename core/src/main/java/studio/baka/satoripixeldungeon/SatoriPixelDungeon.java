package studio.baka.satoripixeldungeon;

import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.scenes.WelcomeScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PlatformSupport;

public class SatoriPixelDungeon extends Game {
	
	//variable constants for specific older versions of shattered, used for data conversion
	//versions older than v0.6.5c are no longer supported, and data from them is ignored
	public static final int v0_6_5c = 264;
	
	public static final int v0_7_0c = 311;
	public static final int v0_7_1d = 323;
	public static final int v0_7_2d = 340;
	public static final int v0_7_3b = 349;
	public static final int v0_7_4c = 362;
	public static final int v0_7_5  = 371;
	
	public SatoriPixelDungeon( PlatformSupport platform ) {
		super( sceneClass == null ? WelcomeScene.class : sceneClass, platform );
		
		//v0.7.0
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.bombs.Bomb.class,
				"studio.baka.satoripixeldungeon.items.Bomb" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRetribution.class,
				"studio.baka.satoripixeldungeon.items.scrolls.ScrollOfPsionicBlast" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.potions.elixirs.ElixirOfMight.class,
				"studio.baka.satoripixeldungeon.items.potions.PotionOfMight" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.spells.MagicalInfusion.class,
				"studio.baka.satoripixeldungeon.items.scrolls.ScrollOfMagicalInfusion" );
		
		//v0.7.1
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.SpiritBow.class,
				"studio.baka.satoripixeldungeon.items.weapon.missiles.Boomerang" );
		
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.melee.Gloves.class,
				"studio.baka.satoripixeldungeon.items.weapon.melee.Knuckles" );
		
		//v0.7.2
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.stones.StoneOfDisarming.class,
				"studio.baka.satoripixeldungeon.items.stones.StoneOfDetectCurse" );
		
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.enchantments.Elastic.class,
				"studio.baka.satoripixeldungeon.items.weapon.curses.Elastic" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.enchantments.Elastic.class,
				"studio.baka.satoripixeldungeon.items.weapon.enchantments.Dazzling" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.enchantments.Elastic.class,
				"studio.baka.satoripixeldungeon.items.weapon.enchantments.Eldritch" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.enchantments.Grim.class,
				"studio.baka.satoripixeldungeon.items.weapon.enchantments.Stunning" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.enchantments.Chilling.class,
				"studio.baka.satoripixeldungeon.items.weapon.enchantments.Venomous" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.enchantments.Kinetic.class,
				"studio.baka.satoripixeldungeon.items.weapon.enchantments.Vorpal" );
		
		//v0.7.3
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.enchantments.Kinetic.class,
				"studio.baka.satoripixeldungeon.items.weapon.enchantments.Precise" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.items.weapon.enchantments.Kinetic.class,
				"studio.baka.satoripixeldungeon.items.weapon.enchantments.Swift" );
		
		//v0.7.5
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.levels.rooms.sewerboss.SewerBossEntranceRoom.class,
				"studio.baka.satoripixeldungeon.levels.rooms.standard.SewerBossEntranceRoom" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.levels.OldPrisonBossLevel.class,
				"studio.baka.satoripixeldungeon.levels.PrisonBossLevel" );
		com.watabou.utils.Bundle.addAlias(
				studio.baka.satoripixeldungeon.actors.mobs.OldTengu.class,
				"studio.baka.satoripixeldungeon.actors.mobs.Tengu" );
	}
	
	@Override
	public void create() {
		super.create();

		updateSystemUI();
		
		Music.INSTANCE.enable( SPDSettings.music() );
		Music.INSTANCE.volume( SPDSettings.musicVol()/10f );
		Sample.INSTANCE.enable( SPDSettings.soundFx() );
		Sample.INSTANCE.volume( SPDSettings.SFXVol()/10f );

		Sample.INSTANCE.load(
				Assets.SND_CLICK,
				Assets.SND_BADGE,
				Assets.SND_GOLD,

				Assets.SND_STEP,
				Assets.SND_WATER,
				Assets.SND_OPEN,
				Assets.SND_UNLOCK,
				Assets.SND_ITEM,
				Assets.SND_DEWDROP,
				Assets.SND_HIT,
				Assets.SND_MISS,
				Assets.SND_PHONE,

				Assets.SND_DESCEND,
				Assets.SND_EAT,
				Assets.SND_READ,
				Assets.SND_LULLABY,
				Assets.SND_DRINK,
				Assets.SND_SHATTER,
				Assets.SND_ZAP,
				Assets.SND_LIGHTNING,
				Assets.SND_LEVELUP,
				Assets.SND_DEATH,
				Assets.SND_CHALLENGE,
				Assets.SND_CURSED,
				Assets.SND_EVOKE,
				Assets.SND_TRAP,
				Assets.SND_TOMB,
				Assets.SND_ALERT,
				Assets.SND_MELD,
				Assets.SND_BOSS,
				Assets.SND_BLAST,
				Assets.SND_PLANT,
				Assets.SND_RAY,
				Assets.SND_BEACON,
				Assets.SND_TELEPORT,
				Assets.SND_CHARMS,
				Assets.SND_MASTERY,
				Assets.SND_PUFF,
				Assets.SND_ROCKS,
				Assets.SND_BURNING,
				Assets.SND_FALLING,
				Assets.SND_GHOST,
				Assets.SND_SECRET,
				Assets.SND_BONES,
				Assets.SND_BEE,
				Assets.SND_DEGRADE,
				Assets.SND_MIMIC );

		
	}

	public static void switchNoFade(Class<? extends PixelScene> c){
		switchNoFade(c, null);
	}

	public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
		PixelScene.noFade = true;
		switchScene( c, callback );
	}
	
	public static void seamlessResetScene(SceneChangeCallback callback) {
		if (scene() instanceof PixelScene){
			((PixelScene) scene()).saveWindows();
			//noinspection unchecked
			switchNoFade((Class<? extends PixelScene>) sceneClass, callback );
		} else {
			resetScene();
		}
	}
	
	public static void seamlessResetScene(){
		seamlessResetScene(null);
	}
	
	@Override
	protected void switchScene() {
		super.switchScene();
		if (scene instanceof PixelScene){
			((PixelScene) scene).restoreWindows();
		}
	}
	
	@Override
	public void resize( int width, int height ) {
		
		if (scene instanceof PixelScene &&
				(height != Game.height || width != Game.width)) {
			((PixelScene) scene).saveWindows();
		}

		super.resize( width, height );

		updateDisplaySize();

	}

	public void updateDisplaySize(){
		platform.updateDisplaySize();
	}

	public static void updateSystemUI() {
		platform.updateSystemUI();
	}
}