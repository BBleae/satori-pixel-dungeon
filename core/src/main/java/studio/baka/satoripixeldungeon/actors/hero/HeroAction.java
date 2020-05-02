package studio.baka.satoripixeldungeon.actors.hero;

import studio.baka.satoripixeldungeon.actors.Char;

public class HeroAction {

    public int dst;

    public static class Move extends HeroAction {
        public Move(int dst) {
            this.dst = dst;
        }
    }

    public static class PickUp extends HeroAction {
        public PickUp(int dst) {
            this.dst = dst;
        }
    }

    public static class OpenChest extends HeroAction {
        public OpenChest(int dst) {
            this.dst = dst;
        }
    }

    public static class Buy extends HeroAction {
        public Buy(int dst) {
            this.dst = dst;
        }
    }

    public static class Interact extends HeroAction {
        public Char ch;

        public Interact(Char ch) {
            this.ch = ch;
        }
    }

    public static class Unlock extends HeroAction {
        public Unlock(int door) {
            this.dst = door;
        }
    }

    public static class Descend extends HeroAction {
        public Descend(int stairs) {
            this.dst = stairs;
        }
    }

    public static class Ascend extends HeroAction {
        public Ascend(int stairs) {
            this.dst = stairs;
        }
    }

    public static class Alchemy extends HeroAction {
        public Alchemy(int pot) {
            this.dst = pot;
        }
    }

    public static class Attack extends HeroAction {
        public Char target;

        public Attack(Char target) {
            this.target = target;
        }
    }
}
