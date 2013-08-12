package pl.doa.wicket.auth;

public abstract class BasicAuthConfig implements IAuthConfig {

    public String getAgentLookupLocation() {
        return "/agents";
    }
}
