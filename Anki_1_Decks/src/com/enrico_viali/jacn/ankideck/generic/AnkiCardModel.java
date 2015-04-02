package com.enrico_viali.jacn.ankideck.generic;

import java.sql.ResultSet;
import java.sql.SQLException;







import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.utils.Utl;

/**
 * @author enrico
 *  
 *  Non chiaro a cosa serve, se era un tentativo per differenziare fra cards di anki1 e 2
 *  
 */
public class AnkiCardModel implements IAnkiCard {

	public AnkiCardModel(long factId, String exprPar) {
		super();
		id = Utl.NOT_INITIALIZED_INT;
		aFormat = Utl.NOT_INITIALIZED_STRING;
		qFormat = Utl.NOT_INITIALIZED_STRING;
		name = Utl.NOT_INITIALIZED_STRING;
	}

	/* (non-Javadoc)
     * @see com.enrico_viali.jacn.ankideck.generic.IAnkiCard#getID()
     */
	@Override
    public long getID() {
		return id;
	}

	/* (non-Javadoc)
     * @see com.enrico_viali.jacn.ankideck.generic.IAnkiCard#getName()
     */
	@Override
    public String getName() {
		return name;
	}

	
	/* (non-Javadoc)
     * @see com.enrico_viali.jacn.ankideck.generic.IAnkiCard#toString()
     */
	@Override
    public String toString() {
		String s = "";
		String sep = Cfg.SEP_TOSTRING;		
		s = "id="+this.id;
		s+=sep+"name="+name; 
		s+=sep+"qformat="+qFormat; 
		s+=sep+"aformat="+aFormat; 		
		return s;
	}


	/* (non-Javadoc)
     * @see com.enrico_viali.jacn.ankideck.generic.IAnkiCard#getQFormat()
     */
	@Override
    public String getQuestionFormat() {
		return qFormat;
	}

	/* (non-Javadoc)
     * @see com.enrico_viali.jacn.ankideck.generic.IAnkiCard#getAFormat()
     */
	@Override
    public String getAnswerFormat() {
		return qFormat;
	}


	/* (non-Javadoc)
     * @see com.enrico_viali.jacn.ankideck.generic.IAnkiCard#fillEntryFromRS(java.sql.ResultSet, long)
     */
	@Override
    public boolean fillFromRS(ResultSet rs, long id) throws SQLException {
		boolean ret = true;

		if (rs.next()) {
			int pos = 1;
			this.id = rs.getLong(pos++);
			this.name = rs.getString(pos++);
			this.qFormat = rs.getString(pos++);
			this.aFormat = rs.getString(pos++);
		} else {
			log.error("");
			return false;
		}

		if (!ret) {
			log.error("");
		}

		return ret;
	}


    private static org.apache.log4j.Logger log = Logger.getLogger(AnkiCardModel.class);

    @Override
    public String questionToString() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return null;
    }

    @Override
    public long getFactId() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return 0;
    }

    @Override
    public long getCardModelId() {
        {
            log.error("metodo non implementato");
            System.exit(-1);
        }
        return 0;
    }

    public long   id;
    public String name;
    public String qFormat;
    public String aFormat;
}
