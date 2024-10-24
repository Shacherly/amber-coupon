package com.trading.backend.template;


import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;


/**
 * @author ~~ trading.s
 * @date 11:57 10/15/21
 */
@Deprecated
public class SpecialBatchProvider extends MapperTemplate {

    public SpecialBatchProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String insertList(MappedStatement statement) {
        Class<?> entityClass = getEntityClass(statement);
        //开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            // if (column.isInsertable()) {
                // //出现类型com.microsoft.sqlserver.jdbc.SQLServerException: 操作数类型冲突: varbinary
                // if(column.getJavaType() == Double.class){
                //     String record = column.getColumnHolder("record");
                //     record = record.substring(0,record.length()-1)+",jdbcType=DECIMAL"+"}";
                //     sql.append(record+ ",");
                // }else {
                    sql.append(column.getColumnHolder("record") + ",");
                // }
            // }
        }
        sql.append("</trim>");
        sql.append("</foreach>");
        return sql.toString();
    }
}
