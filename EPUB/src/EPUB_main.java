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

        Element riga1 = null;
        Element riga2 = null;
        Element riga3 = null;

        // log.info(filename + " " + scanNr + "/" + this.nr +
        // ": ----------------------------------------" + "\n" + table.html());

        String rowsSelector = ".calibre8" + " " + ".calibre9";

        Elements righe = table.select(rowsSelector);
        int nrRighr = righe.size();
        if (nrRighr <= 0) {
            log.error("zero righe");
            return false;
        }
        riga1 = righe.get(0);

        if (nrRighr >= 2) {
            riga2 = righe.get(1);
            if (!mEntry.processRiga2(table, filename, scanNr, this.tablesCounter)) {
                log.error(filename + " " + scanNr + "/" + this.tablesCounter + ": --------------------------------\n" + table.html().substring(0, 80));
            }
        }
        if (nrRighr >= 3) {
            riga3 = righe.get(2);
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
            log.warn("scollamento contatory: rFrame=" + mEntry.getRFrame() + " tablesCounter=" + this.tablesCounter);
        }

        /*
         * if (!processRiga1(es, filename, scanNr)) { // log.error(filename +
         * " " + scanNr + "/" + this.nr + ": --------------------------------\n"
         * + es.html()); }
         */

        return true;
    }

    /**
     * @param es
     * 
     *            Elabora elementi interni della tabella di una entry, che può
     *            consistere di 2 o 3 righe
     * 
     */

    boolean processRiga1(Elements es, String filename, int scanNr) {
        {
            Element riga1 = es.get(0); //
            // log.info(riga1.toString());

            { // ------ kanji ----
                ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-frame-kanji > span"));
                Element kanji = find(riga1, sels);
                if (kanji != null) {
                    // log.info(nr + " signal primitive: " + kanji.text());
                } else {
                    log.error("");
                    errors++;
                    return false;
                }
            }
            { // signal primitive is missing I THINK
                ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".generated-style-override1", ".generated-style-2-override28"));
                Element signPrim = find(riga1, sels);
                if (signPrim != null) {
                    // log.info(nr + " signal primitive: " + signPrim.text());
                } else {
                    errors++;
                    log.error("");
                    return false;
                }
            }
            { // On reading
                ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-example-1-kanji span.no-style-override50", ".generated-style-override2"));
                Element OnE = find(riga1, sels);
                if (OnE != null) {
                    // log.info(nr + " signal primitive: " + OnE.text());
                } else {
                    errors++;
                    log.error("");
                    return false;
                }
            }
            { // link

                ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".generated-style-override3", ".generated-style-2-override18"));
                Element link = find(riga1, sels);
                if (link != null) {
                    // log.info(nr + " link: " + link.text());
                } else {
                    log.warn("no link frame: " + tablesCounter);
                }
            }
            { // kanji frame number
                ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-vol-1-nr", ".generated-style-2-override1",
                        ".generated-style-2-override8",
                        ".generated-style-2-override28"));
                Element link1 = find(riga1, sels);
                // log.info(nr + " kanji frame: " + link1.text());
            }
        }
        return true;
    }

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

    public static Element find(Element e, ArrayList<String> selectors) {
        Element ret = null;
        Elements els;
        for (String sel : selectors) {
            els = e.select(sel);
            if (els.size() > 0) {
                return els.get(0);
            }
        }
        return ret;
    }

    static void bigProblemHook() {
        log.error("fix this");
    }

    void esco(String msg) {
        log.error("esco: " + msg);
        System.exit(1);
    }

    ArrayList<EntryMain>                   entriesMain;

    int                                    tablesCounter;
    int                                    errors;

    private static org.apache.log4j.Logger log = Logger.getLogger(EPUB_main.class);
}
