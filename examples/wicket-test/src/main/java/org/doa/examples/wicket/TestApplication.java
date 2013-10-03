package org.doa.examples.wicket;

import org.doa.examples.wicket.pages.FirstPage;
import pl.doa.entity.IEntity;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.ui.page.EntityPage;

/**
 * @author activey
 * @date: 03.10.13 19:16
 */
public class TestApplication extends WicketDOAApplication {

    @Override
    protected Class<? extends EntityPage<? extends IEntity>> getStartingPage() {
        return FirstPage.class;
    }

    @Override
    protected void initDOAApplication() throws Exception {

    }
}
