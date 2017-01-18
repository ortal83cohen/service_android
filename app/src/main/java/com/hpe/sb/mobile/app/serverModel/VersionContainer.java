package com.hpe.sb.mobile.app.serverModel;

/**
 * Created by alterl on 09/08/2016.
 */
public class VersionContainer {

    private int androidAppMinVersion;
    private int iosAppMinVersion;

    public VersionContainer() {
    }

    public int getAndroidAppMinVersion() {
        return androidAppMinVersion;
    }

    public void setAndroidAppMinVersion(int androidAppMinVersion) {
        this.androidAppMinVersion = androidAppMinVersion;
    }

    public int getIosAppMinVersion() {
        return iosAppMinVersion;
    }

    public void setIosAppMinVersion(int iosAppMinVersion) {
        this.iosAppMinVersion = iosAppMinVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VersionContainer that = (VersionContainer) o;

        if (androidAppMinVersion != that.androidAppMinVersion) {
            return false;
        }
        return iosAppMinVersion == that.iosAppMinVersion;

    }

    @Override
    public int hashCode() {
        int result = androidAppMinVersion;
        result = 31 * result + iosAppMinVersion;
        return result;
    }
}
