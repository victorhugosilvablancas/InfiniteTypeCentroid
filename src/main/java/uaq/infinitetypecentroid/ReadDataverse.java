package uaq.infinitetypecentroid;

import uaq.dcc.datasets.Dataverse;

/**
 *
 * Initialize the Database in Oracle
 */
public class ReadDataverse {
    
    /**
     * Initialize the Database
     */
    public ReadDataverse() {
    }
    /**
     * Assig all data to each research hypothesis.
     * @param max maximum number of records.
     * @param show runtime action.
     */
    public void TakeAllHypothesis(int max,boolean show) {
        Dataverse.LaTeX=false;
        
        Dataverse datos=new Dataverse(0);
        datos.getFields();
        if (show) datos.DrawFields();
        datos.getDataset(max,show);
        if (show) datos.DrawData();
        datos.TakeAllHypothesis(show);
    }

}
