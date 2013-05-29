/**
 * @(#)InfoParser.java, 2013-5-28. Copyright 2013 RenRen, Inc. All rights
 *                      reserved.
 */
package com.renren.traceview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yanyan
 */
public class InfoParser {
    private Map<Long, Stack<Call>> callStacks;

    private static Log LOGGER = LogFactory.getLog(InfoParser.class);

    private List<Call> complateCalls;

    public InfoParser() {
        callStacks = new HashMap<Long, Stack<Call>>();
        complateCalls = new ArrayList<Call>();
    }

    public void parse(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                try {
                    long gid = getGid(line);
                    if (gid < 0) {
                        continue;
                    }
                    Stack<Call> stack = callStacks.get(gid);
                    if (stack == null) {
                        stack = new Stack<Call>();
                        callStacks.put(gid, stack);
                    }
                    if (isStart(line)) {
                        Call call = parseStart(line);
                        call.setLevel(0);
                        call.setTraceId(gid);
                        stack.push(call);
                    } else if (isEnd(line)) {
                        if (stack.isEmpty()) {
                            LOGGER.debug("Met empy stack. This is end line.");
                            continue;
                        }
                        Call call = stack.pop();
                        parseEnd(line, call);
                        complateCalls.add(call);
                    } else if (isCall(line)) {
                        Call call = parseCallLine(line);
                        call.setTraceId(gid);
                        if (stack.isEmpty()) {
                            LOGGER.debug("Met empty stack. This is call line.");
                            
                            continue;
                        }
                        Call parentCall = stack.peek();
                        call.setLevel(parentCall.getLevel() + 1);
                        parentCall.addSubCall(call);
                        stack.push(call);
                    } else if (isEndCall(line)) {
                        if (stack.isEmpty()) {
                            LOGGER.debug("Met empy stack. This is end call line.");
                            continue;
                        }
                        Call call = stack.peek();
                        parseEndCallLine(line, call);
                        stack.pop();
                    } else {
                        LOGGER.error("Invalid line. Dose not match to any type."
                                + line);
                    }
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Met error when parsing line.", e);
                    continue;
                }
            }
        } catch (IOException e) {
            LOGGER.error("IO ERROR.", e);
        } finally {
            try {
                br.close();
            } catch (IOException e1) {
                // ignore
            }
        }
    }

    public List<Call> getCompleteCalls() {
        return this.complateCalls;
    }

    public Long getGid(String line) {
        int pos = line.indexOf(SymbolTable.SEP);
        if (pos < 0) {
            throw new IllegalArgumentException(
                    "Invalid line. Not contain the gid." + line);
        }
        pos += SymbolTable.SEP.length();
        int endPos = line.indexOf(SymbolTable.SEP, pos);
        if (endPos < 0) {
            endPos = line.length();
        }
        if (endPos - pos < 1) {
            throw new IllegalArgumentException(
                    "Invalid line. Not contain the gid." + line);
        }
        try {
            return Long.parseLong(line.substring(pos, endPos));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid line. Not contain the gid.", e);
        }
    }

    public boolean isStart(String line) {
        if (line.startsWith(SymbolTable.START)) {
            return true;
        }
        return false;
    }

    public boolean isEnd(String line) {

        if (line.startsWith(SymbolTable.END)) {
            return true;
        }
        return false;
    }

    public boolean isCall(String line) {
        if (line.startsWith(SymbolTable.START_CALL)) {
            return true;
        }
        return false;
    }

    public boolean isEndCall(String line) {
        if (line.startsWith(SymbolTable.END_CALL)) {
            return true;
        }
        return false;
    }

    private Call parseStart(String line) {
        String[] parts = Utils.split(line, SymbolTable.SEP, 4);
        try {
            Call call = new Call();
            call.setCaller(null);
            call.setLine(-1);
            call.setStartTime(Long.parseLong(parts[0].substring(1)));
            call.setInvoked(new Method(transferClassFormat(parts[2]), parts[3]));
            return call;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid start line:" + line, e);
        }
    }

    private Call parseEnd(String line, Call call) {
        String[] parts = Utils.split(line, SymbolTable.SEP, 2);
        try {
            call.setEndTime(Long.parseLong(parts[0].substring(1)));
            return call;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid end line:" + line, e);
        }
    }

    private String transferClassFormat(String str) {
        char[] charAy = str.toCharArray();
        for (int i = 0; i < charAy.length; i++) {
            if (charAy[i] == '/') {
                charAy[i] = '.';
            }
        }
        return new String(charAy);
    }

    private Call parseEndCallLine(String line, Call call) {
        String[] parts = Utils.split(line, SymbolTable.SEP, 6);
        try {

            Method caller = new Method(transferClassFormat(parts[2]), parts[3]);
            int lineNum = Integer.parseInt(parts[4]);
            String targetMethod = parts[5];

            // In root call,caller is null. using caller.equals() should avoid
            // this problem.
            if (!caller.equals(call.getCaller()) || (call.getLine() != lineNum)) {
                throw new IllegalArgumentException(
                        "End call line is not match start call line."
                                + call.getCaller() + ":" + caller + " call "
                                + call.getInvoked().getClassName() + "#"
                                + targetMethod);
            }

            call.setEndTime(Long.parseLong(parts[0].substring(1)));
            call.setCaller(caller);
            call.setLine(lineNum);
            call.getInvoked().setMethodName(targetMethod);
            return call;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid call line:" + line, e);
        }
    }

    private Call parseCallLine(String line) {
        String[] parts = Utils.split(line, SymbolTable.SEP, 6);
        try {
            Call call = new Call();
            call.setStartTime(Long.parseLong(parts[0].substring(1)));
            call.setCaller(new Method(transferClassFormat(parts[2]), parts[3]));
            call.setLine(Integer.parseInt(parts[4]));
            call.setInvoked(new Method(transferClassFormat(parts[5]), ""));
            return call;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid end call line:" + line,
                    e);
        }
    }

}
