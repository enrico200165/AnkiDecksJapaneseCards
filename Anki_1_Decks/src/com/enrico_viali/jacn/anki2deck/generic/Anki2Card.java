package com.enrico_viali.jacn.anki2deck.generic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.IAnkiCard;

public class Anki2Card implements IAnkiCard {

    Anki2Card(long dummy1, String dummy2) {

    }

    @Override
    public long getID() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return 0;
    }

    @Override
    public String getName() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return null;
    }

    @Override
    public String getQuestionFormat() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return null;
    }

    @Override
    public String getAnswerFormat() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return null;
    }

    @Override
    public boolean fillFromRS(ResultSet rs, long id) throws SQLException {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return false;
    }

    @Override
    public String questionToString() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return null;
    }

    @Override
    public long getFactId() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return 0;
    }

    private static org.apache.log4j.Logger log = Logger
                                                       .getLogger(Anki2Card.class);

    @Override
    public long getCardModelId() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return 0;
    }

}
