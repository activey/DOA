package pl.doa.wicket.ui.panel.document;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.document.IDocument;
import pl.doa.wicket.ui.panel.EntityPanel;

public class DocumentPanel extends EntityPanel<IDocument> {

	private final static Logger log = LoggerFactory
			.getLogger(DocumentPanel.class);

	private static final long serialVersionUID = 1L;

	public DocumentPanel(String id, IDocument entity) {
		super(id, entity);
	}

	public DocumentPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
	}

	public DocumentPanel(String id, String entityLocation) {
		super(id, entityLocation);
	}

	@Override
	protected final void initEntityPanel() throws Exception {
		initDocumentPanel();
	}

	protected void initDocumentPanel() throws Exception {

	}

}
