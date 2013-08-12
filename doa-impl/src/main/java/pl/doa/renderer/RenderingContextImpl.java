/**
 *
 */
package pl.doa.renderer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author activey
 */
public class RenderingContextImpl extends HashMap<String, Object> implements
        IRenderingContext {

    @Override
    public void setVariable(String varName, Object varValue) {
        put(varName, varValue);
    }

    @Override
    public void setVariables(Map<String, Object> rendererContextVariables) {
        putAll(rendererContextVariables);
    }

    @Override
    public Object getVariable(String varName, Object defaultValue) {
        Object object = get(varName);
        if (object == null) {
            return defaultValue;
        }
        return object;
    }

    @Override
    public Object getVariable(String varName) {
        return getVariable(varName, null);
    }

    @Override
    public Object removeVariable(String varName) {
        return remove(varName);
    }

    @Override
    public Iterator<String> getVariablesNames() {
        return keySet().iterator();
    }

}
