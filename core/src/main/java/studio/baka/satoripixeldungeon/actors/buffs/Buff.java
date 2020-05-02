package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Objects;

public class Buff extends Actor {

    public Char target;

    {
        actPriority = BUFF_PRIORITY; //low priority, towards the end of a turn
    }

    //determines how the buff is announced when it is shown.
    public enum buffType {POSITIVE, NEGATIVE, NEUTRAL}

    public buffType type = buffType.NEUTRAL;

    //whether or not the buff announces its name
    public boolean announced = false;

    @SuppressWarnings("rawtypes")
    protected HashSet<Class> resistances = new HashSet<>();

    @SuppressWarnings("rawtypes")
    public HashSet<Class> resistances() {
        return new HashSet<>(resistances);
    }

    @SuppressWarnings("rawtypes")
    protected HashSet<Class> immunities = new HashSet<>();

    @SuppressWarnings("rawtypes")
    public HashSet<Class> immunities() {
        return new HashSet<>(immunities);
    }

    public boolean attachTo(Char target) {

        if (target.isImmune(getClass())) {
            return false;
        }

        this.target = target;
        target.add(this);

        if (target.buffs().contains(this)) {
            if (target.sprite != null) fx(true);
            return true;
        } else {
            this.target = null;
            return false;
        }
    }

    public void detach() {
        if (target.sprite != null) fx(false);
        target.remove(this);
    }

    @Override
    public boolean act() {
        deactivate();
        return true;
    }

    public int icon() {
        return BuffIndicator.NONE;
    }

    public void tintIcon(Image icon) {
        //do nothing by default
    }

    public void fx(boolean on) {
        //do nothing by default
    }

    public String heroMessage() {
        return null;
    }

    public String desc() {
        return "";
    }

    //to handle the common case of showing how many turns are remaining in a buff description.
    protected String dispTurns(float input) {
        return new DecimalFormat("#.##").format(input);
    }

    //creates a fresh instance of the buff and attaches that, this allows duplication.
    public static <T extends Buff> T append(Char target, Class<T> buffClass) {
        T buff = Reflection.newInstance(buffClass);
        Objects.requireNonNull(buff).attachTo(target);
        return buff;
    }

    public static <T extends FlavourBuff> T append(Char target, Class<T> buffClass, float duration) {
        T buff = append(target, buffClass);
        buff.spend(duration * target.resist(buffClass));
        return buff;
    }

    //same as append, but prevents duplication.
    public static <T extends Buff> T affect(Char target, Class<T> buffClass) {
        T buff = target.buff(buffClass);
        if (buff != null) {
            return buff;
        } else {
            return append(target, buffClass);
        }
    }

    public static <T extends FlavourBuff> T affect(Char target, Class<T> buffClass, float duration) {
        T buff = affect(target, buffClass);
        buff.spend(duration * target.resist(buffClass));
        return buff;
    }

    //postpones an already active buff, or creates & attaches a new buff and delays that.
    public static <T extends FlavourBuff> T prolong(Char target, Class<T> buffClass, float duration) {
        T buff = affect(target, buffClass);
        buff.postpone(duration * target.resist(buffClass));
        return buff;
    }

    public static void detach(Buff buff) {
        if (buff != null) {
            buff.detach();
        }
    }

    public static void detach(Char target, Class<? extends Buff> cl) {
        detach(target.buff(cl));
    }
}
