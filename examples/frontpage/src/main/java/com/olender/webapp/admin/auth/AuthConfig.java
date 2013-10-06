/**
 * 
 */
package com.olender.webapp.admin.auth;

import pl.doa.wicket.auth.IAuthConfig;

/**
 * @author dkardanski
 *
 */
public class AuthConfig implements IAuthConfig {

	private final String serviceLocation = "/services/authentication/login";
	
	public String getAuthorizationServiceLocation() {
		return serviceLocation;
	}

	public String getLoginFieldName() {
		return "login";
	}

	public String getPasswordFieldName() {
		return "password";
	}

	@Override
	public String getAgentLookupLocation() {
		return "/agents";
	}

}
