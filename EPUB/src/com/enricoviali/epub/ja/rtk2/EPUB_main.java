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

        pageMainType = new PageMainType(this);
        pageF201Chap4 = new PageNoCNRead_F201Chap04(this);
        pageF701Chap11 = new PageF701Chap11(this);

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

    public void processFiles() {
        ArrayList<File> filesList = listFilesInFolder(new File("./data_in/rtk2/text/"), false);

        int fNumber;
        tablesScanned = 0;
        tablesScannedOK = 0;

        setPreviousTableID("no-table");

        IPage pageParser = pageMainType;
        for (File fileEntry : filesList) {
            fNumber = Utils.nrFromFName(fileEntry.getName());
            // log.error(fNumber+ " " +log.info(fileEntry.getName()());
            Document ePage = parseFile(fileEntry);

            if (fNumber == 201) {
                pageParser = pageF201Chap4;
            }
            if (fNumber == 202) {
                pageParser = pageMainType;
            }
            if (fNumber == 701) {
                pageParser = pageF701Chap11;
            }
            if (fNumber >= 800) {
                log.error("maggiore di 800");
                break;
            }
            pageParser.parsePage(fNumber, ePage, fileEntry.getName());

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

        for (Element e : table.select("td")) {
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
    

    IPage                                  pageMainType;
    PageNoCNRead_F201Chap04                pageF201Chap4;
    PageF701Chap11                         pageF701Chap11;

    TableF201Chap4                         tableF201Chap4;

    String                                 previousTableID;
    String                                 previousTableFile;

    int                                    curTableNr;
    int                                    tablesScanned;

    int                                    tablesScannedOK;

    ArrayList<EntryMain>                   entriesMain;
    ArrayList<EntryMain>                   noCNReading;

    int                                    previousRTK2Frame;

    int                                    errors;

    private static org.apache.log4j.Logger log = Logger.getLogger(EPUB_main.class);
}
