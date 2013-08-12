package pl.doa.wicket.ui.panel;

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.entity.IEntity;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.EntityModel;

public class EntityPanel<T extends IEntity> extends FormComponentPanel<T> {

	private final static Logger log = LoggerFactory
			.getLogger(EntityPanel.class);

	private static final long serialVersionUID = 1L;

	public EntityPanel(String id, IModel<T> entityModel) {
		super(id, entityModel);
	}

	public EntityPanel(String id, T entity) {
		this(id, new EntityModel<T>(entity));
	}

	public EntityPanel(String id, String entityLocation) {
		this(id, new EntityModel<T>(entityLocation));
	}

	protected void initEntityPanel() throws Exception {

	}

	@Override
	protected final void onInitialize() {
		super.onInitialize();

		try {
			initEntityPanel();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	protected final IDOA getDoa() {
		return WicketDOAApplication.get().getDoa();
	}

	protected final IAgent getAgent() {
		return WicketDOAApplication.getAgent();
	}

	@Override
	public String getVariation() {
		return null;
	}
}
