import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
        this.tablesCounter = 0; // prima tabella è di esempio
        entriesMain = new ArrayList<EntryMain>();
        suspendedTable = 0;
        previousTableID = "";
        previousTableFile = "";
        scollamentoContatoriWarn = false;
        previousFrame = 0;

        pageMainType = new PageMainType(this);
        pageF201Chap4 = new PageF201Chap4(this);

        tableF201Chap4 = new TableF201Chap4();
    }

    public static ArrayList<File> listFilesInFolder(final File folder, boolean recurse) {
        ArrayList<File> filesList = new ArrayList<File>();
        for (final File fileEntry : folder.listFiles()) {
            filesList.add(fileEntry);
            if (recurse && fileEntry.isDirectory()) {
                listFilesInFolder(fileEntry, recurse);
            } else {
                // System.out.println(fileEntry.getAbsolutePath());
            }
        }
        return filesList;
    }

    public void processFiles() {
        ArrayList<File> filesList = listFilesInFolder(new File("./data_in/rtk2/text/"), false);

        int fNumber;
        tablesScanned = 0;
        tablesScannedOK = 0;

        for (File fileEntry : filesList) {
            fNumber = Utils.nrFromFName(fileEntry.getName());
            // log.error(fNumber+ " " +log.info(fileEntry.getName()());

            Document ePage = parseFile(fileEntry);

            if ((fNumber < 201 || 201 < fNumber)) {
                pageMainType.parsePage(fNumber, ePage, fileEntry.getName());
                continue;
            }
            if (fNumber == 201) {
                pageF201Chap4.parsePage(fNumber, ePage, fileEntry.getName());
                continue;
            }

            previousTableFile = fileEntry.getPath();
            fileEntry = null;
            System.gc();
        }

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

    public void tablesScannedIncrOK() {
        this.tablesScannedOK++;
    }

    public int getTablesScanned() {
        return tablesScanned;
    }

    public int getCurTableNr() {
        return curTableNr;
    }

    void addToMainEntries(EntryMain mEntry) {
        this.entriesMain.add(mEntry);
    }

    public int getPreviousFrame() {
        return previousFrame;
    }

    public void setPreviousFrame(int previousFrame) {
        this.previousFrame = previousFrame;
    }

    public int getTablesCounter() {
        return tablesCounter;
    }

    public void tablesCounterIncr() {
        tablesCounter++;
    }

    public void setTablesCounter(int tablesCounter) {
        this.tablesCounter = tablesCounter;
    }

    public String getPreviousTableFile() {
        return previousTableFile;
    }

    public void setPreviousTableFile(String previousTableFile) {
        this.previousTableFile = previousTableFile;
    }

    PageMainType   pageMainType;
    PageF201Chap4  pageF201Chap4;

    TableF201Chap4 tableF201Chap4;

    int            curTableNr;

    String         previousTableID;

    public String getPreviousTableID() {
        return previousTableID;
    }

    public void setPreviousTableID(String previousTableID) {
        this.previousTableID = previousTableID;
    }

    String               previousTableFile;
    ArrayList<EntryMain> entriesMain;
    int                  tablesScanned;

    int                  tablesScannedOK;
    int                  previousFrame;

    int                  tablesCounter;
    int                  errors;
    boolean              scollamentoContatoriWarn;

    public boolean isScollamentoContatoriWarn() {
        return scollamentoContatoriWarn;
    }

    public void setScollamentoContatoriWarn(boolean scollamentoContatoriWarn) {
        this.scollamentoContatoriWarn = scollamentoContatoriWarn;
    }

    private static org.apache.log4j.Logger log = Logger.getLogger(EPUB_main.class);
}
