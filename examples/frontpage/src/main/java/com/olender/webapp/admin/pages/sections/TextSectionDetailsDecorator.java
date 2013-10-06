package com.olender.webapp.admin.pages.sections;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.wicket.decorator.IEntityTabBuilder;
import pl.doa.wicket.decorator.impl.TabbedEntityDecorator;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.tabs.TabbedPanel;

import com.olender.webapp.admin.pages.sections.common.BasicSectionDataPanel;
import com.olender.webapp.admin.pages.sections.common.SectionSubsectionsPanel;
import com.olender.webapp.admin.pages.sections.text.SectionLinksPanel;
import com.olender.webapp.admin.pages.sections.text.TextSectionDataPanel;
import com.olender.webapp.components.OlenderTabbedPanel;

public class TextSectionDetailsDecorator extends
		TabbedEntityDecorator<IDocument> {

	@Override
	protected void collectTabs(IModel<IDocument> model) throws Exception {
		createTab(model, new IEntityTabBuilder<IDocument>() {

			public String getTabLabel(IModel<IDocument> entity) {
				return "Dane podstawowe";
			}

			public EntityPanel<IDocument> buildTabPanel(
					IModel<IDocument> entity, String panelId) {
				return new BasicSectionDataPanel(panelId, entity);
			}
		});

		createTab(model, new IEntityTabBuilder<IDocument>() {

			public String getTabLabel(IModel<IDocument> entity) {
				return "Podsekcje";
			}

			public EntityPanel<IDocument> buildTabPanel(
					IModel<IDocument> entity, String panelId) {
				return new SectionSubsectionsPanel(panelId, entity);
			}
		});

		createTab(model, new IEntityTabBuilder<IDocument>() {

			public String getTabLabel(IModel<IDocument> entity) {
				return "Tekst";
			}

			public EntityPanel<IDocument> buildTabPanel(
					IModel<IDocument> entity, String panelId) {
				return new TextSectionDataPanel(panelId, entity);
			}
		});

		createTab(model, new IEntityTabBuilder<IDocument>() {

			public String getTabLabel(IModel<IDocument> entity) {
				return "Linki";
			}

			public EntityPanel<IDocument> buildTabPanel(
					IModel<IDocument> entity, String panelId) {
				return new SectionLinksPanel(panelId, entity);
			}
		});

	}

	@Override
	protected TabbedPanel createTabbedPanel(String componentId,
			List<ITab> collectedTabs, IModel<IDocument> model) {
		return new OlenderTabbedPanel(componentId, collectedTabs);
	}

}
