package com.enrico_viali.jacn.edict;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Edict_2_EntryTest {

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
    public void testHashCode() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testEdict_2_Entry() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testParseKanjiWord() {
        Edict_2_Entry e = new Edict_2_Entry();

        String test = "";
        Edict2K_Ele hw = new Edict2K_Ele(e);

        test = null;
        if (e.parseKanjiWord(test, hw))
            fail("non resiste a stringa null");

        test = "";
        if (e.parseKanjiWord(test, hw))
            fail("non rileva stringa vuota");

        test = "  ";
        if (e.parseKanjiWord(test, hw))
            fail("non rileva stringa blank");

        test = "()";
        if (e.parseKanjiWord(test, hw))
            fail("accetta () non precedute da kanji");

        test = " a() ( )";
        if (e.parseKanjiWord(test, hw))
            fail("accetta annotazioni vuote");

        test = "イタリア・ルネッサンス (Pippo,asasa)(x) (   )";
        if (!e.parseKanjiWord(test, hw))
            fail("fallisce su stringa corretta");

        test = "イタリア・ルネッサンス (Pippo,asasa)(x) イタリア・ルネッサ (   )";
        if (e.parseKanjiWord(test, hw))
            fail("non rileva caratteri fuori posto");

        test = "イタリア・ルネッサンス (Pippo,asasa)(x) イ";
        if (e.parseKanjiWord(test, hw)) {
            fail("non rileva caratteri fuori posto");
        }

        test = "イタリア・ルネッサンス (Pippo,asasa)(x)(xx";
        if (e.parseKanjiWord(test, hw)) {
            fail("non rileva parentesi non chiusa");
        }

        test = "イタリア・;ルネッサンス (Pippo,asasa)(x)(xx";
        if (e.parseKanjiWord(test, hw)) {
            fail("non rileva parentesi non chiusa");
        }
    }

    @Test
    public void testParseKanaWord() {
        Edict_2_Entry e = new Edict_2_Entry();

        String test = "";

        //  --- cancella questo sopra, è una duplicazione

        test = null;
        if (e.parseKana(test))
            fail("non resiste a stringa null");

        test = "";
        if (e.parseKana(test))
            fail("non rileva stringa vuota");

        test = "  ";
        if (e.parseKana(test))
            fail("non rileva stringa blank");

        test = "じゃがいも(じゃが芋,馬鈴薯)";
        if (!e.parseKana(test))
            fail("fallisce su stringa corretta");

        test = "じゃがいも(じゃが芋,馬鈴薯)(P)";
        if (!e.parseKana(test))
            fail("fallisce su stringa corretta");

        test = "  () ";
        if (e.parseKana(test))
            fail("non fallisce su stringa corretta");

        test = "イタリア・ルネッサンス (Pippo,asasa)(x)(xx";
        if (e.parseKana(test)) {
            fail("non rileva parentesi non chiusa");
        }

        test = "イタリア・;ルネッサンス (Pippo,asasa)(x)(xx";
        if (e.parseKana(test)) {
            fail("non rileva parentesi non chiusa");
        }

        test = "ジャガイモ;ばれいしょ(馬鈴薯)(P)";
        if (e.parseKana(test)) {
            fail("non rileva caratteri fuori posto");
        }

        test = " じゃがいも(じゃが芋,馬鈴薯)(P)";
        if (e.parseKana(test)) {
            fail("non rileva parentesi non chiusa");
        }

        test = " じゃがいも(じゃが芋,馬鈴薯)(P);ジャガいも(ジャガ芋)(P);ジャガイモ;ばれいしょ(馬鈴薯)(P)";
        if (e.parseKana(test)) {
            fail("non rileva parentesi non chiusa");
        }

    }

    @Test
    public void testRateAsKanjiComposite() {
        fail("Not yet implemented"); // TODO
    }


    @Test
    public void testParseTopGlosses() {
        String test = "";
        boolean ok = true;
        Edict_2_Entry e = new Edict_2_Entry();

        test = "/(A)(B)(C)uno/due/tre";
        e.parseSenses(test);
        if (!e.getGloss(2).equals("/tre"))
            fail("");

        test = "/(A)(B)(C)//tre/";
        e.parseSenses(test);
        if (!e.getGloss(2).equals("/tre"))
            fail("");
        if (!e.getGloss(3).equals("/"))
            fail("");

        // vedere se ignora la parentesi del significato
        test = "/(A)(B)(C)//tre(ignorami)/";
        e.parseSenses(test);

        test = t1;
        e.buildFromEdictLine(test, "", e, 0);

        test = "/(A)(B)(C)uno/due/tre/(2)/asas/asasa/(3)asasasa";
        e.parseSenses(test);
        if (!e.getGloss(2).equals("/tre"))
            fail("");
    }

    @Test
    public void testBuildFromEdictLine() {
        String test = "";
        boolean ok = true;
        Edict_2_Entry e = new Edict_2_Entry();

        test = t1;

        test = t2;
        e.buildFromEdictLine(test, "", e, 1);

        test = t3;
        e.buildFromEdictLine(test, "", e, 1);
        if (!ok) {
            System.out.println(e.toString());
            fail("could not match line:\n" + test);
        }

        test = t4;
        e.buildFromEdictLine(test, "", e, 1);
        if (!ok) {
            fail("could not match line:\n" + test);
        }

        test = t5;
        e.buildFromEdictLine(test, "", e, 1);
        if (!ok) {
        }

        test = t6;
        e.buildFromEdictLine(test, "", e, 1);
        if (!ok) {
            fail("could not match line:\n" + test);
        }

        test = t7;
        e.buildFromEdictLine(test, "", e, 1);
        if (!ok) {
            fail("could not match line:\n" + test);
        }

        test = t8;
        e.buildFromEdictLine(test, "", e, 1);
        if (!ok) {
            fail("could not match line:\n" + test);
        }

        test = t9;
        e.buildFromEdictLine(test, "", e, 1);
        if (!ok) {
            fail("could not match line:\n" + test);
        }

        test = t10;
        e.buildFromEdictLine(test, "", e, 1);
        if (!ok) {
            fail("could not match line:\n" + test);
        }

        test = t11;
        e.buildFromEdictLine(test, "", e, 1);
        if (!ok) {
            fail("could not match line:\n" + test);
        }

        
        test = "蕓薹;うん薹 [うんだい;うんたい;ウンダイ;ウンタイ] /(n) (See 油菜) rape (seed oil plant, Brassica campestris)/Chinese colza/yuntai/EntL2620910/";
        test = "蝲蛄(oK);蜊蛄(oK) [ざりがに;ザリガニ(P)] /(n) (uk) (See ニホンザリガニ,アメリカザリガニ) crayfish (esp. Japanese crayfish, Cambaroides japonicus or red swamp crayfish, Procambarus clarkii)/(P)/EntL1059040X/";
        e.buildFromEdictLine(test, "", e, 1);
        System.out.println(test+"\n"+e.toString());
        if (!ok) {
            fail("could not match line:\n" + test);
        }

        
    }

    @Test
    public void testParseDescriptions() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testProcessPOSMarker() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetHeadWord() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetKanjiWord() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetReading() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetReading() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetWholeDescription() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetWholeDescription() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetField() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetField() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetIsFrequent() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetFrequent() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetLine() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetIsNoun() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetIsNoun() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetIsAdjective() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetIsAdjective() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetIsVerb() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetIsVerb() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetDescrizioni() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetDescrizioni() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testFillValuesVector() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testGetRating() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testSetRating() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testCompareTo() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testEqualsObject() {
        fail("Not yet implemented"); // TODO
    }

    final String t1  = "いい頃;良い頃;良いころ;よい頃 [いいころ(いい頃,良い頃,良いころ);よいころ(良い頃,良いころ,よい頃)] /(exp) high time/about time/EntL2118250/";
    final String t2  = "いい子いい子 [いいこいいこ] /(n,vs) patting or stroking (pet, child, etc.)/EntL2552390/";
    final String t3  = "いい子ぶる;好い子ぶる [いいこぶる] /(v5r) to act the goody-goody/to pretend to be nice/EntL2121070X/";
    final String t4  = "せぶる /(v5r) (See 臥せる) to sleep/to lie down/EntL2553590/";
    final String t5  = "そら来た [そらきた] /(exp) (col) (See それ来た) there it is/there it comes/got it/of course/it figures/I knew it/expression one says when what is expected comes/EntL2772360/";
    final String t6  = "そういや;そういやあ /(exp) (See そう言えば・そういえば) which reminds me .../come to think of it .../now that you mention it .../on that subject .../so, if you say .../EntL2735320/";
    final String t7  = "そうはイカの金玉;そうは烏賊の金玉;然うは烏賊の金玉 [そうはイカのきんたま(そうはイカの金玉);そうはいかのきんたま(そうは烏賊の金玉,然うは烏賊の金玉);そうはイカのキンタマ] /(exp) (joc) (pun on そうは行かぬ) you wish!/that's not going to happen/not a chance/EntL2716860/";
    final String t9  = "すり合わせ;擦り合わせ;摺り合わせ;すり合せ;擦り合せ;摺り合せ [すりあわせ] /(n) (1) comparing and adjusting/reconciling/knocking into shape/bouncing of ideas, opinions, etc. off each other to obtain a fine-tuned integrated whole/(2) {engr} lapping/precision surface finishing/mating by rubbing together/(3) (uk) {med} margination/EntL2159020X/";
    final String t8  = "しゃぎり /(n) (1) short flute piece (in kyogen; usu. a lively solo)/(2) (also written as 砂切) flute and drum music played after each act but the last (in kabuki)/EntL2560830/";
    final String t10 = "じりじり;ぢりぢり /(adv,adv-to,vs) (1) (on-mim) slowly (but steadily)/gradually/bit-by-bit/(2) (on-mim) irritatedly/impatiently/(3) (on-mim) scorchingly (of the sun)/(4) (on-mim) sizzling (i.e. sound of frying in oil)/(5) (on-mim) sound of a warning bell, alarm clock, etc./(6) (on-mim) oozing out (oil, sweat, etc.)/seeping out/EntL1006000X/";
    final String t11 = "せる;させる /(aux-v,v1) (1) (せる is for 五段 verbs, させる for 一段; follows the imperfective form of (v5) and (vs) verbs; senses 1-3 of せる are sometimes abbreviated as 〜す) auxiliary verb indicating the causative/(2) (hum) (usu. as 〜(さ)せてもらう, 〜(さ)せていただく, etc.) auxiliary verb indicating that one has been granted the permission to do something/(3) auxiliary verb used to make verbs more \"active\"/(4) (hon) (as 〜(さ)せられる, 〜あら(さ)せられる, 〜(さ)せ給う, etc.) auxiliary verb used as an extreme honorific for others' actions/EntL2568020/";
}
