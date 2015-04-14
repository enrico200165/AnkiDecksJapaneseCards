package com.enricoviali.epub.ja.rtk2;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FilesTablesF701Chap11 implements IPage {

    FilesTablesF701Chap11(EPUB_main epubPar) {
        this.epub = epubPar;

    }

    boolean tableToSkip(int tableNr, int fileNr) {
        boolean skip = false;
        skip |= (tableNr == -1 && (fileNr == -1 || fileNr == -1));
        return skip;
    }

    public void parseFiles(int fileNr, Document page, String filename) {
        String tablesSel = "table";

        if (fileNr != 700 && fileNr != 701) {
            log.error("esco, chiamato per nr file sbagliato: " + fileNr);
            Utils.esco("");
        }

        // log.info("fileNr: " + fileNr);
        Elements tablesInPage = page.select(tablesSel);
        for (Element table : tablesInPage) {
            epub.tablesScannedIncr();
            epub.setCurTableNr(Utils.tableNr(table.cssSelector()));

            if (2 <= epub.getCurTableNr() && epub.getCurTableNr() <= 5)
                if (processTable(table)) {
                    epub.tablesScannedIncrOK();
                } else {
                    log.error("");
                }
        }
    }

    public boolean processTable(Element table) {
        final String entrySelector = ".generated-style-3-override6";

        log.info(table.cssSelector() + " " + table.select("tr").size());

        for (Element riga : table.select("tr")) {
            if (riga.select("td").size() != 3)
                log.error(riga.select("td").size() + riga.text());
            processRow(riga);
        }
        return true;
    }

    public boolean processRow(Element row) {
        final String entrySelector = ".generated-style-3-override6";

        mEntry = new EntryMain(this.epub, Defs.ENTRY_TYPE_MNE_KUN);

        mEntry.setKanji(row.select("td").get(0).text());
        mEntry.setReadings(row.select("td").get(1).text());
        mEntry.setCompMean(row.select("td").get(2).text());  // porcata ma uso questo per non creare altro campo
        mEntry.setRTK2Frame(epub.getPreviousRTK2Frame()+1);
        epub.setPreviousRTK2Frame(mEntry.getRTK2Frame());
        if (mEntry.isValid(Defs.ENTRY_TYPE_MNE_KUN)) {
            epub.addToKunMnemonics(mEntry);
            log.info(epub.getCurTableFile() + " rk2-"+ mEntry.getRTK2Frame());
        } else {
            log.error("");
            System.exit(1);
        }
            
        // log.info(mEntry.getKanji() + " - " + mEntry.getReadings()+ " - "  + mEntry.getCompMean());

        return true;
    }


    EntryMain                              mEntry;
    Element[]                              tableRows;
    EPUB_main                              epub;

    private static org.apache.log4j.Logger log = Logger.getLogger(FilesTablesF701Chap11.class);

}
