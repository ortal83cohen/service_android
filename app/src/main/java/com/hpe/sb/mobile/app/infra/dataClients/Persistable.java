package com.hpe.sb.mobile.app.infra.dataClients;

/**
 * Created by chovel on 25/04/2016.
 *
 */
public interface Persistable extends Identifiable {

    String getId();

    int getChecksum();

}
