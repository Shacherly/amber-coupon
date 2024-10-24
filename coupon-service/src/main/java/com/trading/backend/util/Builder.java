package com.trading.backend.util;

import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.http.response.ConcisePossessVO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * @author ~~ trading.s
 * @date 17:18 05/12/21
 */
public class Builder<ENTITY> {
    private final Supplier<? extends ENTITY> INSTANTIATOR;
    private final List<Consumer<ENTITY>> propertySetter = new ArrayList<>();

    public Builder(Supplier<? extends ENTITY> instantiator) {
        this.INSTANTIATOR = instantiator;
    }

    public static <ENTITY> Builder<ENTITY> of(Supplier<? extends ENTITY> instantiator) {
        return new Builder<>(instantiator);
    }

    public <ARG> Builder<ENTITY> with(BiConsumer<ENTITY, ARG> property, ARG arg) {
        Consumer<ENTITY> setter = entity -> property.accept(entity, arg);
        propertySetter.add(setter);
        return this;
    }

    public ENTITY build() {
        ENTITY entity = INSTANTIATOR.get();
        propertySetter.forEach(setter -> setter.accept(entity));
        propertySetter.clear();
        return entity;
    }

    public void buildVoid() {
        ENTITY entity = INSTANTIATOR.get();
        propertySetter.forEach(setter -> setter.accept(entity));
        propertySetter.clear();
    }

    public static void main(String[] args) {
        Builder<BasalExportPossessBO> builder = Builder.of(ConcisePossessVO::new);
        builder.with(BasalExportPossessBO::setCouponId, 1L);
        BasalExportPossessBO build = builder.build();
        System.out.println();
    }
}