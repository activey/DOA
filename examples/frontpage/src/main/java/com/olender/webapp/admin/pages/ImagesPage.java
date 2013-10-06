/**
 * 
 */
package com.olender.webapp.admin.pages;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.ui.link.EntityAjaxLink;
import pl.doa.wicket.ui.resource.StaticResourceReference;
import pl.doa.wicket.ui.window.EntityWindow;

import com.olender.webapp.admin.BasePage;
import com.olender.webapp.admin.pages.images.CreateImagesFolderWindow;
import com.olender.webapp.admin.pages.images.ImagesContainerPanel;
import com.olender.webapp.admin.pages.images.MoveResourceWindow;
import com.olender.webapp.admin.pages.images.UploadImageForm;
import com.olender.webapp.components.ajax.ConfirmListener;
import com.olender.webapp.components.ajax.FormCallListener;

/**
 * @author activey
 * 
 */
public class ImagesPage extends BasePage<IEntitiesContainer> {

	public ImagesPage(PageParameters parameters) {
		super(parameters);
	}

	public ImagesPage() {
		super("/images/application");
	}

	@Override
	protected void initPage() throws Exception {
		CallServiceForm uploadForm = new UploadImageForm("form_upload",
				getModel()) {

			@Override
			protected void handleRunning(IRunningService running,
					AjaxRequestTarget target) {
				target.add(ImagesPage.this.get("container_images"));
			}

		};
		add(uploadForm);

		WebMarkupContainer listContainer = new WebMarkupContainer(
				"container_images");
		listContainer.setOutputMarkupId(true);

		add(new ImagesContainerPanel("panel_current_container", getModel()));
		EntityWindow<IEntitiesContainer> createWindow = new CreateImagesFolderWindow(
				"window", getModel());
		createWindow.setWindowClosedCallback(new WindowClosedCallback() {

			public void onClose(AjaxRequestTarget target) {
				target.add(ImagesPage.this.get("panel_current_container"));
			}
		});
		add(createWindow);
		add(createWindow.showWindowLink("link_folder_create"));

		DataView<IStaticResource> images = new DataView<IStaticResource>(
				"placeholder_image",
				new ContainerEntitiesProvider<IStaticResource>(getModel(),
						new IEntityEvaluator() {

							public boolean isReturnableEntity(
									IEntity currentEntity) {
								return currentEntity instanceof IStaticResource;
							}
						}, false)) {
			@Override
			protected void populateItem(Item<IStaticResource> item) {
				final IStaticResource res = item.getModelObject();
				item.add(new Label("resource_name", res.getName()));

				item.add(new Image("resource_thumbnail",
						new StaticResourceReference(item.getModel())));

				item.add(new EntityAjaxLink<IStaticResource>("remove_link",
						item.getModel(), true) {

					@Override
					public void onClick(IModel<IStaticResource> resource,
							AjaxRequestTarget target) {
						resource.getObject().remove(true);
						target.add(getPage().get("container_images"));
					}

					@Override
					protected void populateListeners(
							List<IAjaxCallListener> listeners) {
						listeners.add(new ConfirmListener());
						listeners.add(new FormCallListener());
					}
				});

				EntityWindow<IStaticResource> moveWindow = new MoveResourceWindow(
						"move_window", item.getModel(), new Model(
								"Wskaż folder docelowy")) {
					@Override
					protected void resourceMoved(AjaxRequestTarget target) {
						target.add(getPage().get("container_images"));
					}
				};
				item.add(moveWindow);
				item.add(moveWindow.showWindowLink("show_move_window"));
			}
		};
		listContainer.add(images);
		add(listContainer);

	}
}