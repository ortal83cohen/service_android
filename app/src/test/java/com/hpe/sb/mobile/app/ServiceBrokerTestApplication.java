package com.hpe.sb.mobile.app;

import com.hpe.sb.mobile.app.common.dataClients.userContext.ConnectionContextModule;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionService;
import com.hpe.sb.mobile.app.features.login.model.DemoHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.model.PropelHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.model.SawHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.utils.CookieParser;

public class ServiceBrokerTestApplication extends ServiceBrokerApplication {

    private ConnectionContextService connectionContextService;

    @Override
    protected ConnectionContextModule getConnectionContextModule() {
        return new ConnectionContextModule(null) {

            @Override
            public ConnectionContextService provideConnectionContextService(
                    CookieParser cookieParser, SawHeadersDefinition sawHeadersDefinition,
                    PropelHeadersDefinition propelHeadersDefinition, DemoHeadersDefinition demoHeadersDefinition,
                    EncryptionService encryptionService) {
                if (connectionContextService != null) {
                    return connectionContextService;
                }
                return super.provideConnectionContextService(cookieParser, sawHeadersDefinition,
                        propelHeadersDefinition, demoHeadersDefinition, encryptionService);
            }
        };
    }

    protected void setConnectionContextService(ConnectionContextService connectionContextService) {
        this.connectionContextService = connectionContextService;
    }
}
