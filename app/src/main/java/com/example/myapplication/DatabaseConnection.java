package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection conn = null;
    private Activity activity;
    private boolean isReady=false;
    public DatabaseConnection(Activity activity) {
        this.activity=activity;



        Runnable r = () -> {

            String connectionUrl = "jdbc:mariadb://database-1.cu6hfzfkqzau.eu-west-3.rds.amazonaws.com:3306/emotionsdb";

            try {

                conn = DriverManager.getConnection(connectionUrl, "admin", "****");


            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("error");
            }
            isReady=true;
        };
        Thread t = new Thread(r);
        t.start();


    }

    public boolean isReady() {
        return isReady;
    }

    public void insertEmotion(String emotion, float ex, float ey, double latitude, double longtitude, float x, float z, String appSessionId) throws SQLException {

        try {
            String query = "INSERT INTO `emotions`( `emotion`,  `emotionx`, `emotiony`, `longtitude`, `latitude`,accelerationX,accelerationZ,appSessionId) VALUES (?,?,?,?,?,?,?,?)";
            if (conn == null) {
                System.out.println("did not connect to db");
                return;
            }
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, emotion);
            pstmt.setFloat(2, ex);
            pstmt.setFloat(3, ey);
            pstmt.setDouble(4, longtitude);
            pstmt.setDouble(5, latitude);
            pstmt.setFloat(6, x);
            pstmt.setFloat(7, z);
            pstmt.setString(8, appSessionId);
            Runnable thread = () -> {
                try {
                    pstmt.executeQuery();
                } catch (SQLException e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                    alertDialog.setTitle("database error");
                    alertDialog.setMessage(e.getMessage());
                    alertDialog.show();
                    //throw new RuntimeException(e);


                }

            };
            Thread t = new Thread(thread);
            t.start();



        } catch (SQLException e) {
            //throw new RuntimeException(e);
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("database error");
            alertDialog.setMessage(e.getMessage());
            alertDialog.show();
        }
    }
}
