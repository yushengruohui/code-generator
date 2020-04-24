package com.ys.code.dao.Impl;

import com.ys.code.bean.ColumnStruct;
import com.ys.code.bean.TableStruct;
import com.ys.code.dao.DaoAutoDao;
import com.ys.code.dao.GetTablesDao;
import com.ys.code.utils.ConfigUtil;
import com.ys.code.utils.DataTypeUtil;
import com.ys.code.utils.FileUtil;
import com.ys.code.utils.NameUtil;

import java.util.List;

public class DaoAutoDaoImpl implements DaoAutoDao {

    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建Dao接口
    @Override
    public boolean createDao() {
        //获得配置文件的参数
        //项目路径
        String projectPath = ConfigUtil.projectPath;
        //是否生成Dao
        String daoFalg = ConfigUtil.daoFlag;
        //Dao接口的包名
        String daoPackage = ConfigUtil.daoPackage;
        //Bean实体类的包名
        String beanPackage = ConfigUtil.beanPackage;
        //作者
        String authorName = ConfigUtil.author;
        //创建时间
        String createTime = ConfigUtil.createTime;
        if ("true".equals(daoFalg)) {
            //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String daoPath = daoPackage.replace(".", "/");
            //Dao接口的路径
            String path = projectPath + "/src/main/java/" + daoPath;

            for (TableStruct tableStruct : list) {
                //文件名
                String fileName = NameUtil.fileName(tableStruct.getTableName()) + "Dao";
                //当前bean的类名
                String TableName = NameUtil.fileName(tableStruct.getTableName());
                //bean的对象名
                String tableName = TableName.substring(0, 1).toLowerCase() + TableName.substring(1);
                //获得每个表的所有列结构
                List<ColumnStruct> columns = tableStruct.getColumns();
                //变量名（属性名）
                String pkName = NameUtil.columnName(columns.get(0).getColumnName());
                //获得数据类型
                String type = columns.get(0).getDataType();
                //将mysql数据类型转换为java数据类型
                String pkType = DataTypeUtil.getType(type);

                //(Dao接口）文件内容
                String packageCon = "package " + daoPackage + ";\n\n";
                StringBuffer importCon = new StringBuffer();
                String headerCon = "/**\n" +
                        " * (" + TableName + ")表数据库访问层\n" +
                        " *\n" +
                        " * Created on " + createTime + "\n" +
                        " * @author " + authorName + "\n" +
                        " */\n";
                String className = "public interface " + fileName + "{\n\n";
                StringBuffer classCon = new StringBuffer();

                //生成导包内容
                importCon.append("import" + "\t").append(beanPackage).append(".").append(TableName).append(";\n\n");
                //有date类型的数据需导包
                if ("Date".equals(pkType)) {
                    importCon.append("import java.util.Date;\n\n");
                }
                //有Timestamp类型的数据需导包
                if ("Timestamp".equals(pkType)) {
                    importCon.append("import java.sql.Timestamp;\n\n");
                }
                importCon.append("import java.util.List;\n\n");

                //生成接口方法
                classCon.append("    /**\n" + "     * 获取 ").append(TableName).append(" 表所有记录\n").append("     *\n").append("     * @return ").append(TableName).append("POList\n").append("     */\n").append("    List<").append(TableName).append("> listAll").append(TableName).append("();\n\n");
                classCon.append("    /**\n" + "     * 通过主键获取一条 ").append(TableName).append(" 表记录\n").append("     *\n").append("     * @param ").append(pkName).append(" ").append(TableName).append("表主键\n").append("     * @return ").append(tableName).append("PO\n").append("     */\n").append("    ").append(TableName).append(" get").append(TableName).append("ById(").append(pkType).append(" ").append(pkName).append(");\n\n");
                classCon.append("    /**\n" + "     * 获取一条符合要求的 ").append(TableName).append(" 表记录\n").append("     *\n").append("     * @param ").append(tableName).append("QO 查询条件对象\n").append("     * @return ").append(tableName).append("PO\n").append("     */\n").append("    ").append(TableName).append(" get").append(TableName).append("(").append(TableName).append(" ").append(tableName).append("QO);\n\n");
                classCon.append("    /**\n" + "     * 获取 ").append(TableName).append(" 表中符合查询条件的所有记录\n").append("     *\n").append("     * @param ").append(tableName).append("QO 查询条件对象\n").append("     * @return ").append(TableName).append("POList\n").append("     */\n").append("    List<").append(TableName).append("> list").append(TableName).append("(").append(TableName).append(" ").append(tableName).append("QO);\n\n");
                classCon.append("    /**\n" + "     * 在 ").append(TableName).append(" 表中添加一条记录\n").append("     *\n").append("     * @param ").append(tableName).append("QO 查询条件对象\n").append("     * @return 1|0\n").append("     */\n").append("    Integer insert").append(TableName).append("(").append(TableName).append(" ").append(tableName).append("QO);\n\n");
                classCon.append("    /**\n" + "     * 在 ").append(TableName).append(" 表中修改一条记录\n").append("     *\n").append("     * @param ").append(tableName).append("QO 主键不为空的查询条件对象\n").append("     * @return 1|0\n").append("     */\n").append("    Integer update").append(TableName).append("(").append(TableName).append(" ").append(tableName).append("QO);\n\n");
                classCon.append("    /**\n" + "     * 通过主键删除 ").append(TableName).append(" 表中的一条记录\n").append("     *\n").append("     * @param ").append(pkName).append(" ").append(TableName).append("表主键\n").append("     * @return 1|0\n").append("     */\n").append("    Integer delete").append(TableName).append("ById(").append(pkType).append(" ").append(pkName).append(");\n\n");
                classCon.append("    /**\n" + "     * 查询 ").append(TableName).append(" 表是否存在符合查询条件的一条记录\n").append("     *\n").append("     * @param ").append(tableName).append("QO 查询条件对象\n").append("     * @return 1|null\n").append("     */\n").append("    Integer exist").append(TableName).append("(").append(TableName).append(" ").append(tableName).append("QO);\n\n");
                classCon.append("    /**\n" + "     * 统计 ").append(TableName).append(" 表中符合查询条件的记录条数\n").append("     *\n").append("     * @param ").append(tableName).append("QO 查询条件对象\n").append("     * @return 统计值\n").append("     */\n").append("    Integer count").append(TableName).append("(").append(TableName).append(" ").append(tableName).append("QO);\n\n");
                classCon.append("    /**\n" +
                        "     * 保存 " + TableName + " 表的一条记录\n" +
                        "     *\n" +
                        "     * @param " + tableName + "QO 查询条件对象\n" +
                        "     * @return true|false\n" +
                        "     */\n" +
                        "    Boolean save" + TableName + "(" + TableName + " " + tableName + "QO);\n\n");

                //拼接(Dao接口）文件内容
                StringBuffer content = new StringBuffer();
                content.append(packageCon);
                content.append(importCon.toString());
                content.append(headerCon);
                content.append(className);
                content.append(classCon.toString());
                content.append("\n}");
                FileUtil.createFileAtPath(path + "/", fileName + ".java", content.toString());
            }
            return true;
        }
        return false;
    }

}