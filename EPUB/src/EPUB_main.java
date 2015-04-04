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
        this.tableCounter = -1;  // prima tabella è di esempio
    }

    public static ArrayList<File> listFilesForFolder(final File folder, boolean recurse) {
        ArrayList<File> filesList = new ArrayList<File>();
        for (final File fileEntry : folder.listFiles()) {
            filesList.add(fileEntry);
            if (recurse && fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, recurse);
            } else {
                // System.out.println(fileEntry.getName());
                // System.out.println(fileEntry.getAbsolutePath());
            }
        }
        return filesList;
    }

    public void process() {
        ArrayList<File> filesList = listFilesForFolder(new File("./data_in/rtk2/text/"), false);

        for (File fileEntry : filesList) {
            // log.info(fileEntry.getAbsolutePath());
            if (fileEntry.getPath().matches(".*part.*_split_.*.html")) {
                Document ePage = parseFile(fileEntry);
                parsePart1(ePage, fileEntry.getPath());
            }
            fileEntry = null;
            // System.gc();
        }

    }

    void parsePart1(org.jsoup.nodes.Document page, String filename) {
        String tablesSel = "table";

        Elements tables = page.select(tablesSel);
        for (Element outerTable : tables) {
            log.info(filename + " tableCounter=" + tableCounter+" selector: "+ outerTable.cssSelector());
            /*
             * String hclass = ".rk2-no-hint"; String tableId = "#table-";
             * String selector = hclass + "" + tableId + i + " " + ".calibre8" +
             * " " + ".calibre9";
             */
            processEntry(outerTable, filename, 0);
            if (errors > 999)
                esco("troppi errori, esco");
        }
        // System.gc();

    }

    public boolean processEntry(Element table, String filename, int scanNr) {
        Element riga1 = null;
        Element riga2 = null;
        Element riga3 = null;

        this.tableCounter++;
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
        }
        if (nrRighr >= 3) {
            riga3 = righe.get(2);
        }

        /*
         * if (!processRiga1(es, filename, scanNr)) { // log.error(filename +
         * " " + scanNr + "/" + this.nr + ": --------------------------------\n"
         * + es.html()); }
         */

        if (!processRiga2(table, filename, scanNr)) {
            log.error(filename + " " + scanNr + "/" + this.tableCounter + ": --------------------------------\n" + table.html().substring(0,80));
        }
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
                    log.warn("no link frame: " + tableCounter);
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

    
    
    boolean processRiga2(Element riga2, String filename, int scanNr) {

        { // ------ frame nr ----
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-r-number"
                    // ,".generated-style-2-override2"
                    ));
            Element rktk2Frame = find(riga2, sels);
            if (rktk2Frame != null) {
                log.info(tableCounter + " rktk2Frame: " + rktk2Frame.text());
            } else {
                errors++;
                log.error(filename + " nr=" + scanNr + " rktk2 Frame not found\nhtml TRONCATO:\n" + riga2.html().substring(0,80));
                bigProblemHook();
                return false;
            }
        }
        return true;
    }

    public static void main(String[] argc) {
        EPUB_main e = new EPUB_main();
        e.process();

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

    Element find(Element e, ArrayList<String> selectors) {
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

    void bigProblemHook() {
        log.error("fix this");
    }

    void esco(String msg) {
        log.error("esco: " + msg);
        System.exit(1);
    }

    int                                    tableCounter; // prima tavola è di esempio e duplicata
    int                                    errors = 0;

    private static org.apache.log4j.Logger log    = Logger.getLogger(EPUB_main.class);
}
