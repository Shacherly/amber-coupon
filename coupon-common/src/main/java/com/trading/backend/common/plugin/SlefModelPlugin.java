package com.trading.backend.common.plugin;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import com.trading.backend.common.util.Predicator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import javax.persistence.Column;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author ~~ trading.s
 * @date 11:19 10/15/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SlefModelPlugin extends PluginAdapter {

    private List<FullyQualifiedJavaType> superInterfaces;

    private String seq_prefix;

    private String seq_suffix;

    private boolean useKeySql; // 用来控制是否需要添加@KeySql

    public SlefModelPlugin() {
    }

    public SlefModelPlugin(String seq_prefix, String seq_suffix, boolean useKeySql, List<FullyQualifiedJavaType> superInterfaces) {
        this.seq_prefix = seq_prefix;
        this.seq_suffix = seq_suffix;
        this.useKeySql = useKeySql;
        this.superInterfaces = superInterfaces;
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(
            Field field,
            TopLevelClass topLevelClass,
            IntrospectedColumn introspectedColumn,
            IntrospectedTable introspectedTable,
            Plugin.ModelClassType modelClassType) {

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        List<IntrospectedColumn> slefColumns = new ArrayList<>();
        Optional.ofNullable(ReflectUtil.getFieldValue(topLevelClass, "superClass"))
                .map(FullyQualifiedJavaType.class::cast)
                .ifPresent(base -> {
                    try {
                        Class<?> baseClass = Class.forName(base.getFullyQualifiedName());
                        Set<String> baseColumns = Arrays.stream(ReflectUtil.getFields(baseClass))
                                                        .filter(field1 -> !Modifier.isStatic(field1.getModifiers()))
                                                        .map(baseField -> AnnotationUtil.getAnnotation(baseField, Column.class))
                                                        .map(Column::name).collect(Collectors.toSet());
                        List<IntrospectedColumn> collect = allColumns.stream().filter(Predicator.notExist(IntrospectedColumn::getActualColumnName, baseColumns)).collect(Collectors.toList());
                        slefColumns.addAll(collect);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
        IntrospectedColumn firstColumn = slefColumns.get(0);

        if (!this.useKeySql) {
            return true;
        }
        /*if (StringUtils.isAllBlank(seq_prefix, seq_suffix)) {
            System.out.println("prefix和suffix均为null，不需要添加KeySql");
            return true;
        }*/
        String seq = null;
        for (IntrospectedColumn column : allColumns) {
            if (introspectedColumn == column) {
                String actualColumnName = column.getActualColumnName();
                if (actualColumnName.equals("id")) {
                    seq = introspectedTable.getFullyQualifiedTableNameAtRuntime();
                    if (StringUtils.isNotBlank(seq_prefix)) {
                        seq = seq_prefix + seq;
                    }
                    if (StringUtils.isNotBlank(seq_suffix)) {
                        seq += seq_suffix;
                    }
                    // String sql = "SELECT " + seq + ".NEXTVAL FROM DUAL";
                    String keySql = "@KeySql(useGeneratedKeys = true)";
                    String columnAnno = "@Column(name = \"id\", insertable = false)";

                    field.addAnnotation(keySql);
                    field.addAnnotation(columnAnno);

                    // 添加 keySql 注释相关引用
                    topLevelClass.addImportedType("tk.mybatis.mapper.annotation.KeySql");
                    // topLevelClass.addImportedType("tk.mybatis.mapper.code.ORDER");
                }
                else {
                    if (column == firstColumn) {
                        List<String> annos = new ArrayList<>(Collections.singleton(""));
                        annos.addAll(field.getAnnotations());
                        ReflectUtil.setFieldValue(field, "annotations", annos);
                    }
                    if (!actualColumnName.contains("_")) {
                        String columnAnno = "@Column(name = \"" + actualColumnName + "\")";
                        field.addAnnotation(columnAnno);
                    }
                }
            }
        }
        if (superInterfaces != null && superInterfaces.size() > 0) {
            for (FullyQualifiedJavaType superInterface : superInterfaces) {
                topLevelClass.addSuperInterface(superInterface);
                topLevelClass.addImportedType(superInterface);
            }
        }

        return true;
    }


    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.EqualsAndHashCode");
        topLevelClass.addImportedType("lombok.NoArgsConstructor");
        topLevelClass.addAnnotation("@NoArgsConstructor");
        topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = true)");
        generateToString(topLevelClass, introspectedTable);
        return true;
    }

    // /**
    //  * mapper 文件添加@Mapper注解
    //  */
    // @Override
    // public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
    //                                IntrospectedTable introspectedTable) {
    //     interfaze.addAnnotation("@Mapper");
    //     FullyQualifiedJavaType mapperImport = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper");
    //     interfaze.addImportedType(mapperImport);
    //     return true;
    // }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        this.seq_prefix = getProperty("seq_prefix");
        this.seq_suffix = getProperty("seq_suffix");
        this.useKeySql = Boolean.parseBoolean(getProperty("useKeySql"));
        String superInterfaces = getProperty("superInterfaces");
        if (superInterfaces == null || superInterfaces.length() == 0) return;
        List<String> collect = Arrays.stream(superInterfaces.split(",")).collect(Collectors.toList());
        this.superInterfaces = collect.stream().map(FullyQualifiedJavaType::new).collect(Collectors.toList());
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    public void generateToString(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method method = new Method("toString");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.addAnnotation("@Override");
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        method.addBodyLine("return new java.util.StringJoiner(\n                \", \", getClass().getSimpleName() + \"[\", \"]\")");
        for (Field field : topLevelClass.getFields()) {
            String name = field.getName();
            if (Objects.equals(name, "serialVersionUID")) continue;
            FullyQualifiedJavaType type = field.getType();
            // if (f)
            if (type.isArray()) {
                method.addBodyLine("        .add(\"" + field.getName() + "=\" + java.util.Arrays.toString(" + field.getName() + "))");
            }
            else if (type.getFullyQualifiedName().equals("java.lang.String")) {
                method.addBodyLine("        .add(\"" + field.getName() + "='\" + " + field.getName() + " + \"'\")");
            }
            else {
                method.addBodyLine("        .add(\"" + field.getName() + "=\" + " + field.getName() + ")");
            }
        }
        Optional.ofNullable(ReflectUtil.getFieldValue(topLevelClass, "superClass"))
                .ifPresent(superClass -> method.addBodyLine("        .add(super.toString())"));
        method.addBodyLine("        .toString();");

        topLevelClass.addMethod(method);
    }

    // private String arrTemplate(Field field) {
    //     return ".add(\"" + field.getName() + "=\" + "
    // }

}
