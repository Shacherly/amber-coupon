package com.trading.backend.common.type;


import com.alibaba.fastjson.JSONObject;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;


/**
 * @author ~~ trading.s
 * @date 11:20 10/15/21
 */
public class SelfTypeResolver extends JavaTypeResolverDefaultImpl {

    public SelfTypeResolver(){
        super();
        super.typeMap.put(Types.OTHER,
                new JavaTypeResolverDefaultImpl
                        .JdbcTypeInformation("OTHER",
                        new FullyQualifiedJavaType(JSONObject.class.getName())));
    }
}
