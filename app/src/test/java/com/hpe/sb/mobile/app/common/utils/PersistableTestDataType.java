package com.hpe.sb.mobile.app.common.utils;

import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

/**
 * Created by chovel on 28/04/2016.
 *
 */
public class PersistableTestDataType implements Persistable {

    private String id;
    private int checksum;

    public PersistableTestDataType(String id, int checksum) {
        this.id = id;
        this.checksum = checksum;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getChecksum() {
        return checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersistableTestDataType that = (PersistableTestDataType) o;

        if (checksum != that.checksum) {
            return false;
        }
        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + checksum;
        return result;
    }

}
