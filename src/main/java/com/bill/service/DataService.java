package com.bill.service;

import com.bill.entity.Database;
import com.bill.entity.TableFileds;
import com.bill.entity.Tables;

import java.util.List;

/**
 * @Author huangxiaotao
 * @Date 2022/11/30 22:45
 **/
public interface DataService {
    /**
     * 获取库名列表
     *
     * @param database
     * @return
     */
    List<String> getDatabases(Database database);

    /**
     * 导出word
     *
     * @param dbName
     */
    String exportWord(String addr, String port, String userName, String password, String dbName);

    /**
     * @param addr
     * @param port
     * @param userName
     * @param password
     * @param dbName
     * @return
     */
    List<Tables> getDataTables(String addr, String port, String userName, String password, String dbName);

    /**
     * @param tables
     * @param fileName
     * @param title
     */
    void toWord(List<Tables> tables, String fileName, String dbName, String addr, String port, String userName, String password);

    /**
     * @param tableName
     * @return
     */
    List<TableFileds> getFileds(String addr, String port, String userName, String password, String dbName, String tableName);
}
