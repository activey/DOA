package com.olender.webapp.decorators;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.wicket.decorator.IEntityDecorator;

public class TextSectionDecorator implements IEntityDecorator<IDocument> {

	public Component decorate(IModel<IDocument> entityModel, String componentId)
			throws Exception {
		return new TextSectionPanel(componentId, entityModel);
	}

}
