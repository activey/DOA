/**
 * 
 */
package com.olender.webapp.admin.pages;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.resource.IResourceStream;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.wicket.behavior.AjaxDownloadBehavior;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.ui.link.EntityAjaxLink;
import pl.doa.wicket.ui.resource.StaticResourceStream;

import com.olender.webapp.admin.BasePage;
import com.olender.webapp.admin.pages.backup.BackupForm;
import com.olender.webapp.admin.pages.backup.RestoreForm;
import com.olender.webapp.components.ajax.ConfirmListener;
import com.olender.webapp.components.ajax.FormCallListener;

/**
 * @author activey
 * 
 */
public class BackupPage extends BasePage<IEntitiesContainer> {

	private final static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"EEEE, d MMM yyyy HH:mm:ss");

	public BackupPage() {
		super("/backup");
	}

	@Override
	protected void initPage() throws Exception {
		CallServiceForm backupForm = new BackupForm("form_backup") {

			@Override
			protected void handleRunning(IRunningService running,
					AjaxRequestTarget target) {
				target.add(BackupPage.this.get("archive"));
			}

		};
		add(backupForm);

		WebMarkupContainer archiveContainer = new WebMarkupContainer("archive");
		archiveContainer.setOutputMarkupId(true);

		IEntitiesContainer archivesContainer = getModelObject();
		DataView<IStaticResource> archives = new DataView<IStaticResource>(
				"row_placeholder",
				new ContainerEntitiesProvider<IStaticResource>(
						archivesContainer, true) {
					@Override
					protected IEntitiesSortComparator getSortComparator() {
						return new IEntitiesSortComparator<IEntity>() {

							public int compare(IEntity entity1, IEntity entity2) {
								return entity2.getCreated().compareTo(
										entity1.getCreated());
							}
						};
					}
				}) {
			@Override
			protected void populateItem(final Item<IStaticResource> item) {
				IStaticResource archiveResource = item.getModelObject();
				item.add(new Label("id", archiveResource.getId() + ""));
				item.add(new Label("date", dateFormat.format(archiveResource
						.getCreated())));
				item.add(new Label("size", archiveResource.getContentSize()
						+ ""));

				item.add(new RestoreForm("form_restore", item.getModel()));

				final AjaxDownloadBehavior download = new AjaxDownloadBehavior() {

					@Override
					protected IResourceStream getResourceStream() {
						return new StaticResourceStream(item.getModel());
					}

					@Override
					protected String getFileName() {
						return "backup-" + item.getModelObject().getId()
								+ ".zip";
					}
				};
				item.add(download);
				item.add(new EntityAjaxLink<IStaticResource>(
						"link_download_archive", item.getModel()) {

					@Override
					public void onClick(IModel<IStaticResource> resourceModel,
							AjaxRequestTarget target) {
						download.initiate(target);
					}
				});

				EntityAjaxLink<IStaticResource> removeLink = new EntityAjaxLink<IStaticResource>(
						"link_remove", item.getModel(), true) {

					@Override
					public void onClick(IModel<IStaticResource> resourceModel,
							AjaxRequestTarget target) {
						IStaticResource res = resourceModel.getObject();
						res.remove();
						target.add(getPage().get("archive"));
					}

					@Override
					protected void populateListeners(
							List<IAjaxCallListener> listeners) {
						listeners.add(new ConfirmListener());
						listeners.add(new FormCallListener());
					}
				};
				item.add(removeLink);
			}
		};
		archiveContainer.add(archives);
		add(archiveContainer);

	}
}