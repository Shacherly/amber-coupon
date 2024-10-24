package com.trading.backend.http.request.dual;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.http.request.InternalParamUid;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DualMathParam extends InternalParamUid {
    private static final long serialVersionUID = -2183079302074286332L;

    @Override
    public String toString() {
        return "DualMathParam{} " + super.toString();
    }
}
