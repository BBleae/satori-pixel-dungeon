package studio.baka.satoripixeldungeon.actors.mobs;

import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class Bestiary {

    public static ArrayList<Class<? extends Mob>> getMobRotation(int depth) {
        ArrayList<Class<? extends Mob>> mobs = standardMobRotation(depth);
        addRareMobs(depth, mobs);
        swapMobAlts(mobs);
        Random.shuffle(mobs);
        return mobs;
    }

    //returns a rotation of standard mobs, unshuffled.
    private static ArrayList<Class<? extends Mob>> standardMobRotation(int depth) {
        switch (depth) {

            // Sewers
            case 1:
            default:
                //3x rat, 2x snake
                return new ArrayList<>(Arrays.asList(
                        Rat.class, Rat.class, Rat.class,
                        Snake.class, Snake.class,
                        Unknown.class
                ));
            case 2:
                //4x rat, 1x snake, 2x gnoll
                return new ArrayList<>(Arrays.asList(
                        Rat.class, Rat.class, Rat.class, Rat.class,
                        Snake.class, Unknown.class, Unknown.class,
                        Gnoll.class, Gnoll.class));
            case 3:
                //1x rat, 1x snake, 3x gnoll, 1x swarm, 2x crab
                return new ArrayList<>(Arrays.asList(Rat.class,
                        Snake.class, Unknown.class, Unknown.class,
                        Gnoll.class, Gnoll.class, Gnoll.class,
                        Swarm.class,
                        Crab.class, Crab.class));
            case 4:
            case 5:
                //1x gnoll, 1x swarm, 2x crab, 2x slime
                return new ArrayList<>(Arrays.asList(Gnoll.class,
                        Swarm.class, Unknown.class, Unknown.class, Unknown.class,
                        Crab.class, Crab.class,
                        Slime.class, Slime.class));

            case 6:
                return new ArrayList<>(Arrays.asList(
                        Skeleton.class, Skeleton.class, Skeleton.class,
                        Thief.class,
                        Shaman.class, Shaman.class,
                        Guard.class
                ));

            case 7:
                return new ArrayList<>(Arrays.asList(
                        Skeleton.class, Skeleton.class, Skeleton.class,
                        Thief.class, Thief.class,
                        Shaman.class, Shaman.class,
                        Guard.class,
                        Necromancer.class
                ));

            case 8:
                return new ArrayList<>(Arrays.asList(
                        Thief.class, Thief.class, Thief.class, Thief.class, Thief.class, Thief.class, Thief.class,
                        Guard.class, Guard.class, Guard.class
                ));

            case 9:
                return new ArrayList<>(Arrays.asList(
                        Shaman.class, Shaman.class, Shaman.class, Shaman.class,
                        Shaman.class, Shaman.class, Shaman.class, Shaman.class,
                        Guard.class, Guard.class
                ));

            case 10:
                return new ArrayList<>(Arrays.asList(
                        Skeleton.class, Skeleton.class, Skeleton.class,
                        Skeleton.class, Skeleton.class, Skeleton.class,
                        Necromancer.class, Necromancer.class, Necromancer.class, Necromancer.class
                ));

            case 11:
                return new ArrayList<>(Arrays.asList(
                        Bat.class, Bat.class, Bat.class, Bat.class, Bat.class, Bat.class,
                        Brute.class, Brute.class, Brute.class
                ));

            case 12:
                //5x bat, 5x brute, 1x spinner
                return new ArrayList<>(Arrays.asList(
                        Bat.class, Bat.class, Bat.class, Bat.class, Bat.class,
                        Brute.class, Brute.class, Brute.class, Brute.class, Brute.class, Brute.class,
                        Spinner.class, Spinner.class, Spinner.class
                ));

            case 13:
                return new ArrayList<>(Arrays.asList(
                        Bat.class, Bat.class, Bat.class,
                        Brute.class, Brute.class, Brute.class, Brute.class,
                        Shaman.class, Shaman.class, Shaman.class,
                        Spinner.class, Spinner.class,
                        Guard.class, Guard.class
                ));

            case 14:
            case 15:
                return new ArrayList<>(Arrays.asList(
                        Bat.class, Bat.class, Bat.class, Bat.class, Bat.class,
                        Brute.class, Brute.class, Brute.class, Brute.class, Brute.class,
                        Shaman.class, Shaman.class, Shaman.class,
                        Spinner.class, Spinner.class, Spinner.class, Spinner.class,
                        Guard.class, Guard.class, Guard.class,
                        Necromancer.class, Necromancer.class, Necromancer.class
                ));

            case 16:
                return new ArrayList<>(Arrays.asList(
                        Elemental.class, Elemental.class, Elemental.class, Elemental.class, Elemental.class,
                        Warlock.class, Warlock.class, Warlock.class, Warlock.class, Warlock.class,
                        Monk.class
                ));

            case 17:
                return new ArrayList<>(Arrays.asList(
                        Elemental.class, Elemental.class,
                        Warlock.class, Warlock.class, Warlock.class, Warlock.class,
                        Monk.class, Monk.class, Monk.class, Monk.class
                ));

            case 18:
                return new ArrayList<>(Arrays.asList(
                        Warlock.class, Warlock.class, Warlock.class, Warlock.class, Warlock.class,
                        Monk.class, Monk.class, Monk.class, Monk.class, Monk.class, Monk.class
                ));


            case 19:
            case 20:
                return new ArrayList<>(Arrays.asList(
                        Warlock.class, Warlock.class, Monk.class,
                        Elemental.class, Elemental.class, Elemental.class, Elemental.class,
                        Golem.class, Golem.class, Golem.class, Golem.class, Golem.class, Golem.class
                ));

            case 21:
            case 22:
                //3x succubus, 3x evil eye
                return new ArrayList<>(Arrays.asList(
                        Succubus.class, Succubus.class, Succubus.class,
                        Eye.class, Eye.class, Eye.class));

            case 23:
                //2x succubus, 4x evil eye, 2x scorpio
                return new ArrayList<>(Arrays.asList(
                        Succubus.class, Succubus.class,
                        Eye.class, Eye.class, Eye.class, Eye.class,
                        Scorpio.class, Scorpio.class));

            case 24:
            case 25:
            case 26:
                //1x succubus, 2x evil eye, 3x scorpio
                return new ArrayList<>(Arrays.asList(
                        Succubus.class,
                        Eye.class, Eye.class,
                        Scorpio.class, Scorpio.class, Scorpio.class));

            case 27:
            case 28:
                return new ArrayList<>(Arrays.asList(
                        Succubus.class,
                        Eye.class, Eye.class, Eye.class, Eye.class,
                        Scorpio.class, Scorpio.class, Scorpio.class));

            case 29:
            case 30:
                return new ArrayList<>(Arrays.asList(
                        Scorpio.class, Scorpio.class, Scorpio.class,
                        Scorpio.class, Scorpio.class, Scorpio.class,
                        Scorpio.class, Scorpio.class, Scorpio.class));
        }

    }

    //has a chance to add a rarely spawned mobs to the rotation
    public static void addRareMobs(int depth, ArrayList<Class<? extends Mob>> rotation) {

        switch (depth) {

            // Sewers
            default:
                return;
            case 4:
                if (Random.Float() < 0.025f) rotation.add(Thief.class);
                return;

            // Prison
            case 8:
                if (Random.Float() < 0.02f) rotation.add(Bat.class);
                return;
            case 9:
                if (Random.Float() < 0.02f) rotation.add(Bat.class);
                if (Random.Float() < 0.01f) rotation.add(Brute.class);
                return;

            // Caves
            case 13:
                if (Random.Float() < 0.02f) rotation.add(Elemental.class);
                return;
            case 14:
                if (Random.Float() < 0.02f) rotation.add(Elemental.class);
                if (Random.Float() < 0.01f) rotation.add(Monk.class);
                return;

            // City
            case 19:
                if (Random.Float() < 0.02f) rotation.add(Succubus.class);


        }
    }

    //switches out regular mobs for their alt versions when appropriate
    private static void swapMobAlts(ArrayList<Class<? extends Mob>> rotation) {
        for (int i = 0; i < rotation.size(); i++) {
            if (Random.Int(50) == 0) {
                Class<? extends Mob> cl = rotation.get(i);
                if (cl == Rat.class) {
                    cl = Albino.class;
                } else if (cl == Slime.class) {
                    cl = CausticSlime.class;
                } else if (cl == Thief.class) {
                    cl = Bandit.class;
                } else if (cl == Brute.class) {
                    cl = Shielded.class;
                } else if (cl == Monk.class) {
                    cl = Senior.class;
                } else if (cl == Scorpio.class) {
                    cl = Acidic.class;
                }
                rotation.set(i, cl);
            }
        }
    }
}
