/**
 * 
 */
package pl.doa.wicket.ui.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;

/**
 * @author activey
 * 
 */
public class TabsModel implements IModel<List<ITab>> {

	private static final long serialVersionUID = 1L;
	
	private List<ITab> tabs = new ArrayList<ITab>();

	@Override
	public void detach() {
	}

	@Override
	public List<ITab> getObject() {
		return this.tabs;
	}

	@Override
	public void setObject(List<ITab> object) {
		this.tabs = object;
	}
	
	public TabsModel addTab(EntityPanelTab<IEntity> tab) {
		tabs.add(tab);
		return this;
	}
}