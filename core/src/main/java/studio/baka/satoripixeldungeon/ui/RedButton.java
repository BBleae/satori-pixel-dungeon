package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Chrome;

public class RedButton extends StyledButton {

    public RedButton(String label) {
        this(label, 9);
    }

    public RedButton(String label, int size) {
        super(Chrome.Type.RED_BUTTON, label, size);
    }

}
