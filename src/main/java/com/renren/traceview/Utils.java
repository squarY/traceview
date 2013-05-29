/**
 * @(#)Utils.java, 2013-5-28. Copyright 2013 RenRen, Inc. All rights reserved.
 */
package com.renren.traceview;

import org.apache.commons.lang.StringUtils;

/**
 * @author yanyan
 */
public class Utils {
    public static void checkStr(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException(
                    "Str should not be null or empty.");
        }
    }

    public static String[] split(String str, String sep, int expectedPartNum) {
        checkStr(str);
        String[] parts = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                str, sep);
        if (parts.length != expectedPartNum) {
            throw new IllegalArgumentException("The parts of String should be:"
                    + expectedPartNum + ", but is:" + parts.length + ". Str:"
                    + str);
        }

        return parts;
    }
}
