package com.enrico_viali.jacn.ankideck.generic;

import java.sql.SQLException;
import java.util.ArrayList;

import com.enrico_viali.jacn.common.FieldUpdate;

public interface IAnkiDeckDataModel {

    public abstract boolean check();

    public abstract boolean copyField(String fNameSource, String fNameTarget,
            FieldUpdate updTime);

    public abstract boolean readFactsDeck(IAnkiDeckGeneric mgr, String keyFieldName);

    public abstract boolean readCardModels(IAnkiDeckGeneric mgr) throws SQLException;

    public abstract boolean readCards(IAnkiDeckGeneric mgr) throws SQLException;

    public abstract boolean updateFieldValue(long fieldId, String newValue);

    public abstract  ArrayList<String> duplicates();    
    
}