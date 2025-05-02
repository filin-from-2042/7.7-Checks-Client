package com.tradeassistant1c77.Entites;


import com.crashlytics.android.Crashlytics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User {

    private String userID;
    private String userName;
    private String stockID;
    private String stockName;
    private String companyID;
    private String companyName;
    private String cashBoxID;
    private String cashBoxName;
    private String currProject;
    private String checkCompanyID;

    public String getUserID() {
        return (userID!=null)?userID:"";
    }

    public String getUserName() {
        return (userName!=null)?userName.trim():"";
    }

    public String getStockID() {
        return (stockID!=null)?stockID:"";
    }

    public String getStockName() {
        return (stockName!=null)?stockName.trim():"";
    }

    public String getCompanyID() {
        return (companyID!=null)?companyID:"";
    }

    public String getCompanyName() {
        return (companyName!=null)?companyName.trim():"";
    }

    public String getCashBoxID() {
        return (cashBoxID!=null)?cashBoxID:"";
    }

    public String getCashBoxName() {
        return (cashBoxName!=null)?cashBoxName.trim():"";
    }

    public String getCurrProject() {
        return (currProject!=null)?currProject:"";
    }

    public String getCheckCompanyID() {
        return (checkCompanyID!=null)?checkCompanyID:"";
    }


    public User(Connection con, String login, String pwd)
    {
        String query = "SELECT TOP 1 US.ID AS  USERID, " +
                "US.DESCR AS USERNAME, " +
                "ST.ID AS STOCKID, " +
                "ST.DESCR AS STOCKNAME, " +
                "CMP.ID AS COMPANYID, " +
                "CMP.DESCR AS COMPANYNAME, " +
                "CB.ID AS CASHBOXID," +
                "CB.DESCR AS CASHBOXNAME, " +
                "US.SP190 AS CURRENTPROJECT " +
                "FROM [SC201] US " +
                "LEFT JOIN [SC288] ST ON ST.ID=US.SP191 AND ST.ISMARK=0 " +
                "LEFT JOIN [SC321] CMP ON CMP.ID=US.SP187 AND CMP.ISMARK=0 " +
                "LEFT JOIN [SC106] CB ON CB.ID=US.SP183 AND CB.ISMARK=0 " +
                "WHERE LTRIM(RTRIM(US.CODE)) = ? AND US.ISMARK = 0 AND LTRIM(RTRIM(US.SP194))= ? " +
                "ORDER BY US.ROW_ID DESC";
        try {
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1,login);
                ps.setString(2,pwd);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    userID = rs.getString(1);
                    userName = rs.getString(2);
                    stockID = rs.getString(3);
                    stockName = rs.getString(4);
                    companyID = rs.getString(5);
                    companyName = rs.getString(6);
                    cashBoxID = rs.getString(7);
                    cashBoxName = rs.getString(8);
                    currProject = rs.getString(9);
                }
                rs.close();
                ps.close();
            } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
    }
}