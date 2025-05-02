package com.tradeassistant1c77.dbconnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.DataHolder;
import com.tradeassistant1c77.MyApplication;
import com.tradeassistant1c77.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;

public class ConnectionClass {

    public static boolean checkAvailability()
    {
        MyApplication myApp = MyApplication.getInstance();
        Context currContext = myApp.getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(currContext);
        String ip = prefs.getString(currContext.getString(R.string.s_key_address), currContext.getString(R.string.s_default_address));
        String port = prefs.getString(currContext.getString(R.string.s_key_port), currContext.getString(R.string.s_default_port));
        boolean checkResult = false;
        int portInt = Integer.parseInt(port);
        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, portInt);
            Socket sock = new Socket();
            int timeoutMs = 2000;   // 2 seconds
            sock.connect(sockaddr, timeoutMs);
            checkResult = true;
            sock.close();
        } catch(Exception e) {
            Crashlytics.logException(e);
        }

        return checkResult;
    }

    public static void initConnectionPool()
    {
        ConnectionPool pool = new ConnectionPool();
        try {
            pool.setUpPool();
            DataHolder.setData("ConnectionPool",pool);
        }
        catch (Exception ex)
        {
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }
    }

    public static boolean isPoolExist()
    {
        return DataHolder.getData("ConnectionPool") != null;
    }

    public static Connection getConnection()
    {
        Connection con;
        if(ConnectionClass.isPoolExist())
        {
            ConnectionPool pool = (ConnectionPool)DataHolder.getData("ConnectionPool");
            con = pool.getConnection();
            if(con==null){
                initConnectionPool();
                pool = (ConnectionPool)DataHolder.getData("ConnectionPool");
                con = pool.getConnection();
            }
        } else{
            initConnectionPool();
            ConnectionPool pool = (ConnectionPool)DataHolder.getData("ConnectionPool");
            con = pool.getConnection();
        }

        return con;
    }

    public static void returnConnection(Connection connection)
    {
        if(ConnectionClass.isPoolExist())
        {
            ConnectionPool pool = (ConnectionPool)DataHolder.getData("ConnectionPool");
            pool.returnConnection(connection);
        }
    }

    /**
     * создание хранмых процедур и функций на сервере
     */
    public static void initDbFunctionality(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String scheme = prefs.getString(context.getString(R.string.s_key_scheme), context.getString(R.string.s_default_scheme));
        AssetManager manager = context.getAssets();
        try {
            String[] functionsDbList = manager.list("sql_sources");
            for (String fileName : functionsDbList)
            {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(manager.open("sql_sources/"+fileName)));
                String line;
                String query = "";
                while ((line = reader.readLine()) != null) {
                    line= line.replace("[dbo]","["+scheme+"]");
                    query += " \n " + line;
                }

                String[] namePaths = fileName.split("_");

                switch (namePaths[1]) {
                    case "sp": {
                        SPDbObject sp = new SPDbObject(fileName,scheme,query);
                        sp.recreateOnServer();
                    }
                    break;
                    case "udf": {
                        UDFDbObject udf = new UDFDbObject(fileName,scheme,query);
                        udf.recreateOnServer();
                    }
                    break;
                    case "i": {
                        /*IndexDbObject index = new IndexDbObject(fileName,scheme,query);
                        index.recreateOnServer();*/
                    }
                    break;
                }

            }
        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
    }
}