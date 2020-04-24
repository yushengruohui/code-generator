package com.ys.code.dao.Impl;

import com.ys.code.bean.ColumnStruct;
import com.ys.code.bean.TableStruct;
import com.ys.code.dao.GetTablesDao;
import com.ys.code.dao.ServiceImplAutoDao;
import com.ys.code.utils.ConfigUtil;
import com.ys.code.utils.DataTypeUtil;
import com.ys.code.utils.FileUtil;
import com.ys.code.utils.NameUtil;

import java.util.List;

public class ServiceImplAutoDaoImpl implements ServiceImplAutoDao {

    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建ServiceImpl实现类
    @Override
    public boolean createServiceImpl() {
        //获得配置文件的参数
        //项目路径
        String projectPath = ConfigUtil.projectPath;
        //是否生成Service
        String serviceImplFalg = ConfigUtil.serviceImplFlag;
        //Service接口的包名
        String serviceImplPackage = ConfigUtil.serviceImplPackage;
        //Bean实体类的包名
        String beanPackage = ConfigUtil.beanPackage;
        //Service接口的包名
        String servicePackage = ConfigUtil.servicePackage;
        //Dao接口的包名
        String daoPackage = ConfigUtil.daoPackage;
        if ("true".equals(serviceImplFalg)) {
            //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String serviceImplPath = serviceImplPackage.replace(".", "/");
            //Service接口的路径
            String path = projectPath + "/src/main/java/" + serviceImplPath;
            //遍历装有所有表结构的List
            for (TableStruct tableStruct : list) {
                //文件名
                String fileName = NameUtil.fileName(tableStruct.getTableName()) + "ServiceImpl";
                String serviceName = NameUtil.fileName(tableStruct.getTableName()) + "Service";
                String TableName = NameUtil.fileName(tableStruct.getTableName());
                String tableName = TableName.substring(0, 1).toLowerCase() + TableName.substring(1);
                String DaoName = NameUtil.fileName(tableStruct.getTableName()) + "Dao";
                String daoName = DaoName.substring(0, 1).toLowerCase() + DaoName.substring(1);
                //获得每个表的所有列结构
                List<ColumnStruct> columns = tableStruct.getColumns();
                //主键变量名（属性名）
                String pkAttrName = NameUtil.columnName(columns.get(0).getColumnName());
                //获得主键数据类型
                String pkFieldType = columns.get(0).getDataType();
                //将mysql数据类型转换为java数据类型
                String pkAttrType = DataTypeUtil.getType(pkFieldType);

                //(ServiceImpl实现类）文件内容
                String packageCon = "package " + serviceImplPackage + ";\n\n";
                StringBuffer importCon = new StringBuffer();
                String className = "public class " + fileName + " implements " + serviceName + " {\n\n";
                StringBuffer classCon = new StringBuffer();

                //生成导包内容
                importCon.append("import ").append(servicePackage).append(".").append(serviceName).append(";\n");
                importCon.append("import" + " ").append(beanPackage).append(".").append(TableName).append(";\n");
                importCon.append("import" + " ").append(daoPackage).append(".").append(DaoName).append(";\n");
                importCon.append("import org.springframework.stereotype.Service;\n");
                importCon.append("import javax.annotation.Resource;\n");

                //有date类型的数据需导包
                if ("Date".equals(pkAttrType)) {
                    importCon.append("import java.util.Date;\n\n");
                }
                //有Timestamp类型的数据需导包
                if ("Timestamp".equals(pkAttrType)) {
                    importCon.append("import java.sql.Timestamp;\n\n");
                }
                importCon.append("import java.util.List;\n\n");
                importCon.append("@Service(\"" + tableName + "Service\")\n");
                //生成Dao属性
                classCon.append("\t@Resource\n");
                classCon.append("\tprivate ").append(DaoName).append(" ").append(daoName).append(";\n\n");

                //生成实现方法
                classCon.append("    /**\n" +
                        "     * 获取 " + TableName + " 表所有记录\n" +
                        "     *\n" +
                        "     * @return " + tableName + "POList\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    public List<" + TableName + "> listAll" + TableName + "() {\n" +
                        "        return " + daoName + ".listAll" + TableName + "();\n" +
                        "    }\n\n");
                classCon.append("    /**\n" +
                        "     * 通过主键获取一条 " + TableName + " 表记录\n" +
                        "     *\n" +
                        "     * @param " + pkAttrName + " " + TableName + "表主键\n" +
                        "     * @return " + tableName + "PO\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    public " + TableName + " get" + TableName + "ById(" + pkAttrType + " " + pkAttrName + ") {\n" +
                        "        return " + daoName + ".get" + TableName + "ById(" + pkAttrName + ");\n" +
                        "    }\n\n");
                classCon.append("    /**\n" +
                        "     * 获取一条符合要求的 " + TableName + " 表记录\n" +
                        "     *\n" +
                        "     * @param " + tableName + "QO 查询条件对象\n" +
                        "     * @return " + tableName + "PO\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    public " + TableName + " get" + TableName + "(" + TableName + " " + tableName + "QO) {\n" +
                        "        return " + daoName + ".get" + TableName + "(" + tableName + "QO);\n" +
                        "    }\n\n");
                classCon.append("    /**\n" +
                        "     * 获取 " + TableName + " 表中符合查询条件的所有记录\n" +
                        "     *\n" +
                        "     * @param " + tableName + "QO 查询条件对象\n" +
                        "     * @return " + TableName + "POList\n" +
                        "     */\n\n" +
                        "    @Override\n" +
                        "    public List<" + TableName + "> list" + TableName + "(" + TableName + " " + tableName + "QO) {\n" +
                        "        return " + daoName + ".list" + TableName + "(" + tableName + "QO);\n" +
                        "    }\n\n");
                classCon.append("    /**\n" +
                        "     * 在 " + TableName + " 表中添加一条记录\n" +
                        "     *\n" +
                        "     * @param " + tableName + "QO 查询条件对象\n" +
                        "     * @return true|false\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    public Boolean insert" + TableName + "(" + TableName + " " + tableName + "QO) {\n" +
                        "        return  1 == " + daoName + ".insert" + TableName + "(" + tableName + "QO);\n" +
                        "    }\n\n");
                classCon.append("    /**\n" +
                        "     * 在 " + TableName + " 表中修改一条记录\n" +
                        "     *\n" +
                        "     * @param " + tableName + "QO 主键不为空的查询条件对象\n" +
                        "     * @return true|false\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    public Boolean update" + TableName + "(" + TableName + " " + tableName + "QO) {\n" +
                        "        return 1 == " + daoName + ".update" + TableName + "(" + tableName + "QO);\n" +
                        "    }\n\n");
                classCon.append("    /**\n" +
                        "     * 通过主键删除 " + TableName + " 表中的一条记录\n" +
                        "     *\n" +
                        "     * @param " + pkAttrName + " " + TableName + "表主键\n" +
                        "     * @return true|false\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    public Boolean delete" + TableName + "ById(" + pkAttrType + " " + pkAttrName + ") {\n" +
                        "        return 1 == " + daoName + ".delete" + TableName + "ById(" + pkAttrName + ");\n" +
                        "    }\n\n");
                classCon.append("    /**\n" +
                        "     * 查询 " + TableName + " 表中是否存在符合查询条件的记录\n" +
                        "     *\n" +
                        "     * @param " + tableName + "QO 查询条件对象\n" +
                        "     * @return true|false\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    public Boolean exist" + TableName + "(" + TableName + " " + tableName + "QO) {\n" +
                        "        return null != " + daoName + ".exist" + TableName + "(" + tableName + "QO);\n" +
                        "    }\n\n");
                classCon.append("    /**\n" +
                        "     * 统计 " + TableName + " 表中符合查询条件的记录条数\n" +
                        "     *\n" +
                        "     * @param " + tableName + "QO 查询条件对象\n" +
                        "     * @return 统计值\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    public Integer count" + TableName + "(" + TableName + " " + tableName + "QO) {\n" +
                        "        return " + daoName + ".count" + TableName + "(" + tableName + "QO);\n" +
                        "    }\n\n");
                classCon.append("    /**\n" +
                        "     * 保存 " + TableName + " 表的一条记录\n" +
                        "     *\n" +
                        "     * @param " + tableName + "QO 查询条件对象\n" +
                        "     * @return true|false\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    public Boolean save" + TableName + "(" + TableName + " " + tableName + "QO) {\n" +
                        "        Long " + pkAttrName + " = " + tableName + "QO.get" + pkAttrName.substring(0, 1).toLowerCase() + pkAttrName.substring(1) + "();\n" +
                        "        if (" + pkAttrName + "!= null ) {\n" +
                        "            return 1 == " + daoName + ".update" + TableName + "(" + tableName + "QO);\n" +
                        "        }\n" +
                        "        return 1 == " + daoName + ".insert" + TableName + "(" + tableName + "QO);\n" +
                        "    }\n\n");


                //拼接(ServiceImpl实现类）文件内容
                StringBuffer content = new StringBuffer();
                content.append(packageCon);
                content.append(importCon.toString());
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