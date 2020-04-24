package com.ys.code.dao;

import com.ys.code.bean.TableStruct;

import java.util.List;

/**
 * 获取数据表及其结构的dao层接口
 *
 * @author
 */
public interface GetTablesDao {

    //获得数据库的所有表名
    List<String> getTablesName();

    //获得数据表中的字段名称、字段类型
    List<TableStruct> getTablesStruct();
}