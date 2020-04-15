package com.example.junior.database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {
    private static final String TAG = "DbConnector";
    
    private Connection con;
    private String dbDriver = "com.mysql.cj.jdbc.Driver";
    private String dbUser = "root";
    private String dbPsw = "87654321";
    //这里后面加上一串是因为在使用mysql的jdbc驱动最新版（6.0+）版本时，数据库和系统时区差异引起的问题。
    private String dbUrl = "jdbc:mysql://192.168.142.1:3306/xcglxt?serverTimezone=UTC&characterEncoding=utf-8";
 //   private String dbUrl = "jdbc:mysql://localhost:3306/xcglxt";
    public Connection getConnection(){
        try {
            Class.forName(dbDriver);
            con= DriverManager.getConnection(dbUrl,dbUser,dbPsw);
//            System.out.println("连接成功");
            Log.d(TAG, "getConnection: "+"数据库连接成功");
        } catch (ClassNotFoundException e) {
            // TODO 自动生成的 catch 块
            Log.e(TAG, e.getMessage() );
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            Log.e(TAG, e.getMessage() );
            e.printStackTrace();
        }
        return con;

    }
}
