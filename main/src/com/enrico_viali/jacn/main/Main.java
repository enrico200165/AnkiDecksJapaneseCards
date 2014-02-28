package com.enrico_viali.jacn.main;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.actions.GenNJLPTWordsList;
import com.enrico_viali.jacn.actions.CreateWordDeckFromWList;
import com.enrico_viali.jacn.actions.Exit;
import com.enrico_viali.jacn.actions.FillSqliteDbFromEdict;
import com.enrico_viali.jacn.actions.GenCSVKanjiList_NEW_JLPT;
import com.enrico_viali.jacn.actions.GenFirefoxCSVLesson;
import com.enrico_viali.jacn.actions.GenHTMLKanjiList;
import com.enrico_viali.jacn.actions.GenHTMLKanjiList_NEW_JLPT;
import com.enrico_viali.jacn.actions.GenerateFileKanjiSenzaHeisig;
import com.enrico_viali.jacn.actions.ImportFileKanjiWithoutHeisig;
import com.enrico_viali.jacn.actions.JPCNCsvExport;
import com.enrico_viali.jacn.actions.KanjdicBuildDB;
import com.enrico_viali.jacn.actions.ScrachFillEdictDB;
import com.enrico_viali.jacn.actions.UpdateDeckWithJPCNCsv;
import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.utils.*;

public class Main {
    /**
     * @param args
     */

    public static void main(String[] args) throws Exception {

        log.info("working directory: "+System.getProperty("user.dir"));
        if (!Utl.checks()) {
            System.err.println("errors detected in preliminary checks");
        }
                        
        drv = new TestDriver();
        // todo EV dat rimuovere
        // mgr.enrichEntries(true);
        drv.addAction(new GenNJLPTWordsList(drv));
        drv.addAction(new GenCSVKanjiList_NEW_JLPT(drv));
        GenHTMLKanjiList_NEW_JLPT a = new GenHTMLKanjiList_NEW_JLPT(drv);
        a.setLevelSup(5).setLevelInf(1);
        drv.addAction(a);
        drv.addAction(new GenHTMLKanjiList(drv));
        drv.addAction(new GenerateFileKanjiSenzaHeisig(drv));
        drv.addAction(new ImportFileKanjiWithoutHeisig(drv));
        drv.addAction(new JPCNCsvExport(drv));
        drv.addAction(new UpdateDeckWithJPCNCsv(drv));
        drv.addAction(new FillSqliteDbFromEdict(drv));
        drv.addAction(new CreateWordDeckFromWList(drv));
        drv.addAction(new ScrachFillEdictDB(drv));
        drv.addAction(new GenFirefoxCSVLesson(drv));
        // acts.add(pos++, new MecabTest(drv));
        drv.addAction(new KanjdicBuildDB(drv));
        drv.addAction(new Exit(drv));
        
        
        Intero cmdCode = new Intero();

        // se c'è sia UI sia comando solo uno viene eseguito
        // la UI ha sempre la precedenza
        processArgs(args, cmdCode);
        if (Cfg.isInteractive() || cmdCode.val < 0) {
            int choice = drv.ui();
            drv.executeAction(choice);
        } else {
            if (cmdCode.val >= 0)
                drv.executeAction(cmdCode.val);
        }
        System.exit(0);
    }

    public static void processArgs(String[] args, Intero commandCode) throws Exception {
        
        int i = 0;
        for (String s : args) {
            i++;
            log.info("arg["+i+"]="+s);
        }
        
        for (String s : args) {
            String val = s;
            if (s.charAt(0) == '-') {
                val = s.substring(1);

                if (val.equals("OVWEVKANJIFILE")) {
                    Cfg.setovwEVKanjiFile(true);
                    continue;
                }

                if (val.matches(Cfg.PATTERN_DEF)) {
                    Cfg.processDef(val);
                    continue;
                }

                if (val.equals("i")) {
                    // interactive
                    Cfg.interactive = true;
                    continue;
                }
                if (val.matches("c\\d+")) {
                    int op = Integer.parseInt(val.substring(1));
                    commandCode.val = op;
                    // launcher(mgr, op);
                    continue;
                }
                if (s.equals("")) {
                    continue;
                }
                log.error("illegal argument: " + s);
            }
        }
    }

    static TestDriver drv;
    private static org.apache.log4j.Logger log = Logger.getLogger(Main.class);
}
