/**
 * @(#)Call.java, 2013-5-28. Copyright 2013 RenRen, Inc. All rights reserved.
 */
package com.renren.traceview;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author yanyan
 */
public class Call {
    private long traceId;

    private int level;

    private Method caller;

    private Method invoked;

    private int line;

    private long startTime;

    private long endTime;

    private SortedMap<Integer, List<Call>> subCalls;

    public void addSubCall(Call call) {
        if (subCalls == null) {
            this.subCalls = new TreeMap<Integer, List<Call>>();

        }
        List<Call> callList = subCalls.get(call.getLine());
        if (callList == null) {
            callList = new ArrayList<Call>();
            subCalls.put(call.getLine(), callList);
        }
        callList.add(call);
    }

    /**
     * @return the subCalls
     */
    public SortedMap<Integer, List<Call>> getSubCalls() {
        return subCalls;
    }

    /**
     * @return the caller
     */
    public Method getCaller() {
        return caller;
    }

    /**
     * @param caller
     *            the caller to set
     */
    public void setCaller(Method caller) {
        this.caller = caller;
    }

    /**
     * @return the invoked
     */
    public Method getInvoked() {
        return invoked;
    }

    /**
     * @param invoked
     *            the invoked to set
     */
    public void setInvoked(Method invoked) {
        this.invoked = invoked;
    }

    /**
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * @param line
     *            the line to set
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     *            the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the traceId
     */
    public long getTraceId() {
        return traceId;
    }

    /**
     * @param traceId
     *            the traceId to set
     */
    public void setTraceId(long traceId) {
        this.traceId = traceId;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level
     *            the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String str = "Call [traceId=" + traceId + ", caller=" + caller
                + ", invoked=" + invoked + ", line=" + line + ", startTime="
                + startTime + ", endTime=" + endTime + "]";
        if (subCalls != null) {
            for (List<Call> cs: subCalls.values()) {
                for (Call c: cs) {
                    str += "\n";
                    for (int i = 0; i < level + 1; i++) {
                        str += "\t";
                    }
                    str += c.toString();
                }
            }
        }
        return str;
    }

    public String toJsonString() {
        String jsonStr = "{'key':'" + invoked + "','stime':" + startTime
                + ",'etime':" + endTime + ",'line':" + line
                + ",'subCalls':[";
        boolean isFirst = true;
        if (subCalls != null) {
            for (List<Call> cs: subCalls.values()) {
                for (Call c: cs) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        jsonStr += ",";
                    }
                    jsonStr += c.toJsonString();
                }
            }
        }
        jsonStr += "]}";
        return jsonStr;
    }

}
