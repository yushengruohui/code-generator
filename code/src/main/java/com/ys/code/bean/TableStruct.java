package com.ys.code.bean;

import java.util.List;

/**
 * 数据表的表结构
 */
public class TableStruct {
    //表名
    private String tableName;
    //所有的列
    private List<ColumnStruct> Columns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnStruct> getColumns() {
        return Columns;
    }

    public void setColumns(List<ColumnStruct> columns) {
        Columns = columns;
    }

    public TableStruct(String tableName, List<ColumnStruct> columns) {
        super();
        this.tableName = tableName;
        Columns = columns;
    }

    public TableStruct() {
        super();
    }

    @Override
    public String toString() {
        return "TableStruct [tableName=" + tableName + ", Columns=" + Columns
                + "]";
    }
}