import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EntryMain {

    public EntryMain() {
        selectors = new Selectors();
        reset();
    }

    void reset() {
        tableID = "-";
        RTK2Frame = -1;
        capitolo = "-";
        tableID = "-";
        // --- da riga 1
        kanji = "知";
        signPrim = "-";
        readings = "-";
        link = "-";
        RTK1Frame = "-";
        // --- da riga 2
        RTK2Frame = -1;
        compKanj = "";
        compReading = "";
        compMean = "";

    }

    public int RFrame(String sPar) {
        String s = sPar.substring(2);
        this.RTK2Frame = Integer.parseInt(s);
        return RTK2Frame;
    }

    public String getCapitolo() {
        if (getRFrame() <= 56)
            return "01_0_kana_their_kanji";

        if (getRFrame() <= 195)
            return "02_1_pure_groups_easy";
        if (getRFrame() <= 336)
            return "02_2_pure_groups_3";
        if (getRFrame() <= 574)
            return "02_3_pure_groups_2";

        if (getRFrame() <= 623)
            return "03_1t_chinese_read";

        return "error";
    }

    public boolean isValid() {
        return true;
    }

    void setComment(String com) {
        this.comment = com;
    }

    public String toString() {
        String sep = "  ";
        String eq = "=";
        String s = "";

        s += sep + "table" + eq + getTableID();
        s += sep + "capitolo" + eq + getCapitolo();
        s += sep + "kanji" + eq + kanji;
        s += sep + "signal primitive" + eq + signPrim;
        s += sep + "On" + eq + readings;
        s += sep + "link" + eq + link;
        s += sep + "RTK1Frame" + eq + RTK1Frame;
        // da riga 2
        s += sep + "RTK2Frame" + eq + RTK2Frame;

        return s;
    }

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
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-example-1-kanji span.no-style-override50", ".generated-style-override2"));
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

    public boolean processRiga1Posizional(int fileNr, Element riga1, String filename, String tableID, int tableNr, int scanNr, int previousFrame) {

        Elements TDs = riga1.select("td");
        int pos = TDs.size();

        pos--; // usually 4
        Element RTK1FrameE = TDs.get(pos);
        setKanji(RTK1FrameE.text());

        pos--; // 3
        Element linkE = TDs.get(pos);
        link = linkE.text();

        pos--; // 2
        Element OnE = TDs.get(pos);
        this.readings = OnE.text();

        pos--; // 1
        Element signPrimE = TDs.get(pos);
        signPrim = signPrimE.text();

        pos--; // 0
        Element kanjiE = TDs.get(pos);
        kanji = kanjiE.text();

        return true;
    }

    boolean processRiga2Positional(Element riga2, String filename, String tableID, int tableNr, int scanNr, int tablesCounter) {

        Elements TDs = riga2.select("td");
        int pos = TDs.size();
        Element cur;
        
        pos--; // usually 4
        
        pos--; // usually 3
        cur = TDs.get(pos);
        this.compMean = cur.text();
        
        pos--; // usually 2
        cur = TDs.get(pos);
        this.compReading = cur.text();

        pos--; // usually 1
        cur = TDs.get(pos);
        this.compKanj = cur.text();

        pos--; // usually 0
        Element RTK2FrameE = TDs.get(pos);
        RFrame(RTK2FrameE.text());

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
                RFrame(rFrame);
            } else {
                log.error(filename + " tableID: " + tableID + " scanNr=" + scanNr + " rktk2 Frame not found\nhtml TRONCATO:\n" + riga2.html().substring(0, 40));
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

    public int getRFrame() {
        return this.RTK2Frame;
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

    public void setKanji(String k) {
        this.kanji = k;
    }

    String                                 capitolo;
    String                                 tableID;
    Selectors                              selectors;

    // --- da riga 1
    String                                 kanji;
    String                                 signPrim;
    String                                 readings;
    String                                 link;
    String                                 RTK1Frame;
    // --- da riga 2
    int                                    RTK2Frame;
    String                                 compKanj;
    String                                 compReading;
    String                                 compMean;
    // --- da riga 3
    String                                 comment;

    private static org.apache.log4j.Logger log = Logger.getLogger(EntryMain.class);
}
