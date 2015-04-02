package com.enrico_viali.jacn.ankideck.generic;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IAnkiCard {

    public abstract long getID();

    public abstract String getName();
    
    public abstract String questionToString();

    public abstract String toString();

    public abstract String getQuestionFormat();

    public abstract String getAnswerFormat();

    public abstract boolean fillFromRS(ResultSet rs, long id) throws SQLException;

    public abstract long getFactId();

    public abstract long getCardModelId();

}