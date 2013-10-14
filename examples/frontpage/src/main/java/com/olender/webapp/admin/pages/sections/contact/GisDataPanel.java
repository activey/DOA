/**
 *
 */
package com.olender.webapp.admin.pages.sections.contact;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 */
public class GisDataPanel extends EntityPanel<IDocument> {

    private IModel<IDocument> inputModel;

    public GisDataPanel(String id, IModel<IDocument> entityModel) {
        super(id, entityModel);
        setOutputMarkupId(true);

    }

    @Override
    protected void initEntityPanel() throws Exception {
        this.inputModel = new DocumentModel(getModelObject(), true);

        add(new UpdateGisDataForm("gis_data_form", inputModel));

        add(new ProcessKmlDataForm("kml_data_form", getModel()) {

            protected void handleRunning(IRunningService running,
                                         AjaxRequestTarget target) {
                try {
                    inputModel.setObject(running.getOutput().createCopy());
                    target.add(GisDataPanel.this.get("gis_data_form"));
                } catch (GeneralDOAException e) {
                    GisDataPanel.this.error(e);
                }
            }
        });

    }
}
