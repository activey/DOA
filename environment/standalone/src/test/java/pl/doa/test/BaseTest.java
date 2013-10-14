package pl.doa.test;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import pl.doa.IDOA;
import pl.doa.StandaloneDOA;

/**
 * @author activey
 * @date 11.10.13 12:20
 */
public class BaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected static IDOA doa;

    @BeforeClass
    public static void initialize() throws Exception {
        doa = new StandaloneDOA(new PropertiesConfiguration("doa.properties"));
    }
}
