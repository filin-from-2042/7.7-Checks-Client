package com.tradeassistant1c77.Entites;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.DataHolder;
import com.tradeassistant1c77.Helpers;
import com.tradeassistant1c77.MyApplication;
import com.tradeassistant1c77.dbconnection.ConnectionClass;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;


public class Check {

    String id;

    public String getCheckNumber() {
        return checkNumber;
    }

    String checkNumber;

    public LinkedHashMap<String, Product> getNewItems() {
        return newItems;
    }

    public void setNewItems(LinkedHashMap<String, Product> newItems) {
        this.newItems = newItems;
    }

    LinkedHashMap<String,Product> newItems;

    public boolean isNew() {
        return isNew;
    }

    boolean isNew = true;

    public Check()
    {
        super();
        this.initNewCheckNumber();
    }

    public Check(KKMItemView item)
    {
        this.checkNumber = item.getDocno();
        this.id = item.getId();
        this.newItems = (LinkedHashMap<String,Product>)item.getItemsDT();
        isNew = false;
    }

    // Блокировка нового номера чека в БД
    public void initNewCheckNumber()
    {
        Connection connection = ConnectionClass.getConnection();
        try {
         CallableStatement cs = connection.prepareCall("{call sa_sp_initNewCheck(?,?)}");
         cs.setString(1,MyApplication.getCurrentPeriod());
         cs.registerOutParameter(2,Types.CHAR);
         cs.execute();
         this.checkNumber = cs.getString(2);
        cs.close();
        }
        catch (Exception ex){
            Crashlytics.logException(ex);
        }
        finally {
            try {
                if(connection!=null) connection.close();
            }catch (SQLException ex){
                Crashlytics.logException(ex);
            }
        }
    }

    // Удаление блокировки на номер чека в БД
    public void checkNumberUnlock(boolean fullRemove)
    {
        Connection connection = ConnectionClass.getConnection();
        try {

            CallableStatement cs = connection.prepareCall("{call sa_sp_BlockDocNumber(?,?,?,?)}");
            cs.setString(1,null);
            cs.setString(2,this.checkNumber);
            cs.setString(3,MyApplication.getCurrentPeriod());
            cs.setInt(4,0);
            cs.execute();

            if(fullRemove) this.checkNumber = null;
            cs.close();
        }
        catch (Exception ex){
            Crashlytics.logException(ex);
        }
        finally {
            try {
                if(connection!=null) connection.close();
            }catch (SQLException ex){
                Crashlytics.logException(ex);
            }
        }
    }
    // сохранения нового чека со всеми данными в БД
    public String save()
    {
        if(isNew) return createCheck();
        else return updateCheck();
    }

    protected String createCheck()
    {
        if(this.checkNumber ==null ) return CheckAddStatus.AddStatus.get(7);

        Connection connection = ConnectionClass.getConnection();
        if(DataHolder.getData("LoggedUser")==null){
            if(!Helpers.reinitUserBySavedData(connection)){
                try{
                    connection.close();
                }catch (Exception ex){
                    Crashlytics.logException(ex);
                    ex.printStackTrace();
                }
                return CheckAddStatus.AddStatus.get(6);
            }
        }
        User currUser = (User) DataHolder.getData("LoggedUser");

        int createCheckStatus = -1;
        try{
            CallableStatement cs = connection.prepareCall("{call sa_sp_CreateNewCheck(?,?,?,?,?,?,?,?,?,?)}");
            cs.setString(1,this.checkNumber);
            cs.setString(2,currUser.getStockID());
            cs.setString(3,currUser.getCashBoxID());
            cs.setString(4,this.getItemsCosts().toString());
            cs.setString(5,currUser.getUserID());
            cs.setString(6,currUser.getCompanyID());
            cs.setString(7,currUser.getCurrProject());
            cs.setString(8,MyApplication.getCurrentPeriod());
            cs.registerOutParameter(9, Types.CHAR);
            cs.registerOutParameter(10, Types.INTEGER);
            cs.execute();

            createCheckStatus = cs.getInt(10);
            if(createCheckStatus != 0 ) return CheckAddStatus.AddStatus.get(createCheckStatus);
            else
            {
                this.id = cs.getString(9);
                checkNumberUnlock(false);
                isNew = false;
            }
            cs.close();
        }
        catch (Exception ex)
        {
            Crashlytics.logException(ex);
            try {
                connection.close();
            }catch (SQLException exSql){
                Crashlytics.logException(ex);
            }
            return "Проблемы с соединением";
        }

        if(this.newItems!=null && this.newItems.size()>0) {
            Integer lineno = 1;
            try{
                CallableStatement cs = connection.prepareCall("{call sa_sp_addCheckDT(?,?,?,?,?)}");
                for (LinkedHashMap.Entry<String, Product> entry : this.newItems.entrySet()) {
                    Product product = entry.getValue();
                    cs.setString(1,this.id);
                    cs.setString(2,lineno.toString());
                    cs.setString(3,product.id);
                    cs.setString(4,String.valueOf(product.count));
                    cs.setString(5,product.price.toString());
                    cs.execute();
                    lineno++;
                }
                cs.close();
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                try {
                    connection.close();
                }catch (SQLException exSql){
                    Crashlytics.logException(ex);
                }
                return "Проблемы с соединением";
            }
        }
        try {
            connection.close();
        }catch (SQLException ex){
            Crashlytics.logException(ex);
        }

        return CheckAddStatus.AddStatus.get(createCheckStatus);
    }

    protected String updateCheck()
    {
        if(this.checkNumber ==null ) return CheckAddStatus.AddStatus.get(5);
        Connection connection = ConnectionClass.getConnection();
        if(DataHolder.getData("LoggedUser")==null){
            if(!Helpers.reinitUserBySavedData(connection)){
                try{
                    connection.close();
                }catch (Exception ex){
                    Crashlytics.logException(ex);
                    ex.printStackTrace();
                }
                return CheckAddStatus.UpdateStatus.get(3);
            }
        }
        int updateCheckStatus = 0;
        try{
            CallableStatement cs = connection.prepareCall("{call sa_sp_UpdateExistCheck(?,?,?)}");
            cs.setString(1,this.id);
            cs.setString(2,this.getItemsCosts().toString());
            cs.registerOutParameter(3,Types.INTEGER);
            cs.execute();
            updateCheckStatus = cs.getInt(3);
            cs.close();
            if(updateCheckStatus != 0 ) return CheckAddStatus.UpdateStatus.get(updateCheckStatus);
        } catch (Exception ex)
        {
            Crashlytics.logException(ex);
            try {
                connection.close();
            }catch (SQLException exSql){
                Crashlytics.logException(ex);
            }
            return "Проблемы с соединением";
        }
        removeCheckDT();

        if(this.newItems!=null && this.newItems.size()>0) {
            Integer lineno = 1;
            try {
                CallableStatement cs = connection.prepareCall("{call sa_sp_addCheckDT(?,?,?,?,?)}");
                for (LinkedHashMap.Entry<String, Product> entry : this.newItems.entrySet()) {
                    Product product = entry.getValue();
                    cs.setString(1,this.id);
                    cs.setString(2,lineno.toString());
                    cs.setString(3,product.id);
                    cs.setString(4,String.valueOf(product.count));
                    cs.setString(5,product.price.toString());
                    lineno++;
                    cs.execute();
                }
                cs.close();
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                try {
                    connection.close();
                }catch (SQLException exSql){
                    Crashlytics.logException(ex);
                }
                return "Проблемы с соединением";
            }
        }
        try {
            connection.close();
        }catch (SQLException ex){
            Crashlytics.logException(ex);
        }
        return CheckAddStatus.UpdateStatus.get(updateCheckStatus);
    }

    // Возвращает полную стоимость чека исходя из добавленного кол-ва товаров и их стоимости
    public Double getItemsCosts()
    {
        Double costs = 0d;
        if(this.newItems!=null && this.newItems.size()>0)
        {
            for (LinkedHashMap.Entry<String, Product> entry : this.newItems.entrySet()) {
                Product product = entry.getValue();
                costs += product.price*product.count;
            }
        }
        return costs;
    }
    // заполняет остатки у товаров в чеке
    public void fillItemsRemains()
    {
        if(this.newItems!=null && this.newItems.size()>0)
        {
            for (LinkedHashMap.Entry<String, Product> entry : this.newItems.entrySet()) {
                Product product = entry.getValue();
                product.fillRemains();
            }
        }
    }

    public void addNewItem(Product productItem)
    {
        newItems.put(productItem.id, productItem);
    }
    // Обновляет кол-во товара в чеке по переданному идентификатору
    public boolean updateItemCount(String productID, Double newCount)
    {
        if (this.newItems != null && this.newItems.size()>0) {
            LinkedHashMap<String, Product> Items = this.newItems;
            Product editingProduct = Items.get(productID);
            editingProduct.count = newCount;
            Items.put(productID, editingProduct);
            this.newItems = Items;
            return true;
        }else return false;
    }
    // удаляет товара из чека по переданнму идентификатору
    public void removeItemById(String itemID)
    {
        if (this.newItems != null && this.newItems.size()>0) {
            this.newItems.remove(itemID);
        }
    }

    // пометка на удаление чека в БД
    public static void remove(String checkID)
    {
        Connection connection = ConnectionClass.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE [_1SJOURN] SET [ISMARK] = 1 WHERE IDJOURNAL=1970 AND IDDOCDEF=1473 AND ISMARK=0 AND IDDOC = ?");
            ps.setString(1,checkID);
            ps.executeUpdate();
            ps.close();
        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
        finally {
            try {
                if(connection!=null) connection.close();
            }catch (SQLException ex){
                Crashlytics.logException(ex);
            }
        }
    }

    public void removeCheckDT()
    {
        Connection connection = ConnectionClass.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM [DT1473] WHERE [IDDOC] = ?");
            ps.setString(1,this.id);
            ps.executeUpdate();
            ps.close();
        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
        finally {
            try {
                if(connection!=null) connection.close();
            }catch (SQLException ex){
                Crashlytics.logException(ex);
            }
        }
    }
}