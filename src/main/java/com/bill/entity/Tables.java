package com.bill.entity;

/**
 * @Author huangxiaotao
 * @Date 2022/12/1 10:00
 **/
public class Tables {
    private String tableName;
    private String tableDesc;

    public Tables() {
    }

    public Tables(String tableName, String tableDesc) {
        this.tableName = tableName;
        this.tableDesc = tableDesc;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableDesc() {
        return tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }
}
