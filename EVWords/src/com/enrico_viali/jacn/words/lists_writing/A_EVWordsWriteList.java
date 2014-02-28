package com.enrico_viali.jacn.words.lists_writing;

import java.util.*;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.enrico_viali.html.*;
import com.enrico_viali.jacn.common.*;
import com.enrico_viali.jacn.edict.Edict_2_Manager;
import com.enrico_viali.jacn.evkanji.actions.EVKanjyEntryActionCheckAdHoc;
import com.enrico_viali.jacn.evkanji.actions.IEVKanjiEntryAction;
import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.utils.*;

/**
 * @author enrico *
 */

public abstract class A_EVWordsWriteList {

    public String kanjidicCreditsHTML() {
        String kissako = HTMLEVUtils.anchor("kissako.it", "kissako.it");
        String text = "";
        text += "Based on information from the Electronic Dictionary Research Group ";
        text += " <br />Browse "
                + kissako
                + " for other lists ";

        text += "";

        return text;
    }

    public String kanjidicCreditsTXT() {
        String kissako = "kissako.it";
        String text = "";
        text += "Produced by " + kissako;
        text += " using kanji information from KANJIDIC";
        text += " see http://www.csse.monash.edu.au/~jwb/kanjidic.html";
        text += " (copyright by EDRG (Electronic Dictionary Research Group)";
        text += " http://www.edrdg.org";

        text += "Browse "
                + kissako
                + " for other lists (ex. different grouping criteria) and different versions of this list (downloadble, with more/less info Etc.)";

        return text;
    }

    abstract String kanjiListStart(String name);

    /* EV rubbish: non dovrebbe stare qui */
    abstract String kanjiListEnd(String text);

    /**
     * @param filePathName
     * @param textBefore
     * @param textAfter
     * @param min
     * @param max
     * @return
     */
    public boolean writeKanjiList(String filePathName, String textBefore,
            String textAfter, String ext) {
        List<IRenderableAsTextLine> coll = new ArrayList<IRenderableAsTextLine>();

        // importiamo tutte le entries

        { // debug temporaneo
            EVKanjyEntryActionCheckAdHoc action = new EVKanjyEntryActionCheckAdHoc(
                    "");
            mgr.forEach(action, null);
        }
        coll.addAll(mgr.container.getEntriesList(comparator));

        String startList = textBefore;
        startList += kanjiListStart("");
        String endList = kanjiListEnd("");
        endList += textAfter;

        /*
         * l'effettiva operatività di questo metodo deriva dai functionoids che
         * gli sono passati, questi sono creati nelle classi derivate
         */
        boolean ret = FileHelper.writeList(filePathName, ext,
                Utl.ENCODING_UTF8, coll, startList, endList, filter, this._renderer,
                0, Cfg.NRMAX_KANJI, Utl.NOT_INITIALIZED_INT, true);

        log.info("in list nr elements: " + coll.size());

        try {
            Runtime.getRuntime().exec("notepad.exe " + filePathName + ext);
        } catch (IOException e) {
            log.error("browser not found, correggi il path", e);
        }
        return ret;
    }

    Comparator<EVKanjiEntry>               comparator;
    IEVRenderableFilter                    filter;
    ITexLineRenderer                       _renderer;
    EVKanjiManager                         mgr;
    Edict_2_Manager em;

    private static org.apache.log4j.Logger log = Logger
                                                       .getLogger(A_EVWordsWriteList.class);
}
