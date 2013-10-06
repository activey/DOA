/**
 * 
 */
package com.olender.webapp.admin;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.model.agent.AgentDataModel;
import pl.doa.wicket.ui.link.EntityLink;
import pl.doa.wicket.ui.page.EntityPage;
import pl.doa.wicket.ui.panel.EntityMarkupContainer;
import pl.doa.wicket.ui.widgets.DocumentFieldLabel;

import com.olender.webapp.admin.feedback.ApplicationFeedbackPanel;
import com.olender.webapp.admin.pages.BackupPage;
import com.olender.webapp.admin.pages.ImagesPage;
import com.olender.webapp.admin.pages.MainPage;
import com.olender.webapp.admin.pages.SectionsPage;
import com.olender.webapp.pages.FrontPage;

/**
 * @author activey
 * 
 */
@AuthorizeInstantiation("admin")
public class BasePage<T extends IEntity> extends EntityPage<T> {

	public BasePage() {
		super();
	}

	public BasePage(T entity) {
		super(entity);
	}

	public BasePage(PageParameters parameters) {
		super(parameters);
	}

	public BasePage(PathIterator<String> entityPath) {
		super(entityPath);
	}

	public BasePage(String entityLocation) {
		super(entityLocation);
	}

	@Override
	protected final void initEntityPage() throws Exception {
		add(new ApplicationFeedbackPanel("errors_panel"));

		add(new LinkContainer<IDocument>("container_link_main",
				new EntityLink<IDocument>("link_main_data", MainPage.class)));
		add(new LinkContainer<IEntitiesContainer>("container_link_sections",
				new EntityLink<IEntitiesContainer>("link_sections_manage",
						SectionsPage.class)));
		add(new LinkContainer<IEntitiesContainer>("container_link_images",
				new EntityLink<IEntitiesContainer>("link_images_manage",
						ImagesPage.class)));
		add(new LinkContainer<IEntitiesContainer>("container_link_backup",
				new EntityLink<IEntitiesContainer>("link_backup",
						BackupPage.class)));

		add(new EntityLink<IEntitiesContainer>("link_main", FrontPage.class));

		IModel<IDocument> profileDocModel = new AgentDataModel<IDocument>(
				"/profile/user_profile");
		add(new DocumentFieldLabel("agentFirstName", profileDocModel,
				"firstName"));
		add(new DocumentFieldLabel("agentLastName", profileDocModel, "lastName"));
		add(new Link<Void>("logout_link") {

			@Override
			public void onClick() {
				getSession().invalidate();
				setResponsePage(MainPage.class);
			}
		});

		initPage();
	}

	protected void initPage() throws Exception {
	}

	private class LinkContainer<T extends IEntity> extends
			EntityMarkupContainer<T> {

		private EntityLink<T> entityLink;

		public LinkContainer(String id, EntityLink<T> entityLink) {
			super(id, null);
			this.entityLink = entityLink;
		}

		protected void initializeContainer() {
			add(entityLink);

			add(new AttributeModifier("class",
					new AbstractReadOnlyModel<String>() {

						@Override
						public String getObject() {
							return (entityLink.isIdenticDestination()) ? "active"
									: null;
						}
					}));
		}

	}
}