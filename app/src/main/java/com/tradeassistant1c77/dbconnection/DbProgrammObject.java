package com.tradeassistant1c77.dbconnection;


import com.crashlytics.android.Crashlytics;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbProgrammObject {
    protected String scheme;
    protected String name;
    protected Boolean exist = false;
    protected String dropQuery;
    protected String createQuery;
    protected String existQuery;

    public String getName() {
        return name;
    }

    public Boolean isExist() {
        return exist;
    }

    public DbProgrammObject(String iName, String iScheme, String iCreateQuery) {
        this.name = iName;
        this.scheme = iScheme;
        this.createQuery = iCreateQuery;
    }

    public void createOnServer()
    {
        Connection connection = ConnectionClass.getConnection();
        if(createQuery != null && connection!=null) {
            try {
                Statement st = connection.createStatement();
                st.execute(createQuery);
                st.close();
            } catch (Exception ex) {
                Crashlytics.logException(ex);
            } finally {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Crashlytics.logException(ex);
                }
            }
        }
    }

    public void dropOnServer()
    {
        Connection connection = ConnectionClass.getConnection();
        if(dropQuery != null && existQuery!=null && connection!=null) {
            try {
                Statement st = connection.createStatement();
                String fullQuery = "IF EXISTS ("+existQuery+")\n" +
                        dropQuery;
                st.execute(fullQuery);
                st.close();
            } catch (Exception ex) {
                Crashlytics.logException(ex);
            } finally {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Crashlytics.logException(ex);
                }
            }
        }
    }

    public void recreateOnServer(){
        dropOnServer();
        createOnServer();
    }

    public void checkExist()
    {
        Connection connection = ConnectionClass.getConnection();
        if(existQuery != null && connection!=null) {
            try {
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(existQuery);
                while (rs.next()) {
                    exist = true;
                }
                rs.close();
                st.close();
            } catch (Exception ex) {
                Crashlytics.logException(ex);
            } finally {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Crashlytics.logException(ex);
                }
            }
        }

    }
}
