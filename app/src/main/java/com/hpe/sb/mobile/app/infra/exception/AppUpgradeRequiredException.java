package com.hpe.sb.mobile.app.infra.exception;


public class AppUpgradeRequiredException extends PropelException {
    private Integer androidAppMinVersion;

    public AppUpgradeRequiredException(Integer androidAppMinVersion) {
        this.androidAppMinVersion = androidAppMinVersion;
    }

    public Integer getAndroidAppMinVersion() {
        return androidAppMinVersion;
    }
}
