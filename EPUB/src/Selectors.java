import java.util.ArrayList;
import java.util.Arrays;

public class Selectors {

    
    public ArrayList<String> selsKanji(int tableNr) {
        switch (tableNr) {
            case 191: {                
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override"));
            }
            default: {
                return new ArrayList<String>(Arrays.asList(".x2-frame-kanji > span"));
            }
        }
    }

    
    public ArrayList<String> selsSignalPrim(int tableNr) {
        switch (tableNr) {
            case 64: {                
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override34"));
            }
            case 331: { 
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override51"));
            }
            case 410: { 
                return new ArrayList<String>(Arrays.asList(".generated-style-2-override56"));
            }            
            default: {
                return new ArrayList<String>(Arrays.asList(".generated-style-override1", ".generated-style-2-override28"));
            }
        }
    }

    
    
    
}
