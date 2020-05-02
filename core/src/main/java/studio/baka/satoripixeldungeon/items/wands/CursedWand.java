package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.*;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Frost;
import studio.baka.satoripixeldungeon.actors.buffs.Recharging;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mimic;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Sheep;
import studio.baka.satoripixeldungeon.effects.*;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.artifacts.TimekeepersHourglass;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRecharging;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.features.Chasm;
import studio.baka.satoripixeldungeon.levels.traps.CursingTrap;
import studio.baka.satoripixeldungeon.levels.traps.PitfallTrap;
import studio.baka.satoripixeldungeon.levels.traps.ShockingTrap;
import studio.baka.satoripixeldungeon.levels.traps.SummoningTrap;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.plants.Swiftthistle;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.scenes.InterlevelScene;
import studio.baka.satoripixeldungeon.ui.TargetHealthIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.io.IOException;
import java.util.ArrayList;

//helper class to contain all the cursed wand zapping logic, so the main wand class doesn't get huge.
public class CursedWand {


    public static void cursedZap(final Item origin, final Hero user, final Ballistica bolt, final Callback afterZap) {
        cursedZap(origin, user, bolt, afterZap, 0);
    }

    public static void cursedZap(final Item origin, final Hero user, final Ballistica bolt, final Callback afterZap, final int curselevel) {
        int what;
        float VERY_RARE_CHANCE = 0.01f;
        float RARE_CHANCE = 0.09f;
        float UNCOMMON_CHANCE = 0.3f;
        float COMMON_CHANCE = 0.6f;
        switch (curselevel) {
            case 0:
            default:
                what = Random.chances(new float[]{COMMON_CHANCE, UNCOMMON_CHANCE, RARE_CHANCE, VERY_RARE_CHANCE});
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                what = curselevel - 1;
                break;
        }
        switch (what) {
            case 0:
            default:
                commonEffect(origin, user, bolt, afterZap);
                GLog.w("1 zap ");
                break;
            case 1:
                uncommonEffect(origin, user, bolt, afterZap);
                GLog.w("2 zap ");
                break;
            case 2:
                rareEffect(origin, user, bolt, afterZap);
                GLog.w("3 zap ");
                break;
            case 3:
                veryRareEffect(origin, user, bolt, afterZap);
                GLog.w("4 zap ");
                break;
        }
    }

    private static void commonEffect(final Item origin, final Hero user, final Ballistica bolt, final Callback afterZap) {
        switch (Random.Int(4)) {

            //anti-entropy
            case 0:
                cursedFX(user, bolt, () -> {
                    Char target = Actor.findChar(bolt.collisionPos);
                    switch (Random.Int(2)) {
                        case 0:
                            if (target != null)
                                Buff.affect(target, Burning.class).reignite(target);
                            Buff.affect(user, Frost.class, Frost.duration(user) * Random.Float(3f, 5f));
                            break;
                        case 1:
                            Buff.affect(user, Burning.class).reignite(user);
                            if (target != null)
                                Buff.affect(target, Frost.class, Frost.duration(target) * Random.Float(3f, 5f));
                            break;
                    }
                    afterZap.call();
                });
                break;

            //spawns some regrowth
            case 1:
                cursedFX(user, bolt, () -> {
                    GameScene.add(Blob.seed(bolt.collisionPos, 30, Regrowth.class));
                    afterZap.call();
                });
                break;

            //random teleportation
            case 2:
                switch (Random.Int(2)) {
                    case 0:
                        ScrollOfTeleportation.teleportHero(user);
                        afterZap.call();
                        break;
                    case 1:
                        cursedFX(user, bolt, () -> {
                            Char ch = Actor.findChar(bolt.collisionPos);
                            if (ch == user) {
                                ScrollOfTeleportation.teleportHero(user);
                            } else if (ch != null && !ch.properties().contains(Char.Property.IMMOVABLE)) {
                                int count = 10;
                                int pos;
                                do {
                                    pos = Dungeon.level.randomRespawnCell();
                                    if (count-- <= 0) {
                                        break;
                                    }
                                } while (pos == -1);
                                if (pos == -1 || Dungeon.bossLevel()) {
                                    GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));
                                } else {
                                    ch.pos = pos;
                                    if (((Mob) ch).state == ((Mob) ch).HUNTING) ((Mob) ch).state = ((Mob) ch).WANDERING;
                                    ch.sprite.place(ch.pos);
                                    ch.sprite.visible = Dungeon.level.heroFOV[pos];
                                }
                            }
                            afterZap.call();
                        });
                        break;
                }
                break;

            //random gas at location
            case 3:
                cursedFX(user, bolt, () -> {
                    switch (Random.Int(3)) {
                        case 0:
                            GameScene.add(Blob.seed(bolt.collisionPos, 800, ConfusionGas.class));
                            break;
                        case 1:
                            GameScene.add(Blob.seed(bolt.collisionPos, 500, ToxicGas.class));
                            break;
                        case 2:
                            GameScene.add(Blob.seed(bolt.collisionPos, 200, ParalyticGas.class));
                            break;
                    }
                    afterZap.call();
                });
                break;
        }

    }

    private static void uncommonEffect(final Item origin, final Hero user, final Ballistica bolt, final Callback afterZap) {
        switch (Random.Int(4)) {

            //Random plant
            case 0:
                cursedFX(user, bolt, () -> {
                    int pos = bolt.collisionPos;
                    //place the plant infront of an enemy so they walk into it.
                    if (Actor.findChar(pos) != null && bolt.dist > 1) {
                        pos = bolt.path.get(bolt.dist - 1);
                    }

                    if (pos == Terrain.EMPTY ||
                            pos == Terrain.EMBERS ||
                            pos == Terrain.EMPTY_DECO ||
                            pos == Terrain.GRASS ||
                            pos == Terrain.HIGH_GRASS ||
                            pos == Terrain.FURROWED_GRASS) {
                        Dungeon.level.plant((Plant.Seed) Generator.random(Generator.Category.SEED), pos);
                    }
                    afterZap.call();
                });
                break;

            //Health transfer
            case 1:
                final Char target = Actor.findChar(bolt.collisionPos);
                if (target != null) {
                    cursedFX(user, bolt, new Callback() {
                        public void call() {
                            int damage = user.lvl * 2;
                            switch (Random.Int(2)) {
                                case 0:
                                    user.HP = Math.min(user.HT, user.HP + damage);
                                    user.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
                                    target.damage(damage, origin);
                                    target.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                                    break;
                                case 1:
                                    user.damage(damage, this);
                                    user.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                                    target.HP = Math.min(target.HT, target.HP + damage);
                                    target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
                                    Sample.INSTANCE.play(Assets.SND_CURSED);
                                    if (!user.isAlive() && origin != null) {
                                        Dungeon.fail(origin.getClass());
                                        GLog.n(Messages.get(CursedWand.class, "ondeath", origin.name()));
                                    }
                                    break;
                            }
                            afterZap.call();
                        }
                    });
                } else {
                    GLog.i(Messages.get(CursedWand.class, "nothing"));
                    afterZap.call();
                }
                break;

            //Bomb explosion
            case 2:
                cursedFX(user, bolt, () -> {
                    new Bomb().explode(bolt.collisionPos);
                    afterZap.call();
                });
                break;

            //shock and recharge
            case 3:
                new ShockingTrap().set(user.pos).activate();
                Buff.prolong(user, Recharging.class, 20f);
                ScrollOfRecharging.charge(user);
                SpellSprite.show(user, SpellSprite.CHARGE);
                afterZap.call();
                break;
        }

    }

    private static void rareEffect(final Item origin, final Hero user, final Ballistica bolt, final Callback afterZap) {
        switch (Random.Int(4)) {

            //sheep transformation
            case 0:
                cursedFX(user, bolt, () -> {
                    Char ch = Actor.findChar(bolt.collisionPos);

                    if (ch != null && ch != user
                            && !ch.properties().contains(Char.Property.BOSS)
                            && !ch.properties().contains(Char.Property.MINIBOSS)) {
                        Sheep sheep = new Sheep();
                        sheep.lifespan = 10;
                        sheep.pos = ch.pos;
                        ch.destroy();
                        ch.sprite.killAndErase();
                        //noinspection SuspiciousMethodCalls
                        Dungeon.level.mobs.remove(ch);
                        TargetHealthIndicator.instance.target(null);
                        GameScene.add(sheep);
                        CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);
                    } else {
                        GLog.i(Messages.get(CursedWand.class, "nothing"));
                    }
                    afterZap.call();
                });
                break;

            //curses!
            case 1:
                CursingTrap.curse(user);
                afterZap.call();
                break;

            //inter-level teleportation
            case 2:
                if (Dungeon.depth > 1 && !Dungeon.bossLevel()) {

                    //each depth has 1 more weight than the previous depth.
                    float[] depths = new float[Dungeon.depth - 1];
                    for (int i = 1; i < Dungeon.depth; i++) depths[i - 1] = i;
                    int depth = 1 + Random.chances(depths);

                    Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
                    if (buff != null) buff.detach();

                    buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
                    if (buff != null) buff.detach();

                    InterlevelScene.mode = InterlevelScene.Mode.RETURN;
                    InterlevelScene.returnDepth = depth;
                    InterlevelScene.returnPos = -1;
                    Game.switchScene(InterlevelScene.class);

                } else {
                    ScrollOfTeleportation.teleportHero(user);

                }
                afterZap.call();
                break;

            //summon monsters
            case 3:
                new SummoningTrap().set(user.pos).activate();
                afterZap.call();
                break;
        }
    }

    private static void veryRareEffect(final Item origin, final Hero user, final Ballistica bolt, final Callback afterZap) {
        switch (Random.Int(4)) {

            //great forest fire!
            case 0:
                for (int i = 0; i < Dungeon.level.length(); i++) {
                    GameScene.add(Blob.seed(i, 15, Regrowth.class));
                }
                do {
                    GameScene.add(Blob.seed(Dungeon.level.randomDestination(), 10, Fire.class));
                } while (Random.Int(5) != 0);
                new Flare(8, 32).color(0xFFFF66, true).show(user.sprite, 2f);
                Sample.INSTANCE.play(Assets.SND_TELEPORT);
                GLog.p(Messages.get(CursedWand.class, "grass"));
                GLog.w(Messages.get(CursedWand.class, "fire"));
                afterZap.call();
                break;

            //superpowered mimic
            case 1:
                cursedFX(user, bolt, () -> {
                    Mimic mimic = Mimic.spawnAt(bolt.collisionPos, new ArrayList<>());
                    if (mimic != null) {
                        mimic.adjustStats(Dungeon.depth + 10);
                        mimic.HP = mimic.HT;
                        Item reward;
                        do {
                            reward = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
                                    Generator.Category.RING, Generator.Category.WAND));
                        } while (reward.level() < 1);
                        Sample.INSTANCE.play(Assets.SND_MIMIC, 1, 1, 0.5f);
                        mimic.items.clear();
                        mimic.items.add(reward);
                    } else {
                        GLog.i(Messages.get(CursedWand.class, "nothing"));
                    }

                    afterZap.call();
                });
                break;

            //crashes the game, yes, really. Tomnycui:That's not good.
            case 2:
                try {
                    Dungeon.saveAll();
                    if (Dungeon.bossLevel() || Dungeon.depth > 25) {
                        GLog.w(Messages.get(PitfallTrap.class, "no_pit"));
                        afterZap.call();
                        return;
                    }
                    int pos = bolt.collisionPos;

                    Heap heap = Dungeon.level.heaps.get(pos);

                    if (heap != null) {
                        for (Item item : heap.items) {
                            Dungeon.dropToChasm(item);
                        }
                        heap.sprite.kill();
                        GameScene.discard(heap);
                        Dungeon.level.heaps.remove(pos);
                    }

                    Char ch = Actor.findChar(pos);

                    if (ch != null && !ch.flying) {
                        if (ch == Dungeon.hero) {
                            Chasm.heroFall(pos);
                        } else {
                            Chasm.mobFall((Mob) ch);
                        }
                    }

                    afterZap.call();
					/*
					if(Messages.lang() != Languages.ENGLISH){
						//Don't bother doing this joke to none-english speakers, I doubt it would translate.
						GLog.i(Messages.get(CursedWand.class, "nothing"));
						afterZap.call();

					} else {
						GameScene.show(
								new WndOptions("CURSED WAND ERROR", "this application will now self-destruct", "abort", "retry", "fail") {
									
									@Override
									protected void onSelect(int index) {
										Game.instance.finish();
									}
									
									@Override
									public void onBackPressed() {
										//do nothing
									}
								}
						);
					}*/
                } catch (IOException e) {
                    SatoriPixelDungeon.reportException(e);
                    //oookay maybe don't kill the game if the save failed.
                    GLog.i(Messages.get(CursedWand.class, "nothing"));
                    afterZap.call();
                }
                break;

            //random transmogrification
            case 3:
                //skips this effect if there is no item to transmogrify
                if (origin == null || Dungeon.hero.belongings.notContains(origin)) {
                    GLog.w("3 failed");
                    cursedZap(origin, user, bolt, afterZap);
                    return;
                }
                //
                Item result;
                do {
                    result = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
                            Generator.Category.RING, Generator.Category.ARTIFACT));
                } while (result.cursed);
                if (result.isUpgradable()) result.upgrade();
                result.cursed = result.cursedKnown = true;
                if (origin instanceof Wand) {
                    //origin.detach(user.belongings.backpack);
                    GLog.w(Messages.get(CursedWand.class, "transmogrify_wand"));
                } else {
                    origin.detach(user.belongings.backpack);
                    GLog.w(Messages.get(CursedWand.class, "transmogrify_other"));
                }
                Dungeon.level.drop(result, user.pos).sprite.drop();
                afterZap.call();
                break;
        }
    }

    private static void cursedFX(final Hero user, final Ballistica bolt, final Callback callback) {
        MagicMissile.boltFromChar(user.sprite.parent,
                MagicMissile.RAINBOW,
                user.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

}
