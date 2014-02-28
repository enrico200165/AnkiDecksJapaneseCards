package com.enrico_viali.jacn.edict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestKanjiCompositesMgr {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoadFromRam() {
        this.km = new KanjiCompositesMgr();
        Edict_2_Manager em = new Edict_2_Manager();
        em.loadFromFile();
        KanjiCompositesMgr km = new KanjiCompositesMgr();
        km.loadFromRam(em.getHeadwordsList());
    }

    @Test
    public void testGetUnsortedComposites() {
        this.km = new KanjiCompositesMgr();
        Edict_2_Manager em = new Edict_2_Manager();
        em.loadFromFile();
        KanjiCompositesMgr km = new KanjiCompositesMgr();
        km.loadFromRam(em.getHeadwordsList());
        // 1月5日(日)は事務局の冬季休業期間
        String k = "務";
        log.info("kanji: " + k + " has nr composites: " + km.getUnsortedComposites(k, true).size());
        ArrayList<Edict2K_Ele> list = km.getUnsortedComposites(k, true);
        Collections.sort(list);
        for (Edict2K_Ele e : list) {
            log.info(e.getKanjiWord());
        }
    }

    
    

    @Test
    public void testGetSortedComposites() {
        this.km = new KanjiCompositesMgr();
        Edict_2_Manager em = new Edict_2_Manager();
        em.loadFromFile();
        KanjiCompositesMgr km = new KanjiCompositesMgr();
        km.loadFromRam(em.getHeadwordsList());
        // 1月5日(日)は事務局の冬季休業期間
        String k = "務";
        log.info("kanji: " + k + " has nr composites: " + km.getSortedComposites(k, true,-1).size());
        List<Edict2K_Ele> list = km.getSortedComposites(k, true,20);
        int i = 0;
        for (Edict2K_Ele e : list) {
            i++;
            log.info(i+ " "+ e.getMotherEntry().toString());
        }
    }

    
    
    
    KanjiCompositesMgr                     km;

    private static org.apache.log4j.Logger log = Logger
                                                       .getLogger(TestKanjiCompositesMgr.class);
}
