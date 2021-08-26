package es.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuqiang on 2016/11/4.
 */
public class StringTools {
    public static String escapeHtml(String text) {
        if (text == null) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '&') {
                sb.append("&amp;");
            } else if (c == '\'') {
                sb.append("&#39;");
            } else if (c == '"') {
                sb.append("&quot;");
            } else if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 生成exception 信息
     *
     * @param e
     * @return
     */
    public static String buildExceptionMessage(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }

        return sw.toString();
    }

    /**
     * 地区名称标准化
     *
     * @param areaName
     * @return
     */
    public static String normalizeArea(String areaName) {
        if (areaName == null || areaName.length() == 0) {
            return areaName;
        }

        String newName = areaName;
        if (newName.endsWith("省") || newName.endsWith("市")) {
            newName = newName.substring(0, newName.length() - 1);
        } else if (newName.indexOf("新疆") > -1) {
            newName = "新疆";
        } else if (newName.indexOf("宁夏") > -1) {
            newName = "宁夏";
        } else if (newName.indexOf("广西") > -1) {
            newName = "广西";
        } else if (newName.indexOf("内蒙古") > -1) {
            newName = "内蒙古";
        } else if (newName.indexOf("香港") > -1) {
            newName = "香港";
        } else if (newName.indexOf("澳门") > -1) {
            newName = "澳门";
        } else if (newName.indexOf("西藏") > -1) {
            newName = "西藏";
        }
        return newName;
    }

    public static String[] reFind(String paramString1, String paramString2) {
        return reFind(Pattern.compile(paramString1), paramString2);
    }

    public static String[] reFind(Matcher paramMatcher) {
        int i = paramMatcher.groupCount();
        String[] arrayOfString;
        if (!paramMatcher.find()) {
            arrayOfString = null;
        }

        arrayOfString = new String[i + 1];
        for (int j = 0; j < i + 1; j++) {
            arrayOfString[j] = paramMatcher.group(j);
        }
        return arrayOfString;
    }

    public static String[] reFind(Pattern paramPattern, String paramString) {
        return reFind(paramPattern.matcher(paramString));
    }

    public static List<String[]> reFindAll(String src, String regex) {
        Matcher localMatcher = Pattern.compile(src).matcher(regex);
        ArrayList<String[]> localArrayList = new ArrayList<String[]>();
        int i = localMatcher.groupCount();
        if (!localMatcher.find()) {
            return localArrayList;
        }

        String[] arrayOfString = new String[i + 1];
        for (int j = 0; ; j++) {
            if (j >= i + 1) {
                localArrayList.add(arrayOfString);
                break;
            }
            arrayOfString[j] = localMatcher.group(j);
        }

        return localArrayList;
    }

    public static String[] reOptFind(String pattern, String src) {
        return reOptFind(Pattern.compile(pattern), src);
    }

    public static String[] reOptFind(Matcher paramMatcher) {
        int i = paramMatcher.groupCount();
        String[] arrayOfString = new String[i + 1];
        boolean bool = paramMatcher.find();
        int j = 0;
        if (!bool) {
            for (; ; ) {
                if (j >= i + 1) {
                    return arrayOfString;
                }
                arrayOfString[j] = "";
                j++;
            }
        }
        while (j < i + 1) {
            arrayOfString[j] = paramMatcher.group(j);
            j++;
        }
        return arrayOfString;
    }

    public static String[] reOptFind(Pattern paramPattern, String paramString) {
        return reOptFind(paramPattern.matcher(paramString));
    }
}
