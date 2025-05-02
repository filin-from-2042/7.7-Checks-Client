package com.tradeassistant1c77.dbconnection;

public class UDFDbObject extends DbProgrammObject  {
    public UDFDbObject(String iName, String iScheme, String iCreateQuery) {
        super(iName, iScheme, iCreateQuery);
        this.dropQuery = "DROP FUNCTION "+name;
        this.existQuery= "SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'["+name+"]') AND type in (N'FN', N'IF', N'TF')";
        checkExist();
    }
}
