package com.enricoviali.epub.ja.rtk2;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FilesTablesNoCNRead_F201Chap04 implements IPage {

    FilesTablesNoCNRead_F201Chap04(EPUB_main epubPar, EntryMain m) {
        this.epub = epubPar;
        clearRows();
        mEntry = m;
    }

    void clearRows() {
        tableRows = new Element[5];
        for (int i = 0; i < tableRows.length; i++)
            tableRows[i] = null;
    }

    boolean tableToSkip(int tableNr, int fileNr) {
        boolean skip = false;
        // skip |= (tableNr == -1 && (fileNr == -1 || fileNr == -1));
        return skip;
    }

    public void parseFiles(int fileNr, Document page, String filename) {
        String tablesSel = "table";

        if (fileNr != 201) {
            log.error("esco, chiamato per nr file sbagliato: " + fileNr);
            Utils.esco("");
        }

        log.debug("fileNr: " + fileNr);
        Elements tablesInPage = page.select(tablesSel);
        for (Element table : tablesInPage) {
            epub.tablesScannedIncr();
            epub.setCurTableNr(Utils.tableNr(table.cssSelector()));

            if (processTable(table)) {
                epub.tablesScannedIncrOK();
            } else {
                log.error("");
            }
            // log.error("error in table scan, " + filename + " tableCounter=" +
            // tablesCounter + " selector: " + table.cssSelector());
        }
    }

    
    public boolean processTable(Element table) {
        final String subTableSel = "tr td";
        
        epub.setCurTableNr(Utils.tableNr(table.cssSelector()));
        
        if (tableToSkip(epub.getCurTableNr(), epub.getfNumber())) {
            log.error(epub.getCurTableFile() + " tableID=" + table.cssSelector() + " previousFrame=" + epub.getPreviousRTK2Frame() + " skippo");
            // epub.addToMainEntries(mEntry);
            log.warn("forse dovrei forzare continuità frame");
            return true;
        }

        Elements cellTables = table.select(subTableSel);
        int cellNr = 0;
        for (Element t : cellTables) {
            mEntry.processCellTable(t,epub.getfNumber(),epub.getCurTableNr(),cellNr);
            cellNr++;
            if (epub.getCurTableNr() == 72 && cellNr == 2) 
                break;
        }
        
        if (epub.getPreviousTableID().equals(table.cssSelector())) {
            log.warn("same table: " + epub.getPreviousTableID() + " \nprevious: " + epub.getPreviousTableFile() + " \nfile    : " + epub.getCurTableFile());
            return true;
        }

        // log.info(filename + " " + scanNr + "/" + this.nr +
        // ": ----------------------------------------" + "\n" + table.html());



        epub.setPreviousTableID(table.cssSelector());
        epub.setPreviousTableFile(epub.getCurTableFile());
        return true;
    }

        
        
    
    
    
    EntryMain processStandardEntry(Element table, Element riga1, Element riga2, Element riga3, String filename, int tableNr, int scanNr, int previousFrame) {
        EntryMain mEntry = new EntryMain(this.epub); // entry to be added
        boolean ret = true;
        String tableID = table.cssSelector();
        int fileNr = Utils.nrFromFName(filename);

        ret = ret && mEntry.processRigaKanji1(true,fileNr, tableRows[0], filename, table.cssSelector(), tableNr, scanNr, epub.getCurTableNr());
        ret = ret && mEntry.processRigaRFrame(true,tableRows[1], filename, table.cssSelector(), tableNr, scanNr, epub.getCurTableNr());
        if (tableRows[2] != null)
            ret = ret && mEntry.processRiga3(tableRows[2], filename, table.cssSelector(), tableNr, scanNr, epub.getCurTableNr());
        if (ret)
            return mEntry;
        else
            return null;
    }

    EPUB_main                              epub;
    Element[]                              tableRows;

    EntryMain mEntry;
    
    private static org.apache.log4j.Logger log = Logger.getLogger(FilesTablesNoCNRead_F201Chap04.class);

}
