package com.hpe.sb.mobile.app.serverModel.user;

import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

/**
 * A data model for a user.
 */
public class User implements Persistable{

    /**
     * User id.
     */
    private String id;

    /**
     * User full name.
     */
    private String name;

    /**
     * User avatar image id.
     */
    private String avatarImageId;

    private String tenantId;

    private String username;

    public User() {
    }

    public User(String id, String name, String avatarImageId) {
        this.id = id;
        this.name = name;
        this.avatarImageId = avatarImageId;
    }

    public User(String id, String name, String avatarImageId, String tenantId, String username) {
        this.id = id;
        this.name = name;
        this.avatarImageId = avatarImageId;
        this.tenantId = tenantId;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatarImageId() {
        return avatarImageId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarImageId(String avatarImageId) {
        this.avatarImageId = avatarImageId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) {
            return false;
        }
        if (name != null ? !name.equals(user.name) : user.name != null) {
            return false;
        }
        if (avatarImageId != null ? !avatarImageId.equals(user.avatarImageId) : user.avatarImageId != null) {
            return false;
        }
        if (username != null ? !username.equals(user.username) : user.username != null) {
            return false;
        }
        return tenantId != null ? tenantId.equals(user.tenantId) : user.tenantId == null;

    }

    @Override
    public int getChecksum() {
        return 0;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (avatarImageId != null ? avatarImageId.hashCode() : 0);
        result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}
