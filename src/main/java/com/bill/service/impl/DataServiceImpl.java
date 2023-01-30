package com.bill.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.bill.MysqlGUI;
import com.bill.entity.Database;
import com.bill.entity.TableFileds;
import com.bill.entity.Tables;
import com.bill.service.DataService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.rtf.RtfWriter2;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Author huangxiaotao
 * @Date 2022/11/30 22:46
 **/
public class DataServiceImpl implements DataService {
    private final String DRIVER = "com.mysql.jdbc.Driver";
//    private final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // 连接数据库
    private Connection getConn(Database database) {
        Connection connection = null;
        try {

            Class.forName(DRIVER);
            //jdbc:mysql://localhost:3306/
            connection = DriverManager.getConnection(StrUtil.trim(database.getUrl()), StrUtil.trim(database.getUserName()), StrUtil.trim(database.getPassword()));
            return connection;
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return null;

    }

    @Override
    public List<String> getDatabases(Database database) {
        List<String> databases = new ArrayList<>();
        Connection conn = getConn(database);
        if (Objects.isNull(conn)) {
            //if null ,connect is fail
            return null;
        }
        try {
            Statement stmt = conn.createStatement();
            // find the name of tables
            // ResultSet rs = stmt.executeQuery("select table_name from information_schema.tables where table_schema='EXP'");

            // get the databases' name
            ResultSet rs = stmt.executeQuery("SHOW DATABASES;");
            ResultSetMetaData rs_metaData = rs.getMetaData();
            while (rs.next()) {
                // System.out.println(rs.getString("Cno"));
                int count = rs_metaData.getColumnCount();
                for (int i = 0; i < count; i++) {
                    databases.add(rs.getString(i + 1));
                }
            }
            return databases;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return null;
    }

    @Override
    public List<Tables> getDataTables(String addr, String port, String userName, String password, String dbName) {
        String url = MysqlGUI.MYSQL_MARK + addr + ":" + port + "/" + dbName;
        Database database = new Database(url, userName, password);
        Connection conn = getConn(database);
        List<Tables> tables = new ArrayList<Tables>();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("select table_name as tableName,table_comment as tableDesc from information_schema.tables where table_schema =? order by table_name;");
            preparedStatement.setString(1, dbName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Tables table = new Tables();
                table.setTableDesc(resultSet.getString("tableDesc"));
                table.setTableName(resultSet.getString("tableName"));
                tables.add(table);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                resultSet.close();
                preparedStatement.close();
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return tables;
    }

    @Override
    public List<TableFileds> getFileds(String addr, String port, String userName, String password, String dbName, String tableName) {
        String url = MysqlGUI.MYSQL_MARK + addr + ":" + port + "/" + dbName;
        Database database = new Database(url, userName, password);
        Connection conn = getConn(database);
        List<TableFileds> tableFileds = new ArrayList<TableFileds>();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("SHOW FULL FIELDS FROM " + tableName + ";");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TableFileds tableFiled = new TableFileds();
                tableFiled.setField(resultSet.getString("Field"));
                tableFiled.setType(resultSet.getString("Type"));
                tableFiled.setIsNull(resultSet.getString("Null"));
                tableFiled.setIsKey(resultSet.getString("Key"));
                tableFiled.setComment(resultSet.getString("Comment"));
                tableFiled.setDefaultValue(resultSet.getString("Default"));
                tableFileds.add(tableFiled);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                resultSet.close();
                preparedStatement.close();
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return tableFileds;
    }

    @Override
    public String exportWord(String addr, String port, String userName, String password, String dbName) {

        List<Tables> dataTables = getDataTables(addr, port, userName, password, dbName);
        String yyyyMMdd = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        String wordName = dbName + "_" + yyyyMMdd + ".doc";
        toWord(dataTables, wordName, dbName, addr, port, userName, password);
        return wordName;
        //

    }

    @Override
    public void toWord(List<Tables> tables, String fileName, String dbName, String addr, String port, String userName, String password) {
        Document document = new Document(PageSize.A4);
        try {
            File file = FileUtil.file("D:\\mysql-word", fileName);
            if (file.exists()) {
                file.delete();
            }
            FileUtil.touch(file);
            // 写入文件信息
            RtfWriter2.getInstance(document, new FileOutputStream(file.getPath()));
            document.open();
            Paragraph ph = new Paragraph();
            Font f = new Font();
            String title = dbName + "数据库表结构";
            Paragraph p = new Paragraph(title, new Font(Font.NORMAL, 24, Font.BOLDITALIC, new Color(0, 0, 0)));
            p.setAlignment(1);
            document.add(p);
            ph.setFont(f);
            for (int i = 0; i < tables.size(); i++) {
                String table_name = tables.get(i).getTableName();
                String table_comment = tables.get(i).getTableDesc();
                List<TableFileds> fields = getFileds(addr, port, userName, password, dbName, table_name);
                String all = "" + (i + 1) + " 表名称:" + table_name + "（" + table_comment + "）";
                Table table = new Table(8);

                document.add(new Paragraph(""));

                table.setBorderWidth(1);
                table.setPadding(0);
                table.setSpacing(0);

                //添加表头的元素，并设置表头背景的颜色
                Color chade = new Color(176, 196, 222);

                Cell cell = new Cell("编号");
                addCell(table, cell, chade);
                cell = new Cell("字段名称");
                addCell(table, cell, chade);
                cell = new Cell("字段描述");
                addCell(table, cell, chade);
                cell = new Cell("字段类型");
                addCell(table, cell, chade);
                cell = new Cell("字段长度");
                addCell(table, cell, chade);
                cell = new Cell("允许空");
                addCell(table, cell, chade);
                cell = new Cell("是否主键");
                addCell(table, cell, chade);
                cell = new Cell("缺省值");
                addCell(table, cell, chade);


                table.endHeaders();

                // 表格的主体
                for (int k = 0; k < fields.size(); k++) {
                    addContent(table, (k + 1) + "");
                    addContent(table, fields.get(k).getField());
                    addContent(table, fields.get(k).getComment());
                    String type = fields.get(k).getType();
                    String length = StrUtil.subBetween(type, "(", ")");
                    addContent(table, StrUtil.subBefore(type, "(", true));
                    addContent(table, length);
                    addContent(table, fields.get(k).getIsNull().equals("YES") ? "否" : "是");
                    addContent(table, !fields.get(k).getIsKey().equals("") ? "是" : "否");
                    addContent(table, fields.get(k).getDefaultValue());
                }
                Paragraph pheae = new Paragraph(all);
                //写入表说明
                document.add(pheae);
                //生成表格
                document.add(table);
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加表头到表格
     *
     * @param table
     * @param cell
     * @param chade
     */
    private void addCell(Table table, Cell cell, Color chade) {
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(chade);
        table.addCell(cell);
    }

    /**
     * 添加内容到表格
     *
     * @param table
     * @param content
     */
    private void addContent(Table table, String content) {
        Cell cell = new Cell(content);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

}
