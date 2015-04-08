package com.enricoviali.epub.ja.rtk2;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EntryMain {

    public EntryMain(EPUB_main epubPar) {
        selectors = new Selectors();
        this.epub = epubPar;
        reset();
    }

    void reset() {

        fillManually = false;

        tableID = null;
        RTK2Frame = -1;
        capitolo = null;
        tableID = null;
        // --- Kanji1
        kanji = null;
        signPrim = null;
        readings = null;
        link = null;
        RTK1Frame = null;
        // --- Kanji2
        kanji2 = null;
        signPrim2 = null;
        readings2 = null;
        link2 = null;
        RTK1Frame2 = null;
        // --- RFrame
        RTK2Frame = -1;
        compKanj = null;
        compReading = null;
        compMean = null;
        // Commento
        comment = null;

    }

    public String calculateCapitolo() {
        if (getRTK2Frame() <= 56)
            return "01_0_kana_their_kanji";

        if (getRTK2Frame() <= 195)
            return "02_1_pure_groups_easy";
        if (getRTK2Frame() <= 336)
            return "02_2_pure_groups_3";
        if (getRTK2Frame() <= 574)
            return "02_3_pure_groups_2";

        if (getRTK2Frame() <= 623)
            return "03_1t_chinese_read";

        return "error";
    }

    public String getCapitolo() {
        return this.capitolo;
    }

    public String setCapitolo(String cap) {
        if (cap != null) {
            this.capitolo = cap;
        } else {
            this.capitolo = calculateCapitolo();
        }
        return "error";
    }

    public boolean isValid() {
        boolean ret = true;

        if (fillManually)
            return true;

        if (getRTK2Frame() < 1) {
            log.error("invalid RTK2 frame: " + getRTK2Frame());
            ret = false;
        }

        if (capitolo == null || capitolo.length() < 4) {
            log.error("capitolo invalid : " + getCapitolo());
            ret = false;
        }

        if (!ret) {
            log.info("brakpoint hook");
            // qui sarebbe il caso di fare un dump della tabella, dovrei inserire un puntatore che però potrebbe essere nulll  log.warn("");
        }
        return ret;
    }

    void setComment(String com) {
        this.comment = com;
    }

    public String toString() {
        String sep = "  ";
        String eq = "=";
        String s = "";

        s += sep + "table" + eq + getTableID();
        s += sep + "capitolo" + eq + calculateCapitolo();
        s += sep + "kanji" + eq + kanji;
        s += sep + "signal primitive" + eq + signPrim;
        s += sep + "On" + eq + readings;
        s += sep + "link" + eq + link;
        s += sep + "RTK1Frame" + eq + RTK1Frame;
        // da riga 2
        s += sep + "RTK2Frame" + eq + RTK2Frame;

        return s;
    }

    public boolean processRigaKanji1(boolean overwrite, int fileNr, Element rigaKanji1, String filename, String tableID, int tableNr,
            int scanNr,
            int previousFrame) {

        if (rigaKanji1 == null) {
            log.debug(filename + " " + tableID + " riga kanji1 is null");
            return false;
        }

        Elements TDs = rigaKanji1.select("td");
        int pos = TDs.size();

        // --- simply check size
        Elements nonEmpty = new Elements();
        for (Element e : TDs) {
            if (e.text().length() > 0)
                nonEmpty.add(e);
        }
        if (nonEmpty.size() < 3) {
            log.error(filename + " " + tableID + " riga kanji 1 con elementi mancanti\n" + rigaKanji1.html());
            return false;
        }

        // -- real processing
        pos--; // usually 4
        if (overwrite || getRTK1Frame() <= 0) {
            Element RTK1FrameE = TDs.get(pos);
            setRTK1Frame(RTK1FrameE.text());
        }

        pos--; // 3
        if (overwrite || getLink() == null) {
            Element linkE = TDs.get(pos);
            link = linkE.text();
        }

        pos--; // 2
        if (overwrite || getReadings() == null) {
            Element OnE = TDs.get(pos);
            this.setReadings(OnE.text());
        }

        pos--; // 1
        if (overwrite || getSignPrim() == null) {
            Element signPrimE = TDs.get(pos);
            setSignPrim(signPrimE.text());
        }

        pos--; // 0
        if (overwrite || getKanji() == null) {
            Element kanjiE = TDs.get(pos);
            setKanji(kanjiE.text());
        }

        return true;
    }

    public boolean processRigaKanji2(boolean overwrite, int fileNr, Element rigaKanji2, String filename, String tableID, int tableNr,
            int scanNr,
            int previousFrame) {

        if (rigaKanji2 == null)
            return false;

        Elements TDs = rigaKanji2.select("td");
        int pos = TDs.size();

        pos--; // usually 4
        if (pos < 0) {
            log.warn("");
            return false;
        }
        if (overwrite || getRTK1Frame() == -1) {
            Element RTK1FrameE = TDs.get(pos);
            setRTK1Frame2(RTK1FrameE.text());
        }

        pos--; // 3
        if (pos < 0) {
            log.warn("");
            return false;
        }
        if (overwrite || getLink2() == null) {
            Element linkE = TDs.get(pos);
            setLink2(linkE.text());
        }

        pos--; // 2
        if (pos < 0) {
            log.warn("");
            return false;
        }
        if (overwrite || getReadings2() == null) {
            Element OnE = TDs.get(pos);
            setReadings2(OnE.text());
        }

        pos--; // 1
        if (pos < 0) {
            log.warn("");
            return false;
        }
        if (overwrite || getSignPrim2() == null) {
            Element signPrimE = TDs.get(pos);
            setSignPrim2(signPrimE.text());
        }

        pos--; // 0
        if (pos < 0) {
            log.warn("");
            return false;
        }
        if (overwrite || getKanji2() == null) {
            Element kanjiE = TDs.get(pos);
            setKanji2(kanjiE.text());
        }

        return true;
    }

    public String getCompKanj() {
        return compKanj;
    }

    public void setCompKanj(String compKanj) {
        this.compKanj = compKanj;
    }

    public String getCompReading() {
        return compReading;
    }

    public void setCompReading(String compReading) {
        this.compReading = compReading;
    }

    public String getCompMean() {
        return compMean;
    }

    public void setCompMean(String compMean) {
        this.compMean = compMean;
    }

    public String getComment() {
        return comment;
    }

    public void setRTK1Frame(String rTK1Frame) {
        RTK1Frame = rTK1Frame;
    }

    public void setRTK2Frame(int rTK2Frame) {
        RTK2Frame = rTK2Frame;
    }

    boolean processRigaRFrame(boolean overwrite, Element rigaRFrame, String filename, String tableID, int tableNr, int scanNr,
            int tablesCounter) {

        if (rigaRFrame == null)
            return false;

        Elements TDs = rigaRFrame.select("td");
        int pos = TDs.size();
        Element cur;

        // --- simply check size
        Elements nonEmpty = new Elements();
        for (Element e : TDs) {
            if (e.text().length() > 0)
                nonEmpty.add(e);
        }
        if (nonEmpty.size() < 3) {
            if (tableNr != 185 || tableNr != 185)
                log.error(filename + " " + tableID + " riga RFrame con elementi mancanti\n" + rigaRFrame.html());
            return false;
        }

        // -- real processing

        pos--; // usually 4

        pos--; // usually 3
        if (pos < 0) {
            log.error("negative pos");
            return false;
        }
        if (overwrite || getCompMean() == null) {
            cur = TDs.get(pos);
            setCompMean(cur.text());
        }

        pos--; // usually 2
        if (pos < 0) {
            log.error("negative pos");
            return false;
        }
        if (overwrite || getCompReading() == null) {
            cur = TDs.get(pos);
            setCompReading(cur.text());
        }

        pos--; // usually 1
        if (pos < 0) {
            log.error("negative pos");
            return false;
        }
        if (overwrite || getCompKanj() == null) {
            cur = TDs.get(pos);
            setCompKanj(cur.text());
        }

        pos--; // usually 0
        if (pos < 0) {
            log.error("negative pos");
            return false;
        }
        if (overwrite || getRTK2Frame() == -1) {
            Element RTK2FrameE = TDs.get(pos);
            setRTK2Frame(RTK2FrameE.text());
        }
        return true;
    }

    boolean processRigaComment(boolean overwrite, Element rigaComm, String filename, String tableID, int tableNr, int scanNr,
            int tablesCounter) {

        if (rigaComm == null)
            return true;

        Elements TDs = rigaComm.select("td");
        int pos = TDs.size();
        int nonEmptyTDs = 0;
        String comms = "";
        for (int i = 0; i < TDs.size(); i++) {
            if (TDs.get(i).text().length() > 0) {
                comms += " " + TDs.get(i).text();
                nonEmptyTDs++;
                if (i != 1)
                    log.debug(tableID + " comment at TD nr: " + i);
            }
        }
        if (overwrite || getComment() == null) {
            setComment(comms.trim());
        }

        if (nonEmptyTDs != 1) {
            log.error("commento sparso su più TD: " + nonEmptyTDs);
            return false;
        }
        return true;
    }

    /**
     * Dovrebbe elaborare solo i casi normali, e fallire se manca il minimo
     * @return
     */
    boolean processStandardEntry(Element table, Element rigaKanji1, Element rigaKanji2, Element rigaRFrame, Element rigaCommento,
            String filename, int tableNr, int scanNr, int previousFrame) {
        boolean ret = true;
        String tableID = table.cssSelector();
        int fileNr = Utils.nrFromFName(filename);

        if (!processRigaKanji1(true, fileNr, rigaKanji1, filename, table.cssSelector(), tableNr, scanNr, epub.getTablesCounter()))
            ret = false;
        if (!processRigaKanji2(true, fileNr, rigaKanji2, filename, table.cssSelector(), tableNr, scanNr, epub.getTablesCounter())) {

        }
        if (!processRigaRFrame(true, rigaRFrame, filename, table.cssSelector(), tableNr, scanNr, epub.getTablesCounter()))
            ret = false;
        if (!processRigaComment(true, rigaCommento, filename, table.cssSelector(), tableNr, scanNr, epub.getTablesCounter())) {
        }

        if (ret) {
        } else {
            if (!splitTable(fileNr, tableNr)) {
                log.error(filename + " tableNr: " + tableNr);
                ret = false;
            } else {
                log.debug("just for a brakpoint " + filename + " tableNr: " + tableNr);
            }
        }
        return ret;
    }

    /**
     * pass to null rows not to process
     * @return
     */
    boolean processAnomalyEntry(Element table, String filename, int tableNr, int scanNr, int previousFrame, boolean xpageTableCompleting) {
        boolean ret = true;
        String tableID = table.cssSelector();
        Elements rows = table.select(Defs.rowsSelector);
        int fileNr = Utils.nrFromFName(filename);

        if (tableNr == 185) {
            if (fileNr == 102) {
                if (!xpageTableCompleting) {
                    Element td = rows.get(1).select("td").get(0);
                    String tmp = td.text();
                    setRTK2Frame(tmp);
                } else {
                    log.error("should not be completing");
                    System.exit(1);
                }
            } else if (fileNr == 103) {
                if (xpageTableCompleting) {
                    setCompKanj(rows.get(0).select("td").get(0).text().trim());
                    setCompReading(rows.get(0).select("td").get(1).text().trim());
                    setCompMean(rows.get(0).select("td").get(2).text().trim());
                } else {
                    log.error("");
                }
            }
        } else if (tableNr == 317) {
            if (fileNr == 103) {
            } else if (fileNr == 104) {
            }
        } else if (tableNr == 446) {
            if (fileNr == 104) {
                if (!xpageTableCompleting) {
                    setCompReading(rows.get(1).select("td").get(2).text());
                    setCompKanj(rows.get(1).select("td").get(1).text());
                    setRTK2Frame(rows.get(1).select("td").get(0).text());
                    // setRTK2Frame(tmp);
                } else {
                    log.error("should not be completing");
                    System.exit(1);
                }
            } else if (fileNr == 105) {
                if (xpageTableCompleting) {
                    setCompMean(rows.get(0).select("td").get(0).text());
                } else {
                    log.error("");
                }
            }
        } else {
            log.error(fileNr + " " + tableNr);
        }

        return ret;
    }

    static boolean manualTable(int fileNr, int tableNr) {
        boolean ret = false;
        ret = ret || ((fileNr == 102) && (tableNr == 185));
        ret = ret || ((fileNr == 103) && (tableNr == 191));
        ret = ret || ((fileNr == 103) && (tableNr == 204));
        if (ret)
            log.debug("");
        return ret;
    }

    static boolean splitTable(int fileNr, int tableNr) {
        boolean ret = false;
        ret = ret || ((fileNr == 102 || fileNr == 103) && (tableNr == 185));
        ret = ret || ((fileNr == 103 || fileNr == 104) && (tableNr == 317));
        ret = ret || ((fileNr == 104 || fileNr == 105) && (tableNr == 446));
        if (ret)
            log.debug("");
        return ret;
    }

    public int getRTK2Frame() {
        return this.RTK2Frame;
    }

    public int setRTK2Frame(String sPar) {
        String s = sPar.substring(2);
        this.RTK2Frame = Integer.parseInt(s);
        return RTK2Frame;
    }

    public int getRTK1Frame() {
        int ret;
        ret = Integer.parseInt(this.RTK1Frame);
        return ret;
    }

    public void setTableID(String id) {
        this.tableID = id;
    }

    public String getTableID() {
        return this.tableID;
    }

    public String getKanji() {
        return this.kanji;
    }

    public void setKanji(String k) {
        this.kanji = k;
    }

    public void setKanji2(String k) {
        this.kanji2 = k;
    }

    public String getSignPrim2() {
        return signPrim2;
    }

    public void setSignPrim2(String signPrim2) {
        this.signPrim2 = signPrim2;
    }

    public String getReadings2() {
        return readings2;
    }

    public void setReadings2(String readings2) {
        this.readings2 = readings2;
    }

    public String getLink2() {
        return link2;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }

    public String getRTK1Frame2() {
        return RTK1Frame2;
    }

    public void setRTK1Frame2(String rTK1Frame2) {
        RTK1Frame2 = rTK1Frame2;
    }

    public String getKanji2() {
        return kanji2;
    }

    // ############### old code, ignore ###################################

    boolean processRiga1(int fileNr, Element riga1, String filename, String tableID, int tableNr, int scanNr, int previousFrame) {

        { // ------ kanji ----
            ArrayList<String> sels = selectors.selsKanji(tableNr, fileNr);
            Element kanjiE = Utils.find(riga1, sels);
            if (kanjiE != null) {
                // log.info(nr + " signal primitive: " + kanji.text());
                kanji = kanjiE.text();
            } else {
                log.error(filename + " kanji not found, table ID: " + tableID);
                return false;
            }
        }
        { // signal primitive is missing I THINK
            ArrayList<String> sels = selectors.selsSignalPrim(tableNr, fileNr);
            Element signPrimE = Utils.find(riga1, sels);
            if (signPrimE != null) {
                signPrim = signPrimE.text();
                // log.info(nr + " signal primitive: " + signPrim.text());
            } else {
                log.error(filename + " missing signal primitive: " + tableID + " last good frame nr: " + previousFrame);
                return false;
            }
        }
        { // On reading
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-example-1-kanji span.no-style-override50",
                    ".generated-style-override2"));
            Element OnE = Utils.find(riga1, sels);
            if (OnE != null) {
                // log.info(nr + " signal primitive: " + OnE.text());
                this.readings = OnE.text();
            } else {
                log.info("no on Reading, tableID: " + tableID + " lastGoodFrame: " + previousFrame);
                return false;
            }
        }
        { // link
            ArrayList<String> sels = selectors.selsLinks(tableNr, fileNr);
            // new ArrayList<String>(Arrays.asList(".generated-style-override3",
            // ".generated-style-2-override18"));
            Element linkE = Utils.find(riga1, sels);
            if (linkE != null) {
                // log.info(nr + " link: " + link.text());
                link = linkE.text();
            } else {
                log.info(filename + " no link: tableID: " + tableID + " lastGoodFrame: " + previousFrame);
                log.info("");
            }
        }
        { // RTK1Frame frame number
            ArrayList<String> sels = selectors.selsRTK1FrameE(tableNr, fileNr);
            Element RTK1FrameE = Utils.find(riga1, sels);
            if (RTK1FrameE != null) {
                this.RTK1Frame = RTK1FrameE.text();
            } else {
                log.info(filename + " no RTK1FrameE: tableID: " + tableID + " lastGoodFrame: " + previousFrame);
                return false;
            }
            // log.info(nr + " kanji frame: " + link1.text());
        }
        return true;
    }

    boolean processRiga2(Element riga2, String filename, String tableID, int tableNr, int scanNr, int tablesCounter) {

        { // ------ RFrame nr ----
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-r-number"
                    // ,".generated-style-2-override2"
                    ));
            Element rktk2FrameE = Utils.find(riga2, sels);
            if (rktk2FrameE != null) {
                String rFrame = rktk2FrameE.text();
                setRTK2Frame(rFrame);
            } else {
                log.error(filename + " tableID: " + tableID + " scanNr=" + scanNr + " rktk2 Frame not found\nhtml TRONCATO:\n"
                        + riga2.html().substring(0, 40));
                EPUB_main.bigProblemHook();
                return false;
            }
        }

        return true;
    }

    boolean processRiga3(Element riga3, String filename, String tableID, int tableNr, int scanNr, int tablesCounter) {

        Elements TDs = riga3.select("td");
        int pos = TDs.size();
        Element cur;

        pos--; // usually 3
        cur = TDs.get(pos);
        this.comment = cur.text();

        return true;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    String    capitolo;
    String    tableID;
    Selectors selectors;

    // --- riga Kanji 1
    String    kanji;
    String    signPrim;


    public String getSignPrim() {
        return signPrim;
    }

    public void setSignPrim(String signPrim) {
        this.signPrim = signPrim;
    }


    String readings;


    public String getReadings() {
        return readings;
    }

    public void setReadings(String readings) {
        this.readings = readings;
    }

    void setFillManually(int RTK2FramePar) {
        this.fillManually = true;
        this.kanji = "#";
        setRTK2Frame(RTK2FramePar);
    }


    boolean                                fillManually;
    EPUB_main                              epub;

    String                                 link;

    String                                 RTK1Frame;
    // --- riga Kanji 1
    String                                 kanji2;
    String                                 signPrim2;
    String                                 readings2;
    String                                 link2;
    String                                 RTK1Frame2;
    // --- riga RFrame
    int                                    RTK2Frame;
    String                                 compKanj;
    String                                 compReading;
    String                                 compMean;
    // --- da riga 3
    String                                 comment;

    private static org.apache.log4j.Logger log = Logger.getLogger(EntryMain.class);
}
