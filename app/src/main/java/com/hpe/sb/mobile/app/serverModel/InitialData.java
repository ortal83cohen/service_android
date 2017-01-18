package com.hpe.sb.mobile.app.serverModel;


import com.hpe.sb.mobile.app.serverModel.displayLabels.DisplayLabelsWrapper;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroupDiffResult;
import com.hpe.sb.mobile.app.serverModel.user.User;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;

public class InitialData {
    private TenantSettings tenantSettings;
    private ThemeSettings themeSettings;
    private User currentUser;
    private DisplayLabelsWrapper displayLabelsWrapper;
    private UserItems userItems;
    private CatalogGroupDiffResult categoriesDiff;
    private VersionContainer minSupportedVersion;

    public InitialData() {
    }

    public TenantSettings getTenantSettings() {
        return tenantSettings;
    }

    public void setTenantSettings(TenantSettings tenantSettings) {
        this.tenantSettings = tenantSettings;
    }

    public ThemeSettings getThemeSettings() {
        return themeSettings;
    }

    public void setThemeSettings(ThemeSettings themeSettings) {
        this.themeSettings = themeSettings;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public UserItems getUserItems() {
        return userItems;
    }

    public void setUserItems(UserItems userItems) {
        this.userItems = userItems;
    }

    public CatalogGroupDiffResult getCategoriesDiff() {
        return categoriesDiff;
    }

    public void setCategoriesDiff(CatalogGroupDiffResult categoriesDiff) {
        this.categoriesDiff = categoriesDiff;
    }

    public DisplayLabelsWrapper getDisplayLabelsWrapper() {
        return displayLabelsWrapper;
    }

    public void setDisplayLabelsWrapper(DisplayLabelsWrapper displayLabelsWrapper) {
        this.displayLabelsWrapper = displayLabelsWrapper;
    }

    public VersionContainer getMinSupportedVersion() {
        return minSupportedVersion;
    }

    public void setMinSupportedVersion(VersionContainer minSupportedVersion) {
        this.minSupportedVersion = minSupportedVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InitialData that = (InitialData) o;

        if (tenantSettings != null ? !tenantSettings.equals(that.tenantSettings) : that.tenantSettings != null) {
            return false;
        }
        if (themeSettings != null ? !themeSettings.equals(that.themeSettings) : that.themeSettings != null) {
            return false;
        }
        if (currentUser != null ? !currentUser.equals(that.currentUser) : that.currentUser != null) {
            return false;
        }
        if (displayLabelsWrapper != null ? !displayLabelsWrapper.equals(that.displayLabelsWrapper) : that.displayLabelsWrapper != null) {
            return false;
        }
        if (userItems != null ? !userItems.equals(that.userItems) : that.userItems != null) {
            return false;
        }
        if (categoriesDiff != null ? !categoriesDiff.equals(that.categoriesDiff) : that.categoriesDiff != null) {
            return false;
        }
        return minSupportedVersion != null ? minSupportedVersion.equals(that.minSupportedVersion) : that.minSupportedVersion == null;

    }

    @Override
    public int hashCode() {
        int result = tenantSettings != null ? tenantSettings.hashCode() : 0;
        result = 31 * result + (themeSettings != null ? themeSettings.hashCode() : 0);
        result = 31 * result + (currentUser != null ? currentUser.hashCode() : 0);
        result = 31 * result + (displayLabelsWrapper != null ? displayLabelsWrapper.hashCode() : 0);
        result = 31 * result + (userItems != null ? userItems.hashCode() : 0);
        result = 31 * result + (categoriesDiff != null ? categoriesDiff.hashCode() : 0);
        result = 31 * result + (minSupportedVersion != null ? minSupportedVersion.hashCode() : 0);
        return result;
    }
}
