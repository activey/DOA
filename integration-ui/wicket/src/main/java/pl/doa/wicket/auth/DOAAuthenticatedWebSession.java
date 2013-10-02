/**
 * 
 */
package pl.doa.wicket.auth;

import java.text.MessageFormat;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.IEntityReference;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.WicketDOAApplication;

public class DOAAuthenticatedWebSession extends AuthenticatedWebSession {

	private final static Logger log = LoggerFactory
			.getLogger(DOAAuthenticatedWebSession.class);

	protected IAgent agent;

	public DOAAuthenticatedWebSession(Request request) {
		super(request);
	}

	public static DOAAuthenticatedWebSession get() {
		return (DOAAuthenticatedWebSession) AbstractAuthenticatedWebSession
				.get();
	}

	/**
	 * Autentykacja z uzyciem okreslonego agenta.
	 * 
	 * @param agent
	 */
	public void authenticate(IAgent agent) {
		if (agent != null) {
			this.agent = agent;
			bind();
		}
	}

	@Override
	/**
	 * Standardowa autentykacja przy pomocy uzytkownika i hasla.
	 */
	public boolean authenticate(String username, String password) {
		IAuthConfig config = WicketDOAApplication.get().getAuthConfig();
		String authServiceLocation = config.getAuthorizationServiceLocation();

		IEntitiesContainer appContainer = WicketDOAApplication.get()
				.getApplicationContainer();
		IEntity entity = appContainer
				.lookupEntityByLocation(authServiceLocation);
		if (entity == null) {
			log.error(MessageFormat
					.format("Unable to find authentication service under location: [{0}]",
							authServiceLocation));
			return false;
		}
		if (!(entity instanceof IServiceDefinition)) {
			log.error(MessageFormat
					.format("Unable to find authentication service under location: [{0}]",
							authServiceLocation));
			return false;
		}
		IServiceDefinition authService = (IServiceDefinition) entity;
		IDocumentDefinition inputDef = authService.getInputDefinition();
		IDocument input;
		try {
			input = inputDef.createDocumentInstance();
		} catch (GeneralDOAException e) {
			log.error("", e);
			return false;
		}

		String loginField = config.getLoginFieldName();
		String passwordField = config.getPasswordFieldName();
		try {
			input.setFieldValue(loginField, username);
			input.setFieldValue(passwordField, password);
			IRunningService executed = authService.executeService(input, false);
			IAgent agent = executed.getAgent();
			if (agent != null) {
				this.agent = agent;
				bind();
				return true;
			}
		} catch (GeneralDOAException e) {
			log.error("", e);
			return false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession
	 * #getRoles()
	 */
	@Override
	public Roles getRoles() {
		if (isSignedIn()) {
			IEntitiesContainer agentsContainer = (IEntitiesContainer) WicketDOAApplication
					.get()
					.getDoa()
					.lookupEntityByLocation(
							WicketDOAApplication.get().getAuthConfig()
									.getAgentLookupLocation());
			IEntitiesContainer rolesContainer = (IEntitiesContainer) agentsContainer
					.lookupEntityByLocation("/security/roles");
			if (rolesContainer == null) {
				return null;
			}
			Roles roles = new Roles();
			Iterable<IEntity> rolesContainers = rolesContainer
					.lookupForEntities(new IEntityEvaluator() {

						@Override
						public boolean isReturnableEntity(IEntity currentEntity) {
							if (!(currentEntity instanceof IEntitiesContainer)) {
								return false;
							}
							IEntitiesContainer roleContainer = (IEntitiesContainer) currentEntity;
							IEntityReference roleRef = (IEntityReference) roleContainer
									.lookupForEntity(new IEntityEvaluator() {

										@Override
										public boolean isReturnableEntity(
												IEntity currentEntity) {
											if (!(currentEntity instanceof IEntityReference)) {
												return false;
											}
											IEntityReference roleReference = (IEntityReference) currentEntity;
											IEntity roleEntity = roleReference
													.getEntity();
											return roleEntity.equals(agent);
										}
									}, false);

							return roleRef != null;
						}
					}, true);

			for (IEntity roleContainer : rolesContainers) {
				roles.add(roleContainer.getName());
			}
			return roles;
		}
		return null;
	}

	/**
	 * @return the agent
	 */
	public IAgent getAgent() {
		return agent;
	}

	public IAgent getAgent(IDocument fingerprint) throws GeneralDOAException {
		if (agent != null) {
			return agent;
		}
		IAuthConfig config = WicketDOAApplication.get().getAuthConfig();
		if (config == null) {
			return null;
		}
		return WicketDOAApplication.get().getDoa()
				.profileAgent(fingerprint, config.getAgentLookupLocation());
	}
}
