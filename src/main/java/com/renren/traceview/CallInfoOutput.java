/**
 * @(#)CallInfoOutput.java, 2013-5-27. Copyright 2013 RenRen, Inc. All rights
 *                          reserved.
 */
package com.renren.traceview;

import static com.sun.btrace.BTraceUtils.*;

import java.util.concurrent.atomic.AtomicLong;

import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.ProbeClassName;
import com.sun.btrace.annotations.ProbeMethodName;
import com.sun.btrace.annotations.TargetInstance;
import com.sun.btrace.annotations.TLS;
import com.sun.btrace.annotations.TargetMethodOrField;
import com.sun.btrace.annotations.Where;

/**
 * @author yanyan
 */
@BTrace
public class CallInfoOutput {
    @TLS
    private static long gid = -1;

    private static AtomicLong idGen = newAtomicLong(0l);

    // com.renren.traceview.TestApplication#test
    @OnMethod(clazz = "<1>", method = "<2>", location = @Location(value = Kind.ENTRY))
    public static void onMethodStart(@ProbeClassName String probeClass,
            @ProbeMethodName String probeMethod) {
        gid = getAndIncrement(idGen);
        Appendable sb = Strings.newStringBuilder();
        Strings.append(sb, "S");
        Strings.append(sb, Strings.str(Time.millis()));
        Strings.append(sb, "-");
        Strings.append(sb, Strings.str(gid));
        Strings.append(sb, "-");
        Strings.append(sb, probeClass);
        Strings.append(sb, "-");
        Strings.append(sb, probeMethod);
        println(Strings.str(sb));
    }

    @OnMethod(clazz = "<1>", method = "<2>", location = @Location(value = Kind.RETURN))
    public static void onMethodEnd(@ProbeClassName String probeClass,
            @ProbeMethodName String probeMethod) {
        Appendable sb = Strings.newStringBuilder();
        Strings.append(sb, "E");
        Strings.append(sb, Strings.str(Time.millis()));
        Strings.append(sb, "-");
        Strings.append(sb, Strings.str(gid));
        gid = -1;
        println(Strings.str(sb));
    }

    // /com\\.renren.*/ /.*/
    @OnMethod(clazz = "<3>", method = "<4>", location = @Location(value = Kind.CALL, clazz = "/.*/", method = "/.*/"))
    public static void before(@ProbeClassName String probeClass,
            @ProbeMethodName String probeMethod,@TargetInstance Object target) {
        if (gid == -1) {
            return;
        }
        Appendable sb = Strings.newStringBuilder();
        Strings.append(sb, "C");
        Strings.append(sb, Strings.str(Time.millis()));
        Strings.append(sb, "-");
        Strings.append(sb, Strings.str(gid));
        Strings.append(sb, "-");
        Strings.append(sb, probeClass);
        Strings.append(sb, "-");
        Strings.append(sb, probeMethod);
        Strings.append(sb, "-");
        Strings.append(sb, Strings.str(probeLine()));
        Strings.append(sb, "-");
        Strings.append(sb, name(classOf(target)));
        println(Strings.str(sb));
    }

    @OnMethod(clazz = "<3>", method = "<4>", location = @Location(value = Kind.CALL, clazz = "/.*/", method = "/.*/", where = Where.AFTER))
    public static void after(@ProbeClassName String probeClass,
            @ProbeMethodName String probeMethod, 
            @TargetMethodOrField String targetMethod) {
        if (gid == -1) {
            return;
        }
        Appendable sb = Strings.newStringBuilder();
        Strings.append(sb, "F");
        Strings.append(sb, Strings.str(Time.millis()));
        Strings.append(sb, "-");
        Strings.append(sb, Strings.str(gid));
        Strings.append(sb, "-");
        Strings.append(sb, probeClass);
        Strings.append(sb, "-");
        Strings.append(sb, probeMethod);
        Strings.append(sb, "-");
        Strings.append(sb, Strings.str(probeLine()));
        Strings.append(sb, "-");
        Strings.append(sb, targetMethod);
        println(Strings.str(sb));

    }

}
