package com.trading.backend.common.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author ~~ trading.s
 * @date 11:20 10/15/21
 */
public class JsonbTypeHandler extends BaseTypeHandler<PGobject> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PGobject parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public PGobject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getObject(columnName, PGobject.class);
        // if (obj instanceof PGobject) {
        //     return (PGobject) obj;
        // }
        // throw new RuntimeException("JdbcType.OTHER property is not PgObject instance");
    }

    @Override
    public PGobject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getObject(columnIndex, PGobject.class);
        // if (obj instanceof PGobject) {
        //     return (PGobject) obj;
        // }
        // throw new RuntimeException("JdbcType.OTHER property is not PgObject instance");
    }

    @Override
    public PGobject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getObject(columnIndex, PGobject.class);
        // if (obj instanceof PGobject) {
        //     return (PGobject) obj;
        // }
        // throw new RuntimeException("JdbcType.OTHER property is not PgObject instance");
    }

    // private static PGobject getPGobject()
}