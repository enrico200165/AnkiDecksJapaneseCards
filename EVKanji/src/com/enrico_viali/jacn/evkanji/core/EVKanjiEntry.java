package com.enrico_viali.jacn.evkanji.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.*;
import com.enrico_viali.jacn.ankideck.heisig.*;
import com.enrico_viali.jacn.ankideck.kanji_enrico.*;
import com.enrico_viali.jacn.common.*;
import com.enrico_viali.jacn.edict.*;
import com.enrico_viali.jacn.evkanji.renderers.EVKanjiEntryHTMLRowRenderer;
import com.enrico_viali.jacn.kanjidic.*;
import com.enrico_viali.utils.*;

/**
 * @author enrico
 * 
 */
public class EVKanjiEntry extends EVJPCNEntry {

    public EVKanjiEntry() {
        init();
    }

    void init() {
        tsetHeisigNumber(Utl.NOT_INITIALIZED_INT);
        tsetGrade(Utl.NOT_INITIALIZED_INT);
        tsetOldJlpt(Utl.NOT_INITIALIZED_INT);
        _newJlpt = 99;
        tsetStrokes(Utl.NOT_INITIALIZED_INT);
        tsetHeisigLesson(Utl.NOT_INITIALIZED_INT);

        _kanjidicMeaning = "";
        _kanjidicMeaning = "";

        tsetCnMean(Utl.NOT_INITIALIZED_STRING, true);
        readFromEVFile = false;
        atomic = false;
        _compos = new ArrayList<KanjiComposite>();
        for (int i = 0; i < Cfg.KANJI_COMPO_NR; i++) {
            // todo EV probabilmente da rimuovere
            _compos.add(new KanjiComposite());
        }
    }

    public EVKanjiEntry(AnkiFactEnricoKanji aDeckEKanj) {
        log.error("da implementare");
        System.exit(1);
        tsetHeisigNumber(0);
        tsetGrade(Utl.NOT_INITIALIZED_INT);
        tsetOldJlpt(Utl.NOT_INITIALIZED_INT);
        tsetStrokes(Utl.NOT_INITIALIZED_INT);
        tsetHeisigLesson(Utl.NOT_INITIALIZED_INT);
        tsetCnMean(Utl.NOT_INITIALIZED_STRING, true);
        readFromEVFile = false;
        atomic = false;
        _compos = new ArrayList<KanjiComposite>(Cfg.KANJI_COMPO_NR);
        for (int i = 0; i < _compos.size(); i++) {
            _compos.add(new KanjiComposite());
        }

    }

    
    public int getNewJLPT() {
        return _newJlpt;
    }
    
    public void setNewJLPT(int val) {
        _newJlpt = val;
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_kanji == null) ? 0 : _kanji.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof EVKanjiEntry))
            return false;
        EVKanjiEntry other = (EVKanjiEntry) obj;
        if (_kanji == null) {
            if (other._kanji != null)
                return false;
        } else if (!_kanji.equals(other._kanji))
            return false;
        return true;
    }

    @Override
    public String getKeyExpression() {
        return getKanji();
    }

    @Override
    public long getNumber() {
        return getHeisigNumber();
    }

    boolean checkHStory(String story, boolean fixNull) {
        boolean ret = checkField(story, 0, 0, null, fixNull,
                /* splitComma */false, /* ignorParent */false, /* tokenWhiteList */null);
        if (!ret) {
            return false;
        }

        if (story.trim().split(" ").length <= 3) {
            // == 1 means empty string or single word
            if (!isAtomic()) {
                log.warn("story too short empty or single word: \"" + story + "\"");
                return false;
            }
        }

        if (!CJKUtils.isAllWestern(story, true)) {
            log.error("heisig story not all in Wester language:\n" + story);
            return false;
        }
        return ret;
    }

    boolean checkHPrim(String prim, boolean fixNull) {
        if (prim == null || prim.trim().length() <= 0) {
            log.error("missing primitives");
            return false;
        }
        if (!checkField(prim, 0, 0, null, fixNull, /* splitComma */true, /* IgnParent */true, /* tokenWhiteList */
                null))
            return false;
        if (!CJKUtils.isAllWestern(prim, true))
            return false;
        return true;
    }

    /**
     * @param story
     * @param english
     *            0 = no check, 1 = must be all in this language, 2 must contain
     *            chars
     * @param hira
     * @param kata
     * @param kanji
     * @return
     */
    boolean checkEVKanjiField(String f, int english, int hira, int kata, int kanji, boolean ignoreNonAlpha,
            boolean fixNull, boolean splitComma) {
        boolean ret = checkField(f, 0, 0, null, fixNull, splitComma, /* ignParent */false, /* tokenWhiteList */null);

        switch (english) {
            case 0:
                break;
            case 1: {
                ret = ret && CJKUtils.isAllWestern(f, ignoreNonAlpha);
                break;
            }
            case 2: {
                break;
            }
            default:
                log.error("");
        }

        switch (hira) {
            case 0:
                break;
            case 1: {
                ret = ret && CJKUtils.isAllHiragana(f, ignoreNonAlpha);
                break;
            }
            case 2: {
                ret = ret && CJKUtils.containsHiragana(f);
                break;
            }
            default:
                log.error("");
        }

        switch (kata) {
            case 0:
                break;
            case 1: {
                ret = ret && CJKUtils.isAllKatakana(f, ignoreNonAlpha);
                break;
            }
            case 2: {
                ret = ret && CJKUtils.containsKatakana(f);
                break;
            }
            default:
                log.error("");
        }

        switch (kanji) {
            case 0:
                break;
            case 1: {
                ret = ret && CJKUtils.isAllKanji(f, ignoreNonAlpha);
                break;
            }
            case 2: {
                ret = ret && CJKUtils.containsKanji(f);
                break;
            }
            default:
                log.error("");
        }
        return ret;
    }

    boolean checkCompKanjiField(String compKanjiField, boolean fixNull) {
        if (!checkField(compKanjiField, 2, 16, null, fixNull,/* splitComma */false,
                /* ignParent */false, /* tokenWhiteList */null)) {
            log.error("");
            return false;
        }
        if (!CJKUtils.isAllKanji(compKanjiField, false)) {
            log.error("");
            return false;
        }
        return true;
    }

    boolean checkCompositeMeaning(String mean, boolean fixNull) {
        if (!checkField(mean, 1, 96, null, fixNull,/* splitComma */true,
                /* ignParent */true, /* tokenWhiteList */null)) {
            log.error(" invalid compMeaning: " + mean);
            return false;
        }
        if (!CJKUtils.isAllWestern(mean, false)) {
            log.error("");
            return false;
        }
        return true;
    }

    public boolean checkEntry() {
        boolean ret = true;
        String errMsg = "";

        // log.info("checking "+kanji);
        ret = (ret && checkField(getKanji(), 1, 1, null, false,
                /* splitComma */false, /* ignParent */false, /* tokenWhiteList */null) && CJKUtils.isAllKanji(getKanji(), false));
        if (!ret) {
            errMsg = " error in kanji field";
            ret = false;
        }

        if (getHanziS() != null && getHanziS().length() != 0) {
            if (getHanziS().equalsIgnoreCase("ne")) {
                setHanziS("", true);
            } else {
                if (!(ret = ret && checkField(getHanziS(), 1, 1, null, true,
                        /* splitComma */false, /* ignParent */false, /* tokenWhiteList */null)
                        && CJKUtils.isAllKanji(getHanziS(), false))) {
                    errMsg = " errore in campo HanzkiS = " + getHanziS();
                    ret = ret && false;
                }
            }
        }

        if (getHanziT() != null && getHanziT().length() != 0) {
            if (getHanziT().equalsIgnoreCase("ne")) {
                setHanziT("", true);
            } else {
                if (!(ret = ret && checkField(getHanziT(), 1, 1, null, true,
                        /* splitComma */false, /* ignParent */false, /* tokenWhiteList */null)
                        && CJKUtils.isAllKanji(getHanziT(), false))) {
                    errMsg = " errore in campo HanzkiT = " + getHanziT();
                    ret = ret && false;
                }
            }
        }

        if (getHeisigNumber() <= 0 || getHeisigNumber() > Cfg.NRMAX_KANJI) {
            errMsg = " errore in campo heisig nr = " + getHeisigNumber();
            ret = false;
        }

        if (!checkHPrim(getHeisigPrimitives(), true)) {
            errMsg = "error in heisigPrimitives: " + getHeisigPrimitives();
            ret = false;
        } else {
            if (getHeisigPrimitives().equalsIgnoreCase("pict")) {
                log.warn(" correggo IN-MEMORY H Primites da pict a pictograph");
                setHeisigPrimitives("pictograph");
            }
        }

        if (!checkHStory(getHStory(), false)) {
            errMsg = "error in story: \"" + getHStory() + "\"";
            ret = false;
        }

        if (!checkField(getHMean(), 1, 100, null, false, /* splitComma */false,
                /* ignParent */true, /* tokenWhiteList */null)
                && checkEVKanjiField(getHMean(), 1, 0, 0, 0, false, true, /* splitComma */false)) {
            errMsg = "error in HMean: \"" + getHMean() + "\"";
            ret = false;
        }

        if (!checkField(getKanjidicMeaning(), 2, 128, null, false,/* splitComma */true,
                /* ignParent */false, /* tokenWhiteList */null)
                && checkEVKanjiField(getKanjidicMeaning(), 1, 0, 0, 0, false, false,/* splitComma */true)) {
            errMsg = "error in kanjidicmean: " + getKanjidicMeaning();
            ret = false;
        }

        // fix ad hoc for a specific mistake
        /*
         * if (checkField(comp4Kanji, 1, 24, null) && comp4Kanji.equals("-1"))
         * comp4Kanji = ""; if (checkField(comp4Kana, 1, 24, null) &&
         * comp4Kana.equals("-1")) comp4Kana = ""; if (checkField(comp4Meanings,
         * 1, 24, null) && comp4Meanings.equals("-1")) comp4Meanings = "";
         */
        /*
         * 
         * 
         * String on; // blocco dati jp standard String kun; String
         * 
         * String pinyin; // blocco dati standard cinese, per ora limitato al
         * pinyin String cnMean;// per ora vuoto, non ho il significato cinese
         * 
         * String comp1Kanji; String comp1Kana; String comp1Meanings;
         * 
         * String comp2Kanji; String comp2Kana; String comp2Meanings;
         * 
         * String comp3Kanji; String comp3Kana; String comp3Meanings;
         * 
         * String comp4Kanji; String comp4Kana; String comp4Meanings;
         * 
         * String comp5Kanji; String comp5Kana; String comp5Meanings;
         */

        if (!ret) {
            log.error(getNrChar() + errMsg);
        } else {
            // log.info("controllo OK");
        }
        return ret;
    }

    public static boolean checkCompKanji(String f, String regex, boolean fixNull) {
        if (fixNull && (f == null || f.length() <= 0)) {
            return true;
        }

        if (!checkField(f, 1, 16, null, fixNull, /* splitComma */false,
                /* ignParent */false, /* tokenWhiteList */null)) {
            log.error("");
            return false;
        }
        if (!CJKUtils.containsKanji(f)) {
            log.error("compKanji non tutto kanji: " + f);
            // probabilmente dovrebbe essere containsKanji
            return false;
        }
        return true;
    }

    public static boolean checkCompKanaField(String f, String regex, boolean fixNull) {
        if (!checkField(f, 1, 16, null, fixNull,/* splitComma */false,
                /* ignParent */false, /* tokenWhiteList */null)) {
            log.error("");
            return false;
        }
        if (!CJKUtils.isAllKanaSame(f, false)) {
            log.error("illegal compKana, not all SAME kana: <" + f + ">");
            return true;
        }
        return true;
    }

    public static boolean checkCompMeanField(String f, String regex, boolean fixNull) {
        if (!checkField(f, 1, 16, null, fixNull,/* splitComma */true,
                /* ignParent */false, /* tokenWhiteList */null)) {
            log.error("");
            return false;
        }
        if (!CJKUtils.isAllWestern(f, false)) {
            log.error("");
            return false;
        }
        return true;
    }

    /**
     * @param f
     * @param minLen
     * @param maxLen
     * @param regex
     * @param fixNull
     * @param splitComma
     * @return
     */
    public static boolean checkField(String f, int minLen, int maxLen, String regex, boolean fixNull,
            boolean splitComma, boolean ignParen, String[] tokenWhiteList) {

        if (fixNull && (f == null || f.trim().length() == 0)) {
            return true;
        }

        if (f == null) {
            log.trace("null string");
            return false;
        }
        if (f.equals("null") || f.equals("#REF!")) {
            log.error("string with value " + f);
            return false;
        }
        if (minLen != 0 && f.length() < minLen && !f.equals(Utl.STRING_NOTAVAILABLE)) {
            log.error("stringa \"" + f + "\" len " + f.length() + " < min " + minLen);
            return false;
        }

        ValuesList vl = new ValuesList(f, ignParen, splitComma, maxLen, tokenWhiteList);
        if (!vl.maxLenOK()) {
            log.error("string too long, length " + f.length() + " max len " + maxLen + " string: " + f);
            return false;
        }

        if (regex != null) {
            log.error("controllo regex non implementato");
            return false;
        }
        return true;
    }

    public boolean heisigInfoMissing() {
        if (getHeisigPrimitives() == null || getHeisigPrimitives().trim().length() <= 0)
            return true;

        if (getHeisigPrimitives() != null && getHeisigPrimitives().equals(Cfg.KANJ_PICT_KWORD))
            return false;

        if (getHStory() == null || getHStory().length() <= 0 || getHStory().equals(Cfg.HEISIG_TEMPLATE)) {
            return true;
        }
        return false;
    }

    public void setIsReadFromEVFile(boolean val) {
        readFromEVFile = val;
    }

    public boolean getIsReadFromEVFile() {
        return readFromEVFile;
    }

    /**
     * Modifica preleva il MIO primo significato di heisig e lo ripulisce per
     * renderlo conforontabile con quello del deck heisig
     * 
     * @return
     */
    String getCleanHMean(String str) {
        String s = "";
        s = str.split("[,\\(]")[0].trim();

        if (s != null && s.length() >= 0)
            s = s.trim();
        else
            s = getHMean();
        s = s.toLowerCase();

        s = s.replaceAll("-", " ");

        s = s.replaceAll("[Ã¨Ã©]", "e");
        s = s.replaceAll("Ã ", "a");

        if (!getHMean().equals(s)) {
            log.trace("if giusto per ispezionare");
        }

        return s.toLowerCase();
    }

    public String getKanji() {
        return _kanji;
    }

    public String getOn() {
        return _on;
    }

    public String getKun() {
        if (_kun == null) {
            // non lo modifichiamo per non confondere l'errore che ne Ã¨ la
            // causa, se errato
            // log.warn(getKanji()+" kun is null, returning \"\"");
            return "";
        }
        return _kun;
    }

    public boolean enrichFromAnkiDeck(ADeckHeisigMgr ADeMgr) {

        if (!ADeMgr.getLoaded()) {
            log.error("enrich with empty deck");
            return false;
        }

        if (ADeMgr.findByHNr(getHeisigNumber()) == null) {
            log.warn(getKanji() + " h. nr " + getHeisigNumber() + " unable to enrich: not found in Anki's Heisig deck");
            return false;
        }
        if (!enrichFromADeckFactHeisig(ADeMgr.findByHNr(getHeisigNumber()))) {
            log.warn("failed to enrich entry[" + getHeisigNumber() + "] from Heisig deck: " + getKanji());
            return false;
        }
        return true;
    }

    public boolean enrichFromADeckFactHeisig(AnkiFactHeisig ade) {
        if (getHMean() == null || getHMean().length() <= 0) {
            setHeisigMean(ade.getHKeyword());
            log.debug("H mean vuoto in evkanji, assegnato da anki deck");
        } else {
            if (ade.getHKeyword() == null) {
                log.error("null keyword in anki deck");
            } else {
                String cleanEV = getCleanHMean(getHMean());
                String cleanAnki = getCleanHMean(ade.getHKeyword());
                if (!cleanEV.equals(cleanAnki)) {
                    log.debug("EVKanji H nr: " + getHeisigNumber() + " kanji: " + getKanji()
                            + " Hmean EVkanji != Heisig AnkiDek: \n<" + getHMean() + ">\n<" + cleanEV + ">vs\n<" + cleanAnki
                            + ">");
                }
            }
        }

        if (getHStory() == null || getHStory().length() <= 0) {
            if (ade.getHStory() != null && ade.getHStory().length() > 0) {
                setHeisigStory(ade.getHStory(), isAtomic() /*
                                                            * buttato a caso
                                                            * durante una patch
                                                            * frettolosa, forse
                                                            * errato
                                                            */);
            }
        } else {
            if (!getHStory().equals(ade.getHStory())) {
                log.debug("H nr: " + getHeisigNumber() + " anomalia, heisig stories diversi: <" + getHStory()
                        + "> vs <" + ade.getHStory() + ">");
            }
        }

        if (getHeisigLesson() <= 0) {
            setHeisigLesson(ade.getHLesson(), true);
        } else {
            if (getHeisigLesson() != ade.getHLesson()) {
                log.error("anomalia, heisig lesson nr diversi: <" + getHeisigLesson() + "> vs <" + ade.getHLesson()
                        + ">");
            }
        }

        return true;
    }

    public boolean enrichFromKanjidic(KanjiDicManager kjdMgr) {
        if (kjdMgr.findByHNr(getHeisigNumber()) != null && enrichFromKanjidic(kjdMgr.findByHNr(getHeisigNumber()))) {
            return true;
        }
        return false;
    }

    /**
     * @param ke
     * @return
     */
    public boolean enrichFromKanjidic(KanjidicEntry ke) {

        if (getKanji() == null || getKanji().length() <= 0) {
            setKanji(ke.getKanji());
        } else {
            if (!getKanji().equals(ke.getKanji())) {
                log.error("anomalia, kanji diversi: <" + getKanji() + "> vs <" + ke.getKanji() + ">");
            }
        }

        if (getOn() == null || getOn().length() <= 0) {
            setOn(ke.getOnReadingsStr(","), /* errorEmpty */false);
        } else {
            if (!getOn().equals(ke.getOnReadingsStr(","))) {
                log.debug("anomalia, on-readings diversi: <" + getOn() + "> vs <" + ke.getOnReadingsStr(",") + ">");
            }
        }

        if (ke.getKunReadingsStr(",").length() > 0 && (getKun() == null || getKun().length() <= 0)) {
            setKun(ke.getKunReadingsStr(","), false);
        } else {
            if (!getKun().equals(ke.getKunReadingsStr(","))) {
                log.debug("anomalia, kun-readings diversi: <" + getKun() + "> vs <" + ke.getKunReadingsStr(",") + ">");
            }
        }

        setPinyin(ke.getPinyin(), true);

        // copia il significato, pero ora kanjidic sovrascrive sempre, ho pero
        // inserito il controllo vuoto per il futuro
        if (getKanjidicMeaning() != null) {
            if (getKanjidicMeaning().length() > 0) {
                if (!getKanjidicMeaning().equals(ke.getMeaningsStr(","))) {
                    log.debug("significati diversi:\n1: <" + getKanjidicMeaning() + ">\n2: <" + ke.getMeaningsStr(",")
                            + ">");
                }
            }
            setKanjidicMeaning(ke.getMeaningsStr(","));
        } else {
            // non Ã¨ errore perchÃ¨ a volte le entries non sono lette da file,
            // questo succede con la generazione dei delta dove si esaminano
            // tutti i numeri di Heisig, anche quelli assenti, e se assenti
            // vengono creati
            setKanjidicMeaning(ke.getMeaningsStr(","));
        }

        setGrade(ke.getJouyou()); // copia l'anno di studio

        setOldJlpt(ke.getOldJlpt()); // old JLPT

        // controlla numero tratti
        if (getStrokes() != ke.getStroke()) {
            log.debug("numero di strokes disuguale fra evkanji e kanjidic: " + getStrokes() + " vs " + ke.getStroke());
            setStrokes(ke.getStroke());
        }

        // non ricordo più logica, probabilmente faccio diagnostica
        if (getHeisigNumber() == Utl.NOT_INITIALIZED_INT) {
            if (this.readFromEVFile) {
                log.info("kanji: " + getKanji() + " senza heisig nr in ev file");
            }
        } else {
            if (getHeisigNumber() != ke.getHeisig()) {
                log.warn("kanji: " + getKanji() + " Hesig nr disiguale fra evkanji e kanjidic: " + getHeisigNumber()
                        + " vs " + ke.getHeisig());
            }
        }
        setHeisigNumber(ke.getHeisig());
        setOldJlpt(ke.jlpt);

        return true;
    }

    public boolean enrichFromADeckFactEnricoKanji(AnkiFactEnricoKanji ek) {

        for (int i = 0; i < Cfg.KANJI_COMPO_NR; i++) {
            if (_compos.get(i) == null) {
                log.error("unexpected null, creating it");
                _compos.add(new KanjiComposite());
            }
            if (_compos.get(i)._headWord == null) {
                _compos.get(i)._headWord = "";
            }
            if (_compos.get(i)._headWord.length() == 0) {
                try {
                    String v = ek.getFieldStrValue("jpc" + (i + 1), true);
                    if (v != null && v.length() > 0) {
                        _compos.get(i)._headWord = v;
                        _compos.get(i)._edictReading = ek.getFieldStrValue("jpc" + (i + 1) + "_kana", true);
                        _compos.get(i)._meaning = ek.getFieldStrValue("jpc" + (i + 1) + "_mean", true);
                    }
                } catch (Exception e) {
                    log.error("errore", e);
                }
            }
        }
        return true;
    }

    public boolean enrichWithEdictComposites(ArrayList<IEdictEntry> compos) {
        log.error("questa funzione va rimoss, non fatto manca tempo sistemare codice");
        System.exit(1);
        return true;
    }

    public boolean enrichWithEdictComposites(List<Edict2K_Ele> compos) {
        int i = 0;
        for (Edict2K_Ele c : compos) {
            // ritorna solo una delle headwords
            _compos.get(i)._headWord = CJKUtils.remParen(c.getKanjiWord());

            for (String k : c.getReadings()) {
                _compos.get(i)._edictReading += CJKUtils.remParen(k);
            }
            
            _compos.get(i)._meaning += c.getMotherEntry().getFewSimpleSenses(3, 32);
            i++;
        }
        // log.info(toString());
        return true;
    }

    public boolean enrichWithEdictComposites(EdictManager eMgr) {
        return enrichWithEdictComposites(eMgr.selectComposForKanji(this.getKanji()));
    }

    public boolean enrichFromAll(ADeckHeisigMgr ade, KanjiDicManager ke) {
        return enrichFromAnkiDeck(ade) && enrichFromKanjidic(ke);
    }

    /**
     * @param sep
     * @return
     */
    public String toString(String sep) {
        String s;

        s = "";
        s += getKanji();
        s += sep + getHanziS() + sep + getHanziT();
        s += sep + "heisig: " + getHeisigNumber();
        s += sep + "grade: " + getGrade();
        s += sep + "Old Jlpt: " + getOldJlpt();

        for (KanjiComposite compo : _compos) {
            s += compo._headWord;
            s += compo._edictReading;
            s += compo._meaning;
        }

        /*
         * + on + sep + kun + sep + heisigPrimitives + sep + HMean + sep +
         * kanjidicMeaning + sep + comp1Kanji + sep + comp1Kana + sep +
         * comp1Meanings + sep + comp2Kanji + sep + comp2Kana + sep +
         * comp2Meanings + sep + comp3Kanji + sep + comp3Kana + sep +
         * comp3Meanings + sep + comp4Kanji + sep + comp4Kana + sep +
         * comp4Meanings + sep + comp5Kanji + sep + comp5Kana + sep +
         * comp5Meanings + sep;
         */
        return s;
    }

    @Override
    public String toString() {
        return toString(" : ");
    }

    /**
     * crea linee per il file di testo con i kanji senza informaizioni Heisig
     * che verrÃ  riempito a mano con tali informazioni e poi importato
     * 
     * @param sep
     * @param replc
     * @return
     */
    public String toLineForHeisigDeltaFile(String sep, String replc) {
        String s = "";

        s = getKanji();
        s += sep + getHeisigNumber();
        s += sep + StringUtils.escapeForDelimited(getHeisigPrimitives(), sep, replc);
        s += sep + StringUtils.escapeForDelimited(getHStory(), sep, replc);
        s += sep + StringUtils.escapeForDelimited(getHMean(), sep, replc);

        return s;
    }

    public String detailedDump(String msg, String sep) {
        String s = msg + "\n";

        s += sep + "kanji = " + getKanji() + sep + " getHanziS() = " + getHanziS() + sep + " getHanziT() = "
                + getHanziT() + "\n";

        s += sep + "Heisig nr = " + getHeisigNumber() + "\n";
        s += sep + "heisigPrimitives = " + "\n";
        s += sep + "HStory = " + getHStory() + "\n";
        s += sep + "Hmean = " + getHMean() + sep + "\n";

        s += sep + "on = " + getOn() + sep + "kun = " + getKun() + "\n";
        s += sep + "kanjidicMeaning = " + getKanjidicMeaning() + "\n";

        for (int i = 0; i < _compos.size(); i++) {
            s += sep + "comp1 = " + getCompHeadword(i) + sep + getCompReading(i) + sep + getCompMeaning(i) + "\n";
        }

        s += sep + "Heisig Lesson = " + getHeisigLesson() + "\n";
        s += sep + "_jlpt = " + getOldJlpt() + "\n";
        s += sep + "grade = " + getGrade() + "\n";

        return s;
    }

    /**
     * Created to fill the parameter to be fed to the table insert function Fill
     * a string vector with values of its attributes
     * 
     * @return
     */
    public boolean fillValuesVector(String[] v) {

        if (v.length < 30) {
            log.error("vettore valori troppo piccolo, nr elementi: " + v.length);
            return false;
        }
        int i = 0;
        if ((getKanji() != null) && (getKanji().length() >= 0)) {
            v[i++] = getKanji().toString();
        } else {
            log.error("valore indefinito");
            v[i++] = "";
        }
        if ((getHanziS() != null) && (getHanziS().length() >= 0)) {
            v[i++] = getHanziS().toString();
        } else {
            log.error("valore indefinito");
            v[i++] = "";
        }
        if ((getHanziT() != null) && (getHanziT().length() >= 0)) {
            v[i++] = getHanziT().toString();
        } else {
            log.error("valore indefinito");
            v[i++] = "";
        }

        v[i] = "" + getHeisigNumber();

        if ((getHeisigPrimitives() != null) && (getHeisigPrimitives().length() >= 0)) {
            v[i++] = getHeisigPrimitives().toString();
        } else {
            log.error("valore indefinito");
            v[i++] = "";
        }
        if ((getHStory() != null) && (getHStory().length() >= 0)) {
            v[i] = getHStory();
        } else {
            v[i] = "";
        }
        i++;
        if ((getHMean() != null) && (getHMean().length() >= 0)) {
            v[i++] = getHMean().toString();
        } else {
            log.error("HMean: valore indefinito, evkanji content:\n" + this.toString(":"));
            v[i++] = "";
        }

        if ((getOn() != null) && (getOn().length() >= 0)) {
            v[i++] = getOn().toString();
        } else {
            log.error("valore indefinito");
            v[i++] = "";
        }
        if ((getKun() != null) && (getKun().length() >= 0)) {
            v[i++] = getKun().toString();
        } else {
            log.error("valore indefinito");
            v[i++] = "";
        }
        if ((getKanjidicMeaning() != null) && (getKanjidicMeaning().length() >= 0)) {
            v[i++] = getKanjidicMeaning().toString();
        } else {
            log.error("kanjidicMeaning: valore indefinito, evkanji content:\n" + this.toString(":"));
            v[i++] = "";
        }

        if (getPinyin() != null && getPinyin().length() > 0) {
            v[i] = getPinyin();
        } else {
            v[i] = "";
        }

        i++;
        if (getCnMean() != null && getCnMean().length() > 0) {
            v[i] = getCnMean();
        } else {
            v[i] = "";
        }

        i++;

        for (int j = 0; j < _compos.size(); j++) {
            if ((getCompHeadword(j) != null) && (getCompHeadword(j).length() >= 0)) {
                v[i++] = getCompHeadword(j).toString();
            } else {
                log.debug("valore indefinito");
                v[i++] = "";
            }
            if ((getCompReading(j) != null) && (getCompReading(j).length() >= 0)) {
                v[i++] = getCompReading(j).toString();
            } else {
                log.debug("valore indefinito");
                v[i++] = "";
            }
            if ((getCompMeaning(j) != null) && (getCompMeaning(j).length() >= 0)) {
                v[i++] = getCompMeaning(j).toString();
            } else {
                log.debug("valore indefinito");
                v[i++] = "";
            }
        }
        v[i] = "" + this.getHeisigLesson();
        i++;
        v[i] = "" + this.getOldJlpt();
        i++;
        v[i] = "" + this.getGrade();

        log.info("fill values, last index (30 expected): " + i);

        return true;
    }

    /**
     * For when we import from an Heisig delta file, to check that the entry
     * from it that overwrites the current entry does not have less data
     * 
     * @param previous
     * @return
     */
    public boolean checkHeisigImportNoDataLoss(EVKanjiEntry previous) {

        if (!getKanji().equals(previous.getKanji())) {
            log.error("kanjis do not correspond");
            return false;
        }
        // Heisig data
        if (getHeisigNumber() != previous.getHeisigNumber()) {
            log.error("heisig numbers do not correspond");
            return false;
        }
        if (previous.getHeisigPrimitives() != null && previous.getHeisigPrimitives().length() > 0) {
            log.error("pre-existing element has data in heisigPrimitives:\n" + previous.getHeisigPrimitives());
            return false;
        }
        if ((previous.getHStory() == null) && (previous.getHStory().length() > 0)
                && !previous.getHStory().equals(Cfg.HEISIG_TEMPLATE)) {
            log.error("pre-existing element has data in  HStory: " + previous.getHStory());
            return false;
        }
        if (// in teoria mai nulli dato che entrambi arricchiti
        // previous.HMean != null && previous.HMean.length() > 0 &&
        !previous.getHMean().equals(this.getHMean())) {
            log.error("");
            return false;
        }

        // jp data
        if (!this.getOn().equals(previous.getOn())) {
            log.error("");
            return false;
        }
        if (!this.getKun().equals(previous.getKun())) {
            log.error("");
            return false;
        }
        if (!this.getKanjidicMeaning().equals(previous.getKanjidicMeaning())) {
            log.error("");
            return false;
        }

        // Chinese data
        if (!this.getPinyin().equals(previous.getKanjidicMeaning())) {
            log.error("");
            return false;
        }
        if (!this.getCnMean().equals(previous.getCnMean())) {
            log.error("");
            return false;
        }

        if (this.getStrokes() != previous.getStrokes()) {
            log.error("");
            return false;
        }
        if (this.getHeisigLesson() != previous.getHeisigLesson()) {
            log.error("");
            return false;
        }

        if (this.getOldJlpt() != previous.getOldJlpt()) {
            log.error("");
            return false;
        }

        if (this.getGrade() != previous.getGrade()) {
            log.error("");
            return false;
        }

        return true;
    }

    /**
     * linea organizzata in modo posizionale
     * 
     * @param lineNr
     *            passare -1 quando non ha senso
     * @param line
     * @param kanjidicLineSep
     * @param entry
     * @return
     */
    public boolean buildVKanjiFromFileLine(int lineNr, String line, String kanjidicLineSep, boolean cleanEmbelish) {
        boolean rc = true;

        // eventualmente rimuoviamo blanks duplicati
        if (cleanEmbelish)
            line = line.replaceAll("  ", " ");

        String pre_entries[] = line.split(kanjidicLineSep);

        if (pre_entries.length != 30) {
            log.error("unusual number of entries in csv line, nr entries: " + pre_entries.length + "\n line: " + line);
            rc = false;
        }

        if (cleanEmbelish) {
            // some clean-ups:
            for (int i = 0; i < pre_entries.length; i++) {
                if (i < 27 && // ultimi tre campi almeno ora sono indefiniti
                        pre_entries[i].matches("^ *-1 *$")) {
                    pre_entries[i] = "";
                    log.warn("removed -1 from field[" + i + "] line " + lineNr);
                }
                // remove " that are put in by open office
                if (pre_entries[i].startsWith("\"") && pre_entries[i].endsWith("\"")) {
                    pre_entries[i] = pre_entries[i].substring(1, pre_entries[i].length() - 1);
                    log.warn("removed beginning and ending \"\" from field");
                }
                if (pre_entries[i].contains("ev_duep_&gt;")) {
                    pre_entries[i] = pre_entries[i].replace("ev_duep_&gt;", "");
                    log.warn("removed: ev_duep_&gt; from line");
                }
                if (pre_entries[i].contains("ev_pvirg_&gt;")) {
                    pre_entries[i] = pre_entries[i].replace("ev_pvirg_&gt;", "");
                    log.warn("removed: ev_pvirg_&gt; from line");
                }
                if (pre_entries[i].contains(";;")) {
                    pre_entries[i] = pre_entries[i].replace(";;", ";");
                    log.warn("line contains ;;");
                }
                if (pre_entries[i].contains("ev_")) {
                    log.warn("line contains ev_");
                }
            }
        }

        int elemIdx = 0;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setKanji(pre_entries[elemIdx]);
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setHanziS(pre_entries[elemIdx], true);
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setHanziT(pre_entries[elemIdx], true);
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            if (pre_entries[elemIdx].length() <= 0) {
                log.warn("EVKanjiEntry senza Heisig Nr: " + getKanji() + " line:\n" + line);
            } else {
                setHeisigNumber(Integer.parseInt(pre_entries[elemIdx]));
            }
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setHeisigPrimitives(pre_entries[elemIdx]);
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setHeisigStory(pre_entries[elemIdx], isAtomic());
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setHeisigMean(pre_entries[elemIdx]);
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setOn(pre_entries[elemIdx], false);
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setKun(pre_entries[elemIdx], true);
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setKanjidicMeaning(pre_entries[elemIdx]);
        }
        elemIdx++;

        // --- dati per cinese
        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setPinyin(pre_entries[elemIdx], /* fixNull */true);
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setCnMean(pre_entries[elemIdx], true);
        }
        elemIdx++;

        // --- dati per composti giapponesi
        for (int j = 1; j <= Cfg.KANJI_COMPO_NR; j++) {

            if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
                setCompKanji(j, pre_entries[elemIdx], true);
            }
            elemIdx++;

            if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
                setCompKana(j, pre_entries[elemIdx], true);
            }
            elemIdx++;

            if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
                setCompMeaning(j, pre_entries[elemIdx], true);
            }
            elemIdx++;
        }

        // --- dati utili per gestire le entries relativamente allo studio
        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setHeisigLesson(Utl.intFromString(pre_entries[elemIdx]), true);
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            tsetOldJlpt(Utl.intFromString(pre_entries[elemIdx]));
        }
        elemIdx++;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            setGrade(Utl.intFromString(pre_entries[elemIdx]));
        }
        elemIdx++;

        return rc;
    }

    public boolean buildFromAnkiHeisigJPCNFact(AnkiFactEnricoKanji anki) {
        boolean ret = true;
        // a questo punto abbiamo una Map di campi da assegnare
        // agli attributi
        try {

            ret = ret && setKanji(anki.getFieldStrValue("Expression", true));
            ret = ret && setHanziS(anki.getFieldStrValue("hanzi_s", false), true);
            ret = ret && setHanziT(anki.getFieldStrValue("hanzi_t", false), true);

            ret = ret && setHeisigNumber(anki.getFieldIntValue("heisigNr", false));
            ret = ret && setHeisigPrimitives(anki.getFieldStrValue("heisigPrim", false));
            ret = ret && setHeisigStory(anki.getFieldStrValue("heisigStory", false), false);
            ret = ret && setHeisigMean(anki.getFieldStrValue("heisig_mean", false));

            // blocco dati jp standard
            ret = ret && setOn(anki.getFieldStrValue("on", false), false);
            ret = ret && setKun(anki.getFieldStrValue("kun", false), false);
            ret = ret && setKanjidicMeaning(anki.getFieldStrValue("kanjidicMean", false));

            // blocco dati standard cinese, per ora limitato al pinyin
            ret = ret && setPinyin(anki.getFieldStrValue("pinyin", false), true);
            ret = ret && setCnMean(anki.getFieldStrValue("cnMean", false), true);

            for (int j = 1; j <= Cfg.KANJI_COMPO_NR; j++) {
                ret = ret && setCompKanji(j, anki.getFieldStrValue("jpc" + j, false), true);
                ret = ret && setCompKana(j, anki.getFieldStrValue("jpc" + j + "_kana", false), true);
                ret = ret && setCompMeaning(j, anki.getFieldStrValue("jpc" + j + "_mean", false), true);
            }

            // non presente nel deck strokes = anki.getFieldIntValue("", false);

            // dati di management, da ignorare nel CSV
            ret = ret && setHeisigLesson(anki.getFieldIntValue("heisigLesson", false), true);
            ret = ret && setOldJlpt(anki.getFieldIntValue("jlpt", false));
            ret = ret && setGrade(anki.getFieldIntValue("grade", false));

        } catch (AnkiDeckMalformedFact e) {
            log.error("", e);
        }

        return ret;
    }

    public int getHNumber() {
        return _heisigNumber;
    }

    public boolean buildEVKanjiFromHDeltaLine(int lineNr, String line, String deltaSep) {
        boolean rc = true;

        String pre_entries[] = line.split(deltaSep);

        if (pre_entries.length != 5) {
            log.error("unusual number of entries in csv line, nr entries: " + pre_entries.length + "\n line: " + line);
            return false;
        }

        int elemIdx = 0;

        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            if (!setKanji(pre_entries[elemIdx]))
                return false;
        }

        elemIdx++;
        // HEISIG number
        if (elemIdx < pre_entries.length) {
            if (pre_entries[elemIdx] == null) {
                log.error("");
                return false;
            }
            if (pre_entries[elemIdx].length() <= 0) {
                log.warn("EVKanjiEntry senza Heisig Nr: " + getKanji() + " line:\n" + line);
                return false;
            }
            setHeisigNumber(Utl.intFromString(pre_entries[elemIdx]));
            if (Utl.NOT_INITIALIZED_INT == getHeisigNumber()) {
                log.error("");
                return false;
            }
        }

        elemIdx++;
        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            if (!setHeisigPrimitives(pre_entries[elemIdx]))
                return false;
        }

        elemIdx++;
        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            if (!setHeisigStory(pre_entries[elemIdx], isAtomic()))
                return false;
        }

        elemIdx++;
        if ((elemIdx < pre_entries.length) && (pre_entries[elemIdx] != null)) {
            if (!setHeisigMean(pre_entries[elemIdx]))
                return false;
        }

        return rc;
    }

    public String getHanziS() {
        return _hanziS;
    }

    public String getHanziT() {
        return _hanziT;
    }

    public int getHeisigNumber() {
        return _heisigNumber;
    }

    public String getHStory() {
        return _HStory;
    }

    public String getHMean() {
        return _HMean;
    }

    public String getKanjidicMeaning() {
        return _kanjidicMeaning;
    }

    public String getKanjidicMeaningIT() {
        if (_kanjidicMeaning_IT == null) {
            log.error("null");
            return "it was null";
        }
        return _kanjidicMeaning_IT;
    }

    public String getPinyin() {
        return _pinyin;
    }

    public String getCnMean() {
        return _cnMean;
    }

    public String getCompKanji(int i) {
        return _compos.get(i)._headWord;
    }

    public String getCompKana(int i) {
        return _compos.get(i)._edictReading;
    }

    public String getCompMeanings(int i) {
        return _compos.get(i)._meaning;
    }

    public int getStrokes() {
        return _strokes;
    }

    public int getHeisigLesson() {
        return _heisigLesson;
    }

    public String getHeisigPrimitives() {
        return _heisigPrimitives;
    }

    public int getOldJlpt() {
        return _oldJlpt;
    }

    public int getGrade() {
        return _grade;
    }

    public boolean isAtomic() {
        return atomic;
    }

    public boolean isReadFromEVFile() {
        return readFromEVFile;
    }

    public boolean setKanji(String kanji) {
        if (!checkField(kanji, 1, 1, null, /* fixNull */false,/* splitcomma */false,
                /* ignParent */false, /* tokenWhiteList */null)) {
            log.error("");
            return false;
        }
        _kanji = kanji;
        return true;
    }

    public boolean setHanziS(String hanziS, boolean fixNull) {
        if (!checkField(hanziS, 1, 1, null, fixNull, /* splitComma */false,
                /* ignParent */false, /* tokenWhiteList */null)) {
            log.error("");
            return false;
        }
        if (!CJKUtils.isAllKanji(hanziS, false)) {
            log.error("");
            return false;
        }
        _hanziS = hanziS;
        return true;
    }

    public boolean setHanziT(String hanziT, boolean fixNull) {
        if (!checkField(hanziT, 1, 1, null, fixNull, /* splitComma */false,
                /* ignParent */false, /* tokenWhiteList */null)) {
            log.error("");
            return false;
        }
        if (!CJKUtils.isAllKanji(hanziT, false)) {
            log.error(this.getKanji() + " heisig nr " + this.getHNumber() + " set hanziT non Ã¨ un kanji: " + hanziT);
            return false;
        }
        _hanziT = hanziT;
        return true;
    }

    public boolean tsetHeisigNumber(int heisigNumber) {
        if (heisigNumber <= 0 || heisigNumber > Cfg.NRMAX_KANJI) {
            log.debug("setHeisigNumber() wrong value: " + heisigNumber);
        }
        _heisigNumber = heisigNumber;
        return true;
    }

    public boolean setHeisigNumber(int heisigNumber) {
        if (heisigNumber <= 0 || heisigNumber > Cfg.NRMAX_KANJI) {
            log.error("setHeisigNumber() wrong value: " + heisigNumber);
            return false;
        }
        _heisigNumber = heisigNumber;
        return true;
    }

    public boolean checkHeisigPrimitives(String heisigPrimitives) {
        String[] wl = { "tongue-wagging-in-the-mouth" };
        if (!checkField(heisigPrimitives, 3 /* "sun" */, 24, null, /* fixNull */false,
                /* splitComma */true, /* ignParent */true, /* tokenWhiteList */wl)) {
            log.error(getKanji() + " set invalid H prim: " + heisigPrimitives);
            return false;
        }

        if (!CJKUtils.isAllWestern(heisigPrimitives, true)) {
            log.error(getKanji() + " set invalid H prim: " + heisigPrimitives);
            return false;
        }

        _heisigPrimitives = heisigPrimitives;
        return true;
    }

    public boolean setHeisigPrimitives(String heisigPrimitives) {
        if (!checkHeisigPrimitives(heisigPrimitives)) {
            log.error(getKanji() + " set invalid H prim: " + heisigPrimitives);
            return false;
        }

        _heisigPrimitives = heisigPrimitives;

        if (_heisigPrimitives.equals("primitive") || _heisigPrimitives.equals("basic")
                || _heisigPrimitives.equals("pictograph")) {
            atomic = true;
        }

        return true;
    }

    public boolean checkHeisigStory(String story, boolean primRequired) {

        if (!primRequired || isAtomic()) {
            // accettiamo qualunque cosa
            return true;
        }

        if (story.matches("[^\\+]*\\[\\d+\\].*")) {
            log.error("story may contain strokes number:\n" + story);
            return false;
        }

        if (!checkField(story, 16, 1283, null, /* fixNull */false, /* splitComma */false,
                /* ignParent */false, /* tokenWhiteList */null)) {
            log.error(getKanji() + " set invalid H Story: \"" + story + "\"");
            return false;
        }
        if (!CJKUtils.isAllWestern(story, true)) {
            log.error(getKanji() + " set invalid H Story: \"" + story + "\"");
            return false;
        }

        final String primPattern = ".*&lt;.*&gt;.*";
        if (!story.matches(primPattern)) {
            return false;
        }
        final String meanPattern = ".*\\[[^]]+\\].*";
        if (!story.matches(meanPattern)) {
            log.error("story does not include correct meaning of form [word], story:\n" + story);
            return false;
        }

        return true;
    }

    public boolean setHeisigStory(String story, boolean emptyOk) {
        if (emptyOk && (story == null || story.length() <= 0)) {
            _HStory = "";
            return true;
        }
        if (!checkHeisigStory(story, isAtomic())) {
            log.error(getKanji() + " set invalid H Story: \"" + story + "\"");
            return false;
        }
        _HStory = story;
        return true;
    }

    public boolean checkHMean(String mean) {
        String[] wl = { "*tongue-wagging-in-the-mouth", "*someone-sitting-on-the-ground" };
        if (!checkField(mean, 1, 19, null, /* fixNull */false, /* splitComma */false,
                /* ignParent */true, /* tokenWhiteList */wl)) {
            log.error(getKanji() + " illegal H meaning: " + mean);
            return false;
        }
        if (!CJKUtils.isAllWestern(mean, false)) {
            log.error("HMean is not all English: " + mean);
            return false;
        }
        return true;
    }

    public boolean setHeisigMean(String mean) {
        if (!checkHMean(mean)) {
            log.error(getKanji() + " illegal H meaning: " + mean);
            return false;
        }
        _HMean = mean;
        return true;
    }

    public boolean checkOn(String on) {

        if (!checkField(on, 1, 4, null, /* fixNull */false, /* splitComma */true,
                /* ignParent */false, /* tokenWhiteList */null)) {
            return false;
        }

        if (!CJKUtils.isAllKatakana(on, true) && !on.equals(Utl.STRING_NOTAVAILABLE)) {
            log.error(getKanji() + " on not all katakana: " + on);
            return false;
        }

        if (!CJKUtils.isAllKatakana(on, true) && !on.equals(Utl.STRING_NOTAVAILABLE)) {
            log.error(getKanji() + " on not all katakana: " + on);
            return false;
        }

        return true;
    }

    public boolean setOn(String on, boolean errorEmpty) {

        if (!errorEmpty && (on == null || on.length() <= 0)) {
            _on = "";
            return true;
        }

        if (!checkOn(on)) {
            log.error(getNrChar() + " set on invalid: \"" + on + "\"");
            return false;
        }

        _on = on;
        return true;
    }

    public boolean checkKun(String kun, boolean fixNull) {

        // andrebbe creata la whitelist
        if (getKanji().equals("æ—¬")) {
            kun = "this does not have a kun";
            return true;
        }
        if (!checkField(kun, 1, 7, null, /* fixNull */fixNull, /* splitComma */true,
                /* ignParent */false, /* tokenWhiteList */null)) {
            log.error(getKanji() + " set kun, invalid length: " + kun.length());
            return false;
        }

        if (!CJKUtils.isAllHiragana(kun, true)) {
            if (kun.equals("0") || kun.equals(Cfg.NOT_AVAIL)) {
                log.warn("aggiusto " + kun + " in kun, rimuovere quando li file Ã¨ pulito");
                return true;
            }
            log.error(getKanji() + " kun is not all hiragana: " + kun);
            return false;
        }
        return true;
    }

    public boolean setKun(String kun, boolean fixNull) {
        if (!kun.equals(Utl.STRING_NOTAVAILABLE)) {
            if (!checkKun(kun, fixNull)) {
                log.error(getKanji() + " set kun, invalid: " + kun);
                return false;
            }
        }
        _kun = kun;
        return true;
    }

    public boolean setKanjidicMeaning(String kanjidicMeaning) {
        if (!checkField(kanjidicMeaning, 2, 32, null, /* fixNull */false,
                /* splitComma */true, /* ignParent */true, /* tokenWhiteList */null)
                || !CJKUtils.isAllWestern(kanjidicMeaning, true)) {
            log.error("");
            return false;
        }
        _kanjidicMeaning = kanjidicMeaning;
        return true;
    }

    public boolean setKanjidicMeaningIT(String kanjidicMeaningIT) {

        if (!checkField(kanjidicMeaningIT, 2, 32, null, /* fixNull */false,
                /* splitComma */true, /* ignParent */true, /* tokenWhiteList */null)
                || !CJKUtils.isAllWestern(kanjidicMeaningIT, true)) {
            log.error("");
            return false;
        }

        _kanjidicMeaning_IT = kanjidicMeaningIT;
        return true;
    }

    public boolean setPinyin(String pinyin, boolean fixNull) {
        if (!checkField(pinyin, 2, 8, null, fixNull, /* splitComma */false,
                /* ignParent */false, /* tokenWhiteList */null) || !CJKUtils.isAllWestern(pinyin, false)) {
            log.error(getKanji() + " invalid pinyin: \"" + pinyin + "\"");
            return false;
        }

        _pinyin = pinyin;
        return true;
    }

    public boolean setCnMean(String cnMean, boolean fixNull) {

        if (cnMean.contains("variant of")) {
            _cnMean = cnMean;
            return true;
        }

        if (!checkField(cnMean, 2, 36, null, fixNull, /* splitComma */true,
                /* ignParent */true, /* tokenWhiteList */null)) {
            log.error(getKanji() + " set invalid cnMean: " + cnMean);
            return false;
        }
        if (!CJKUtils.isAllWestern(cnMean, true)) {
            if (cnMean.contains("å…¬å�‡")) {

            } else {
                log.error(getKanji() + " set invalid cnMean: " + cnMean);
                return false;
            }
        }
        _cnMean = cnMean;
        return true;
    }

    public boolean tsetCnMean(String cnMean, boolean fixNull) {
        if (!checkField(cnMean, 2, 16, null, fixNull, /* splitComma */true,
                /* ignParent */false, /* tokenWhiteList */null) || !CJKUtils.isAllWestern(cnMean, true)) {
            log.debug(getKanji() + " set invalid cnMean: " + cnMean);
        }
        // _cnMean = cnMean;
        return true;
    }

    public boolean setCompKanji(int i, String comp1Kanji, boolean fixNull) {
        if (fixNull && (comp1Kanji == null || comp1Kanji.length() <= 0)) {
            return true;
        }
        if (i < 1 || i > Cfg.KANJI_COMPO_NR) {
            log.error("");
            return false;
        }

        if (!checkCompKanji(comp1Kanji, null, fixNull)
                || (!CJKUtils.containsKanji(comp1Kanji) && !CJKUtils.containsKatakana(comp1Kanji))) {
            log.error(getKanji() + " comp1Kanji invalid: " + comp1Kanji);
            return false;
        }
        _compos.get(i - 1)._headWord = comp1Kanji;
        return true;
    }

    public boolean setCompKana(int i, String comp1Kana, boolean fixNull) {
        if (i < 1 || i > Cfg.KANJI_COMPO_NR) {
            log.error("");
            return false;
        }
        if (!checkCompKanaField(comp1Kana, null, fixNull)) {
            log.error("error in reading of composites, ignored: " + comp1Kana);
            return false;
        }
        _compos.get(i - 1)._edictReading = comp1Kana;
        return true;
    }

    public boolean setCompMeaning(int i, String compMeanings, boolean fixNull) {
        if (i < 1 || i > Cfg.KANJI_COMPO_NR) {
            log.error("");
            return false;
        }
        if (!checkCompositeMeaning(compMeanings, fixNull)) {
            log.error("");
            return false;
        }

        // add space before /
        compMeanings = compMeanings.replaceAll("/", " /");

        _compos.get(i - 1)._meaning = compMeanings;
        return true;
    }

    public boolean setCompFromEdict(int i, IEdictEntry e) {
        if (i >= _compos.size()) {
            log.error("wrong compo index: " + i + " ignored");
            return false;
        }
        _compos.get(i)._headWord = e.getHeadWord();
        _compos.get(i)._edictReading = e.getReading();
        _compos.get(i)._meaning = e.getWholeDescription();
        return true;
    }

    public boolean checkStrokes(char kanji, int strokes) {
        if (strokes > 0 && strokes <= 28) {
            return true;
        }
        return false;
    }

    public String getNrChar() {
        return "H.Nr: " + getHNumber() + " char " + getKanji() + " ";
    }

    public boolean setStrokes(int strokes) {
        if (!checkStrokes(getKanji().charAt(0), strokes)) {
            log.error(getNrChar() + "setStrokes() wrong value: " + strokes);
            return false;
        }
        _strokes = strokes;
        return true;
    }

    public boolean tsetStrokes(int strokes) {
        if (strokes <= 0 || strokes > 8) {
            log.debug(getNrChar() + "setStrokes() wrong value: " + strokes);
        }
        // _strokes = strokes;
        return true;
    }

    public boolean setHeisigLesson(int heisigLesson, boolean fixInv) {
        if (heisigLesson <= 0) {
            if (fixInv)
                return true;
            else {
                log.error(getKanji() + " setting invalid heisig lesson: " + heisigLesson);
                return false;
            }
        }
        _heisigLesson = heisigLesson;
        return true;
    }

    public boolean tsetHeisigLesson(int heisigLesson) {
        if (heisigLesson <= 0) {
            log.debug(getKanji() + " set illegal H Lesson value: " + heisigLesson);
        }
        _heisigLesson = heisigLesson;
        return true;
    }

    public boolean checkOldJlpt(int jlpt) {
        if (jlpt <= 0 || jlpt > 4) {
            if (jlpt != Utl.NOT_INITIALIZED_INT) {
                return false;
            }
        }
        return true;
    }

    public boolean setOldJlpt(int jlpt) {
        if (!checkOldJlpt(jlpt)) {
            log.error(getKanji() + " attempt to set invalid _jlpt value: " + jlpt);
            return false;
        }
        _oldJlpt = jlpt;
        return true;
    }

    public boolean tsetOldJlpt(int jlpt) {
        if (jlpt <= 0 || jlpt > 4) {
            log.debug(" ignore attempt to set invalid _jlpt value: " + jlpt);
        }
        return true;
    }

    public boolean setGrade(int grade) {
        if (grade < 1 || grade > 9) {
            if (getKanji().equalsIgnoreCase("ä¹ž")) {
                // log.warn("kanji " + getKanji() + " non ha grade");
            } else {
                // log.warn(getNrChar() + "setGrade() wrong value: " + grade);
                _grade = Utl.NOT_INITIALIZED_INT;
            }
            return false;
        } else {
            _grade = grade;
            return true;
        }
    }

    public boolean tsetGrade(int grade) {
        if (grade < 1 || grade > 6) {
            log.debug(getNrChar() + "setGrade() wrong value: " + grade);
        }
        return true;
    }

    public String getCompHeadword(int i) {
        return this._compos.get(i)._headWord;
    }

    public String getCompReading(int i) {
        return this._compos.get(i)._edictReading;
    }

    public String getCompMeaning(int i) {
        if (this._compos.get(i)._meaning == null) {
            log.debug("_compos.get(i) is null");
            SystemUtils.maybeExit();
            return "";
        }
        return this._compos.get(i)._meaning;
    }

    public int getCompLength() {
        log.error("E' un array, la dimensione è fissa");
        return _compos.size();
    }

    public boolean setReadFromEVFile(boolean readFromEVFilePar) {
        readFromEVFile = readFromEVFilePar;
        return true;
    }

    @Override
    public String getAsHTMLLine(long scanned, long included, int level) {
        EVKanjiEntryHTMLRowRenderer r =
                new EVKanjiEntryHTMLRowRenderer("KanjiEntry", "KanjiID", "KanjiElement");
        return r.render(this, scanned, included, level);
    }

    
    // NON CAMBIARE L'ORDINE
    // l'ordine deve corrispondere a quello sul file, come norma metodologica
    String                                 _kanji;                                             // 1
    String                                 _hanziS;
    String                                 _hanziT;

    // blocco dati heisig
    int                                    _heisigNumber;
    String                                 _heisigPrimitives;                                  // 5
    String                                 _HStory;
    String                                 _HMean;

    String                                 _on;                                                // blocco
                                                                                                // dati
                                                                                                // jp
                                                                                                // standard
    String                                 _kun;
    String                                 _kanjidicMeaning;                                   // 10
    String                                 _kanjidicMeaning_IT;                                // 10

    // blocco dati standard cinese, per ora limitato al pinyin
    String                                 _pinyin;
    String                                 _cnMean;                                            // per

    ArrayList<KanjiComposite>              _compos;

    // blocco info secondarie
    int                                    _strokes;                                           // ignorato
    int                                    _heisigLesson;                                      // 28
    int                                    _oldJlpt;                                           // 29
    int                                    _newJlpt;                                           // 29
    int                                    _grade;                                             // 30

    private boolean                        readFromEVFile;
    private boolean                        atomic;
    static String                          HMeanWList[] = { "*someone-sitting-on-the-ground" };

    
    private static org.apache.log4j.Logger log          = Logger.getLogger(EVKanjiEntry.class);
}
