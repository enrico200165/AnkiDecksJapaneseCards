package com.enrico_viali.jacn.edict;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.CJKUtils;

/**
 * @author enrico
 * 
 */
public class Edict2K_Ele implements Comparable<Edict2K_Ele> {

    public Edict2K_Ele(Edict_2_Entry motherEntry) {
        super();
        reset(motherEntry);
    }

    public void reset(Edict_2_Entry motherEntry) {
        this._motherEntry = motherEntry;
        this._keb = "";
        _readings = new ArrayList<String>();
        this._annotations = new ArrayList<String>();
        _isP = false;
        _badExample = false;
        this.obsKanj = false;
    }

    public String toString() {
        String s = "";
        s = "kanji: " + this._keb;
        for (String a : this._annotations) {
            s += a;
        }
        for (String kana : _readings) {
            s += " [" + kana + "]";
        }
        return s;
    }

    public String getEntSeq() {
        return this._motherEntry.getEntSeq();
    }

    public boolean isBadExample() {
        return _badExample;
    }

    public void setBadExample(boolean val) {
        _badExample = val;
    }

    public String getSerial() {
        return _motherEntry.getEntSeq();
    }

    public void addAnnotation(String ann) {
        if (!ann.matches("\\(.*\\)")) {
            log.error("valore non valido, atteso (...), hai cambiato il codice esterno? Esco");
            System.exit(1);
        }

        if (ann.equals("(P)")) {
            setIsP(true);

        } else if (ann.equals("(ik)")) {
            setBadExample(true);
        } else if (ann.equals("(iK)")) {
            setBadExample(true);
        } else if (ann.equals("(io)")) {
            setBadExample(true);
        } else if (ann.equals("(oK)")) {
            setBadExample(true);
        } else if (ann.equals("(ok)")) {
            setBadExample(true);
        }

        this._annotations.add(ann);
    }

    public String getKanjiWord() {
        return _keb;
    }

    public void setKanjiWord(String kanjiWord) {
        this._keb = kanjiWord.trim();
    }

    public void setIsP(boolean val) {
        this._isP = true;
    }

    public boolean getIsPBoth() {
        return this._isP && getMotherEntry().getIsP();
    }

    public boolean getIsPMotherEntry() {
        return getMotherEntry().getIsP();
    }

    
    boolean addKana(String kana) {
        if (!CJKUtils.containsKana(kana)) {
            log.error("setting invalid kana: " + kana);
            return false;
        }
        _readings.add(kana);
        return true;
    }

    public boolean isObsKanj() {
        return obsKanj;
    }

    public void setObsKanj(boolean obsKanj) {
        this.obsKanj = obsKanj;
    }

    public Edict_2_Entry getMotherEntry() {
        return _motherEntry;
    }

    @Override
    public int compareTo(Edict2K_Ele other) {
        if (this.rateAsKanjiComposite() > other.rateAsKanjiComposite())
            return -1;
        if (this.rateAsKanjiComposite() < other.rateAsKanjiComposite())
            return 1;

        return 0;
    }

    /**
     * @param forKanji
     *            in case in the future we wanto to take it into account, as it
     *            would be right, ex. knowing the kanji can get the level and
     *            rate the word based on whether other kanjs are at same or
     *            lower level
     */
    int rateAsKanjiComposite() {
        int rating = 0;

        if (this.getIsPBoth()) // common string and common work
            rating += 10000;

        // the shorter the better
        rating += (8 - getKanjiWord().length()) * 1000;

        // simple grammatical role is ok
        if (this.getMotherEntry().getIsPlainNoun())
            rating += 500;
        if (this.getMotherEntry().getIsAdjective())
            rating += 400;
        if (this.getMotherEntry().getIsVerb())
            rating += 300;

        return rating;
    }

    public ArrayList<String> getReadings() {
        return _readings;
    }

    Edict_2_Entry                          _motherEntry;
    String                                 _keb;
    ArrayList<String>                      _readings;
    ArrayList<String>                      _annotations;
    boolean                                _isP;
    private boolean                        _badExample;
    boolean                                obsKanj;

    private static org.apache.log4j.Logger log = Logger.getLogger(Edict2K_Ele.class);

}
