package com.hpe.sb.mobile.app.serverModel.diff;

public class DiffQueryItem {

    public String id;
    public int checksum;

    public DiffQueryItem() {

    }

    public DiffQueryItem(String id, int checksum) {
        this.id = id;
        this.checksum = checksum;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DiffQueryItem diffQueryItem = (DiffQueryItem) o;

        if (checksum != diffQueryItem.checksum) {
            return false;
        }
        return id != null ? id.equals(diffQueryItem.id) : diffQueryItem.id == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + checksum;
        return result;
    }
}
