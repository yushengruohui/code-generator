package com.ys.code.dao.Impl;

import com.ys.code.bean.ColumnStruct;
import com.ys.code.bean.TableStruct;
import com.ys.code.dao.GetTablesDao;
import com.ys.code.dao.MapperXmlAutoDao;
import com.ys.code.utils.*;

import java.util.List;

public class MapperXmlAutoDaoImpl implements MapperXmlAutoDao {

    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建Mapper.xml
    @Override
    public boolean createMapperXml() {
        //获得配置文件的参数
        //项目路径
        String projectPath = ConfigUtil.projectPath;
        //是否生成Mapper.xml
        String mapperXmlFlag = ConfigUtil.mapperXmlFlag;
        //Mapper.xml的包名
        String mapperXmlPackage = ConfigUtil.mapperXmlPackage;
        //Bean实体类的包名
        String beanPackage = ConfigUtil.beanPackage;
        //Dao接口的包名
        String daoPackage = ConfigUtil.daoPackage;
        if ("true".equals(mapperXmlFlag)) {
            //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String mapperXmlPath = mapperXmlPackage.replace(".", "/");
            //Mapper.xml的路径
            String path = projectPath + "/src/main/resources/" + mapperXmlPath;
            //遍历装有所有表结构的List
            for (TableStruct tableStruct : list) {
                //数据库中表名
                String dbTableName = tableStruct.getTableName();

                //文件名
                String mapperName = NameUtil.fileName(dbTableName) + "Mapper";
                // 首字母大写的Bean类名
                String TableName = NameUtil.fileName(dbTableName);
                // 首字母小写的bean类名
                String tableName = TableName.substring(0, 1).toLowerCase() + TableName.substring(1);
                String daoName = NameUtil.fileName(dbTableName) + "Dao";

                //获得每个表的所有列结构
                List<ColumnStruct> columns = tableStruct.getColumns();

                //主键名
                String beanIdName = NameUtil.columnName(columns.get(0).getColumnName());
                String fieldIdName = columns.get(0).getColumnName();
                //主键类型
                String IdType = DataTypeUtil.getType(columns.get(0).getDataType());
                String IdParamType = ParamTypeUtil.getParamType(IdType);
                String IdJdbcType = JdbcTypeUtil.getJdbcType(IdType);
                if ("INT".equals(IdJdbcType)) {
                    IdJdbcType = "INTEGER";
                }

                //(Mapper.xml）文件内容
                StringBuffer headCon = new StringBuffer();
                headCon.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
                headCon.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
                headCon.append("<mapper namespace=\"").append(daoPackage).append(".").append(daoName).append("\">\n");

                // 基本resultMap
                StringBuffer resultMapCon = new StringBuffer();
                resultMapCon.append("\t" + "<resultMap id=\"").append(tableName).append("Map\" type=\"").append(beanPackage).append(".").append(TableName).append("\">\n");

                // 获取当前表的所有列名
                StringBuffer allColumnsCon = new StringBuffer();
                allColumnsCon.append("\t" + "<sql id=\"" + tableName + "_all_column\">\n");

                // 如果不为空则为查询条件
                StringBuffer whereReadyCon = new StringBuffer();
                whereReadyCon.append("\t" + "<sql id=\"" + tableName + "_notnull_condition\">\n");

                StringBuffer insertColumnCon = new StringBuffer();
                insertColumnCon.append("\t" + "<sql id=\"" + tableName + "_insert_column\">\n");

                StringBuffer insertValueCon = new StringBuffer();
                insertValueCon.append("\t" + "<sql id=\"" + tableName + "_insert_value\">\n");

                //遍历List，将字段名称和字段类型、属性名写进文件
                for (int j = 0, len = columns.size(); j < len; j++) {
                    //字段名
                    String columnName = columns.get(j).getColumnName();
                    //属性（变量）名
                    String attrName = NameUtil.columnName(columns.get(j).getColumnName());
                    //字段类型
                    String type = DataTypeUtil.getType(columns.get(j).getDataType());
                    String jdbcType = JdbcTypeUtil.getJdbcType(type);
                    if ("INT".equals(jdbcType)) {
                        jdbcType = "INTEGER";
                    }
                    if (j == 0) {
                        // 主键处理
                        resultMapCon.append("\t\t" + "<id column=\"" + columnName + "\" property=\"" + attrName + "\" jdbcType=\"" + jdbcType + "\"/>\n");
                        allColumnsCon.append("\t\t" + columnName);
                        if ("String".equals(type)) {
                            // 拼接非空查询语句
                            whereReadyCon.append("\t\t<if test=\"" + attrName + " != null and " + attrName + " != ''\">\n");
                            whereReadyCon.append("\t\t\tand " + columnName + " = #{" + attrName + "}\n");
                            whereReadyCon.append("\t\t</if>\n");
                            insertColumnCon.append("\t\t<if test=\"" + attrName + " != null and " + attrName + " != ''\">\n");
                            insertColumnCon.append("\t\t\t" + columnName + " ,\n");
                            insertColumnCon.append("\t\t</if>\n");
                            insertValueCon.append("\t\t<if test=\"" + attrName + " != null and " + attrName + " != ''\">\n");
                            insertValueCon.append("\t\t\t#{" + attrName + "} ,\n");
                            insertValueCon.append("\t\t</if>\n");
                        } else {
                            whereReadyCon.append("\t\t<if test=\"" + attrName + " != null \">\n");
                            whereReadyCon.append("\t\t\tand " + columnName + " = #{" + attrName + "}\n");
                            whereReadyCon.append("\t\t</if>\n");
                            insertColumnCon.append("\t\t<if test=\"" + attrName + " != null \">\n");
                            insertColumnCon.append("\t\t\t" + columnName + " ,\n");
                            insertColumnCon.append("\t\t</if>\n");
                            insertValueCon.append("\t\t<if test=\"" + attrName + " != null and " + attrName + " != ''\">\n");
                            insertValueCon.append("\t\t\t#{" + attrName + "} ,\n");
                            insertValueCon.append("\t\t</if>\n");
                        }
                    } else {
                        // 非主键处理
                        resultMapCon.append("\t\t" + "<result column=\"" + columnName + "\" property=\"" + attrName + "\" jdbcType=\"" + jdbcType + "\"/>\n");
                        allColumnsCon.append("," + columnName);
                        if ("String".equals(type)) {
                            // 拼接非空查询语句
                            whereReadyCon.append("\t\t<if test=\"" + attrName + " != null and " + attrName + " != ''\">\n");
                            whereReadyCon.append("\t\t\tand " + columnName + " = #{" + attrName + "}\n");
                            whereReadyCon.append("\t\t</if>\n");
                            insertColumnCon.append("\t\t<if test=\"" + attrName + " != null and " + attrName + " != ''\">\n");
                            insertColumnCon.append("\t\t\t" + columnName + " ,\n");
                            insertColumnCon.append("\t\t</if>\n");
                            insertValueCon.append("\t\t<if test=\"" + attrName + " != null and " + attrName + " != ''\">\n");
                            insertValueCon.append("\t\t\t#{" + attrName + "} ,\n");
                            insertValueCon.append("\t\t</if>\n");
                        } else {
                            whereReadyCon.append("\t\t<if test=\"" + attrName + " != null \">\n");
                            whereReadyCon.append("\t\t\tand " + columnName + " = #{" + attrName + "}\n");
                            whereReadyCon.append("\t\t</if>\n");
                            insertColumnCon.append("\t\t<if test=\"" + attrName + " != null \">\n");
                            insertColumnCon.append("\t\t\t" + columnName + " ,\n");
                            insertColumnCon.append("\t\t</if>\n");
                            insertValueCon.append("\t\t<if test=\"" + attrName + " != null and " + attrName + " != ''\">\n");
                            insertValueCon.append("\t\t\t#{" + attrName + "} ,\n");
                            insertValueCon.append("\t\t</if>\n");
                        }
                    }

                }
                resultMapCon.append("\t" + "</resultMap>\n");
                allColumnsCon.append("\n\t</sql>\n");
                whereReadyCon.append("\t" + "</sql>\n");
                insertColumnCon.append("\t" + "</sql>\n");
                insertValueCon.append("\t" + "</sql>\n");

                // 准备查询语句
                StringBuffer listAllBeanCon = new StringBuffer();
                listAllBeanCon.append("    <!--获取" + TableName + "表的所有记录-->\n" +
                        "    <select id=\"listAll" + TableName + "\" resultMap=\"" + tableName + "Map\">\n" +
                        "        select\n" +
                        "          <include refid=\"" + tableName + "_all_column\"/>\n" +
                        "        from `" + dbTableName + "`\n" +
                        "    </select>\n\n");

                StringBuffer getBeanByIdCon = new StringBuffer();
                getBeanByIdCon.append("    <!--通过主键获取" + tableName + "表的一条记录-->\n" +
                        "    <select id=\"get" + tableName + "ById\" resultMap=\"" + tableName + "Map\">\n" +
                        "        select\n" +
                        "          <include refid=\"" + tableName + "_all_column\"/>\n" +
                        "        from `" + dbTableName + "`\n" +
                        "        where " + fieldIdName + " = #{" + beanIdName + "}\n" +
                        "    </select>\n\n");

                StringBuffer existBeanCon = new StringBuffer();
                existBeanCon.append("    <!--查询" + TableName + "表中是否存在符合查询条件的记录-->\n" +
                        "    <select id=\"exist" + TableName + "\" resultType=\"java.lang.Integer\">\n" +
                        "        select 1\n" +
                        "        from `" + dbTableName + "`\n" +
                        "        <where>\n" +
                        "            <include refid=\"" + tableName + "_notnull_condition\"/>\n" +
                        "        </where>\n" +
                        "        limit 1\n" +
                        "    </select>\n\n");

                StringBuffer getBeanByParamsCon = new StringBuffer();
                getBeanByParamsCon.append("    <!--获取一条符合查询条件的" + TableName + "表记录-->\n" +
                        "    <select id=\"get" + TableName + "\" resultMap=\"" + tableName + "Map\">\n" +
                        "        select\n" +
                        "          <include refid=\"" + tableName + "_all_column\"/>\n" +
                        "        from `" + dbTableName + "`\n" +
                        "        <where>\n" +
                        "            <include refid=\"" + tableName + "_notnull_condition\"/>\n" +
                        "        </where>\n" +
                        "        limit 1\n" +
                        "    </select>\n\n");

                StringBuffer listBeanByParamsCon = new StringBuffer();
                listBeanByParamsCon.append("    <!--获取" + TableName + "表中符合查询条件的所有记录-->\n" +
                        "    <select id=\"list" + TableName + "\" resultMap=\"" + tableName + "Map\">\n" +
                        "        select\n" +
                        "          <include refid=\"" + tableName + "_all_column\"/>\n" +
                        "        from `" + dbTableName + "`\n" +
                        "        <where>\n" +
                        "            <include refid=\"" + tableName + "_notnull_condition\"/>\n" +
                        "        </where>\n" +
                        "    </select>\n\n");

                StringBuffer insertBeanCon = new StringBuffer();
                insertBeanCon.append("    <!--在" + TableName + "表中添加一条记录-->\n" +
                        "    <insert id=\"insert" + TableName + "\" keyProperty=\"" + beanIdName + "\" useGeneratedKeys=\"true\">\n" +
                        "        insert into `" + dbTableName + "`(\n" +
                        "            <trim suffixOverrides=\",\">\n" +
                        "            <include refid=\"" + tableName + "_insert_column\"/>\n" +
                        "            </trim>\n" +
                        "                )\n" +
                        "        values (\n" +
                        "            <trim suffixOverrides=\",\">\n" +
                        "            <include refid=\"" + tableName + "_insert_value\"/>\n" +
                        "            </trim>\n" +
                        "                )\n" +
                        "    </insert>\n\n");

                StringBuffer updateBeanCon = new StringBuffer();
                updateBeanCon.append("    <!--在" + TableName + "表中更新一条记录-->\n" +
                        "    <update id=\"update" + TableName + "\">\n" +
                        "        update `" + dbTableName + "`\n" +
                        "        <set>\n" +
                        "            <include refid=\"" + tableName + "_notnull_condition\"/>\n" +
                        "        </set>\n" +
                        "        where " + fieldIdName + " = #{" + beanIdName + "}\n" +
                        "    </update>\n\n");

                StringBuffer deleteCon = new StringBuffer();
                deleteCon.append("    <!--通过主键删除" + TableName + "表中的一条记录-->\n" +
                        "    <delete id=\"delete" + TableName + "ById\">\n" +
                        "        delete from `" + dbTableName + "` where " + fieldIdName + " = #{" + beanIdName + "}\n" +
                        "    </delete>\n\n");

                StringBuffer countBeanCon = new StringBuffer();
                countBeanCon.append("    <!--统计" + TableName + "表中符合查询条件的记录条数-->\n" +
                        "    <select id=\"count" + TableName + "\" resultType=\"java.lang.Integer\">\n" +
                        "        select count(*)\n" +
                        "        from `" + dbTableName + "`\n" +
                        "        <where>\n" +
                        "            <include refid=\"" + tableName + "_notnull_condition\"/>\n" +
                        "        </where>\n" +
                        "    </select>\n\n");
                //拼接(Mapper.xml）文件内容
                StringBuffer content = new StringBuffer();
                content.append(headCon);
                content.append(resultMapCon);
                content.append(allColumnsCon);
                content.append(whereReadyCon);
                content.append(insertColumnCon);
                content.append(insertValueCon);
                content.append(listAllBeanCon);
                content.append(getBeanByIdCon);
                content.append(existBeanCon);
                content.append(getBeanByParamsCon);
                content.append(listBeanByParamsCon);
                content.append(insertBeanCon);
                content.append(updateBeanCon);
                content.append(deleteCon);
                content.append(countBeanCon);

                content.append("\n</mapper>");

                FileUtil.createFileAtPath(path + "/", mapperName + ".xml", content.toString());
            }
            return true;
        }
        return false;
    }

}