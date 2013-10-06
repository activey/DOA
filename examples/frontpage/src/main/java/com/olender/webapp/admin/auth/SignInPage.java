/**
 * 
 */
package com.olender.webapp.admin.auth;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.entity.IEntity;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.ui.page.EntityPage;

public class SignInPage extends EntityPage<IEntity> {

	public SignInPage() {
		super();
	}

	public SignInPage(IEntity entity) {
		super(entity);
	}

	public SignInPage(PageParameters parameters) {
		super(parameters);
	}

	public SignInPage(PathIterator<String> entityPath) {
		super(entityPath);
	}

	public SignInPage(String entityLocation) {
		super(entityLocation);
	}

	@Override
	protected void initEntityPage() throws Exception {
		add(new SignInPanel("signInPanel"));
	}

}
