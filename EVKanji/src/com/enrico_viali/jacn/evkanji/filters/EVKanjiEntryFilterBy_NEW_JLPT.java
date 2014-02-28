package com.enrico_viali.jacn.evkanji.filters;

//import java.util.ArrayList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.utils.*;

import com.enrico_viali.jacn.common.Cfg;

/**
 * Calcola i kanji L1 pr differenza: quelli con Heisig < 2042 che non sono negli
 * altri livelli
 * 
 * 
 * @author enrico
 * 
 */
public class EVKanjiEntryFilterBy_NEW_JLPT implements IEVRenderableFilter {

    static Set<String> buildSetOfChars(String kanjis) {
        HashSet<String> set = new HashSet<String>();

        for (int i = 0; i < kanjis.length(); i++) {
            String s = "" + kanjis.charAt(i);
            if (set.contains(s)) {
                log.warn(s + " already present in set");
            }
            set.add(s);
        }

        return set;
    }

    public EVKanjiEntryFilterBy_NEW_JLPT(int min, int max, EVKanjiManager mgr) {
        this.mgr = mgr;
        minLev = min;
        maxLev = max;
        if (minLev < 1 || maxLev > 5) {
            log.error("invalid level");
            System.exit(-1);
        }
        level5 = buildSetLev5();
        log.debug("New JLPT Tentaive filter for L5 contains nr kanji: "
                + buildSetLev5().size());
        level4 = buildSetLev4();
        log.debug("New JLPT Tentaive filter for L4 contains nr kanji: "
                + buildSetLev4().size());
        level3 = buildSetLev3();
        log.debug("New JLPT Tentaive filter for L3 contains nr kanji: "
                + buildSetLev3().size());
        level2 = buildSetLev2();
        log.debug("New JLPT Tentaive filter for L2 contains nr kanji: "
                + buildSetLev2().size());
        level1 = buildSetLev1();
        log.debug("New JLPT Tentaive filter for L1 contains nr kanji: "
                + buildSetLev1().size());

        // ---------- check intersections -----

        { // --- L5 ----
            Set<String> inters5;
            inters5 = new HashSet<String>(level5); // use the copy constructor
            inters5.retainAll(level4);
            if (inters5.size() > 0) {
                log.error("ci sono duplicati l5 - l4, nr: " + inters5.size());
                for (String k : inters5) {
                    log.info(k);
                }
                System.exit(1);
            }
            inters5 = new HashSet<String>(level5);
            inters5.retainAll(level3);
            if (inters5.size() > 0) {
                log.error("ci sono duplicati l5 - l3, nr: " + inters5.size());
                for (String k : inters5) {
                    log.info(k);
                }
                System.exit(1);
            }
            inters5 = new HashSet<String>(level5);
            inters5.retainAll(level2);
            if (inters5.size() > 0) {
                log.error("ci sono duplicati l5 - l2, nr: " + inters5.size());
                for (String k : inters5) {
                    log.info(k);
                }
                System.exit(1);
            }
            inters5 = new HashSet<String>(level5);
            inters5.retainAll(level1);
            if (inters5.size() > 0) {
                log.error("ci sono duplicati l5 - l1, nr: " + inters5.size());
                for (String k : inters5) {
                    log.info(k);
                }
                System.exit(1);
            }
        }
        { // --- L4 ----
            Set<String> inters4;
            inters4 = new HashSet<String>(level4); // use the copy constructor
            inters4.retainAll(level3);
            if (inters4.size() > 0) {
                log.error("ci sono duplicati l4 - l3, nr: " + inters4.size());
                for (String k : inters4) {
                    log.info(k);
                }
                System.exit(1);
            }
            inters4 = new HashSet<String>(level4);
            inters4.retainAll(level2);
            if (inters4.size() > 0) {
                log.error("ci sono duplicati l4 - l2, nr: " + inters4.size());
                for (String k : inters4) {
                    log.info(k);
                }
                System.exit(1);
            }
            inters4 = new HashSet<String>(level4);
            inters4.retainAll(level1);
            if (inters4.size() > 0) {
                log.error("ci sono duplicati l4 - l1, nr: " + inters4.size());
                for (String k : inters4) {
                    log.info(k);
                }
                System.exit(1);
            }
        }
        { // --- L3 ----
            Set<String> inters3;
            inters3 = new HashSet<String>(level3); // use the copy constructor
            inters3.retainAll(level2);
            if (inters3.size() > 0) {
                log.error("ci sono duplicati l3 - l2, nr: " + inters3.size());
                for (String k : inters3) {
                    log.info(k);
                }
                System.exit(1);
            }
            inters3 = new HashSet<String>(level3);
            inters3.retainAll(level1);
            if (inters3.size() > 0) {
                log.error("ci sono duplicati l3 - l1, nr: " + inters3.size());
                for (String k : inters3) {
                    log.info(k);
                }
                System.exit(1);
            }
        }
        { // --- L2 ----
            Set<String> inters2;
            inters2 = new HashSet<String>(level2); // use the copy constructor
            inters2.retainAll(level1);
            if (inters2.size() > 0) {
                log.error("ci sono duplicati l2 - l1, nr: " + inters2.size());
                for (String k : inters2) {
                    log.info(k);
                }
                System.exit(1);
            }
        }
    }

    public int getNewJLPTLevel(String k) {
        if (level5.contains(k))
            return 5;
        if (level4.contains(k))
            return 4;
        if (level3.contains(k))
            return 3;
        if (level2.contains(k))
            return 2;
        if (level1.contains(k))
            return 1;
        log.warn("livello non trovato per: " + k);
        return 0;
    }

    @Override
    public boolean includeIt(IRenderableAsTextLine ePar) {
        if (!(ePar instanceof EVKanjiEntry)) {
            log.error("tipo errato, atteso: " + EVKanjiEntry.class.getName());
            System.exit(1);
        }

        EVKanjiEntry e = (EVKanjiEntry) ePar;
        int livello = 0;
        // --- assegna livello ---        
        if (level1.contains(e.getKanji())) {
            if (livello != 0) {
                log.error("kanji: " + e.getKanji() + " appartiene già al livello: " + livello + " e ora anche a 1");
                System.exit(1);
            }
            livello = 1;
        }
        if (level2.contains(e.getKanji())) {
            if (livello != 0) {
                log.error("kanji: " + e.getKanji() + " appartiene già al livello: " + livello + " e ora anche a 2");
                System.exit(1);
            }
            livello = 2;
        }
        if (level3.contains(e.getKanji())) {
            if (livello != 0) {
                log.error("kanji: " + e.getKanji() + " appartiene già al livello: " + livello + " e ora anche a 3");
                System.exit(1);
            }
            livello = 3;
        }
        if (level4.contains(e.getKanji())) {
            if (livello != 0) {
                log.error("kanji: " + e.getKanji() + " appartiene già al livello: " + livello + " e ora anche a 4");
                System.exit(1);
            }
            livello = 4;
        }
        if (level5.contains(e.getKanji())) {
            if (livello != 0) {
                log.error("kanji: " + e.getKanji() + " appartiene già al livello: " + livello + " e ora anche a 5");
                System.exit(1);
            }
            livello = 5;
        }


        return (minLev <= livello && livello <= maxLev);
    }

    EVKanjiManager                         mgr;

    // per calcolare i kanji L1 per differenza
    ArrayList<HashSet<String>>             levels = new ArrayList<HashSet<String>>();
    Set<String>                            level5;
    Set<String>                            level4;
    Set<String>                            level3;
    Set<String>                            level2;
    Set<String>                            level1;

    int                                    minLev;
    int                                    maxLev;

    private static org.apache.log4j.Logger log    = Logger
                                                          .getLogger(EVKanjiEntryFilterBy_NEW_JLPT.class);

    /**
     * @return
     */
    HashSet<String> buildSetLev1() {
        HashSet<String> l1 = new HashSet<String>(1000);

        if (level2.size() <= 0 ||
                level3.size() <= 0 ||
                level4.size() <= 0 ||
                level5.size() <= 0) {
            log.error("Level 1 can be built only after all others are built");
            System.exit(-1);
        }

        for (int i = 1; i < Cfg.KANJI_LAST_HEISIG1; i++) {
            String kanji = mgr.findByHNr(i).getKanji();
            if (level2.contains(kanji))
                continue;
            if (level3.contains(kanji))
                continue;
            if (level4.contains(kanji))
                continue;
            if (level5.contains(kanji))
                continue;
            l1.add(kanji);
        }

        return l1;
    }

    /**
     * @return
     */
    Set<String> buildSetLev2() {
        String l2 = ""
                + "党協総区領県設改府査委軍団各島革村勢減再税営比防補境導副算輸述線農州武象域額欧担準賞辺造被技低復移個"
                + "門課脳極含蔵量型況針専谷史階管兵接細効丸湾録省旧橋岸周材戸央券編捜竹超並療採森競介根販歴将幅般貿講林"
                + "装諸劇河航鉄児禁印逆換久短油暴輪占植清倍均億圧芸署伸停爆陸玉波帯延羽固則乱普測豊厚齢囲卒略承順岩練軽"
                + "了庁城患層版令角絡損募裏仏績築貨混昇池血温季星永著誌庫刊像香坂底布寺宇巨震希触依籍汚枚複郵仲栄札板骨"
                + "傾届巻燃跡包駐弱紹雇替預焼簡章臓律贈照薄群秒奥詰双刺純翌快片敬悩泉皮漁荒貯硬埋柱祭袋筆訓浴童宝封胸砂"
                + "塩賢腕兆床毛緑尊祝柔殿濃液衣肩零幼荷泊黄甘臣浅掃雲掘捨軟沈凍乳恋紅郊腰炭踊冊勇械菜珍卵湖喫干虫刷湯溶"
                + "鉱涙匹孫鋭枝塗軒毒叫拝氷乾棒祈拾粉糸綿汗銅湿瓶咲召缶隻脂蒸肌耕鈍泥隅灯辛磨麦姓筒鼻粒詞胃畳机膚濯塔沸"
                + "灰菓帽枯涼舟貝符憎皿肯燥畜挟曇滴伺";
        return buildSetOfChars(l2);
    }

    Set<String> buildSetLev3() {
        String l3 = ""
                + "与両乗予争互亡交他付件任伝似位余例供便係信倒候値偉側偶備働優光全共具内冷処列初判利到制刻割加助努労務"
                + "勝勤化単危原参反収取受号合向君否吸吹告呼命和商喜回因困園在報増声変夢太夫失好妻娘婚婦存宅守完官定実客"

                + "害容宿寄富寒寝察対局居差市師席常平幸幾座庭式引当形役彼徒得御必忘忙念怒怖性恐恥息悲情想愛感慣成戦戻所"
                + "才打払投折抜抱押招指捕掛探支放政敗散数断易昔昨晩景晴暗暮曲更最望期未末束杯果格構様権横機欠次欲歯歳残"

                + "段殺民求決治法泳洗活流浮消深済渡港満演点然煙熱犯状猫王現球産由申留番疑疲痛登皆盗直相眠石破確示礼祖神"
                + "福科程種積突窓笑等箱米精約組経給絵絶続緒罪置美老職育背能腹舞船良若苦草落葉薬術表要規覚観解記訪許認"

                + "誤説調談論識警議負財貧責費資賛越路辞込迎返迷追退逃途速連進遅遊過達違遠適選部都配酒閉関降限除険陽際雑"
                + "難雪静非面靴頂頭頼顔願類飛首馬髪鳴";

        return buildSetOfChars(l3);
    }

    Set<String> buildSetLev4() {
        String l4 = ""
                + "不世主事京仕代以住体作使借元兄公写冬切別力勉動医去台同味品員問図地堂場売夏夕夜妹姉始字室家"
                + "屋工帰広度建弟強待心思急悪意持教文料方旅族早明映春昼曜有服朝業楽歌止正歩死注洋海漢牛物特犬理"

                + "用田町画界病発真着知研私秋究答紙終習考者肉自色英茶親計試貸質赤走起転近送通運重野"
                + "銀開院集青音題風飯館験鳥黒";
        return buildSetOfChars(l4);
    }

    Set<String> buildSetLev5() {
        String l5 = ""
                + "一七万三上下中九二五人今休会何先入八六円出分前北十千午半南友口古右名四国土外多大天女子学安小少山川左"
                + "年店後手新日時書月木本来東校母毎気水火父生男白百目社空立耳聞花行西見言話語読買足車週道金長間雨電食飲"
                + "駅高魚";

        return buildSetOfChars(l5);
    }
}
