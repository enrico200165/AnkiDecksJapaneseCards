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

        for (File fileEntry : filesList) {
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
            log.info(filename + " tableCounter=" + tablesCounter + " selector: " + outerTable.cssSelector());
            processInfoTable(outerTable, filename, 0);
            if (errors > 999)
                esco("troppi errori, esco");
        }
    }

    public boolean processInfoTable(Element table, String filename, int scanNr) {

        EntryMain mEntry = new EntryMain(); // entry to be added

        // log.info(filename + " " + scanNr + "/" + this.nr +
        // ": ----------------------------------------" + "\n" + table.html());

        String rowsSelector = ".calibre8" + " " + ".calibre9";
        Elements righe = table.select(rowsSelector);

        int nrRighe = righe.size();
        if (nrRighe <= 0) {
            log.error("zero righe, esco");
            System.exit(1);
            return false;
        }

        // log.info("fine tabella sospesa, file: " + filename + " selector: " +
        // table.cssSelector() + " nrTable:" + this.tablesCounter);
        if (nrRighe >= 1) {
            riga1 = righe.get(0);
            if (nrRighe >= 2)
                riga2 = righe.get(1);
            if (nrRighe >= 3)
                riga3 = righe.get(2);
        }        
       
        
        if (previousTableID.equals(table.cssSelector())) {
            log.warn("same table: " + previousTableID+" \nprevious: "+this.previousTableFile+ " \nfile    : "+filename);
            log.debug("");
        } else {
            // salva
            previousTableID = table.cssSelector();
        }

        // --- --- ---- elaboriamo righe --- ---- ---- ----

        if (!mEntry.processRiga1(table, filename, scanNr, this.tablesCounter)) {
            log.error(filename + " " + scanNr + "/" + this.tablesCounter + ": --------------------------------\n" + table.html().substring(0, 80));
        }

        if (!mEntry.processRiga2(table, filename, scanNr, this.tablesCounter)) {
            log.error(filename + " " + scanNr + "/" + this.tablesCounter + ": --------------------------------\n" + table.html().substring(0, 80));
        }

        if (riga3 != null && !mEntry.processRiga2(table, filename, scanNr, this.tablesCounter)) {
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
            log.warn("file: " + filename + " scollamento contatory: rFrame=" + mEntry.getRFrame() + " tablesCounter=" + this.tablesCounter + " selector: " + table.cssSelector());
        }

        /*
         * if (!processRiga1(es, filename, scanNr)) { // log.error(filename +
         * " " + scanNr + "/" + this.nr + ": --------------------------------\n"
         * + es.html()); }
         */
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
    Element                                riga1 = null;
    Element                                riga2 = null;
    Element                                riga3 = null;

    int                                    suspendedTable;                           // 0=no,
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

    private static org.apache.log4j.Logger log   = Logger.getLogger(EPUB_main.class);
}
