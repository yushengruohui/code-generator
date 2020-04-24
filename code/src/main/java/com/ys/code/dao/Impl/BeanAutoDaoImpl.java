package com.ys.code.dao.Impl;

import com.ys.code.bean.ColumnStruct;
import com.ys.code.bean.TableStruct;
import com.ys.code.dao.BeanAutoDao;
import com.ys.code.dao.GetTablesDao;
import com.ys.code.utils.ConfigUtil;
import com.ys.code.utils.DataTypeUtil;
import com.ys.code.utils.FileUtil;
import com.ys.code.utils.NameUtil;

import java.util.List;

public class BeanAutoDaoImpl implements BeanAutoDao {

    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建Bean实体
    @Override
    public boolean createBean() {
        //获得配置文件的参数
        //项目路径
        String projectPath = ConfigUtil.projectPath;
        //是否生成实体类
        String beanFlag = ConfigUtil.beanFlag;
        //Bean实体类的包名
        String beanPackage = ConfigUtil.beanPackage;
        //作者
        String authorName = ConfigUtil.author;
        //创建时间
        String createTime = ConfigUtil.createTime;
        //判断是否生成实体类
        if ("true".equals(beanFlag)) {
            //将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String beanPath = beanPackage.replace(".", "/");
            //Bean实体类的路径
            String path = projectPath + "/src/main/java/" + beanPath;
            //遍历装有所有表结构的List
            for (TableStruct tableStruct : list) {
                //文件名
                String fileName = NameUtil.fileName(tableStruct.getTableName());
                //获得每个表的所有列结构
                List<ColumnStruct> columns = tableStruct.getColumns();
                //(实体类）文件内容
                String packageCon = "package " + beanPackage + ";\n\n";
                StringBuffer importCon = new StringBuffer();
                String headerCon = "\n/**\n" +
                        " * (" + fileName + ")表实体类\n" +
                        " *\n" +
                        " * Created on " + createTime + "\n" +
                        " * @author " + authorName + "\n" +
                        " */\n";
                importCon.append("import java.io.Serializable;\n" +
                        "import java.lang.reflect.Field;\n" +
                        "import java.lang.reflect.Modifier;\n");
                String className = "public class " + fileName + " implements Serializable {\n\tprivate static final long serialVersionUID = 1L;\n";
                StringBuffer classCon = new StringBuffer();
                StringBuffer gettersCon = new StringBuffer();
                StringBuffer settersCon = new StringBuffer();
                StringBuffer noneConstructor = new StringBuffer();
                StringBuffer constructor = new StringBuffer();
                String constructorParam = "";
                StringBuffer constructorCon = new StringBuffer();
                //遍历List，将字段名称和字段类型写进文件
                for (ColumnStruct column : columns) {
                    //变量名（属性名）
                    String columnName = NameUtil.columnName(column.getColumnName());
                    //获得数据类型
                    String type = column.getDataType();
                    //将mysql数据类型转换为java数据类型
                    String dateType = DataTypeUtil.getType(type);
                    //有date类型的数据需导包
                    if ("Date".equals(dateType)) {
                        importCon.append("import java.util.Date;\n\n");
                    }
                    //有Timestamp类型的数据需导包
                    if ("Timestamp".equals(dateType)) {
                        importCon.append("import java.sql.Timestamp;\n\n");
                    }

                    //生成属性
                    classCon.append("\t" + "private ").append(dateType).append(" ").append(columnName).append(";\n");
                    //get、set的方法名
                    String getSetName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    //生成get方法
                    gettersCon.append("\n\t" + "public ").append(dateType).append(" ").append("get").append(getSetName).append("(){\n").append("\t\t").append("return").append("\t").append(columnName).append(";\n").append("\t").append("}\n");
                    //生成set方法
                    settersCon.append("\n\t" + "public void set").append(getSetName).append("(").append(dateType).append(" ").append(columnName).append("){\n").append("\t\t").append("this.").append(columnName).append(" = ").append(columnName).append(";\n").append("\t").append("}\n");
                    //获得有参构造器参数
                    if ("".equals(constructorParam)) {
                        constructorParam = dateType + " " + columnName;
                    } else {
                        constructorParam += "," + dateType + " " + columnName;
                    }
                    //获得有参构造器的内容
                    constructorCon.append("\t\t" + "this.").append(columnName).append(" = ").append(columnName).append(";\n");
                }
                //生成无参构造器
                noneConstructor.append("\n\t" + "public ").append(fileName).append("() {\n").append("\t\t").append("super();\n").append("\t").append("}\n");
                //生成有参构造
                constructor.append("\n\t" + "public" + " ").append(fileName).append("(").append(constructorParam).append("){\n").append("\t\t").append("super();\n").append(constructorCon).append("\t").append("}\n");
                // 生成toString()方法
                String toStringCon = "\n    @Override\n" +
                        "    public String toString() {\n" +
                        "        StringBuffer sb = new StringBuffer(300);\n" +
                        "        sb.append(this.getClass().getSimpleName()).append(\":{\");\n" +
                        "        Field[] fields = this.getClass().getDeclaredFields();\n" +
                        "        try {\n" +
                        "            for (Field field : fields) {\n" +
                        "                field.setAccessible(true);\n" +
                        "                String modifier = Modifier.toString(field.getModifiers());\n" +
                        "                if (modifier.contains(\"static final\")) {\n" +
                        "                    continue;\n" +
                        "                }\n" +
                        "                sb.append(field.getName()).append(\":\").append(field.get(this)).append(\",\");\n" +
                        "            }\n" +
                        "        } catch (Exception e) {\n" +
                        "            e.printStackTrace();\n" +
                        "        }\n" +
                        "        sb.deleteCharAt(sb.lastIndexOf(\",\"));\n" +
                        "        sb.append(\"}\");\n" +
                        "        return sb.toString();\n" +
                        "    }\n";
                //拼接(实体类）文件内容
                String content = packageCon +
                        importCon.toString() +
                        headerCon +
                        className +
                        classCon.toString() +
                        gettersCon.toString() +
                        settersCon.toString() +
                        noneConstructor.toString() +
                        constructor.toString() +
                        toStringCon +
                        "\n}";
                FileUtil.createFileAtPath(path + "/", fileName + ".java", content);
            }
            return true;
        }
        return false;
    }

}