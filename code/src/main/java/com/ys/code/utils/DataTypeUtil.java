package com.ys.code.utils;

/**
 * mysql数据类型处理工具类
 *
 * @author
 */
public class DataTypeUtil {

    public static String getType(String dataType) {
        String type = "";
        String temp = "(";
        String databaseType = substringBefore(dataType, temp);
        switch (databaseType) {
            case "int": {
                if (dataType.contains("unsigned")) {
                    type = "Long";
                } else {
                    type = "Integer";
                }
                break;
            }
            case "varchar":
            case "char": {
                type = "String";
                break;
            }
            case "tinyint":
            case "smallint":
            case "mediumint": {
                type = "Integer";
                break;
            }
            case "integer": {
                if (dataType.contains("unsigned")) {
                    type = "Long";
                } else {
                    type = "Integer";
                }
                break;
            }
            case "bigint": {
                type = "Long";
                break;
            }
            case "bit": {
                type = "Boolean";
                break;
            }
            case "double": {
                type = "Double";
                break;
            }
            case "float": {
                type = "Float";
                break;
            }
            default: {
                type = "String";
                break;
            }
        }
        return type;
    }

    private static String substringBefore(String str, String separator) {
        if (str != null && separator != null) {
            if (separator.length() == 0) {
                return "";
            } else {
                int pos = str.indexOf(separator);
                return pos == -1 ? str : str.substring(0, pos);
            }
        } else {
            return str;
        }
    }
}