package com.tradeassistant1c77.Entites;

import com.tradeassistant1c77.Helpers;

// Класс описывающий остаток на одном конкретном складе
public class StoreRemainUnit
{
    String storeID;
    String storeDESCR;
    double unitCount;

    public StoreRemainUnit(String storeID, String storeDESCR, double unitCount )
    {
        super();
        this.storeID = storeID;
        this.storeDESCR = (storeDESCR!=null)?storeDESCR.trim():"";
        this.unitCount = unitCount;
    }

    @Override
    public String toString() {
        return this.storeDESCR + " - " + Helpers.formatNumData(this.unitCount) ;
    }
}