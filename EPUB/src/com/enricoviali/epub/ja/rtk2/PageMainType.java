package com.enricoviali.epub.ja.rtk2;

import java.util.SplittableRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageMainType implements IPage {

    PageMainType(EPUB_main epubPar) {
        this.epub = epubPar;
        workRows = new Elements();
        clearRows();
        mEntry = new EntryMain(epubPar);
    }

    void clearRows() {
        workRows.clear();
    }

    boolean tableToSkip(Element table, int fileNr, int tableNr) {
        boolean skip = false;

        skip |= (tableNr == 1 && (fileNr == 5)); // tabella di esempio
        skip |= (tableNr == 201 && fileNr == 103); // embeddata in una riga
        /*
        skip |= (tableNr == 185 && (fileNr == 102 || fileNr == 103));
        skip |= (tableNr == 317 && fileNr == 103);
        skip |= (tableNr == 317 && fileNr == 104);
        skip |= (tableNr == 446 && (fileNr == 104 || fileNr == 105));
        skip |= (tableNr == 41 && fileNr == 200);
        skip |= (tableNr == 218 && (fileNr == 202 || fileNr == 203));
        skip |= (tableNr == 9 && fileNr == 300);
        skip |= (tableNr == 112 && (fileNr == 300 || fileNr == 301));

        skip |= (tableNr == 539 && (fileNr == 304 || fileNr == 305));
        // non ha la cella vuota a fine seconda riga, è splittata
        skip |= (tableNr == 123 && (fileNr == 401 || fileNr == 401));
        skip |= (tableNr == 75 && (fileNr == 500 || fileNr == 501));
        skip |= (tableNr == 88 && (fileNr == 600 || fileNr == 601));
        skip |= (tableNr == 256 && (fileNr == 602 || fileNr == 603));
               
        */

        return skip;
    }

    /* (non-Javadoc)
     * @see com.enricoviali.epub.ja.rtk2.IPage#parsePage(int, org.jsoup.nodes.Document, java.lang.String)
     */
    @Override
    public void parsePage(int fileNr, Document page, String filename) {
        String tablesSel = "table";

        if (!(fileNr < 201 || 201 < fileNr)) {
            log.error("esco, chiamato per nr file sbagliato: " + fileNr);
            Utils.esco("");
        }

        // log.info("fileNr: " + fileNr);

        Elements tablesInPage = page.select(tablesSel);
        for (Element table : tablesInPage) {
            int tableIDNr = Utils.tableNr(table.cssSelector());

            if (tableToSkip(table, fileNr, tableIDNr)) {
                epub.setPreviousRTK2FrameFromTable(table);
                continue;
            }
            epub.tablesScannedIncr();

            epub.setCurTableNr(Utils.tableNr(table.cssSelector()));
            // log.info(table.cssSelector()+ " "+epub.getCurTableNr());
            if (epub.getCurTableNr() == 185)
                log.debug("");

            if (processTable(fileNr, table, filename, epub.getTablesScanned())) {
                epub.tablesScannedIncrOK();
            } else {
                log.error(filename + " " + table.cssSelector() + "errore tabella ");
            }
            epub.setPreviousTableID(table.cssSelector());
            epub.setPreviousTableFile(filename);

        }
    }

    /* (non-Javadoc)
     * @see com.enricoviali.epub.ja.rtk2.IPage#processTable(int, org.jsoup.nodes.Element, java.lang.String, int)
     */
    @Override
    public boolean processTable(int fileNr, Element table, String filename, int scanNr) {

        /*
        if (epub.getCurTableNr() == 185)
            log.info("breakpoint");
        if (epub.getCurTableNr() == 203)
            log.info("breakpoint");
        */

        xpageTableCompleting = (scanNr == 1) || epub.getPreviousTableID().equals(table.cssSelector());
        if (xpageTableCompleting) {
            if (((scanNr != 1) || (scanNr != 1)) && !mEntry.splitTable(fileNr, epub.getCurTableNr()))
                log.warn("intial table  or same table: " + epub.getPreviousTableID() + " \nprev file: " + epub.getPreviousTableFile()
                        + " \nfile     : "
                        + filename);
        } else {
            // --- if change of table we must write
            if (mEntry != null) {
                if (mEntry.isValid()) {
                    // eliminare epub.curTableNrInc();
                    epub.addToMainEntries(mEntry);
                    epub.setPreviousRTK2Frame(mEntry.getRTK2Frame());
                    mEntry.reset();
                    // log.info(mEntry.toString());
                } else {
                    log.warn(filename + " " + table.cssSelector() + " entry not valid");
                    log.error("invalid esco");
                    // System.exit(1);
                }
            }
        }

        // --- --- --- --- --- load data to write at next iteration --- --- --- --- 
        Element kanji1Row = null;
        Element kanji2Row = null;
        Element RFrameRow = null;
        Element CommentRow = null;
        int nrNonEmptyRows = 0;
        int recognizedRows = 0;
        { // clean and diagnose row problems                                    
            this.workRows = table.select(Defs.rowsSelector);
            // fix mistake of tables inside row
            String wrongTableSel = "table";
            Elements wrongTables;
            // Element rowToUse = null;
            for (int i = 0; i < workRows.size(); i++) {
                // skip wrong rows containing table
                wrongTables = workRows.get(i).select(wrongTableSel);
                if (wrongTables.size() > 0) {
                    if (epub.getCurTableNr() != 200) {
                        log.warn("skip row with embedded table not to pick it twice: " + table.cssSelector());
                        log.warn("full table " + table.cssSelector() + ": \n" + table);
                    }
                    continue;
                }
                // skip blanks
                if (workRows.get(i).text().trim().length() <= 0) {
                    continue;
                }
                nrNonEmptyRows++;

                if (Utils.isKanjiRow(workRows.get(i))) {
                    if (kanji1Row == null) {
                        kanji1Row = workRows.get(i);
                        recognizedRows++;
                    }
                    else {
                        kanji2Row = workRows.get(i);
                        recognizedRows++;
                    }
                } else if (Utils.isFrameRow(workRows.get(i))) {
                    RFrameRow = workRows.get(i);
                    recognizedRows++;
                } else if (Utils.isComment(workRows.get(i))) {
                    CommentRow = workRows.get(i);
                    recognizedRows++;
                } else {
                    if (!mEntry.splitTable(fileNr, epub.getCurTableNr()) && !mEntry.manualTable(fileNr, epub.getCurTableNr())) {
                        log.warn(fileNr + " " + table.cssSelector() + " unable to recognize row:\n" + workRows.get(i));
                        log.warn("");
                    }
                }
            }
        }
        // assert ((nrNonEmptyRows == recognizedRows));

        // just to set breakpoints
        if ((((fileNr != -1) || (fileNr != -1)) && epub.getCurTableNr() == 185)
                || (epub.getCurTableNr() == 186)) {
            // log.info("breakpoint");
        }

        if (mEntry.manualTable(fileNr, epub.getCurTableNr())) {
            mEntry.setFillManually(epub.getRTK2FrameFromTable(table));
        } else {
            if (!mEntry.processStandardEntry(table, kanji1Row, kanji2Row, RFrameRow, CommentRow, filename, epub.getCurTableNr(), scanNr,
                    nrNonEmptyRows)) {
                mEntry.processAnomalyEntry(table, filename, epub.getCurTableNr(), scanNr, nrNonEmptyRows, xpageTableCompleting);
            }
        }
        // --- here things should be ok ---
        mEntry.setCapitolo(mEntry.calculateCapitolo());
        mEntry.setTableID(table.cssSelector());

        // --- --- ---- elaboriamo workRows --- ---- ---- ----

        // log.info(filename + " " + table.cssSelector() + " prev " + epub.getPreviousRTK2Frame() + " curr= " + mEntry.getRTK2Frame());
        // Utils.esco("discontinuità rFrames");

        if ((mEntry.getRTK2Frame() != epub.getPreviousRTK2Frame() + 1)
                && ((scanNr != 1) || (scanNr != 1))
                && !mEntry.splitTable(fileNr, epub.getCurTableNr())) {
            log.error("discontinuità rFrames, prev=" + epub.getPreviousRTK2Frame() + " curr= " + mEntry.getRTK2Frame() +
                    " " + table.cssSelector() + " " + filename + " forzo allineamento manualmente");
            // Utils.esco("discontinuità rFrames");
            epub.setPreviousRTK2Frame(mEntry.getRTK2Frame() - 1);
        }

        // --- scritture per l'ultimo tavola, codifica caso per caso

        return true;
    }


    boolean                                xpageTableCompleting;

    EntryMain                              mEntry;
    EPUB_main                              epub;
    Elements                               workRows;

    private static org.apache.log4j.Logger log = Logger.getLogger(PageMainType.class);

}
