package pl.doa.test;

import org.junit.Test;
import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author activey
 * @date 11.10.13 12:20
 */
public class EntitiesContainerTest extends BaseTest {

    @Test
    public void shouldCreateContainer() throws GeneralDOAException {
        IEntitiesContainer testContainer = doa.createContainer("test", doa);

        IEntity entity = doa.lookupEntityByLocation("/test");
        assertNotNull(entity);
        assertTrue(entity instanceof IEntitiesContainer);
    }

    @Test
    public void shouldRemovecontainer() throws GeneralDOAException {
        IEntity entity = doa.lookupEntityByLocation("/test");
        assertNotNull(entity);
        assertTrue(entity instanceof IEntitiesContainer);

        entity.remove(true);
        entity = doa.lookupEntityByLocation("/test");
        assertNull(entity);
    }
}
