package com.dnyferguson.voteplugin.voteparty;

import java.util.UUID;

public class MainAccount {
    private UUID uuid;
    private String ign;

    public MainAccount(UUID uuid, String ign) {
        this.uuid = uuid;
        this.ign = ign;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getIgn() {
        return ign;
    }

    public void setIgn(String ign) {
        this.ign = ign;
    }
}