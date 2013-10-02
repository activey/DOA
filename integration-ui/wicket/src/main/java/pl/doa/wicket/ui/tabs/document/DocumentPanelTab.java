/**
 * 
 */
package pl.doa.wicket.ui.tabs.document;

import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.panel.document.DocumentPanel;
import pl.doa.wicket.ui.tabs.EntityPanelTab;

/**
 * @author activey
 * 
 */
public abstract class DocumentPanelTab extends EntityPanelTab<IDocument> {

	public DocumentPanelTab(IDocument entity) {
		super(entity);
	}

	public DocumentPanelTab(IModel<IDocument> entityModel) {
		super(entityModel);
	}

	public DocumentPanelTab(String documentLocation) {
		super(documentLocation);
	}

	@Override
	public IModel<String> getTitle() {
		return null;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	protected final EntityPanel<IDocument> getEntityPanel(String panelId) {
		return getDocumentPanel(panelId, getModel());
	}

	protected abstract DocumentPanel getDocumentPanel(String panelId,
			IModel<IDocument> documentModel);

}
