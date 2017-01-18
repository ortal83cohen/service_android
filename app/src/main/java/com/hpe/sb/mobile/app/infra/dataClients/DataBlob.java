package com.hpe.sb.mobile.app.infra.dataClients;

/**
 * Created by chovel on 25/04/2016.
 *
 */
public class DataBlob<T extends Persistable> {

    private String id;
    private T data;
    private Class<T> dataClass;
    private long lastViewed;
    private long lastFetched;

    public DataBlob(String id, T data, Class<T> dataClass, long lastViewed, long lastFetched) {
        this.id = id;
        this.data = data;
        this.dataClass = dataClass;
        this.lastViewed = lastViewed;
        this.lastFetched = lastFetched;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

    public long getLastFetched() {
        return lastFetched;
    }

    public void setLastFetched(long lastFetched) {
        this.lastFetched = lastFetched;
    }

    public long getLastViewed() {
        return lastViewed;
    }

    public void setLastViewed(long lastViewed) {
        this.lastViewed = lastViewed;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DataBlob<?> dataBlob = (DataBlob<?>) o;

        if (lastViewed != dataBlob.lastViewed) {
            return false;
        }
        if (lastFetched != dataBlob.lastFetched) {
            return false;
        }
        if (id != null ? !id.equals(dataBlob.id) : dataBlob.id != null) {
            return false;
        }
        if (data != null ? !data.equals(dataBlob.data) : dataBlob.data != null) {
            return false;
        }
        return dataClass != null ? dataClass.equals(dataBlob.dataClass) : dataBlob.dataClass == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (dataClass != null ? dataClass.hashCode() : 0);
        result = 31 * result + (int) (lastViewed ^ (lastViewed >>> 32));
        result = 31 * result + (int) (lastFetched ^ (lastFetched >>> 32));
        return result;
    }
}
