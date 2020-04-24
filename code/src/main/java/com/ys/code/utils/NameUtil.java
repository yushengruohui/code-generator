package com.ys.code.utils;

/**
 * 名字处理工具类（文件名、变量名等）
 *
 * @author
 */
public class NameUtil {


    /**
     * 处理文件名
     *
     * @param tableName 数据表表名
     * @return
     */
    public static String fileName(String tableName) {
        //获得表名
        StringBuffer fileName = new StringBuffer();
        //去掉表名的下划线
        String[] tableNames = tableName.split("_");
        for (int j = 0, len = tableNames.length; j < len; j++) {
            //将每个单词的首字母变成大写
            tableNames[j] = tableNames[j].substring(0, 1).toUpperCase() + tableNames[j].substring(1);
            fileName.append(tableNames[j].replace("Tb", ""));
        }
        return fileName.toString();
    }

    /**
     * 处理变量名（属性名）
     *
     * @param columnName 字段名称
     * @return
     */
    public static String columnName(String columnName) {
        //将字段名称user_name格式变成userName格式
        StringBuffer colName = new StringBuffer();
        //根据下划线将名字分为数组
        String[] columnsName = columnName.split("_");
        //遍历数组，将除第一个单词外的单词的首字母大写
        for (int h = 0; h < columnsName.length; h++) {
            for (int k = 1; k < columnsName.length; k++) {
                columnsName[k] = columnsName[k].substring(0, 1).toUpperCase() + columnsName[k].substring(1);
            }
            //拼接字符串以获得属性名（字段名称）
            colName.append(columnsName[h]);
        }
        return colName.toString();
    }
}