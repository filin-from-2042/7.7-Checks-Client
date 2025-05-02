package com.tradeassistant1c77.dbconnection;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.MyApplication;
import com.tradeassistant1c77.R;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.sql.Connection;
import java.util.Properties;


public class ConnectionPool {
    static final String JDBC_DRIVER = "net.sourceforge.jtds.jdbc.Driver";

    private static GenericObjectPool gPool = null;
    private static DataSource ds = null;

    @SuppressWarnings("unused")
    public void setUpPool() throws Exception {
        if(gPool!=null){
            gPool.clear();
            gPool.close();
        }
        MyApplication myApp = MyApplication.getInstance();
        Context currContext = myApp.getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(currContext);
        String ip = prefs.getString(currContext.getString(R.string.s_key_address), currContext.getString(R.string.s_default_address));
        String db = prefs.getString(currContext.getString(R.string.s_key_db), currContext.getString(R.string.s_default_db));
        String un = prefs.getString(currContext.getString(R.string.s_key_username), currContext.getString(R.string.s_default_username));
        String password = prefs.getString(currContext.getString(R.string.s_key_password), currContext.getString(R.string.s_default_password));
        String port = prefs.getString(currContext.getString(R.string.s_key_port), currContext.getString(R.string.s_default_port));

        Class.forName(JDBC_DRIVER);

        String ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port ;

        Properties props = new Properties();
        props.setProperty("databaseName",db);
        props.setProperty("user",un);
        props.setProperty("password",password);
        props.setProperty("appName ","77checksclient-"+un);
        props.setProperty("sendStringParametersAsUnicode","false");

        gPool = new GenericObjectPool();
        ConnectionFactory cf = new DriverManagerConnectionFactory(ConnURL, props);
        new PoolableConnectionFactory(cf, gPool, null, null, false, true);
        ds = new PoolingDataSource( gPool );
        gPool.setMaxIdle( 5 );//устанавливаем максимальное кол-во простаивающих соединений
        gPool.setMaxActive( 20 );//устанавилваем макс. кол-во активных соединений
    }

    public final Connection getConnection()
    {
        try
        {
            return ds.getConnection();
        }
        catch( Exception ex )
        {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            return null;
        }
    }
    public final void returnConnection( Connection con )
    {
        try
        {
            gPool.returnObject( con );
        }
        catch( Exception ex )
        {
            Crashlytics.logException(ex);
        }
    }
}
