/**
 * 
 */
package com.olender.webapp.behavior.impl.wysywig;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;

import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.ui.resource.StaticResourceReference;

import com.olender.webapp.behavior.JQueryBehavior;

/**
 * @author activey
 * 
 */
public class WysywigEditorBehavior extends JQueryBehavior {

	public WysywigEditorBehavior() {
		super("wysihtml5");
	}

	@Override
	protected void renderResources(IHeaderResponse resources) {
		resources.render(new CssReferenceHeaderItem(
				new StaticResourceReference(new EntityModel<IStaticResource>(
						"/admin/css/bootstrap-wysihtml5.css")), null, "screen",
				null));
		resources.render(new CssReferenceHeaderItem(
				new StaticResourceReference(new EntityModel<IStaticResource>(
						"/common/css/wysihtml5-formatting.css")), null,
				"screen", null));

		resources.render(new JavaScriptReferenceHeaderItem(
				new StaticResourceReference(new EntityModel<IStaticResource>(
						"/admin/js/wysihtml5-0.3.0.min.js")), null,
				"wysihtml5", true, "UTF-8", null));
		resources.render(new JavaScriptReferenceHeaderItem(
				new StaticResourceReference(new EntityModel<IStaticResource>(
						"/admin/js/bootstrap-wysihtml5.js")), null,
				"bootstrap-wysihtml5", true, "UTF-8", null));
	}

}
