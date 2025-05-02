package com.tradeassistant1c77.dbconnection;


public class SPDbObject extends DbProgrammObject {
    public SPDbObject(String iName, String iScheme, String iCreateQuery) {
        super(iName, iScheme, iCreateQuery);
        this.dropQuery = "DROP PROCEDURE "+name;
        this.existQuery= "SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'["+name+"]') AND type in (N'P', N'PC')";
        checkExist();
    }
}
