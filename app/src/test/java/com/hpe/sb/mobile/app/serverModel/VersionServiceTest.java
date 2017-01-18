package com.hpe.sb.mobile.app.serverModel;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.hpe.sb.mobile.app.common.services.version.VersionService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class VersionServiceTest {
    private Context contextMock = mock(Context.class);
    private PackageManager packageManager = mock(PackageManager.class);
    private VersionService tested = new VersionService();

    @Before
    public void setUp() {
        when(contextMock.getPackageManager()).thenReturn(packageManager);
    }

    @Test
    public void testGetAppCurrentVersion() {
        PackageInfo pInfo = new PackageInfo();
        pInfo.versionCode = 1;

       try{ // we use try/catch to avoid compilation error, because packageManager.getPackageInfo can throw exception
            when(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(pInfo);
        }catch(Exception e){}


        Integer appCurrentVersion = tested.getAppCurrentVersion(contextMock);
        Assert.assertEquals(pInfo.versionCode, appCurrentVersion.intValue());
    }

    @Test(expected = RuntimeException.class)
    public void testGetAppCurrentVersionWithError() {
        try{ // we use try/catch to avoid compilation error, because packageManager.getPackageInfo can throw exception
            when(packageManager.getPackageInfo(anyString(), anyInt())).thenThrow(PackageManager.NameNotFoundException.class);
        }catch(Exception e){}


        tested.getAppCurrentVersion(contextMock);
    }


}
