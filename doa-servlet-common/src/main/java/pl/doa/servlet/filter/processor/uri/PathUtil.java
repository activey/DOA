package pl.doa.servlet.filter.processor.uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright 2013 XSolve.
 */

/**
 * The <code>PathUtil</code> represents ... TODO
 *
 * @author Piotr Wyczolkowski
 */
public class PathUtil {

    private static final String PARAM_PATTERN = "\\{(.*?)\\}";
    private static final String URI_PARSE_PATTERN = "\\{.*?\\}";

    public static final Map<String, String> getPathVariables(String patternStr,
                                                             String path) {
        String[] params = gatherParameters(patternStr).toArray(new String[0]);
        return getPathVariables(patternStr, params, path);
    }

    private static final Map<String, String> getPathVariables(
            String patternStr, String[] names, String path) {
        String regExPattern = patternStr.replaceAll(URI_PARSE_PATTERN, "(.*)");
        Map<String, String> tokenMap = new HashMap<String, String>();
        Pattern p = Pattern.compile(regExPattern);
        Matcher matcher = p.matcher(path);

        if (matcher.find()) {
            // Get all groups for this match
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String groupStr = matcher.group(i);
                tokenMap.put(names[i - 1], groupStr);
            }
        }
        return tokenMap;
    }

    public static final boolean matches(String inputUri, String patternStr) {
        String regExPattern = patternStr.replaceAll(URI_PARSE_PATTERN, "(.*)");
        return Pattern.matches(regExPattern, inputUri);
    }

    private static List<String> gatherParameters(String pathTemplate) {
        Pattern paramPattern = Pattern.compile(PARAM_PATTERN);
        Matcher matcher = paramPattern.matcher(pathTemplate);

        List<String> params = new ArrayList<String>();
        while (matcher.find()) {
            String param = matcher.group(1);
            params.add(param);
        }
        return params;
    }
}
