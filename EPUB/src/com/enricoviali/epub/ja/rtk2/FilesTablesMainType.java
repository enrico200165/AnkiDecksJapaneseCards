package com.enricoviali.epub.ja.rtk2;

import java.util.SplittableRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FilesTablesMainType implements IPage {

    FilesTablesMainType(EPUB_main epubPar) {
        this.epub = epubPar;
        workRows = new Elements();
        clearRows();
        tProcVars = new TProcVars();
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
    public void parseFiles(int fileNr, Document page, String filename) {
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
            

            switch (tableType(epub.getPreviousRTK2Frame())) {
                case Defs.TAB_STANDARD: {
                    EntryMain mEntry = new EntryMain(this.epub);
                    this.mEntry = mEntry;
                    if (processTable(table)) {
                        epub.finalizeMainEntry(table, mEntry);
                    } else {
                        log.error(filename + " " + table.cssSelector() + " errore tabella ");
                        if (!processAnomaly(table)) {
                            log.error("");
                            System.exit(1);
                        }
                    }
                    break;
                }
                case Defs.TAB_ANOMALY:
                case Defs.TAB_ANOMALY_SPLIT:
                case Defs.TAB_STD_SPLIT: {
                    if (epub.getPass() <= 1) {
                        int ret = mEntry.processAnomalyEntry(table);
                        epub.passIncr();
                        switch (ret) {
                            case 0: {
                                log.error("");
                                break;
                            }
                            case 1: {
                                break;
                            }
                            case 2: {
                                epub.finalizeMainEntry(table, mEntry);
                                epub.passReset();
                                break;
                            }
                        }
                    } else {
                        log.error("");
                    }
                    break;
                }

                default:
                    log.error("break");
            }

        }
    }

    /**
     * Detects imperfections, fixes little imperfections, 
     * IMPLICITLY sets/produces the workable rows
     */
    boolean preProcessRows(int fileNr, Element table, String filename, int scanNr, TProcVars procStatus) {
        boolean ret = true;
        this.workRows = table.select(Defs.rowsSelector);
        String wrongTableSel = "table";

        Elements embeddedTable;
        for (int i = 0; i < workRows.size(); i++) {
            // skip wrong rows containing table inside row
            embeddedTable = workRows.get(i).select(wrongTableSel);
            if (embeddedTable.size() > 0) {
                log.warn("skip row with embedded table not to pick it twice: " + table.cssSelector());
                log.info("full table " + table.cssSelector() + ": \n" + table);
                continue;
            }
            // skip blanks
            if (workRows.get(i).text().trim().length() <= 0) {
                continue;
            }
            tProcVars.nrNonEmptyRows++;

            if (Utils.isKanjiRow(workRows.get(i))) {
                if (tProcVars.kanji1Row == null) {
                    // è la prima riga kanji
                    tProcVars.kanji1Row = workRows.get(i);
                }
                else { // è la seconda riga kanji
                    tProcVars.kanji2Row = workRows.get(i);
                }
                tProcVars.nrRcgndRows++;
            } else if (Utils.isFrameRow(workRows.get(i))) {
                tProcVars.RFrameRow = workRows.get(i);
                tProcVars.nrRcgndRows++;
            } else if (Utils.isComment(workRows.get(i))) {
                tProcVars.CommentRow = workRows.get(i);
                tProcVars.nrRcgndRows++;
            } else {
                ret = ret && false;
                log.debug(fileNr + " " + table.cssSelector() + " unable to recognize row:\n" + workRows.get(i));
            }
        }
        return true;
    }

    /* gestisce i casi normali e solo quelli
     * returns false if it fails, this should prompt a tentative analysis of the causes
     */
    public boolean processTable(Element table) {

        this.tProcVars.t = table;
        boolean procOK = false;
        boolean preProcOK = false;
        boolean finalizeOK = false;
        boolean checksOK = false;
        preProcOK = preProcessRows(epub.getfNumber(), table, epub.getCurTableFile(), epub.getTablesScanned(), this.tProcVars);

        if (preProcOK) {
            procOK = mEntry.processStandardEntry(tProcVars, epub.getCurTableFile(), epub.getCurTableNr(), epub.getTablesScanned(),
                    tProcVars.nrNonEmptyRows);
        }
        if (procOK) {
            finalizeOK = finalizeEntry(epub.getCurTableFile(), table, mEntry);
        }

        if (finalizeOK) {
            checksOK = checkFrNr(epub, epub.getPreviousRTK2Frame(), mEntry.getRTK2Frame());
        }

        if (checksOK) {
            return true;
        } else {
            log.debug("standard processing failed: " + epub.getCurTableFile() + " " + table.cssSelector()
                    + " " + epub.getPreviousRTK2Frame()
                    + " curr= " + mEntry.getRTK2Frame()
                    + " " + " tabella \n" + table);
            return false;
        }
    }

    boolean checkFrNr(EPUB_main epub, int prevFrNr, int curFrNr) {
        boolean ret = true;

        if (prevFrNr == 762 && curFrNr == 1 && epub.getfNumber() == 101)
            return true;

        ret = ret && (prevFrNr + 1 == curFrNr);
        return ret;
    }

    boolean processAnomaly(Element table) {

        log.error("procesAnomaly should manage: \n" +
                "current File=" + epub.getCurTableFile()
                + " cur file nr=" + epub.getfNumber()
                + " tables scanned=" + epub.getTablesScanned()

                + " pre table ID=" + epub.getPreviousTableID()
                + " prev RTK2 frame=" + epub.getPreviousRTK2Frame()
                );

        return false;
    }

    boolean isLastBeforeTypeChange(Element table, int rtk2fr) {
        return (rtk2fr == 574) || (rtk2fr == 623);
    }

    boolean pageParserChanged(Element table, int RTK2PRECEDENTE) {
        return isLastBeforeTypeChange(table, RTK2PRECEDENTE);
    }

    boolean finalizeEntry(String fileName, Element table, EntryMain e) {

        if (e.getRTK2Frame() == 734)
            log.debug("");

        mEntry.setCapitolo(mEntry.calculateCapitolo());
        mEntry.setTableID(table.cssSelector());

        // --- if change of table we must write
        if (e != null) {
            if (e.isValid(0)) {
                return true;
            } else {
                log.warn(fileName + " " + table.cssSelector() + " entry not valid");
                log.error("invalid esco");
                System.exit(1);
                return false;
            }
        }
        return false;
    }

    int tableType(int preRTK2Fr) {

        switch (preRTK2Fr) {
            case 184: {
                return Defs.TAB_STD_SPLIT;
            }
            case 316: {
                return Defs.TAB_STD_SPLIT;
            }

            default: {
                return Defs.TAB_STANDARD;
            }
        }

        /*
        TAB_ANOMALY= 1;
        TAB_STD_SPLIT= 2;
        TAB_ANOMALY_SPLIT= 3;
        TAB_UNKN= 9;
        */

    }


    EntryMain                              mEntry;
    TProcVars                              tProcVars;

    boolean                                xpageTableCompleting;

    EPUB_main                              epub;
    Elements                               workRows;

    private static org.apache.log4j.Logger log = Logger.getLogger(FilesTablesMainType.class);

}
