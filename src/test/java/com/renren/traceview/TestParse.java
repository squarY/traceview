/**
 * @(#)TestParse.java, 2013-5-28. Copyright 2013 RenRen, Inc. All rights
 *                     reserved.
 */
package com.renren.traceview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

/**
 * @author yanyan
 */
public class TestParse {
    private static String replacePlaceHolder(String line, String placeHolder,
            String value) {
        String result = "";
        int placeIndex = line.indexOf(placeHolder);
        if (placeIndex > 0) {
            result = line.substring(0, placeIndex) + value
                    + line.substring(placeIndex + placeHolder.length());
            return result;
        } else {
            return line;
        }

    }

    public static void main(String[] args) throws Exception {
        String logPath = args[0];
        String templatePath = args[1];
        String htmlPath = args[2];
        long start = System.currentTimeMillis();
        InfoParser parser = new InfoParser();
        FileInputStream fis = new FileInputStream(new File(logPath));
        parser.parse(fis);
        fis.close();
        List<Call> calls = parser.getCompleteCalls();
        long maxCostTime = -1;
        long minSTime = Long.MAX_VALUE;
        String jsonStr = "";
        boolean isFirst = true;
        for (Call c: calls) {
            if (isFirst) {
                isFirst = false;
            } else {
                jsonStr += ",";
            }
            long time = c.getEndTime() - c.getStartTime();
            if (time > maxCostTime) {
                maxCostTime = time;
            }
            if (c.getStartTime() < minSTime) {
                minSTime = c.getStartTime();
            }
            jsonStr += c.toJsonString();
        }
        BufferedReader br = new BufferedReader(new FileReader(new File(
                templatePath)));
        BufferedWriter bw = new BufferedWriter(new FileWriter(
                new File(htmlPath)));
        String line;
        while ((line = br.readLine()) != null) {
            line = replacePlaceHolder(line, "$callData$", jsonStr);
            line = replacePlaceHolder(line, "$maxTime$",
                    String.valueOf(maxCostTime));
            line = replacePlaceHolder(line, "$minSTime$",
                    String.valueOf(minSTime));
            bw.write(line);
            bw.newLine();
        }
        br.close();
        bw.close();
        System.out.println("done:" + (System.currentTimeMillis() - start)
                + "ms");
    }
}
