package uaq.dcc.datasets;

import uaq.infinitetypecentroid.Tools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * Price share analysis.
 */
public class PricePerShares {
    public PricePerSharesdata data;
    private String filename;
    
    private Tools l=new Tools();
    
    /**
     * Class that defines PricePerShares.
     */
    public PricePerShares() {
        data=new PricePerSharesdata();
        filename=Dataverse.listaarchivos[uaq.dcc.datasets.Dataverse.HARVARD_INSIDERTRADING];
    }
    
    /**
     * Retrieve dynamic list from database
     */
    public void getList() {
        
        List<PricePerSharesdata> mylist=new ArrayList<>();
        PricePerSharesdata mydata=new PricePerSharesdata();
        
        for (int i=0;i<PricePerSharesdata.listpricepershares.size();i++) {
            mydata=PricePerSharesdata.listpricepershares.get(i);
            mydata.transactionPricePerShare=Tools.Redondea(mydata.transactionPricePerShare);
            PricePerSharesdata.listpricepershares.set(i,mydata);
        }
        PricePerSharesdata.listpricepershares.sort(new Comparator<PricePerSharesdata>() {
            @Override
            public int compare(PricePerSharesdata o1, PricePerSharesdata o2) {
                return o1.transactionPricePerShare.compareTo(o2.transactionPricePerShare);
            }
        });
        
        Double rmax=0.0;
        Double rmin=999999999.0;
        Double dmax=0.0;
        Double dmin=3000.0;
        
        PricePerSharesdata aux=new PricePerSharesdata();
        aux.transactionPricePerShare=-1.0;
        int k=1;
        boolean thefirst=true;
        for (int i=0;i<PricePerSharesdata.listpricepershares.size();i++) {
            mydata=PricePerSharesdata.listpricepershares.get(i);
            if (mydata.transactionPricePerShare>0) {
                if (!aux.transactionPricePerShare.equals(mydata.transactionPricePerShare)) {
                    if (thefirst) thefirst=false;
                    else {
                        if (aux.transactionPricePerShare>1000 && aux.transactionPricePerShare<10000) {
                            aux.idpricepershare=k;
                            mylist.add(aux);
                            k++;
                        }
                    }
                    aux=mydata;
                }
                aux.records++;
            }
        }
        if (aux.transactionPricePerShare>1000 && aux.transactionPricePerShare<10000) {
            aux.idpricepershare=k;
            mylist.add(aux);
        }
        //System.out.println(PricePerSharesdata.listpricepershares.size()+":"+mylist.size());
        
        //normalizing
        if (mylist.size()>0) {
            PricePerSharesdata.listpricepershares=mylist;
            //normalizing
            for (int i=0;i<PricePerSharesdata.listpricepershares.size();i++) {
                mydata=PricePerSharesdata.listpricepershares.get(i);
                
                if (rmax<mydata.transactionPricePerShare) rmax=mydata.transactionPricePerShare;
                if (rmin>mydata.transactionPricePerShare) rmin=mydata.transactionPricePerShare;
                if (dmax<mydata.records) dmax=mydata.records.doubleValue();
                if (dmin>mydata.records) dmin=mydata.records.doubleValue();
            }
            
            for (int i=0;i<PricePerSharesdata.listpricepershares.size();i++) {
                mydata=PricePerSharesdata.listpricepershares.get(i);
                mydata.apoint=Tools.Normaliza(rmin,rmax,mydata.transactionPricePerShare);
                mydata.bpoint=Tools.Normaliza(dmin,dmax,mydata.records.doubleValue());
                PricePerSharesdata.listpricepershares.set(i,mydata);
            }
        }
    }
    /**
     * Draws list in table format
     * @return a table with all DataList list
     */
    public String[][] DrawTable() {
        String[] ahead=PricePerSharesdata.Head().split(Tools.TAB);
        int r=PricePerSharesdata.listpricepershares.size();
        String[][] atable=new String[r][ahead.length]; 
        PricePerSharesdata midato=new PricePerSharesdata();
        for (int i=0;i<r;i++) {
            midato=PricePerSharesdata.listpricepershares.get(i);
            atable[i]=midato.string().split(Tools.TAB);
        }
        return atable;
    }
    /**
     * Draws list in LaTeX table format
     * @return formatted string to be paste in LaTeX editor.
     */
    public String DrawTableLaTeX() {
        PricePerSharesdata midato=new PricePerSharesdata();
        String cade="""
                    \\begin{table}[H]
                    \\caption{Price per Share on Acquired Dispose Code.\\label{tabla:pricePerShares}}
                    \\begin{tabularx}{\\textwidth}{ l r r r }
                    \\toprule
                    """;
        cade+=PricePerSharesdata.LatexHead();
        cade+=Tools.latexMidrule();
        int r=PricePerSharesdata.listpricepershares.size();
        for (int i=0;i<r;i++) {
            midato=PricePerSharesdata.listpricepershares.get(i);
            if (true) {
            cade+=midato.latex();
                if (i+1 < r)
                    cade+=Tools.latexMidrule();
            }
        }
        cade+="""
              \\bottomrule
              \\end{tabularx}
              \\noindent{\\footnotesize{Source: Proposed work.}}
              \\end{table}""";
        return cade;
    }
    
    /**
     * Retrieve dinamic list from CSV file
     * @param maxdata
     */
    public void getDataset(int maxdata) {
        List<PricePerSharesdata> milista=new ArrayList<>();
        PricePerSharesdata.listpricepershares=new ArrayList<>();
        PricePerSharesdata midato=new PricePerSharesdata();
        
        String line="";
        int max=0;
        try {
            FileRecordsdata rdato=new FileRecordsdata();
            File f=new File(filename);
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                boolean primera=true;
                line = br.readLine();
                while (line != null && max<maxdata) {
                    if (primera) primera=false;
                    else {
                        rdato=new FileRecordsdata(line);
                        
                        midato=new PricePerSharesdata();
                        midato.transactionPricePerShare=rdato.transactionPricePerShare;
                        midato.transactionAcquiredDisposedCode=rdato.transactionAcquiredDisposedCode;
                        if (midato.transactionPricePerShare>100000 && midato.transactionPricePerShare<1000000) { 
                            milista.add(midato);
                            max++;
                            //if (max % 1000 == 0) 
                            //System.out.println(max+"\t"+midato.getTransactionAcquiredDisposedCode()+"\t"+midato.transactionPricePerShare);
                        }
                    }
                    line = br.readLine();
                }
                br.close();
                //System.out.println("Price.getDataset: size "+milista.size());
            }
            
            milista.sort(new Comparator<PricePerSharesdata>() {
                @Override
                public int compare(PricePerSharesdata o1, PricePerSharesdata o2) {
                    return o1.transactionPricePerShare.compareTo(o2.transactionPricePerShare);
                }
            });
            
            PricePerSharesdata aux=new PricePerSharesdata();
            Boolean primera=true;
            PricePerSharesdata auxdato=new PricePerSharesdata();
            for (int i=0;i<milista.size();i++) {
                midato=milista.get(i);
                if (!midato.transactionPricePerShare.equals(aux.transactionPricePerShare)) {
                    if (primera) primera=false;
                    else {
                        PricePerSharesdata.listpricepershares.add(aux);
                    }
                    aux=midato;
                }
                aux.records++;
            }
            PricePerSharesdata.listpricepershares.add(auxdato);
            
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("getDataset: "+e.getLocalizedMessage());
        }
    }
    
}
