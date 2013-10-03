package org.doa.examples.wicket.pages;

import pl.doa.document.IDocument;
import pl.doa.wicket.ui.page.EntityPage;

/**
 * @author activey
 * @date: 03.10.13 19:18
 */
public class FirstPage extends EntityPage<IDocument> {

    public FirstPage() {
        super("/documents/first");
    }

    @Override
    protected void initEntityPage() throws Exception {

    }
}
