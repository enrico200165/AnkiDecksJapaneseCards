package com.enrico_viali.jacn.edict;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.CJKUtils;
import com.enrico_viali.utils.Utl;

/*
 * @author enrico
 */
public class Edict_2_Entry implements IEdictEntry {

    public Edict_2_Entry() {
        super();
        reset();
    }

    void reset() {
        _lineNr = Utl.NOT_INITIALIZED_INT;
        _len = 0;
        _reading = "undefined";
        _field = Utl.NOT_INITIALIZED_STRING;
        _isFrequent = false;
        _isNoun = false;
        _isVerb = false;
        _isAdjective = false;
        _rating = Utl.NOT_INITIALIZED_INT;

        _headWords = new ArrayList<Edict2K_Ele>();
        _senses = new Senses(this);
        _ent_seq = "serial non impostato";
        _isP = false;
        _badForLearner = false;
    }

    
    public String getFewSimpleSenses(int maxSenses,int maxLength) {
        return _senses.getFewSimpleSenses(maxSenses,maxLength);
    }
    
    public boolean getBadForLearner() {
        return _badForLearner;
    }

    public void setBadForLearner(boolean val) {
        _badForLearner = val;
    }

    public boolean seqMatchesRgx(String toMatch) {
        return this._ent_seq.matches(toMatch);
    }

    /**
     * Reconstructed, it seems that: Calculates a score of how a word is
     * suitable as composite
     */
    void rateAsKanjiComposite() {
        _rating = 0;

        if (this._isFrequent)
            _rating += 10000;

        _rating += (8 - getHeadWord().length()) * 1000;

        if (this.getIsPlainNoun())
            _rating += 500;

        if (this.getIsAdjective())
            _rating += 400;

        if (this.getIsVerb())
            _rating += 300;
    }

    String getGloss(int i) {
        return _senses.get(i);
    }

    public String toString(String sep) {
        String s = "";

        int i = 0;
        s += "line: " + this._lineNr + " entry: " + _ent_seq;
        for (Edict2K_Ele hw : _headWords) {
            i++;
            s += "\n" + "hw[" + i + "]: " + hw.toString();
        }

        i = 0;
        for (String inf : _senses._sensesStrs) {
            i++;
            s += "\nsense[" + i + "]: " + inf;
        }

        s += "\nnotes: ";

        if (this._isFrequent)
            s += " frequent";

        if (this.getIsPlainNoun())
            s += " noun";

        if (this.getIsAdjective())
            s += " adjective";

        if (this.getIsVerb())
            s += " verb";

        return s;
    }

    @Override
    public String toString() {
        return toString(" : ");
    }

    boolean parseKanjiWord(String kanjiWord, Edict2K_Ele headWord) {
        int area = 0;
        // 0 starting 1 scanning kanji
        // 2 scanning parenthesis

        headWord.reset(this);

        if (kanjiWord == null || kanjiWord.length() == 0 || kanjiWord.matches(" *")) {
            log.error("empty or null or whitespace only kanjiword: <" + kanjiWord + ">");
            return false;
        }

        int state = 0; // 0 completed // 1 processing

        int startWord = 0;
        int endWord = 0;

        char c = ' ';
        for (int i = 0; i < kanjiWord.length(); i++) {
            c = kanjiWord.charAt(i);
            switch (area) {
                case 0: { // initializing
                    switch (c) {
                        case '(':
                            if (area != 1) {
                                log.error("trovato ( senza prima aver trovato kanj");
                                return false;
                            }
                            area = 2;
                            state = 1;
                            break;
                        case ' ':
                            break; // skip whitespace
                        case ')':
                            log.error("");
                            System.exit(-1);
                            break;
                        default: { // any other character
                            area = 1;
                            startWord = i;
                            state = 1;
                        }
                    } // switch
                    break; // state 0
                }
                case 1: { // kanji
                    switch (c) {
                        case '(': {
                            area = 2;
                            state = 1;
                            endWord = i - 1;
                            headWord.setKanjiWord(kanjiWord.substring(startWord, endWord + 1));
                            startWord = i;
                            break;
                        }
                        case ')': {
                            log.error("");
                            System.exit(-1);
                        }
                        default: {
                            // si continua ad andare avanti
                        }
                    } // switch
                    break; // case 1
                }
                case 2: { // scandendo annotazione
                    switch (c) {
                        case ')': { // fine annotazione
                            assert (state == 1);
                            endWord = i;
                            state = 0;
                            String ann = kanjiWord.substring(startWord, endWord + 1);
                            if (ann.length() <= 2) {
                                log.error("invalid annotation: " + ann);
                                return false;
                            }
                            headWord.addAnnotation(ann);
                            break;
                        }
                        case '(': {
                            assert (state == 0);
                            startWord = i;
                            state = 1;
                            break;
                        }

                        default: { // normal character
                            if (state == 0) { // not processing, ie outside ()
                                if (c != ' ') {
                                    log.error("characters ouside () after ()s areas in\n" + kanjiWord);
                                    return false;
                                }
                            }
                            break;
                        }
                    } // switch
                    break; // case 2
                }
            }
        }

        if (state == 1) {
            if (area == 1) {
                headWord.setKanjiWord(kanjiWord.substring(startWord));
                return true;
            } else {
                log.error("string finished while processing open, area: " + area);
                return false;
            }
        }
        return true;
    }

    boolean parseKanjiWords(String wordsString) {
        String[] words = wordsString.split(";");
        for (int i = 0; i < words.length; i++) {
            Edict2K_Ele hw = new Edict2K_Ele(this);
            if (!parseKanjiWord(words[i], hw)) {
                log.error("");
                return false;
            }
            _headWords.add(hw);
        }
        return true;
    }

    boolean parseKana(String kanaStr) {
        String kana = "";
        String info1 = ""; // può contenere kanji o info
        String info2 = ""; // può contenere solo info
        String targetKanjis = null;
        String info = null;
        String nopar = "([^(]*)";
        String paren = "\\(([^(]*)\\)";
        Pattern p1 = Pattern.compile("^" + nopar + "$"); // nessuna parentesi
        Pattern p2 = Pattern.compile("^" + nopar + paren + "[^(]*$"); // nessuna
                                                                      // parentesi
        Pattern p3 = Pattern.compile(nopar + paren + " *" + paren + " *"); // nessuna
        // parentesi

        if (kanaStr == null || kanaStr.length() == 0 || kanaStr.matches(" *")) {
            log.error("invalid kana received");
            return false;
        }

        Matcher m1 = p1.matcher(kanaStr);
        if (m1.find()) {
            kana = m1.group(1);
            // log.info(kana);
        } else {
            Matcher m2 = p2.matcher(kanaStr);
            if (m2.find()) {
                kana = m2.group(1);
                info1 = m2.group(2);
                // log.info(kana + " # " + info1);
                // qui non sappiamo se è Kanji o annotation, capiamolo
                if (CJKUtils.containsJapanese(info1)) {
                    targetKanjis = info1;
                } else {
                    info = info1;
                }
            } else {
                Matcher m3 = p3.matcher(kanaStr);
                if (m3.find()) {
                    kana = m3.group(1);
                    targetKanjis = m3.group(2);
                    info = m3.group(3);
                    log.debug(kana + " # " + info1 + " # " + info2);
                } else {
                    log.error("unable to match");
                    return false;
                }
            }
        }

        // associo ai kanj targets, il kana e l'info eventualmente seguente

        String kanaToAssociate = kana;
        if (info != null)
            kanaToAssociate += "(" + info + ")";

        if (targetKanjis == null) {
            // se non sono specificati kanj targets associo a tutti
            for (Edict2K_Ele hw : this._headWords) {
                hw.addKana(kanaToAssociate);
            }
            return true;
        }

        String[] maybeTargets = targetKanjis.split(",");
        ArrayList<String> targets = new ArrayList<String>();
        for (String maybe : maybeTargets) {
            if (maybe.equals("gikun")) {
                continue;
            }
            if (maybe.equals("ok")) {
                continue;
            }
            targets.add(maybe);
        }

        int foundNr = 0;
        for (Edict2K_Ele hw : this._headWords) {
            for (String kanjiPointer : targets) {
                // log.info("<"+hw.getKanjiWord()+">"+"<"+kanjiPointer+">");
                if (hw.getKanjiWord().equals(kanjiPointer)) {
                    hw.addKana(kanaToAssociate);
                    foundNr++;
                }
            }
        }
        if (foundNr != targets.size()) {
            log.error("did not found all targets parsing\n" + kanaStr);
            return false;
        }

        return true;
    }

    boolean parseKanas(String kanasString) {
        // じゃがいも(じゃが芋,馬鈴薯)(P);ジャガいも(ジャガ芋)(P);ジャガイモ;ばれいしょ(馬鈴薯)(P)
        String[] kanas = kanasString.split(";");
        for (int i = 0; i < kanas.length; i++) {
            if (!parseKana(kanas[i])) {
                log.error("line: " + this._lineNr + " error parsing reading: " + kanas[i]);
                return false;
            }
        }
        return true;
    }

    boolean parseHeadwords(String hwStr) {
        Matcher l1match = _headwordsParsePattern.matcher(hwStr);
        // temp debug

        // if (_lineNr == 1015) // breakpoint per debug
        // log.info("");

        if (!l1match.find()) {
            log.info("headword non matchata");
            System.exit(1);
            return false;
        }

        String words = l1match.group(1); // leggibilità
        if (words.matches(".*\\(P.*")) // for debug
            System.out.print("");
        if (!parseKanjiWords(words)) {
            return false;
        }

        String kana = l1match.group(2);
        if (kana != null) {
            if (kana.matches(".*\\(.*")) // for debug
                System.out.print("");
            return parseKanas(kana);
        }
        return true;
    }

    boolean parseSenses(String sensesSubStr) {
        String seqPattern = ".*\\([0-9]\\).*";
        _senses = new Senses(this);

        // per semplificare l'elaborazioe divido in parti separate da /
        String[] delimitedParts = sensesSubStr.split("/");

        // riaggrego secondo eventuale presenza di (1) Etc.
        String temp = "";
        for (int i = 0; i < delimitedParts.length; i++) {
            // log.info(delimitedParts[i]);
            if (i == 0) {
                temp += delimitedParts[i];
                continue;
            }
            if (delimitedParts[i].matches(seqPattern)) {
                // nuovo senso
                if (temp.length() > 0)
                    _senses.add(temp);
                temp = "";
            }
            if (i == delimitedParts[i].length() - 1 && delimitedParts[i].matches(" *\\(P\\) *")) {
                _isP = true;
            } else {
                temp += "/" + delimitedParts[i];
            }
        }
        if (temp.length() > 0)
            _senses.add(temp);

        return true;
    }

    /*
     * KANJI-1;KANJI-2 [KANA-1;KANA-2] /(general information) (see xxxx)
     * gloss/gloss/.../ In addition, the EDICT2 has as its last field the
     * sequence number of the entry. This matches the "ent_seq" entity value in
     * the XML edition. The field has the format: EntLnnnnnnnnX. The EntL is a
     * unique string to help identify the field. The "X", if present, indicates
     * that an audio clip of the entry reading is available from the
     * JapanesePod101.com site.
     */
    public boolean buildFromEdictLine(String line, String sep,
            IEdictEntry entry, int lineNr) {

        String kanjiKanaLineChunk;
        String allInfoLineChunk;
        reset();

        _lineNr = lineNr;
        int totLen = line.length();

        if (line.matches("七"))
            System.out.print("");
        
        // log.info("line: " + line);

        Matcher lineMatcher = _linePattern.matcher(line);
        lineMatcher.find();
        if (lineMatcher.groupCount() < 3) {
            log.error("failed processing line:\n" + line);
            return false;
        } else {
            // debug for (int i = 1; i <= lineMatcher.groupCount(); i++)
            // System.out.print(lineMatcher.group(i) + " ");
            kanjiKanaLineChunk = lineMatcher.group(1);
            allInfoLineChunk = lineMatcher.group(2);
            this._ent_seq = lineMatcher.group(3);
        }

        if (!parseHeadwords(kanjiKanaLineChunk)) {
            return false;
        }
        if (!parseSenses(allInfoLineChunk)) {
            return false;
        }

        return true;
    }

    boolean processPOSMarker(String mark) {
        boolean found = false;
        if (mark.equals("P")) {
            setIsFrequent(true);
            found = true;
        }

        if (mark.startsWith("n")) {
            setIsPlainNoun(true);
            found = true;
        }

        if (mark.startsWith("v")) {
            setIsVerb(true);
            found = true;
        }

        if (mark.startsWith("adj")) {
            setIsAdjective(true);
            found = true;
        }

        if (mark.equals("Buddh") || mark.equals("MA") || mark.equals("comp")
                || mark.equals("food") || mark.equals("geom")
                || mark.equals("gram") || mark.equals("ling")
                || mark.equals("math") || mark.equals("mil")
                || mark.equals("physics")) {
            setField(mark);
            found = true;
        }

        if (!found)
            log.debug("unknown POS marker: " + mark);
        return found;
    }

    public ArrayList<Edict2K_Ele> getHeadWords() {
        return _headWords;
    }

    public String getReading() {
        return _reading;
    }

    public void setReading(String Reading) {
        _reading = Reading;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.enrico_viali.jacn.edict.IEdictEntry#getWholeDescription()
     */

    public String getWholeDescription() {
        log.error("codice rotto ma non sistemato, esco");
        System.exit(1);
        return "bah";
    }

    public void setWholeDescription(String wholeDescription) {
        log.error("codice rotto ma non sistemato, esco");
        System.exit(1);
        // _allDescriptions = wholeDescription;
        // parseDescriptions(_allDescriptions, Utl.NOT_INITIALIZED_INT);
        rateAsKanjiComposite();
    }

    public String getField() {
        return _field;
    }

    public void setField(String val) {
        _field = val;
    }

    public boolean getIsFrequent() {
        return _isFrequent;
    }

    public void setIsFrequent(boolean p) {
        _isFrequent = p;
    }

    @Override
    public long getLine() {
        return _lineNr;
    }

    public boolean getIsPlainNoun() {
        return _isNoun;
    }

    /**
     * Plain noun = (n) ie NOT n-adv adverbial noun (fukushitekimeishi) n-pref
     * noun, used as a prefix n-suf noun, used as a suffix n-t noun (temporal)
     * (jisoumeishi)
     * 
     * @param v
     */
    public void setIsPlainNoun(boolean v) {
        _isNoun = v;
    }

    @Override
    public boolean getIsAdjective() {
        return _isAdjective;
    }

    @Override
    public void setIsAdjective(boolean v) {
        _isAdjective = v;
    }

    @Override
    public boolean getIsVerb() {
        return _isVerb;
    }

    @Override
    public void setIsVerb(boolean v) {
        _isVerb = v;
    }

    @Override
    public ArrayList<String> getDescrizioni() {
        // old delet return _senses;
        log.error("this function must be removed, exiting");
        System.exit(1);
        return null;
    }

    @Override
    public void setDescrizioni(ArrayList<String> descrizioni) {
        log.error("this function must be removed, exiting");
        System.exit(1);
        // _senses = descrizioni;
    }

    public boolean fillValuesVector(String[] v) {
        if (v.length < 30) {
            log.error("vettore valori troppo piccolo, nr elementi: " + v.length);
            return false;
        }

        int i = 0;
        if ((getHeadWord() != null) && (getHeadWord().length() >= 0)) {
            v[i++] = getHeadWord();
        } else {
            log.error("valore indefinito");
            v[i++] = "";
            return false;
        }

        if ((this._reading != null) && (_reading.length() >= 0)) {
            v[i++] = _reading;
        } else {
            log.error("valore indefinito");
            v[i++] = "";
            return false;
        }

        log.error("codice rotto ma non aggiustato, esco");
        System.exit(1);

        // --- lunghezza ----
        v[i++] = "" + _len;

        if (getIsFrequent()) {
            v[i++] = "1";
        } else {
            v[i++] = "0";
        }

        v[i++] = getField();

        if (getIsPlainNoun()) {
            v[i++] = "1";
        } else {
            v[i++] = "0";
        }

        if (getIsVerb()) {
            v[i++] = "1";
        } else {
            v[i++] = "0";
        }

        if (getIsVerb()) {
            v[i++] = "1";
        } else {
            v[i++] = "0";
        }

        v[i++] = "" + _jlpt;

        v[i++] = "" + _lineNr;
        return true;
    }

    @Override
    public int getRating() {
        return _rating;
    }

    @Override
    public void setRating(int v) {
        _rating = v;
    }

    @Override
    public int compareTo(IEdictEntry other) {
        log.error("vecchio metodo ereditato in cut&paste, probabilmente da riscrivere, oppure riscrivere getRating");
        if (this.getRating() > other.getRating())
            return -1;
        if (this.getRating() < other.getRating())
            return 1;

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edict_2_Entry))
            return false;
        return this.getHeadWord().equals(((IEdictEntry) o).getHeadWord());
    }

    @Override
    public int hashCode() {
        return this.getHeadWord().hashCode();
    }

    public String getEntSeq() {
        return this._ent_seq;
    }

    public void setEntSeq(String val) {
        _ent_seq = val;
    }

    @Override
    public void setKanjiWord(String kanjiWord) {
        // TODO Auto-generated method stub

    }

    public boolean getIsP() {
        return this._isP;
    }

    static Pattern                         _linePattern           = Pattern.compile(EdictUtils.LINE_REGEX);
    static Pattern                         _topGlossPattern       = Pattern.compile(EdictUtils.GLOSS_REGEX);
    static Pattern                         _infoPattern           = Pattern.compile(EdictUtils.INFO_REGEX);
    static Pattern                         _headwordsParsePattern = Pattern.compile(EdictUtils.HEADWORDS_REGEX);

    ArrayList<Edict2K_Ele>                 _headWords;
    public Senses                          _senses;
    String                                 _ent_seq;
    boolean                                _isP;

    boolean                                _badForLearner;

    long                                   _lineNr;
    // private String _headWord; // quando non ci sono kanjiWord duplica campo
    // Reading
    private String                         _reading;
    int                                    _len;
    boolean                                _isFrequent;
    String                                 _field;
    boolean                                _isNoun;
    boolean                                _isVerb;
    boolean                                _isAdjective;
    int                                    _rating;

    int                                    _jlpt;

    private static org.apache.log4j.Logger log                    = Logger.getLogger(Edict_2_Entry.class);

    @Override
    public String getHeadWord() {
        // TODO Auto-generated method stub
        return null;
    }
}
