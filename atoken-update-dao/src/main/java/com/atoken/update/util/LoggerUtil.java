package com.atoken.update.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * @author dfg
 */
public class LoggerUtil {
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";
    private static final String CAUSE_CAPTION = "Caused by: ";

    public static String parseErrorStacks(Throwable t) {
        StringBuilder result = new StringBuilder();
        try {
            Set<Throwable> dejaVu =
                    Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
            dejaVu.add(t);
            result.append(t);
            Arrays.stream(t.getStackTrace()).forEach(stackTraceElement -> result
                    .append("\n\tat ").append(stackTraceElement));

            Arrays.stream(t.getSuppressed()).forEach(suppressed -> parseEnclosedStackTrace(result, suppressed, t.getStackTrace(), SUPPRESSED_CAPTION, "\t", dejaVu));

            // Print cause, if any
            Throwable ourCause = t.getCause();
            if (ourCause != null) {
                parseEnclosedStackTrace(result, ourCause, t.getStackTrace(), CAUSE_CAPTION, "", dejaVu);
            }
        } catch (Throwable e) {}
        return result.toString();
    }

    private static void parseEnclosedStackTrace(StringBuilder result,
                                                Throwable t,
                                                StackTraceElement[] enclosingTrace,
                                                String caption,
                                                String prefix,
                                                Set<Throwable> dejaVu) {
        if (dejaVu.contains(t)) {
            result.append("\n\t[CIRCULAR REFERENCE:").append(t).append("]");
        } else {
            dejaVu.add(t);
            // Compute number of frames in common between this and enclosing trace
            StackTraceElement[] trace = t.getStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >=0 && trace[m].equals(enclosingTrace[n])) {
                m--; n--;
            }
            int framesInCommon = trace.length - 1 - m;

            // Print our stack trace
            result.append("\n").append(prefix).append(caption).append(t);

            Arrays.stream(trace).forEach(item -> result.append("\n").append(prefix).append("\tat ").append(item));
            if (framesInCommon != 0) {
                result.append("\n").append(prefix).append("\t... ").append("framesInCommon").append(" more");
            }

            // Print suppressed exceptions, if any
            Arrays.stream(t.getSuppressed()).forEach(suppressed -> parseEnclosedStackTrace(result
                    , suppressed, suppressed.getStackTrace(), SUPPRESSED_CAPTION, prefix +"\t", dejaVu));

            // Print cause, if any
            Throwable ourCause = t.getCause();
            if (ourCause != null) {
                parseEnclosedStackTrace(result, ourCause, trace, CAUSE_CAPTION, prefix, dejaVu);
            }
        }
    }
}
