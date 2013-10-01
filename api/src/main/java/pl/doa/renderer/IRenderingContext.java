package pl.doa.renderer;

import java.util.Iterator;
import java.util.Map;

public interface IRenderingContext {

	public void setVariable(String varName, Object varValue);

	public void setVariables(Map<String, Object> rendererContextVariables);

	public Iterator<String> getVariablesNames();
	
	public Object getVariable(String varName, Object defaultValue);

	public Object getVariable(String varName);

	public Object removeVariable(String varName);

}
