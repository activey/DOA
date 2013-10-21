package pl.doa;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

public abstract class AbstractUnitTest {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
