package com.tradeassistant1c77.Entites;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.MyApplication;
import com.tradeassistant1c77.R;
import com.tradeassistant1c77.dbconnection.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Product implements Parcelable {
    ///TODO: получение id должно быть через  get
    public String id;
    private String code;
    private String articul;
    private String name;
    public Double count = 1.0;

    public Double getPrice() {
        return price==null ? 0d : price;
    }

    public Double price;
    public ArrayList<StoreRemainUnit> remains;

    public String getCode() {
        return code==null ? "" : code.trim();
    }

    public String getArticul() {
        return articul==null ? "" : articul.trim();
    }

    public String getName() {
        return name==null ? "" : name.trim();
    }


    public Product(String id, String code, String name, String articul, Double count, Double price ) {
        this.id = id;
        this.code = code;
        this.articul = articul;
        this.name = name;
        this.count = count;
        this.price = price;
    }
    public Product(String id, String code, String name, String articul) {
        super();
        this.id = id;
        this.code = code;
        this.articul = articul;
        this.name = name;
    }
    // строка с остатками через разделитель
    public String getRemainsStr()
    {
        String remainsText ="";
        int counter = 0;
        if(this.remains!=null) {
            for (StoreRemainUnit remain : this.remains) {
                if (remain.unitCount > 0) {
                    if (counter > 0) remainsText += " | ";
                    remainsText += remain.toString();
                    counter++;
                }
            }
        }
        if(remainsText.isEmpty()) remainsText = MyApplication.getInstance().getResources().getString(R.string.tm_check_remains_empty);
        return remainsText;
    }
    /**
     * возвращает остатки товара в виде массива строк
     */
    @Nullable
    public String[] getRemainsArray()
    {
        String[] strRemains = null;
        if(remains!=null && remains.size()>0) {
            strRemains = new String[this.remains.size()];
            int counter = 0;
            for (StoreRemainUnit remain :
                    remains) {
                strRemains[counter] = remain.toString();
                counter++;
            }
        }
        return strRemains;
    }

    public void fillPrice()
    {
        Connection connection = ConnectionClass.getConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT NOMPRICE FROM [sa_udf_GetNomenclatureData](?)");
            ps.setString(1,this.id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                this.price =  rs.getDouble(1);
            }
            rs.close();
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

    public void fillRemains()
    {
        Connection connection = ConnectionClass.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT STOREID, STORENAME, UNITCOUNT FROM [sa_udf_GetUnitRemains](?)");
            ps.setString(1,this.id);
            ResultSet rs = ps.executeQuery();
            if(this.remains == null) this.remains = new ArrayList<>();
            while (rs.next()) {
                StoreRemainUnit remain = new StoreRemainUnit(rs.getString(1),rs.getString(2), rs.getDouble(3));
                this.remains.add(remain);
            }
            rs.close();
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

    private Product(Parcel in) {
        id = in.readString();
        code = in.readString();
        articul = in.readString();
        name = in.readString();
        count = in.readDouble();
        price = in.readDouble();
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return getCode() + ": " + getName() ;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(getCode());
        out.writeString(getArticul());
        out.writeString(getName());
        out.writeDouble(count);
        out.writeDouble(price);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}