package com.trading.backend.util;

import com.trading.backend.http.ContextHeader;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ~~ trading.s
 * @date 23:57 10/03/21
 */
@Slf4j
public final class ContextHolder {

    private static final ThreadLocal<ContextHeader> HOLDER = ThreadLocal.withInitial(ContextHeader::new);

    private static final ThreadLocal<List<String>> SERVICE_WARN = ThreadLocal.withInitial(ArrayList::new);

    static public void set(ContextHeader header) {
        HOLDER.set(header);
    }

    static public void setWarn(List<String> warn) {
        SERVICE_WARN.set(warn);
    }

    static public ContextHeader get() {
        return HOLDER.get();
    }

    static public List<String> getWarn() {
        return SERVICE_WARN.get();
    }

    static public void clear() {
        HOLDER.remove();
        SERVICE_WARN.remove();
    }
}
