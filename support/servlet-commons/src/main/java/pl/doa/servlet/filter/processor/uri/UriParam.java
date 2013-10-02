package pl.doa.servlet.filter.processor.uri;

public class UriParam {
    private final String paramName;
    private String paramValue;

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public UriParam(String paramName, String paramValue) {
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    public UriParam(String paramName) {
        this(paramName, null);
    }

    public String getParamValue() {
        return paramValue;
    }

    public String getParamName() {
        return paramName;
    }
}