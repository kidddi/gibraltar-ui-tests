package com.bmc.gibraltar.automation.items.element;

import java.util.HashMap;
import java.util.Map;

public enum Halo {
    RESIZE("resize"),
    LINK("link"),
    UNLINK("unlink"),
    REMOVE("remove"),
    CLONE("clone"),
    FORK("fork");

    private static final Map<String, Halo> lookup = new HashMap<>();

    static {
        for (Halo halo : Halo.values())
            lookup.put(halo.getHaloName(), halo);
    }

    private String haloOfActiveElement = "xpath=//div[@class='paper']//div[@class='handles']/div";
    private String haloName;
    private String haloPath;

    Halo(String haloName) {
        this.haloName = haloName;
    }

    public static Halo get(String haloName) {
        return lookup.get(haloName);
    }

    public String getHaloName() {
        return haloName;
    }

    public String getHaloPath() {
        return haloOfActiveElement + String.format("[@data-action='%s']", haloName);
    }
}