package com.tradeassistant1c77.Entites;


import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.DataHolder;
import com.tradeassistant1c77.dbconnection.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class KKMItemView {

    private String id;
    // номер документа типа 'ОСк0000001'
    private String docno;
    private int type;
    private Date date;
    private String companyName;
    private String stockName;
    private String cashboxkName;
    private float summ;
    private float summNDS;
    private String userID;
    private String username;
    private String cause;
    private boolean closed;

    private boolean editable = false;

    private LinkedHashMap<String,Product> itemsDT;


    public String getId() {
        return id;
    }

    public String getDocno() {
        return docno;
    }

    public int getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getCompanyName() {
        return  companyName==null ? "" : companyName.trim();
    }

    public String getStockName() {
        return stockName==null ? "" : stockName.trim();
    }

    public String getCashboxkName() {
        return cashboxkName==null ? "" : cashboxkName.trim();
    }

    public float getSumm() {
        return summ;
    }

    public float getSummNDS() {
        return summNDS;
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username==null ? "" : username.trim();
    }

    public String getCause() {
        return cause==null ? "" : cause.trim();
    }

    public boolean isClosed() {
        return closed;
    }

    public Map<String, Product> getItemsDT() {
        return itemsDT;
    }
    public boolean isEditable() {
        return editable;
    }


    public KKMItemView(String id) {
        this.id = id;
        Connection connection = ConnectionClass.getConnection();

        ResultSet rsDH = getItemDH(connection, id);
        try {
            while (rsDH.next()) {

                this.docno = rsDH.getString(1).trim();
                this.type = rsDH.getInt(2);
                this.date = rsDH.getDate(3);
                this.companyName = rsDH.getString(4);
                this.stockName = rsDH.getString(5);
                this.cashboxkName = rsDH.getString(6);
                this.summ = rsDH.getFloat(7);
                this.summNDS = rsDH.getFloat(8);
                this.userID = rsDH.getString(9);
                this.username = rsDH.getString(10);
                this.cause = rsDH.getString(11);
                this.closed = rsDH.getInt(12) != 0;
            }
            rsDH.close();
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }

        ResultSet rsDT = getItemDT(connection, id);
        this.itemsDT = new LinkedHashMap<>();
        try {
            while (rsDT.next()) {
                itemsDT.put(rsDT.getString(1), new Product(rsDT.getString(1), rsDT.getString(2), rsDT.getString(4), rsDT.getString(3), rsDT.getDouble(5), rsDT.getDouble(6)));
            }
            rsDT.close();
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
        finally {
            try {
                if(connection!=null) connection.close();
            }catch (SQLException ex){
                Crashlytics.logException(ex);
            }
        }

        // определения возможности редактирования
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        User usr = (User) DataHolder.getData("LoggedUser");
        // редактировать можно только чек, на текущую дату, не проведенный и только создателем
        if(date!=null && date.compareTo(cal.getTime())==0 ){
            // заполнять остатки только для сегодняшнего чека
            fillDTRemains();
            if(type== DocumentTypes.CHECK && !closed && userID.equalsIgnoreCase(usr.getUserID())) editable = true;
        }
    }

    private ResultSet getItemDH(Connection connection,String id)
    {
        String query = "SELECT docno, docType, docDate date, companyName, stockName, cashboxName, summ, sumNDS, userID, userName, cause, closed" +
                " FROM [sa_udf_GetKKMItemDH](?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,id);
            return ps.executeQuery();
        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
        return null;
    }

    private ResultSet getItemDT(Connection connection, String id)
    {
        String query = "SELECT product_id, code, articul, name, item_count, item_price" +
                " FROM [sa_udf_GetKKMItemDT]('"+id+"')";

        try{
            PreparedStatement ps = connection.prepareStatement(query);
            return ps.executeQuery();

        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
        return null;
    }
    // заполняет остатки у товаров в табличной части
    public void fillDTRemains()
    {
        if(itemsDT!=null && itemsDT.size()>0)
        {
            for (LinkedHashMap.Entry<String, Product> entry : itemsDT.entrySet()) {
                Product product = entry.getValue();
                product.fillRemains();
            }
        }
    }
}