package com.enricoviali.epub.ja.rtk2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EPUB_main {

    EPUB_main() {
        reset();
    }

    void reset() {
        entriesMain = new ArrayList<EntryMain>();
        noCNReading = new ArrayList<EntryMain>();

        suspendedTable = 0;
        previousTableID = "";
        previousTableFile = "";
        setPreviousRTK2Frame(0);

        pageMainType = new FilesTablesMainType(this/*,me*/);
        pageF201Chap4 = new FilesTablesNoCNRead_F201Chap04(this);
        pageF701Chap11 = new FilesTablesF701Chap11(this);

        tableF201Chap4 = new TableF201Chap4();
    }

    public static ArrayList<File> listFilesInFolder(final File folder, boolean recurse) {
        ArrayList<File> filesList = new ArrayList<File>();
        for (final File fileEntry : folder.listFiles()) {
            filesList.add(fileEntry);
            if (recurse && fileEntry.isDirectory()) {
                listFilesInFolder(fileEntry, recurse);
            } else {
                // System.out.println(fileEntry.getName();
            }
        }
        return filesList;
    }

    IPage selectParser(int fnumber) {

        if (fNumber < 201) {
            return pageMainType;
        }
        if (fNumber == 201) {
            return pageF201Chap4;
        }

        if (202 <= fNumber && fnumber < 700) {
            return pageMainType;
        }
        if (700 <= fNumber) {
            return pageF701Chap11;
        }
        log.error("maggiore di 800");
        return null;
    }

    public void processFiles() {
        ArrayList<File> filesList = listFilesInFolder(new File("./data_in/rtk2/text/"), false);

        fNumber = 0;
        tablesScanned = 0;
        tablesScannedOK = 0;

        setPreviousTableID("");
        setCurTableFile("");

        IPage pageParser = pageMainType;
        for (File fileEntry : filesList) {
            setCurTableFile(fileEntry.getName());
            fNumber = Utils.nrFromFName(getCurTableFile());
            // log.error(fNumber+ " " +log.info(fileEntry.getName()());
            Document ePage = parseFile(fileEntry);

            pageParser = selectParser(fNumber);
            pageParser.parseFiles(fNumber, ePage, fileEntry.getName());

            setPreviousTableFile(fileEntry.getName());
            fileEntry = null;
            System.gc();
        }
        log.info("================= normale uscita =======================");
    }

    /**
     * @param es
     * 
     *            Elabora elementi interni della tabella di una entry, che può
     *            consistere di 2 o 3 righe
     * 
     */

    public static void main(String[] argc) {
        EPUB_main e = new EPUB_main();
        e.processFiles();

    }

    Document parseFile(File f) {
        Document doc;
        try {
            doc = Jsoup.parse(f, "UTF-8", "http://example.com/");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            doc = null;
            Utils.esco("errore di parsin");

        }
        return doc;
    }

    static void bigProblemHook() {
        log.error("fix this");
    }

    int setPreviousRTK2FrameFromTable(Element table) {
        // SE c'è un RTKFrame dobbiamo settare previous a tale valore
        Pattern pattern = Pattern.compile("[\\s]*r-([0-9]+)[^0-9]*");

        /* possibile trovare più elementi della forma r-  ex. tabella di esempio
         * l'ultimo dovrebbe essere quello giusto (non sicuro)
        */

        for (int i = table.select("td").size() - 1; i >= 0; i--) {
            Element e = table.select("td").get(i);
            if (e.text().trim().length() < 7) {
                Matcher matcher = pattern.matcher(e.text());
                if (matcher.find()) {
                    String matched = matcher.group(1);
                    int prevFr = Integer.parseInt(matched);
                    setPreviousRTK2Frame(prevFr);
                    return prevFr;
                }
            }
        }
        return -2;
    }

    int getRTK2FrameFromTable(Element table) {
        // SE c'è un RTKFrame dobbiamo settare previous a tale valore

        if (table.text().indexOf("r-") != 1) { // operazione pesante solo quando necessaria
            Pattern pattern = Pattern.compile(".*r-([0-9]+)[^0-9]*");
            Matcher matcher = pattern.matcher(table.text());
            if (matcher.find())
            {
                int prevFr = Integer.parseInt(matcher.group(1));
                return prevFr;
            }
        }
        return -2;
    }

    boolean finalizeMainEntry(Element table, EntryMain mEntry) {
        addToMainEntries(mEntry);
        tablesScannedIncrOK();
        setPreviousTableID(table.cssSelector());
        setPreviousTableFile(getCurTableFile());
        
        setPreviousRTK2Frame(mEntry.getRTK2Frame());
        log.info(getfNumber() + " " + table.cssSelector() + " last-good/probably-prev: " 
        + getPreviousRTK2Frame()+ " "+getmEntry().getKanji());
        return true;
    }


    boolean finalizeBadEntry(Element table, EntryMain mEntry, String comment) {
        mEntry.setComment(mEntry.getComment()+"\n###bad###");
        addToMainEntries(mEntry);
        tablesScannedIncrOK();
        setPreviousTableID(table.cssSelector());
        setPreviousTableFile(getCurTableFile());
        
        setPreviousRTK2Frame(getRTK2FrameFromTable(table));
        log.info(getfNumber() + " " + table.cssSelector() + " last-good/probably-prev: " 
        + getPreviousRTK2Frame()+ " "+getmEntry().getKanji()+ " ###bad###");
        return true;
    }

    
    
    // elementi di lavoro, dovrebbero essere variabili locali ma per gestire le
    // tabelle spezzate su due file devo mantener i valori

    int suspendedTable; // 0=no,
                        // 1
                        // yes,
                        // non
                        // gestiamo
                        // tabelle
                        // in
                        // cui
                        // è
                        // sospesa
                        // la
                        // terza
                        // riga


    public void setTablesScanned(int val) {
        this.tablesScanned = val;
    }

    public void tablesScannedIncr() {
        this.tablesScanned++;
    }

    public void setCurTableNr(int val) {
        this.curTableNr = val;
    }

    public void curTableNrInc() {
        this.curTableNr++;
    }

    public int getCurTableNr() {
        return curTableNr;
    }

    public void tablesScannedIncrOK() {
        this.tablesScannedOK++;
    }

    public int getTablesScanned() {
        return tablesScanned;
    }

    void addToMainEntries(EntryMain mEntry) {
        this.entriesMain.add(mEntry);
    }

    public void addToNoCNReadEntries(EntryMain mEntry) {
        this.noCNReading.add(mEntry);
    }

    public int getPreviousRTK2Frame() {
        return previousRTK2Frame;
    }

    public void setPreviousRTK2Frame(int previousFrame) {
        if ((previousFrame != previousRTK2Frame + 1)
                && (previousRTK2Frame > 2)
                && !(previousRTK2Frame == 762 && previousFrame == 1))
            log.warn("setting RTK2Frame inconsistent: previous value: " + previousRTK2Frame + " new value: " + previousFrame);
        this.previousRTK2Frame = previousFrame;

        // log.info("previousRTK2Frame: "+previousRTK2Frame);
        if (previousRTK2Frame == 622)
            log.debug("just to break");
    }

    public String getPreviousTableFile() {
        return previousTableFile;
    }

    public void setPreviousTableFile(String previousTableFile) {
        this.previousTableFile = previousTableFile;
    }

    public String getPreviousTableID() {
        return previousTableID;
    }

    public void setPreviousTableID(String previousTableID) {
        this.previousTableID = previousTableID;
    }

    public int getTablesScannedOK() {
        return tablesScannedOK;
    }

    public void setTablesScannedOK(int tablesScannedOK) {
        this.tablesScannedOK = tablesScannedOK;
    }

    public String getCurTableFile() {
        return curTableFile;
    }

    public void setCurTableFile(String curTableFile) {
        this.curTableFile = curTableFile;
    }

    public int getfNumber() {
        return fNumber;
    }

    public void setfNumber(int fNumber) {
        this.fNumber = fNumber;
    }

    public int getPass() {
        return pass;
    }

    public void passIncr() {
        this.pass++;
    }

    public void passReset() {
        this.pass = 0;
    }

    public EntryMain getmEntry() {
        return mEntry;
    }

    public void setmEntry(EntryMain mEntry) {
        this.mEntry = mEntry;
    }


    int                                    pass;

    IPage                                  pageMainType;
    FilesTablesNoCNRead_F201Chap04         pageF201Chap4;
    FilesTablesF701Chap11                  pageF701Chap11;

    TableF201Chap4                         tableF201Chap4;

    EntryMain                              mEntry;

    String                                 previousTableID;
    String                                 previousTableFile;
    String                                 curTableFile;
    int                                    fNumber;

    int                                    curTableNr;
    int                                    tablesScanned;

    int                                    tablesScannedOK;

    ArrayList<EntryMain>                   entriesMain;
    ArrayList<EntryMain>                   noCNReading;

    int                                    previousRTK2Frame;

    int                                    errors;

    private static org.apache.log4j.Logger log = Logger.getLogger(EPUB_main.class);
}
