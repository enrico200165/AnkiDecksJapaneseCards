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
        clearRows();
        scollamentoContatoriWarn = false;
        previousFrame = 0;
    }

    void clearRows() {
        tableRows = new Element[5];
        for (int i = 0; i < tableRows.length; i++)
            tableRows[i] = null;
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

    int nrFromFName(String fname) {
        int ret = -1;
        String high = fname.substring(4, 8);
        String low = fname.substring(15, 18);

        ret = 100 * Integer.parseInt(high) + Integer.parseInt(low);

        return ret;
    }

    public void processFiles() {
        ArrayList<File> filesList = listFilesInFolder(new File("./data_in/rtk2/text/"), false);

        int fNumber;
        tablesScanned = 0;
        tablesScannedOK = 0;

        for (File fileEntry : filesList) {
            fNumber = nrFromFName(fileEntry.getName());
            // log.error(fNumber);
            // log.info(fileEntry.getAbsolutePath());
            if (fNumber == 201) {
            } else {
                Document ePage = parseFile(fileEntry);
                parsePage(fNumber, ePage, fileEntry.getName());
                previousTableFile = fileEntry.getPath();
            }
            fileEntry = null;
            System.gc();
        }

    }

    void parsePage(int fileNr, Document page, String filename) {
        String tablesSel = "table";

        Elements tablesInPage = page.select(tablesSel);
        for (Element outerTable : tablesInPage) {
            tablesScanned++;
            if (fileNr < 99999) {
                if (processInfoTable(fileNr, outerTable, filename, tablesScanned)) {
                    tablesScannedOK++;
                } else {
                    log.error("error in table scan, " + filename + " tableCounter=" + tablesCounter + " selector: " + outerTable.cssSelector());
                }
            } else {
                log.error("algoritmo da sviluppare per fileNr: " + fileNr + " tables scanned: " + tablesScanned);
            }
            if (errors > 999)
                Utils.esco("troppi errori, esco");
        }
    }

    boolean tableToSkip(int tableNr, int fileNr) {
        boolean skip = false;
        skip |= (tableNr == 185 && (fileNr == 102 || fileNr == 103));
        skip |= (tableNr == 201 && fileNr == 103);
        skip |= (tableNr == 317 && fileNr == 103);
        skip |= (tableNr == 317 && fileNr == 104);
        skip |= (tableNr == 446 && (fileNr == 104 || fileNr == 105));
        skip |= (tableNr == 41 && fileNr == 200);
        skip |= (tableNr == 218 && (fileNr == 202 || fileNr == 203));
        skip |= (tableNr == 9 && fileNr == 300);
        skip |= (tableNr == 112 && (fileNr == 300 || fileNr == 301));

        skip |= (tableNr == 539 && (fileNr == 304 || fileNr == 305)); // non ha la cella vuota a fine seconda riga, è splittata
        skip |= (tableNr == 123 && (fileNr == 401 || fileNr == 401)); 
        skip |= (tableNr == 75 && (fileNr == 500 || fileNr == 501)); 
        skip |= (tableNr == 88 && (fileNr == 600 || fileNr == 601)); 
        skip |= (tableNr == 256 && (fileNr == 602 || fileNr == 603)); 
        
        return skip;
    }

    public boolean processInfoTable(int fileNr, Element table, String filename, int scanNr) {
        final String rowsSelector = ".calibre8" + " " + ".calibre9";
        int tableNr = Utils.tableNr(table.cssSelector());

        if (tableToSkip(tableNr, fileNr)) {
            EntryMain mEntry = new EntryMain(); // entry to be added
            String msg = filename + " tableID=" + table.cssSelector() + " previousFrame=" + this.previousFrame + " gestire a mano";
            log.error(msg);
            mEntry.setKanji("狭*");
            mEntry.setComment(msg);
            entriesMain.add(mEntry);
            log.warn("forse dovrei forzare continuità frame");
            return true;
        }

        // mEntry.setTableID(table.cssSelector());

        if (previousTableID.equals(table.cssSelector())) {
            log.warn("same table: " + previousTableID + " \nprevious: " + this.previousTableFile + " \nfile    : " + filename);
            return true;
        }

        // log.info(filename + " " + scanNr + "/" + this.nr +
        // ": ----------------------------------------" + "\n" + table.html());

        Elements righe = table.select(rowsSelector);

        if (righe.size() <= 1) {
            log.error("zero o una riga, esco");
            System.exit(1);
            return false;
        }

        int nrNonEmptyRows = 0;
        for (int i = 0; i < righe.size(); i++) {
            if (righe.get(i).text().trim().length() > 0) {
                this.tableRows[i] = righe.get(i);
                nrNonEmptyRows++;
            }
        }

        EntryMain mEntry = null;
        if (nrNonEmptyRows <= 1) {
            log.error("");
            return true;
        }
        if (nrNonEmptyRows == 2) {
            mEntry = processStandardEntry(table, tableRows[0], tableRows[1], tableRows[2], filename, tableNr, scanNr, nrNonEmptyRows);
        }
        if (nrNonEmptyRows == 3) {
            boolean terzaCommento = (nrNonEmptyRows == 2 && tableRows[2].select("td").size() <= 3);
            if (terzaCommento) {
                log.info("comment: " + tableRows[2].text());
                mEntry = processStandardEntry(table, tableRows[0], tableRows[1], tableRows[2], filename, tableNr, scanNr, nrNonEmptyRows);
            } else {
                log.error("");
                return true;
            }
        }
        if (nrNonEmptyRows == 4) {
            log.error("");
            return true;
        }

        // --- --- ---- elaboriamo righe --- ---- ---- ----

        if (tablesCounter == 0 && mEntry.getRFrame() == 762) {
            log.warn("salto tabella di esempio");
            return true;
        }

        if (mEntry.getRFrame() != previousFrame + 1) {
            log.error("discontinuità rFrames, \nprevious=" + previousFrame + " \ncurrent=  " + mEntry.getRFrame());
            // Utils.esco("discontinuità rFrames");
        }

        if (mEntry.isValid()) {
            this.tablesCounter++;
            entriesMain.add(mEntry);
            previousFrame = mEntry.getRFrame();
            log.info("lastGoodFrame: " + previousFrame);
        } else {
            log.warn("entry not complete");
        }

        if (mEntry.getRFrame() != this.tablesCounter) {
            if (!scollamentoContatoriWarn) {
                log.warn("file: " + filename + " scollamento contatory: rFrame=" + mEntry.getRFrame() + " tablesCounter=" + this.tablesCounter + " selector: " + table.cssSelector());
                scollamentoContatoriWarn = true;
            }
        }

        // log.info(mEntry.toString());

        previousTableID = table.cssSelector();
        this.previousTableFile = filename;
        return true;
    }

    EntryMain processStandardEntry(Element table, Element riga1, Element riga2, Element riga3, String filename, int tableNr, int scanNr, int previousFrame) {
        EntryMain mEntry = new EntryMain(); // entry to be added
        boolean ret = true;
        String tableID = table.cssSelector();
        int fileNr = nrFromFName(filename);

        ret = ret && mEntry.processRiga1Posizional(fileNr, tableRows[0], filename, table.cssSelector(), tableNr, scanNr, this.tablesCounter);
        ret = ret && mEntry.processRiga2Positional(tableRows[1], filename, table.cssSelector(), tableNr, scanNr, this.tablesCounter);
        if (tableRows[2] != null)
            ret = ret && mEntry.processRiga3(tableRows[2], filename, table.cssSelector(), tableNr, scanNr, this.tablesCounter);
        if (ret)
            return mEntry;
        else
            return null;
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
    Element[]                              tableRows;

    int                                    suspendedTable;                         // 0=no,
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

    String                                 previousTableID;
    String                                 previousTableFile;
    ArrayList<EntryMain>                   entriesMain;
    int                                    tablesScanned;
    int                                    tablesScannedOK;
    int                                    previousFrame;

    int                                    tablesCounter;
    int                                    errors;
    boolean                                scollamentoContatoriWarn;

    private static org.apache.log4j.Logger log = Logger.getLogger(EPUB_main.class);
}
