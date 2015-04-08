package com.enricoviali.epub.ja.rtk2;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class Selectors {

    public ArrayList<String> selsKanji(int tableNr, int fileNr) {
        switch (tableNr) {
            case 191: {
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override"));
            }
            default: {
                return new ArrayList<String>(Arrays.asList(".x2-frame-kanji > span"));
            }
        }
    }

    public ArrayList<String> selsSignalPrim(int tableNr, int fileNr) {
        switch (tableNr) {
            case 64: {
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override34"));
            }
            case 331: {
                if (fileNr == 104)
                    return new ArrayList<String>(Arrays.asList(".generated-style-2-override51"));
                if (fileNr == 203)
                    return new ArrayList<String>(Arrays.asList(".generated-style-override1"));
                log.error("esco");
                System.exit(1);
            }
            case 410: {
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override56"));
            }
            default: {
                return new ArrayList<String>(Arrays.asList(".generated-style-override1", ".generated-style-2-override28"));
            }
        }
    }

    public ArrayList<String> selsLinks(int tableNr, int fileNr) {
        String defaultVal = ".generated-style-override3";
        switch (tableNr) {
            case 5: {
                if (fileNr == 200)
                    return new ArrayList<String>(Arrays.asList(".generated-style-2-override43"));
                break;
            }
            case 37: {
                if (fileNr == 101)
                    return new ArrayList<String>(Arrays.asList(".generated-style-2-override18"));
                break;
            }
            case 64: {
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override36"));
            }
            case 115: {
                if (fileNr == 202)
                    return new ArrayList<String>(Arrays.asList(".generated-style-2-override43"));
            }
            case 132: {
                if (fileNr == 202)
                    return new ArrayList<String>(Arrays.asList(".generated-style-3-override23"));
            }
            
            case 220: {
                if (fileNr == 202 || fileNr == 203)
                    return new ArrayList<String>(Arrays.asList(".generated-style-3-override12"));
            }

            
            case 137:
            case 142:
            case 152:
            case 156:
            case 160:
            case 164:
            case 168:
            case 172:
            case 176:
            case 180:
            case 188:
            case 192: 
            case 196: 
            case 200:
            case 204:
            case 208:
            case 212:
            case 216: 
            case 224:
            case 228: {
                if (fileNr == 202)
                    return new ArrayList<String>(Arrays.asList(".generated-style-3-override12"));
            }
            case 138:
            case 165: {
                if (fileNr == 202)
                    return new ArrayList<String>(Arrays.asList(".generated-style-2-override43"));
            }
            case 143: {
                if (fileNr == 202)
                    return new ArrayList<String>(Arrays.asList(".generated-style-3-override25"));
            }

            case 78:
            case 84:
            case 92:
            case 98:
            case 103:
            case 109:
            case 110:
            case 111:
            case 118:
            case 123:
            case 127:
            case 148: {
                if (fileNr == 202)
                    return new ArrayList<String>(Arrays.asList(".generated-style-3-override12"));
                break;
            }
            case 184: {
                if (fileNr == 202)
                    return new ArrayList<String>(Arrays.asList(".generated-style-3-override12"));
                else
                    return new ArrayList<String>(Arrays.asList(".generated-style-2-override43"));
            }

            case 247:
            case 270:
            case 342:
            case 494:
            case 519:
            case 546:
            case 566: {
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override43"));
            }
            case 331:
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override1"));
            case 406:
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override53"));
            case 448:
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override59"));
            case 521:
            case 558:
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override62"));
            case 539:
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override65"));

            default: {
                // log.error("should never pass here");
                // Utils.esco("");
                return new ArrayList<String>(Arrays.asList(defaultVal));
            }
        }
        return new ArrayList<String>(Arrays.asList(defaultVal));
    }

    public ArrayList<String> selsRTK1FrameE(int tableNr, int fileNr) {
        String defaultVal = ".x2-vol-1-nr";
        switch (tableNr) {
            case 27:
            case 28: {
                if (fileNr == 101)
                    return new ArrayList<String>(Arrays.asList(".generated-style-2-override8"));
            }
            default: {
                return new ArrayList<String>(Arrays.asList(defaultVal));
            }
        }
    }

    private static org.apache.log4j.Logger log = Logger.getLogger(Selectors.class);

}
