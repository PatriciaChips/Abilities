package org.pat.abilities.Objects;

import org.pat.pattyEssentialsV3.Utils;

public enum Affinity {
    movement(Utils.lightblue + "Movement"),
    passive(Utils.green + "Passive"),
    combat(Utils.darkred + "Combative"),
    support(Utils.lightred + "Support");

    private String text;

    Affinity(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /**
     * Can be expanded to include affinity descriptions or an associated colour/s for example
     */
}
