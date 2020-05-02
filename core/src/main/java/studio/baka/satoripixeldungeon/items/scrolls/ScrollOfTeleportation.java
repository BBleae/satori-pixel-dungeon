package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.levels.RegularLevel;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.rooms.secret.SecretRoom;
import studio.baka.satoripixeldungeon.levels.rooms.special.SpecialRoom;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.CellSelector;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.HeroSprite;
import studio.baka.satoripixeldungeon.utils.BArray;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScrollOfTeleportation extends Scroll {

    {
        initials = 8;
    }

    @Override
    public void doRead() {

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        teleportPreferringUnseen(curUser);
        setKnown();

        readAnimation();
    }

    public void doRead2() {

        Sample.INSTANCE.play(Assets.SND_PHONE);
        Invisibility.dispel();

		/*if(Dungeon.depth == 10 && ((NewPrisonBossLevel)Dungeon.level).state()!= NewPrisonBossLevel.State.WON)
			GLog.n( "%s: \"%s\" ", Messages.titleCase(Messages.get(NewTengu.class, "name")), Messages.get(NewTengu.class,"no_tele") );
		else*/
        teleportPreferringUnseen(curUser, false, true);
        setKnown();

        readAnimation();
    }

    @Override
    public void empoweredRead() {

        if (Dungeon.bossLevel()) {
            GLog.w(Messages.get(this, "no_tele"));
            return;
        }

        GameScene.selectCell(new CellSelector.Listener() {
            @Override
            public void onSelect(Integer target) {
                if (target != null) {
                    //time isn't spent
                    ((HeroSprite) curUser.sprite).read();
                    teleportToLocation(curUser, target);

                }
            }

            @Override
            public String prompt() {
                return Messages.get(ScrollOfTeleportation.class, "prompt");
            }
        });
    }

    public static void teleportToLocation(Char hero, int pos) {
        PathFinder.buildDistanceMap(pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
        if (PathFinder.distance[hero.pos] == Integer.MAX_VALUE
                || (!Dungeon.level.passable[pos] && !Dungeon.level.avoid[pos])
                || Actor.findChar(pos) != null) {
            GLog.w(Messages.get(ScrollOfTeleportation.class, "cant_reach"));
            return;
        }

        appear(hero, pos);
        Dungeon.level.occupyCell(hero);
        Dungeon.observe();
        GameScene.updateFog();

    }

    public static void teleportHero(Hero hero) {
        teleportHero(hero, false);
    }

    public static void teleportHero(Hero hero, boolean forced) {

        if (Dungeon.bossLevel() && !forced) {
            GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));
            return;
        }

        int count = 10;
        int pos;
        do {
            pos = Dungeon.level.randomRespawnCell();
            if (count-- <= 0) {
                break;
            }
        } while (pos == -1);

        if (pos == -1) {

            GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));

        } else {

            GLog.i(Messages.get(ScrollOfTeleportation.class, "tele"));

            appear(hero, pos);
            Dungeon.level.occupyCell(hero);
            Dungeon.observe();
            GameScene.updateFog();

        }
    }

    public static void teleportPreferringUnseen(Hero hero, boolean special, boolean forced) {
        if ((Dungeon.bossLevel() && !special) || !(Dungeon.level instanceof RegularLevel) || forced) {
            teleportHero(hero, forced);
            return;
        }

        RegularLevel level = (RegularLevel) Dungeon.level;
        ArrayList<Integer> candidates = new ArrayList<>();

        for (Room r : level.rooms()) {
            if (r instanceof SpecialRoom) {
                int terr;
                boolean locked = false;
                for (Point p : r.getPoints()) {
                    terr = level.map[level.pointToCell(p)];
                    if (terr == Terrain.LOCKED_DOOR || terr == Terrain.BARRICADE) {
                        locked = true;
                        break;
                    }
                }
                if (locked) {
                    continue;
                }
            }

            int cell;
            for (Point p : r.charPlaceablePoints(level)) {
                cell = level.pointToCell(p);
                if (level.passable[cell] && !level.visited[cell] && Actor.findChar(cell) == null) {
                    candidates.add(cell);
                }
            }
        }

        if (candidates.isEmpty()) {
            teleportHero(hero);
        } else {
            int pos = Random.element(candidates);
            boolean secretDoor = false;
            int doorPos = -1;
            if (level.room(pos) instanceof SpecialRoom) {
                SpecialRoom room = (SpecialRoom) level.room(pos);
                if (room.entrance() != null) {
                    doorPos = level.pointToCell(room.entrance());
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (!room.inside(level.cellToPoint(doorPos + i))
                                && level.passable[doorPos + i]
                                && Actor.findChar(doorPos + i) == null) {
                            secretDoor = room instanceof SecretRoom;
                            pos = doorPos + i;
                            break;
                        }
                    }
                }
            }
            GLog.i(Messages.get(ScrollOfTeleportation.class, "tele"));
            appear(hero, pos);
            Dungeon.level.occupyCell(hero);
            if (secretDoor && level.map[doorPos] == Terrain.SECRET_DOOR) {
                Sample.INSTANCE.play(Assets.SND_SECRET);
                int oldValue = Dungeon.level.map[doorPos];
                GameScene.discoverTile(doorPos, oldValue);
                Dungeon.level.discover(doorPos);
                ScrollOfMagicMapping.discover(doorPos);
            }
            Dungeon.observe();
            GameScene.updateFog();
        }
    }

    public static void teleportPreferringUnseen(Hero hero) {
        teleportPreferringUnseen(hero, false, false);
    }

    public static void teleportPreferringUnseen(Hero hero, boolean special) {
        teleportPreferringUnseen(hero, special, false);
    }

    public static void appear(Char ch, int pos) {

        ch.sprite.interruptMotion();

        ch.move(pos);
        if (ch.pos == pos) ch.sprite.place(pos);

        if (ch.invisible == 0) {
            ch.sprite.alpha(0);
            ch.sprite.parent.add(new AlphaTweener(ch.sprite, 1, 0.4f));
        }

        ch.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3);
        Sample.INSTANCE.play(Assets.SND_TELEPORT);
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
