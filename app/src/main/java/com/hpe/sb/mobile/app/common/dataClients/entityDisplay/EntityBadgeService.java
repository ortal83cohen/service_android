package com.hpe.sb.mobile.app.common.dataClients.entityDisplay;


import com.hpe.sb.mobile.app.common.dataClients.displaylabels.BadgeProperties;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;

/**
 * Created by salemo on 23/03/2016.
 *
 */
public interface EntityBadgeService {

    BadgeProperties getBadgeProperties(EntityType entityType);
}