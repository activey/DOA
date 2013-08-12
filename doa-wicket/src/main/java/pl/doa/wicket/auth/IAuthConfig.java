/**
 * 
 */
package pl.doa.wicket.auth;


/**
 * @author dkardanski
 *
 */
public interface IAuthConfig {
	
	public String getAuthorizationServiceLocation();
	
	public String getAgentLookupLocation();
	
	public String getLoginFieldName();
	
	public String getPasswordFieldName();
}
