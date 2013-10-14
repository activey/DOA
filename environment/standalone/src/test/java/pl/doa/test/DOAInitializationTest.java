package pl.doa.test;

import org.junit.Test;
import pl.doa.GeneralDOAException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author activey
 * @date 11.10.13 12:20
 */
public class DOAInitializationTest extends BaseTest {

    @Test
    public void shouldStartupDOA() throws GeneralDOAException {
        doa.startup();
        assertTrue(doa.isStartedUp());
    }

    @Test
    public void shouldShutdownDOA() throws GeneralDOAException {
        doa.shutdown();
        assertFalse(doa.isStartedUp());
    }
}
