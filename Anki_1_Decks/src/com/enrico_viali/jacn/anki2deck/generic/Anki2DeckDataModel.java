package com.enrico_viali.jacn.anki2deck.generic;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.anki1deck.generic.FieldModel;
import com.enrico_viali.jacn.ankideck.generic.AnkiCardModel;
import com.enrico_viali.jacn.ankideck.generic.Fact;
import com.enrico_viali.jacn.ankideck.generic.IAnkiCard;
import com.enrico_viali.jacn.ankideck.generic.IAnkiDeckDataModel;
import com.enrico_viali.jacn.ankideck.generic.IAnkiDeckGeneric;
import com.enrico_viali.jacn.common.FieldUpdate;
import com.enrico_viali.libs.rdb_jdbc.*;
import com.enrico_viali.utils.Utl;

/**
 * 
 * @author it068498
 * 
 */
public class Anki2DeckDataModel implements IAnkiDeckDataModel {

    // protected per implementare il singleton
    public Anki2DeckDataModel(IRDBManager dmdb) throws Exception {
        this.dmdb = dmdb;
    }

    FieldModel buildFieldModelFromRs(ResultSet rs) {
        FieldModel f = new FieldModel();

        try {
            f.name = rs.getString(4);
            f._description = rs.getString(5);
            f._unique = rs.getBoolean(8);
            return f;
        } catch (SQLException e) {
            log.error("", e);
            return null;
        }
    }

    public boolean check() {
        return !uniqueViolation();
    }

    public boolean copyField(String fNameSource, String fNameTarget,
            FieldUpdate updTime) {
        // TODO Auto-generated method stub
        return false;
    }

    List<Long> getFactsIDs(String ModelName) {
        List<Long> ids = new ArrayList<Long>();
        return ids;
    }

    Map<String, FieldModel> getFielModels() {
        HashMap<String, FieldModel> fmodels = new HashMap<String, FieldModel>();

        String s = "SELECT * FROM fieldModels";
        log.debug("eseguo query: " + s);
        if (dmdb.getConnection() == null) {
            log.error("conn(ection) nulla)");
            return null;
        }

        try {
            Statement st = dmdb.getConnection().createStatement();
            ResultSet rs = st.executeQuery(s);
            log.debug("esamino lo statement per capire, st.getMaxRows(): "
                    + st.getMaxRows());
            while (rs.next()) {
                FieldModel f = buildFieldModelFromRs(rs);
                if (f != null)
                    fmodels.put(f.name, f);
            }
            return fmodels;
        } catch (SQLException e) {
            log.error(
                    "fallita query statement era:\n" + s + "\ndbname "
                            + dmdb.getDBName(), e);
            return null;
        }
    }

    boolean uniqueViolation() {
        boolean ret = false;
        Map<String, FieldModel> fmodels = getFielModels();

        String q = "select fieldModels.name, count (*), fields.value from fields, fieldModels"
                + " where fields.fieldModelId = FieldModels.id"
                + " AND fieldModels.name = '";
        String q2 = "' group by fields.value having count (*) > 1 ";

        if (dmdb.getConnection() == null) {
            log.error("conn(ection) nulla)");
            return true;
        }

        String query = "";
        try {
            Statement st = dmdb.getConnection().createStatement();
            for (String s : fmodels.keySet()) {
                if (!fmodels.get(s)._unique) {
                    continue;
                }
                query = q + s + q2;
                ResultSet rs = st.executeQuery(query);
                log.debug("eseguo query: " + query);
                while (rs.next()) {
                    int maxDupl = rs.getInt(2);
                    if (maxDupl > 1) {
                        log.error("fieldModel <" + s
                                + "> contiene fields con valori non unici: "
                                + maxDupl + " val=" + rs.getString(3));
                        ret = true;
                    }
                }
            }
            return ret;
        } catch (SQLException e) {
            log.error("fallita query statement era:\n" + query, e);
            return true;
        }

    }

    boolean factEntryExists(String kanjiWord) {

        String s = "SELECT * FROM " + "SETTA QUESTO PER FARLA FUNZIONARE: NOME TABELLA" + " WHERE "
                + "SETTA QUESTO: NOME CAMPO" + "= '" + kanjiWord + "'";
        log.debug("eseguo query: " + s);
        if (dmdb.getConnection() == null) {
            log.error("conn(ection) nulla)");
            return false;
        }

        try {
            Statement st = dmdb.getConnection().createStatement();
            ResultSet rs = st.executeQuery(s);
            log.debug("esamino lo statement per capire, st.getMaxRows(): "
                    + st.getMaxRows());
            while (rs.next()) {
                log.debug("trovata edictByHeadWord entry per kanji: "
                        + kanjiWord);
                rs = null;
                return true;
            }// end while loop
        } catch (SQLException e) {
            log.error("fallita query kanjword : " + kanjiWord
                    + "statement era:\n" + s, e);
        }

        return false;
    }

    public ArrayList<String> duplicates() {
        ArrayList<String> dups = new ArrayList<String>();

        String s = "select value, count (*) from fields, fieldModels"
                + " where " + " fields.fieldModelId = FieldModels.id "
                + " AND fieldModels.name = 'Expression' "
                + " group by fields.value " + " having count (*) > 1 ";

        log.debug("eseguo query: " + s);
        if (dmdb.getConnection() == null) {
            log.error("conn(ection) nulla)");
            return dups;
        }

        try {
            Statement st = dmdb.getConnection().createStatement();
            ResultSet rs = st.executeQuery(s);
            log.debug("esamino lo statement per capire, st.getMaxRows(): "
                    + st.getMaxRows());
            while (rs.next()) {
                log.debug("trovata edictByHeadWord entry per kanji: "
                        + rs.getString(1));
                dups.add(rs.getString(1));
            }// end while loop
        } catch (SQLException e) {
            log.error("fallita query statement era:\n" + s, e);
        } finally {
        }
        return dups;
    }

    /**
     * @param tabella
     * @return
     * @throws SQLException
     */
    public boolean readFactsDeck(IAnkiDeckGeneric mgr, String keyFieldName) {
        
        
        try {
            readCardModels(mgr);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        
        Statement stFacts;

        if (dmdb == null) {
            log.error("dmdb null)");
            return false;
        }
        if (!dmdb.open(false)) {
            log.error("unable to open DB");
            return false;
        }

        try {
            ArrayList<Long> factIDs = new ArrayList<Long>();
            {
                int nr = 0;
                stFacts = dmdb.getConnection().createStatement();
                ResultSet factsRs = stFacts
                        .executeQuery("SELECT flds FROM notes");
                while (factsRs.next()) {
                    nr++;
                    String fields = factsRs.getString(1);
                    if (nr >= 300 && nr <= 500) {
                        log.info("letto: " + fields);
                    }

                    /*
                     * long factID = factsRs.getLong(1); factIDs.add(factID);
                     * log.debug("letta fact entry " + ", ID: " +
                     * factsRs.getLong(1));
                     */
                }
                if (nr < 200)
                    log.warn("too few facts found: " + nr);
            }
            {
                int nrItems = 0;
                for (Long factID : factIDs) {
                    nrItems++;
                    String fieldsQueryString = "SELECT F1.*,F2.name"
                            + " FROM fields F1,fieldModels F2"
                            + " WHERE F1.fieldModelId=F2.id AND F1.factID = "
                            + factID;
                    if (factID.equals("1130326187")) {
                        log.trace("giusto per mettere i breakpoint su entry anomale");
                    }
                    Statement stFields = dmdb.getConnection().createStatement();
                    ResultSet fieldsRs = stFields
                            .executeQuery(fieldsQueryString);
                    Fact e = mgr.buildFact(factID, keyFieldName);
                    if (e.fillFromRS(fieldsRs, factID)) {
                        log.debug("letto&added fatto: " + e.toString());
                        if (!mgr.addFact(e)) {
                            log.error("failed to insert fact in hashtable, factID: "
                                    + e.getID()
                                    + "query: "
                                    + fieldsQueryString
                                    + "\ndump: " + e.toString());
                            return false;
                        }
                    } else {
                        log.error("failed to fill fact from result set, factID: "
                                + e.getID() + "\nquery: " + fieldsQueryString);
                        return false;
                    }
                }
                log.debug("portato deck in memoria, nr fatti completi: "
                        + nrItems);
            }
        } catch (SQLException e) {
            log.error("fallita query sui fatti o fields ", e);
        } finally {
            dmdb.closeConnection();
        }

        return true;
    }

    public boolean readCardModels(IAnkiDeckGeneric mgr) throws SQLException {
        Statement stFacts;

        if (dmdb == null) {
            log.error("dmdb null)");
            return false;
        }
        if (dmdb.getConnection() == null) {
            log.error("conn(ection) nulla)");
            return false;
        }

        String queryStr = "";
        try {
            ArrayList<Long> cardModelIDs = new ArrayList<Long>();
            {
                int nr = 0;
                queryStr = "SELECT models FROM col";
                stFacts = dmdb.getConnection().createStatement();
                ResultSet modelsRs = stFacts.executeQuery(queryStr);
                while (modelsRs.next()) {
                    nr++;
                    String factID = modelsRs.getString(1);
                    // cardModelIDs.add(factID);
                    log.info("letta cardModel entry " + ", ID: "
                            + modelsRs.getString(1));
                }
                log.info("select ha trovato id nr: " + nr);
            }
        } catch (SQLException e) {
            log.error("fallita query: " + queryStr, e);
        }

        return true;
    }

    public boolean readCards(IAnkiDeckGeneric mgr) throws SQLException {
        Statement stFacts;

        if (dmdb == null) {
            log.error("dmdb null)");
            return false;
        }
        if (dmdb.getConnection() == null) {
            log.error("conn(ection) nulla)");
            return false;
        }
        String queryStr = "";
        try {
            ArrayList<Long> cardsIDs = new ArrayList<Long>();
            {
                int nr = 0;
                stFacts = dmdb.getConnection().createStatement();
                ResultSet cardsRs = stFacts
                        .executeQuery("SELECT id FROM cards");
                while (cardsRs.next()) {
                    nr++;
                    long cardID = cardsRs.getLong(1);
                    cardsIDs.add(cardID);
                    log.debug("letta card entry " + ", ID: "
                            + cardsRs.getLong(1));
                }
                log.info("select ha trovato  nr cards: " + nr);
            }
            {
                int nrItems = 0;
                for (Long id : cardsIDs) {
                    nrItems++;
                    queryStr = "select id, factId, cardModelId, tags, question, answer  from cards where id = "
                            + id;
                    if (id == -1) {
                        log.trace("giusto per mettere i breakpoint su entry anomale");
                    }
                    Statement stFields = dmdb.getConnection().createStatement();
                    ResultSet fieldsRs = stFields.executeQuery(queryStr);
                    IAnkiCard c = new Anki2Card(id, "Kanji");
                    if (c.fillFromRS(fieldsRs, id)) {
                        log.info("letto&added card: " + c.toString());
                        if (!mgr.addCard(c)) {
                            log.error("failed to insert fact in hashtable, factID: "
                                    + c.getID()
                                    + "query: "
                                    + queryStr
                                    + "\ndump: "
                                    + c.toString());
                            return false;
                        }
                    } else {
                        log.error("failed to fill fact from result set, factID: "
                                + c.getID() + "\nquery: " + queryStr);
                        return false;
                    }
                }
                log.info("portato deck in memoria, nr fatti completi: "
                        + nrItems);
            }
        } catch (SQLException e) {
            log.error("fallita query sui fatti o fields ", e);
        }

        return true;
    }

    public boolean updateFieldValue(long fieldId, String newValue) {

        newValue = JDBCEVUtility.escapeForSQL(newValue);
        String sqlupdate = "UPDATE fields SET value = '" + newValue
                + "' WHERE id = " + fieldId;
        // log.info(sqlupdate);
        Connection conn = null;
        try {
            conn = dmdb.getConnection();
            Statement stField = conn.createStatement();
            int ret = stField.executeUpdate(sqlupdate);
            if (ret == 1) {
                // di fatto può aver fallito, per ora non cancello il trace
                // log.info("field " + fieldId + " set value to:\n" + newValue);
            } else {
                log.error("sembra aver fallito statement " + sqlupdate);
            }
            // controllo
            ResultSet rs = stField
                    .executeQuery("SELECT * FROM FIELDS WHERE ID=" + fieldId
                            + "");
            if (rs.next()) {
                String newStoredValue = rs.getString("value");
                if (!newValue
                        .equals(JDBCEVUtility.escapeForSQL(newStoredValue))) {
                    log.error("fallita memorizzazione");
                } else {
                    log.info("adesso field " + rs.getLong("id")
                            + " has value:\n" + rs.getString("value"));
                }
            } else {
                log.error("fallito aggiornamento field value, fieldID: "
                        + fieldId);
            }
            conn.close();
        } catch (SQLException e) {
            log.error("update failed, statement:\n" + sqlupdate, e);
            return false;
        } finally {
        }
        return true;
    }

    protected IRDBManager                  dmdb;

    private static org.apache.log4j.Logger log = Logger
                                                       .getLogger(Anki2DeckDataModel.class);
}