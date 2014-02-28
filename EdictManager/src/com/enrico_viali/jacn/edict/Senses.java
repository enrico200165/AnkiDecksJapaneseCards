package com.enrico_viali.jacn.edict;

import java.util.ArrayList;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

/**
 * Esiste unicamente per costringere all'analisi delle (...) nelle definizioni
 * di significato altrimenti avrebbe potuto esserci una lista di stringhe
 * direttamente dentro l'entry,
 * 
 * @author enrico
 */
public class Senses {

    public Senses(Edict_2_Entry e) {
        super();
        reset(e);
    }

    void reset(Edict_2_Entry e) {
        this._sensesStrs = new ArrayList<String>();
        _mother = e;

    }

    public ArrayList<String> getSenses()  {
        return this._sensesStrs;
    }
    
    public String get(int i) {
        if (i < 0 || i >= _sensesStrs.size()) {
            String msg = "indice fuori range: " + i + " ci sono solo: "
                    + _sensesStrs.size();
            log.error(msg);
            System.exit(-1);
            return "";
        }
        return _sensesStrs.get(i);
    }

    public void add(String sense) {
        Matcher m = EdictUtils.infoPattern.matcher(sense);
        while (m.find()) {
            String info = m.group(1);
            if (info.equals("P"))
                _mother.setIsFrequent(true);
            if (info.equals("p")) {
                log.error("found lowercase: " + info + "exit");
                System.exit(1);
            } else if (info.equals("n"))
                _mother.setIsPlainNoun(true);

            else if (info.equals("v1"))
                _mother.setIsVerb(true);
            else if (info.equals("v5b"))
                _mother.setIsVerb(true);
            else if (info.equals("v5g"))
                _mother.setIsVerb(true);
            else if (info.equals("v5k"))
                _mother.setIsVerb(true);
            else if (info.equals("v5m"))
                _mother.setIsVerb(true);
            else if (info.equals("v5n"))
                _mother.setIsVerb(true);
            else if (info.equals("v5r"))
                _mother.setIsVerb(true);
            else if (info.equals("v5s"))
                _mother.setIsVerb(true);
            else if (info.equals("v5t"))
                _mother.setIsVerb(true);
            else if (info.equals("v5u"))
                _mother.setIsVerb(true);
            else if (info.equals("v5z"))
                _mother.setIsVerb(true);
            else if (info.equals("vi"))
                _mother.setIsVerb(true);
            else if (info.equals("vn"))
                _mother.setIsVerb(true);
            else if (info.equals("vt"))
                _mother.setIsVerb(true);

            else if (info.equals("adj"))
                _mother.setIsAdjective(true);

            // --- those that are not good for a learner
            else if (info.equals("exp"))
                _mother.setBadForLearner(true);
            else if (info.equals("X"))
                _mother.setBadForLearner(true);
            else if (info.equals("arch"))
                _mother.setBadForLearner(true);
            else if (info.equals("chn"))
                _mother.setBadForLearner(true);
            else if (info.equals("col"))
                _mother.setBadForLearner(true);
            else if (info.equals("derog"))
                _mother.setBadForLearner(true);
            else if (info.equals("fam"))
                _mother.setBadForLearner(true);
            else if (info.equals("fem"))
                _mother.setBadForLearner(true);
            else if (info.equals("hon"))
                _mother.setBadForLearner(true);
            else if (info.equals("hum"))
                _mother.setBadForLearner(true);
            else if (info.equals("m-sl"))
                _mother.setBadForLearner(true);
            else if (info.equals("male-sl"))
                _mother.setBadForLearner(true);
            else if (info.equals("obs"))
                _mother.setBadForLearner(true);
            else if (info.equals("obsc"))
                _mother.setBadForLearner(true);
            else if (info.equals("on-mim"))
                _mother.setBadForLearner(true);
            else if (info.equals("poet"))
                _mother.setBadForLearner(true);
            else if (info.equals("pol"))
                _mother.setBadForLearner(true);
            else if (info.equals("sens"))
                _mother.setBadForLearner(true);
            else if (info.equals("sl"))
                _mother.setBadForLearner(true);
            else if (info.equals("vulg"))
                _mother.setBadForLearner(true);
            else {
                System.out.print(""); // debug
            }
        }
        _sensesStrs.add(sense);
    }

    
    
    /**
     * 
     * @param maxNrSenses
     * @return
     */
    public String getFewSimpleSenses(int maxNrSenses, int maxChars) {
        String s = "";
               
        int foundNr = 0;                
        for (String sense: _sensesStrs) {
            
            if (sense.matches(".*standpoint.*")) // debug
                System.out.print("");
            
            String cleaned = sense.replaceAll("\\([^)]*\\)", "");
            if (foundNr >= 1)
                s+= " / "; // reinserisci la / rimossa dal match
            s += cleaned.split("/")[1];            
            if (s.length() >= maxChars)
                break;
            foundNr++;
            if (foundNr >= maxNrSenses)
                break;
        }
        
        // s = s.replaceFirst("[^/]*/ *","");
        return s;
    }
    
    Edict_2_Entry                          _mother;
    public ArrayList<String>               _sensesStrs;
    private static org.apache.log4j.Logger log = Logger.getLogger(Senses.class);
}
