package pl.doa.wicket.ui.panel.container;

import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.wicket.ui.panel.EntityPanel;

public class EntitiesContainerPanel extends EntityPanel<IEntitiesContainer> {

	private static final long serialVersionUID = 1L;

	public EntitiesContainerPanel(String id, IEntitiesContainer container) {
		super(id, container);
	}

	public EntitiesContainerPanel(String id,
			IModel<IEntitiesContainer> containerModel) {
		super(id, containerModel);
	}

	public EntitiesContainerPanel(String id, String containerLocation) {
		super(id, containerLocation);
	}

	@Override
	protected final void initEntityPanel() throws Exception {
		initContainerPanel();
	}

	protected void initContainerPanel() throws Exception {

	}

}
