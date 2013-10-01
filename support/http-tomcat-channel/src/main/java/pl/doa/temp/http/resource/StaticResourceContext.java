package pl.doa.temp.http.resource;

import java.util.List;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.naming.NamingEntry;
import org.apache.naming.resources.BaseDirContext;

import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.resource.IStaticResource;

public class StaticResourceContext extends BaseDirContext {

	private final IDOA doa;
	private String baseContainer;
	private boolean isCacheEnabled = true;

	public StaticResourceContext(IDOA doa, IDocument applicationDocument) {
		this.doa = doa;
		IEntitiesContainer applicationContainer = applicationDocument
				.getContainer();
		this.baseContainer = applicationContainer.getLocation();
		this.isCacheEnabled = (Boolean) applicationDocument.getFieldValue(
				"cacheEnabled", true);
	}

	@Override
	public void unbind(String name) throws NamingException {
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		return null;
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		return null;
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		return null;
	}

	@Override
	public void modifyAttributes(String name, int mod_op, Attributes attrs)
			throws NamingException {
	}

	@Override
	public void modifyAttributes(String name, ModificationItem[] mods)
			throws NamingException {
	}

	@Override
	public void bind(String name, Object obj, Attributes attrs)
			throws NamingException {
	}

	@Override
	public void rebind(String name, Object obj, Attributes attrs)
			throws NamingException {
	}

	@Override
	public DirContext createSubcontext(String name, Attributes attrs)
			throws NamingException {
		return null;
	}

	@Override
	public DirContext getSchema(String name) throws NamingException {
		return null;
	}

	@Override
	public DirContext getSchemaClassDefinition(String name)
			throws NamingException {
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes matchingAttributes, String[] attributesToReturn)
			throws NamingException {
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes matchingAttributes) throws NamingException {
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, String filter,
			SearchControls cons) throws NamingException {
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			String filterExpr, Object[] filterArgs, SearchControls cons)
			throws NamingException {
		return null;
	}

	@Override
	protected Attributes doGetAttributes(String name, String[] attrIds)
			throws NamingException {
		IEntitiesContainer baseContainer = (IEntitiesContainer) doa
				.lookupEntityByLocation(this.baseContainer);
		if (baseContainer == null)
			return null;
		IEntity entity = baseContainer.lookupEntityByLocation(name);
		if (!(entity instanceof IStaticResource)) {
			return null;
		}
		IStaticResource resource = (IStaticResource) entity;
		return new StaticResourceAttributes(resource, doa);
	}

	@Override
	protected Object doLookup(String name) {
		IEntitiesContainer baseContainer = (IEntitiesContainer) doa
				.lookupEntityByLocation(this.baseContainer);
		if (baseContainer == null)
			return null;
		IEntity entity = baseContainer.lookupEntityByLocation(name);
		if (!(entity instanceof IStaticResource)) {
			return null;
		}
		IStaticResource resource = (IStaticResource) entity;
		return new StaticResource(resource, doa);
	}

	@Override
	protected List<NamingEntry> doListBindings(String name)
			throws NamingException {
		return null;
	}

	@Override
	protected String doGetRealPath(String name) {
		return null;
	}

	@Override
	public boolean isCached() {
		return this.isCacheEnabled;
	}

}
