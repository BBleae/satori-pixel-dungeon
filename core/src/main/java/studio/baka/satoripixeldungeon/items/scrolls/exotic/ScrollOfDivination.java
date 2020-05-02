package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.effects.Identification;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.rings.Ring;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.IconTitle;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class ScrollOfDivination extends ExoticScroll {

    {
        initials = 0;
    }

    @Override
    public void doRead() {

        curUser.sprite.parent.add(new Identification(curUser.sprite.center().offset(0, -16)));

        readAnimation();
        setKnown();

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        HashSet<Class<? extends Potion>> potions = Potion.getUnknown();
        HashSet<Class<? extends Scroll>> scrolls = Scroll.getUnknown();
        HashSet<Class<? extends Ring>> rings = Ring.getUnknown();

        int total = potions.size() + scrolls.size() + rings.size();

        if (total == 0) {
            GLog.n(Messages.get(this, "nothing_left"));
            return;
        }

        ArrayList<Item> IDed = new ArrayList<>();
        int left = 4;

        float[] baseProbs = new float[]{3, 3, 3};
        float[] probs = baseProbs.clone();

        while (left > 0 && total > 0) {
            switch (Random.chances(probs)) {
                default:
                    probs = baseProbs.clone();
                    continue;
                case 0:
                    if (potions.isEmpty()) {
                        probs[0] = 0;
                        continue;
                    }
                    probs[0]--;
                    Potion p = Reflection.newInstance(Random.element(potions));
                    Objects.requireNonNull(p).setKnown();
                    IDed.add(p);
                    potions.remove(p.getClass());
                    break;
                case 1:
                    if (scrolls.isEmpty()) {
                        probs[1] = 0;
                        continue;
                    }
                    probs[1]--;
                    Scroll s = Reflection.newInstance(Random.element(scrolls));
                    Objects.requireNonNull(s).setKnown();
                    IDed.add(s);
                    scrolls.remove(s.getClass());
                    break;
                case 2:
                    if (rings.isEmpty()) {
                        probs[2] = 0;
                        continue;
                    }
                    probs[2]--;
                    Ring r = Reflection.newInstance(Random.element(rings));
                    Objects.requireNonNull(r).setKnown();
                    IDed.add(r);
                    rings.remove(r.getClass());
                    break;
            }
            left--;
            total--;
        }

        GameScene.show(new WndDivination(IDed));
    }

    private class WndDivination extends Window {

        private static final int WIDTH = 120;

        WndDivination(ArrayList<Item> IDed) {
            IconTitle cur = new IconTitle(new ItemSprite(ScrollOfDivination.this),
                    Messages.titleCase(Messages.get(ScrollOfDivination.class, "name")));
            cur.setRect(0, 0, WIDTH, 0);
            add(cur);

            RenderedTextBlock msg = PixelScene.renderTextBlock(Messages.get(this, "desc"), 6);
            msg.maxWidth(120);
            msg.setPos(0, cur.bottom() + 2);
            add(msg);

            float pos = msg.bottom() + 10;

            for (Item i : IDed) {

                cur = new IconTitle(i);
                cur.setRect(0, pos, WIDTH, 0);
                add(cur);
                pos = cur.bottom() + 2;

            }

            resize(WIDTH, (int) pos);
        }

    }
}
