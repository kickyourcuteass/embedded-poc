package ro.home.app.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StaticUtils.class})
public class StaticUtilsTest {

    @Before
    public void setUp() {
        PowerMockito.mockStatic(StaticUtils.class);
    }

    @Test
    public void testReturnString() {
        assertNull(StaticUtils.returnString("fas"));
        when(StaticUtils.returnString(anyString())).thenReturn("testCrap");
        assertEquals("testCrap", StaticUtils.returnString("fas"));
    }
}
