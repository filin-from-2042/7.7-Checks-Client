package com.tradeassistant1c77.dbconnection;


public class IndexDbObject extends DbProgrammObject {
    public IndexDbObject(String iName, String iScheme, String iCreateQuery) {
        super(iName, iScheme, iCreateQuery);
        String[] namePaths = iName.split("_");
        String tableName = namePaths[2];
        if(tableName.substring(0,2).equals("1s")) tableName = "_"+tableName;
        this.dropQuery = "DROP INDEX ["+name+"] ON ["+scheme+"].["+tableName+"] ";
        this.existQuery= "SELECT * FROM sys.indexes WHERE name='"+name+"' AND object_id = OBJECT_ID('["+scheme+"].["+tableName+"]')";
        checkExist();
    }

}
