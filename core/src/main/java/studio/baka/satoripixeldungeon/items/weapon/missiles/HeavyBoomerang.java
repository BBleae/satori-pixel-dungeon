package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.sprites.MissileSprite;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;

public class HeavyBoomerang extends MissileWeapon {

    {
        image = ItemSpriteSheet.BOOMERANG;

        tier = 4;
        sticky = false;
    }

    @Override
    public int max(int lvl) {
        return 4 * tier +                  //16 base, down from 20
                (tier) * lvl;               //scaling unchanged
    }

    @Override
    protected void rangedHit(Char enemy, int cell) {
        decrementDurability();
        if (durability > 0) {
            Buff.append(Dungeon.hero, CircleBack.class).setup(this, cell, Dungeon.hero.pos, Dungeon.depth);
        }
    }

    @Override
    protected void rangedMiss(int cell) {
        parent = null;
        Buff.append(Dungeon.hero, CircleBack.class).setup(this, cell, Dungeon.hero.pos, Dungeon.depth);
    }

    public static class CircleBack extends Buff {

        private MissileWeapon boomerang;
        private int thrownPos;
        private int returnPos;
        private int returnDepth;

        private int left;

        public void setup(MissileWeapon boomerang, int thrownPos, int returnPos, int returnDepth) {
            this.boomerang = boomerang;
            this.thrownPos = thrownPos;
            this.returnPos = returnPos;
            this.returnDepth = returnDepth;
            left = 3;
        }

        public int returnPos() {
            return returnPos;
        }

        public MissileWeapon cancel() {
            detach();
            return boomerang;
        }

        @Override
        public boolean act() {
            if (returnDepth == Dungeon.depth) {
                left--;
                if (left <= 0) {
                    final Char returnTarget = Actor.findChar(returnPos);
                    final Char target = this.target;
                    MissileSprite visual = ((MissileSprite) Dungeon.hero.sprite.parent.recycle(MissileSprite.class));
                    visual.reset(thrownPos,
                            returnPos,
                            boomerang,
                            () -> {
                                if (returnTarget == target) {
                                    if (target instanceof Hero && boomerang.doPickUp((Hero) target)) {
                                        //grabbing the boomerang takes no time
                                        ((Hero) target).spend(-TIME_TO_PICK_UP);
                                    } else {
                                        Dungeon.level.drop(boomerang, returnPos).sprite.drop();
                                    }

                                } else if (returnTarget != null) {
                                    if (((Hero) target).shoot(returnTarget, boomerang)) {
                                        boomerang.decrementDurability();
                                    }
                                    if (boomerang.durability > 0) {
                                        Dungeon.level.drop(boomerang, returnPos).sprite.drop();
                                    }

                                } else {
                                    Dungeon.level.drop(boomerang, returnPos).sprite.drop();
                                }
                                CircleBack.this.next();
                            });
                    visual.alpha(0f);
                    float duration = Dungeon.level.trueDistance(thrownPos, returnPos) / 20f;
                    target.sprite.parent.add(new AlphaTweener(visual, 1f, duration));
                    detach();
                    return false;
                }
            }
            spend(TICK);
            return true;
        }

        private static final String BOOMERANG = "boomerang";
        private static final String THROWN_POS = "thrown_pos";
        private static final String RETURN_POS = "return_pos";
        private static final String RETURN_DEPTH = "return_depth";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOOMERANG, boomerang);
            bundle.put(THROWN_POS, thrownPos);
            bundle.put(RETURN_POS, returnPos);
            bundle.put(RETURN_DEPTH, returnDepth);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            boomerang = (MissileWeapon) bundle.get(BOOMERANG);
            thrownPos = bundle.getInt(THROWN_POS);
            returnPos = bundle.getInt(RETURN_POS);
            returnDepth = bundle.getInt(RETURN_DEPTH);
        }
    }

}
