package com.olender.webapp.admin;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;
import pl.doa.wicket.ui.link.EntityLink;
import pl.doa.wicket.ui.page.EntityPage;

public abstract class MenuLinkPanel extends Panel {

	private class SelectedModifier extends AttributeModifier {

		public SelectedModifier(final IModel<? extends IEntity> entityModel) {
			super("class", new AbstractReadOnlyModel<String>() {

				@Override
				public String getObject() {
					Page currentPage = getPage();
					if (currentPage instanceof EntityPage) {
						EntityPage<? extends IEntity> entityPage =
								(EntityPage<? extends IEntity>) currentPage;
						IEntity currentEntity = entityPage.getModelObject();
						if (currentEntity == null) {
							return null;
						}
						if (currentEntity.equals(entityModel.getObject())) {
							return "active";
						}

					}
					return null;
				}
			});
		}
	};

	public MenuLinkPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		EntityLink<? extends IEntity> link = getLink();
		//add(new SelectedModifier(link.getModel()));
		add(link);
	}

	protected abstract EntityLink<? extends IEntity> getLink();
}
