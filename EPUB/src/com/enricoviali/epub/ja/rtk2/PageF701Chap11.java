package com.enricoviali.epub.ja.rtk2;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageF701Chap11 implements IPage {

    PageF701Chap11(EPUB_main epubPar) {
        this.epub = epubPar;
        clearRows();

    }

    void clearRows() {
        tableRows = new Element[5];
        for (int i = 0; i < tableRows.length; i++)
            tableRows[i] = null;
    }

    boolean tableToSkip(int tableNr, int fileNr) {
        boolean skip = false;
        skip |= (tableNr == -1 && (fileNr == -1 || fileNr == -1));
        return skip;
    }

    public void parsePage(int fileNr, Document page, String filename) {
        String tablesSel = "table";

        if (fileNr != 701) {
            log.error("esco, chiamato per nr file sbagliato: " + fileNr);
            Utils.esco("");
        }

        log.info("fileNr: " + fileNr);
        Elements tablesInPage = page.select(tablesSel);
        for (Element table : tablesInPage) {
            epub.tablesScannedIncr();
            epub.setCurTableNr(Utils.tableNr(table.cssSelector()));

            if (processTable(fileNr, table, filename, epub.getTablesScanned())) {
                epub.tablesScannedIncrOK();
            } else {
                log.error("");
            }
            // log.error("error in table scan, " + filename + " tableCounter=" +
            // tablesCounter + " selector: " + table.cssSelector());
        }
    }

    public boolean processTable(int fileNr, Element table, String filename, int scanNr) {
        final String entrySelector = ".generated-style-3-override6";

        
        if (tableToSkip(epub.getCurTableNr(), fileNr)) {
            // crea entry co contenuti dummy e riconscibile per successiva correzione manuale
            EntryMain mEntry = new EntryMain(this.epub); // entry to be added
            String msg = filename + " tableID=" + table.cssSelector() + " previousFrame=" + epub.getPreviousRTK2Frame() + " gestire a mano";
            log.error(msg);
            mEntry.setKanji("狭*");
            mEntry.setComment(msg);
            epub.addToMainEntries(mEntry);
            log.warn("forse dovrei forzare continuità frame");
            return true;
        }

        // mEntry.setTableID(table.cssSelector());

        if (epub.getPreviousTableID().equals(table.cssSelector())) {
            log.warn("same table: " + epub.getPreviousTableID() + " \nprevious: " + epub.getPreviousTableFile() + " \nfile    : " + filename);
            return true;
        }

        // log.info(filename + " " + scanNr + "/" + this.nr +
        // ": ----------------------------------------" + "\n" + table.html());

        Elements celleRiga = table.select(entrySelector);
        assert(celleRiga.size() == 5);

        if (celleRiga.size() > 1) {
            log.error("zero o una riga, esco");
//            System.exit(1);
            return true
                   ;
        }

        int nrNonEmptyRows = 0;
        for (int i = 0; i < celleRiga.size(); i++) {
            if (celleRiga.get(i).text().trim().length() > 0) {
                this.tableRows[i] = celleRiga.get(i);
                nrNonEmptyRows++;
            }
        }

        EntryMain mEntry = null;
        if (nrNonEmptyRows <= 1) {
            log.error("");
            return true;
        }
        if (nrNonEmptyRows == 2) {
            mEntry = processStandardEntry(table, tableRows[0], tableRows[1], tableRows[2], filename, epub.getCurTableNr(), scanNr, nrNonEmptyRows);
        }
        if (nrNonEmptyRows == 3) {
            boolean terzaCommento = (nrNonEmptyRows == 2 && tableRows[2].select("td").size() <= 3);
            if (terzaCommento) {
                log.info("comment: " + tableRows[2].text());
                mEntry = processStandardEntry(table, tableRows[0], tableRows[1], tableRows[2], filename, epub.getCurTableNr(), scanNr, nrNonEmptyRows);
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

        if (epub.getCurTableNr() == 0 && mEntry.getRTK2Frame() == 762) {
            log.warn("salto tabella di esempio");
            return true;
        }

        if (mEntry.getRTK2Frame() != epub.getPreviousRTK2Frame() + 1) {
            log.error("discontinuità rFrames, \nprevious=" + epub.getPreviousRTK2Frame() + " \ncurrent=  " + mEntry.getRTK2Frame());
            // Utils.esco("discontinuità rFrames");
        }

        if (mEntry.isValid()) {
            epub.curTableNrInc();
            epub.addToMainEntries(mEntry);
            epub.setPreviousRTK2Frame(mEntry.getRTK2Frame());
            // log.info("lastGoodFrame: " + previousFrame);
        } else {
            log.warn("entry not complete");
        }

        if (mEntry.getRTK2Frame() != epub.getCurTableNr()) {
                log.warn("file: " + filename + " scollamento contatory: rFrame=" + mEntry.getRTK2Frame() + " tablesCounter=" + epub.getCurTableNr() + " selector: " + table.cssSelector());
        }

        // log.info(mEntry.toString());

        epub.setPreviousTableID(table.cssSelector());
        epub.setPreviousTableFile(filename);
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

    private static org.apache.log4j.Logger log = Logger.getLogger(PageF701Chap11.class);

}
