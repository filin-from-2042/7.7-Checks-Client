package com.tradeassistant1c77.Entites;
import java.util.Date;

public class CheckListItem {
    String id;
    Date checkDate;

    public String getId() {
        return id;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public int getDocType() {
        return docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public Double getSumm() {
        return summ;
    }

    public String getCreator() {
        return creator;
    }

    public boolean isClosed() {
        return closed;
    }

    int docType;
    String docNumber;
    Double summ;
    String creator;
    boolean closed;

    public CheckListItem(String id, Date checkDate, int docType, String docNumber, Double summ, String creator, int closed) {
        this.id = id;
        this.checkDate = checkDate;
        this.docType = docType;
        this.docNumber = docNumber;
        this.summ = summ;
        this.creator = creator;
        this.closed = (closed!=0);
    }
}
