package com.ys.code;

import com.ys.code.dao.*;
import com.ys.code.dao.Impl.*;

public class MainRunner {
    public void generateCode() {
        //1.生成Bean实体类
        BeanAutoDao beanAuto = new BeanAutoDaoImpl();
        if (beanAuto.createBean()) {
            System.out.println("实体类生成成功");
        } else {
            System.out.println("实体类生成失败");
        }
        //2.生成Dao接口
        DaoAutoDao daoAuto = new DaoAutoDaoImpl();
        if (daoAuto.createDao()) {
            System.out.println("Dao接口生成成功");
        } else {
            System.out.println("Dao接口生成失败");
        }
        //3.生成Mapper.xml
        MapperXmlAutoDao mapperXmlAuto = new MapperXmlAutoDaoImpl();
        if (mapperXmlAuto.createMapperXml()) {
            System.out.println("Mapper.xml生成成功");
        } else {
            System.out.println("Mapper.xml生成失败");
        }
        //4.生成Service接口
        ServiceAutoDao serviceAuto = new ServiceAutoDaoImpl();
        if (serviceAuto.createService()) {
            System.out.println("Service接口生成成功");
        } else {
            System.out.println("Service接口生成失败");
        }
        //5.生成ServiceImpl实现类
        ServiceImplAutoDao serviceImplAuto = new ServiceImplAutoDaoImpl();
        if (serviceImplAuto.createServiceImpl()) {
            System.out.println("ServiceImpl实现类生成成功");
        } else {
            System.out.println("ServiceImpl实现类生成失败");
        }
    }

    public static void main(String[] args) {
        MainRunner mr = new MainRunner();
        mr.generateCode();
    }
}