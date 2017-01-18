package com.hpe.sb.mobile.app.serverModel;

import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

public class ThemeSettings implements Persistable {

    private String backgroundImageId;

    public ThemeSettings() {
    }

    @Override
    public String getId() {
        return "ThemeSettings";
    }

    @Override
    public int getChecksum() {
        return 0;
    }

    public String getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(String backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        ThemeSettings that = (ThemeSettings)o;

        return backgroundImageId.equals(that.backgroundImageId);
    }

    @Override
    public int hashCode() {
        return backgroundImageId != null ? backgroundImageId.hashCode() : 0;
    }
}
