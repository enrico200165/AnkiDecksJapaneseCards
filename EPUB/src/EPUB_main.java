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
        String high = fname.substring(4,8);
        String low = fname.substring(15,18);
        
        ret = 100*Integer.parseInt(high)+Integer.parseInt(low);
        
        return ret;
    }
    
    public void processFiles() {
        ArrayList<File> filesList = listFilesInFolder(new File("./data_in/rtk2/text/"), false);

        int fNumber;
        for (File fileEntry : filesList) {
            fNumber= nrFromFName(fileEntry.getName());
            log.error(fNumber);
            if (true) continue;
            // log.info(fileEntry.getAbsolutePath());
            if (fileEntry.getPath().matches(".*part.*_split_.*.html")) {
                Document ePage = parseFile(fileEntry);
                parsePage(ePage, fileEntry.getPath());
                previousTableFile = fileEntry.getPath();
            }
            fileEntry = null;
            System.gc();
        }

    }

    void parsePage(Document page, String filename) {
        String tablesSel = "table";

        Elements tablesInPage = page.select(tablesSel);
        for (Element outerTable : tablesInPage) {
            if (!processInfoTable(outerTable, filename, 0))
                log.info(filename + " tableCounter=" + tablesCounter + " selector: " + outerTable.cssSelector());
            if (errors > 999)
                esco("troppi errori, esco");
        }
    }

    public boolean processInfoTable(Element table, String filename, int scanNr) {
        final String rowsSelector = ".calibre8" + " " + ".calibre9";
        EntryMain mEntry = new EntryMain(); // entry to be added
        int tableNr = Utils.tableNr(table.cssSelector());

        switch (tableNr) {
            case 201:
                log.error("tabella 201 inclusa in TD di 200, gestire la 200 a mano");
                mEntry.setComment("DUMMY DA SISTEMARE MANINA,  causa difetto ebook");
                return true;
            default: // fall off below
        }
        
        
        mEntry.setTableID(table.cssSelector());

        if (previousTableID.equals(table.cssSelector())) {
            log.warn("same table: " + previousTableID + " \nprevious: " + this.previousTableFile + " \nfile    : " + filename);
            return true;
        }

        // log.info(filename + " " + scanNr + "/" + this.nr +
        // ": ----------------------------------------" + "\n" + table.html());

        Elements righe = table.select(rowsSelector);

        if (righe.size() <= 0) {
            log.error("zero righe, esco");
            System.exit(1);
            return false;
        }

        for (int i = 0; i < righe.size(); i++) {
            this.tableRows[i] = righe.get(i);
        }

        // --- --- ---- elaboriamo righe --- ---- ---- ----

        if (!mEntry.processRiga1(tableRows[0], filename, table.cssSelector(), tableNr, scanNr, this.tablesCounter)) {
            log.error(filename + " " + scanNr + "/" + this.tablesCounter + ": --------------------------------\n" + table.html().substring(0, 80));
        }
        if (!mEntry.processRiga2(tableRows[1], filename, table.cssSelector(), tableNr, scanNr, this.tablesCounter)) {
            log.error(filename + " " + scanNr + "/" + this.tablesCounter + ": --------------------------------\n" + table.html().substring(0, 80));
        }
        if (tableRows[2] != null
                && !mEntry.processRiga3(tableRows[2], filename, table.cssSelector(), tableNr, scanNr, this.tablesCounter)) {
            log.error(filename + " " + scanNr + "/" + this.tablesCounter + ": --------------------------------\n" + table.html().substring(0, 80));
        }

        if (tablesCounter == 0 && mEntry.getRFrame() == 762) {
            log.warn("salto tabella di esempio");
            return true;
        }

        if (mEntry.isComplete()) {
            this.tablesCounter++;
            entriesMain.add(mEntry);
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
            esco("errore di parsin");

        }
        return doc;
    }

    static void bigProblemHook() {
        log.error("fix this");
    }

    void esco(String msg) {
        log.error("esco: " + msg);
        System.exit(1);
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

    int                                    tablesCounter;
    int                                    errors;
    boolean                                scollamentoContatoriWarn;

    private static org.apache.log4j.Logger log = Logger.getLogger(EPUB_main.class);
}
