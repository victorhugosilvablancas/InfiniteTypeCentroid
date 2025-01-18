package uaq.dcc.ploteo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import uaq.dcc.definitions.Datasdata;
import uaq.dcc.definitions.Kmeans;
import uaq.dcc.datasets.PricePerSharesdata;

/**
 *
 * Draws the PricePanel
 */
public class PricePanel extends JPanel {
    private Integer n_clusters;
    private Integer n_epochs;
    private List<Ellipse2D> mylist;
    private KmeansForPrice kfp;
    //List pointList;
    //Color selectedColor;
    //Ellipse2D selectedPoint;
    
    /**
     * Constructor
     * @param aclusters number of clusters
     * @param aepochs number of epochs for training
     */
    public PricePanel(String aclusters,String aepochs) {
        n_clusters=Integer.valueOf(aclusters);
        n_epochs=Integer.valueOf(aepochs);
        mylist=new ArrayList<>();
        kfp=new KmeansForPrice();
        
        setBackground(new Color(250,250,250));
        
        Ellipse2D mipunto;
        PricePerSharesdata data=new PricePerSharesdata();
        for (int i=0;i<PricePerSharesdata.listpricepershares.size();i++) {
            data=PricePerSharesdata.listpricepershares.get(i);
            mipunto=new Ellipse2D.Double(
                    data.getApointgraphic(),
                    data.getBpointgraphic(), 4, 4);
            mylist.add(mipunto);
        }
        
        //create clusters
        kfp.Execute();
    }
    /**
     * Sets mylist values.
     * 
     * @param alist 
     */
    public void setLista(List<Ellipse2D> alist) {
        mylist=alist;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D e;
        Color color;
        Datasdata ddata=new Datasdata();
        Double rx=0.0;
        Double ry=0.0;
        
        //a-point
        g2.drawLine( 0,440,600,440);
        g2.drawString("A-Price per Share", 100, 420);
        //b-point
        g2.drawLine(14,  0, 14,600);
        g2.drawString("B-Records", 20, 100);
        //c-point
        g2.drawLine(14,440,500,114);
        g2.drawString("C-Undefined", 300, 200);
        
        //drawing clusters
        for(int j = 0; j < Kmeans.Cluster.length; j++) {
            ddata = Kmeans.Cluster[j];
            rx=ddata.A.x*400;
            ry=ddata.B.x*400;
            
            e=new Ellipse2D.Double(rx+PloteoFrame.XMARGIN,ry+PloteoFrame.YMARGIN, 18, 18);
            switch (j) {
                case 0: color = Color.red; break;
                case 1: color = Color.blue; break;
                default: color = Color.white; break;
            }
            g2.setPaint(color);
            g2.fill(e);
        }
        
        for(int i = 0; i < Datasdata.Dataset.size(); i++) {
            ddata = Datasdata.Dataset.get(i);
            rx=ddata.A.x*400;
            ry=ddata.B.x*400;
            
            e=new Ellipse2D.Double(rx+PloteoFrame.XMARGIN,ry+PloteoFrame.YMARGIN, 6, 6);
            switch (ddata.getK()) {
                case 0: color = Color.red; break;
                case 1: color = Color.blue; break;
                default: color = Color.green; break;
            }
            
            g2.setPaint(color);
            g2.fill(e);
        }
    }
    private class KmeansForPrice extends Kmeans {
        
        public KmeansForPrice() {
        }
        public void Execute() {
            Kmeans.PredictionResult="";
            Kmeans.Print=false;
            //step 1
            DataInitialization();
            //step 2
            ClustersDefinition(n_clusters);
            //step 3
            //Clusters defined by random selection from dataset
            for(int j = 0; j < Kmeans.Cluster.length; j++) {
                Datasdata ddata=Kmeans.Cluster[j];
                Random rn=new Random();
                int k=rn.nextInt(0, PricePerSharesdata.listpricepershares.size());
                PricePerSharesdata tdata=PricePerSharesdata.listpricepershares.get(k);
                ddata.A.x=tdata.apoint;
                ddata.B.x=tdata.bpoint;
                Kmeans.Cluster[j]=ddata;
                //System.out.println(String.format("k=%d,%.3f,%.3f",k,ddata.A.x,ddata.B.x));
            }
            //step 4
            //Euclidean Estimation
            ComputeEachEuclideanDistance();
            /*
            //step 5
            //Training
            CentroidTraining(n_epochs);
            /*
            //step 6
            //prediction
            Datasdata Hy=new Datasdata();
            
            Hy.A.x=0.03;
            Hy.A.y=0.06;
            Hy.A.z=0.09;
            Hy.B.x=0.04;
            Hy.B.y=0.07;
            Hy.B.z=0.10;
            Hy.C.x=0.05;
            Hy.C.y=0.08;
            Hy.C.z=0.11;
            
            AssignCathegory(Hy);
            t_results.setText(Kmeans.PredictionResult);*/
        }
        
        /**
        * 1.Data initializacion.
        * 
        */
        public final void DataInitialization() {
            Datasdata.Dataset=new ArrayList<>();
            Datasdata midato=new Datasdata();
            
            PricePerSharesdata pdata=new PricePerSharesdata();
            for (int i=0;i<PricePerSharesdata.listpricepershares.size();i++) {
                pdata=PricePerSharesdata.listpricepershares.get(i);
                
                midato=new Datasdata(pdata);
                Datasdata.Dataset.add(midato);
            }
        } //DataInitialization
        
    } //KmeansForPrice

}
