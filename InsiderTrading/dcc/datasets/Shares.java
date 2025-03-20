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
 * Transaction shares analysis.
 */
public class Shares {
    public Sharesdata data;
    private String filename;
    
    private Tools l=new Tools();
    
    /**
     * Class that defines Shares.
     */
    public Shares() {
        data=new Sharesdata();
        filename=Dataverse.listaarchivos[uaq.dcc.datasets.Dataverse.HARVARD_INSIDERTRADING];
    }
    
    /**
     * Retrieve dynamic list from database
     */
    public void getList() {
        Sharesdata.listshares.sort(new Comparator<Sharesdata>() {
            @Override
            public int compare(Sharesdata o1, Sharesdata o2) {
                return o1.transactionSharesFn.compareTo(o2.transactionSharesFn);
            }
        });
        
        List<Sharesdata> mylist=new ArrayList<>();
        Sharesdata mydata=new Sharesdata();
        
        Double rmax=0.0;
        Double rmin=999999999.0;
        Double dmax=0.0;
        Double dmin=3000.0;
        
        Sharesdata aux=new Sharesdata();
        aux.transactionSharesFn="X";
        int k=1;
        boolean thefirst=true;
        for (int i=0;i<Sharesdata.listshares.size();i++) {
            mydata=Sharesdata.listshares.get(i);
            if (mydata.getTransactionSharesFn().length()>0) {
                if (!aux.transactionSharesFn.equals(mydata.transactionSharesFn)) {
                    if (thefirst) thefirst=false;
                    else {
                        aux.idshare=k;
                        mylist.add(aux);
                        k++;
                    }
                    aux=mydata;
                }
                aux.transactionShares+=mydata.transactionShares;
            }
        }
        if (aux.getTransactionSharesFn().length()>0) {
            aux.idshare=k;
            mylist.add(aux);
        }
        //System.out.println(Sharesdata.listshares.size()+":"+mylist.size());
        
        if (mylist.size()>0) {
            Sharesdata.listshares=mylist;
            
            //normalizing
            Sharesdata.listshares.sort(new Comparator<Sharesdata>() {
                @Override
                public int compare(Sharesdata o1, Sharesdata o2) {
                    return o1.transactionShares.compareTo(o2.transactionShares);
                }
            });
            for (int i=0;i<Sharesdata.listshares.size();i++) {
                mydata=Sharesdata.listshares.get(i);
                
                if (rmax<mydata.transactionShares) rmax=mydata.transactionShares.doubleValue();
                if (rmin>mydata.transactionShares) rmin=mydata.transactionShares.doubleValue();
                if (dmax<mydata.getTransactionYear()) dmax=mydata.getTransactionYear().doubleValue();
                if (dmin>mydata.getTransactionYear()) dmin=mydata.getTransactionYear().doubleValue();
            }
            //System.out.println(String.format("%,.0f:%,.0f:%.0f:%.0f",rmax,rmin,dmax,dmin));
            
            for (int i=0;i<Sharesdata.listshares.size();i++) {
                mydata=Sharesdata.listshares.get(i);
                mydata.apoint=Tools.Normaliza(rmin,rmax,mydata.transactionShares.doubleValue());
                mydata.bpoint=Tools.Normaliza(dmin,dmax,mydata.getTransactionYear().doubleValue());
                Sharesdata.listshares.set(i,mydata);
            }
        }
    }
    /**
     * Draws list in table format
     * @return a table with all DataList list
     */
    public String[][] DrawTable() {
        String[] head=Sharesdata.Head().split(Tools.TAB);
        int r=Sharesdata.listshares.size();
        int k=r/2;
        String[][] atable=new String[k+1][head.length];
        Sharesdata midato=new Sharesdata();
        String cade="";
        k=0;
        for (int i=0;i<r;i++) {
            midato=Sharesdata.listshares.get(i);
            if (i % 2 == 0) {
                cade+=midato.stringright();
                atable[k]=cade.split(Tools.TAB);
                cade="";
                k++;
            }
            else {
                cade=midato.stringleft()+Tools.TAB;
            }
            
        }
        return atable;
    }
    /**
     * Draws list in LaTeX table format
     * @return formatted string to be paste in LaTeX editor.
     */
    public String DrawTableLaTeX() {
        Sharesdata midato=new Sharesdata();
        String cade="""
                    \\begin{table}[H]
                    \\caption{transactionShares per Year for insider trading*.\\label{tabla:transactionShares}}
                    \\begin{tabularx}{\\textwidth}{ l r l r}
                    \\toprule
                    """;
        cade+=Sharesdata.LatexHead();
        cade+=Tools.latexMidrule();
        int r=Sharesdata.listshares.size();
        for (int i=0;i<r;i++) {
            midato=Sharesdata.listshares.get(i);
            if (midato.getTransactionYear()>=2000) {
                if (i % 2 == 0) {
                    cade+=midato.latexright();
                    //cade+=Librerias.latexMidrule();
                } else {
                    cade+=midato.latexleft();
                }
            }
        }
        cade+="""
              \\bottomrule
              \\end{tabularx}
              \\noindent{\\footnotesize{Source: Proposed work. *Data since 2000}}
              \\end{table}""";
        return cade;
    }
    
    /**
     * Retrieve dinamic list from CSV file
     */
    public void getDataset() {
        List<Sharesdata> milista=new ArrayList<>();
        Sharesdata.listshares=new ArrayList<>();
        Sharesdata midato=new Sharesdata();
        
        String line="";
        int max=0;
        try {
            FileRecordsdata rdato=new FileRecordsdata();
            File f=new File(filename);
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                boolean primera=true;
                line = br.readLine();
                while (line != null) {
                    if (primera) primera=false;
                    else {
                        rdato=new FileRecordsdata(line);
                        midato=new Sharesdata();
                        midato.transactionDate=rdato.transactionDate;
                        midato.transactionShares=rdato.transactionShares;
                        midato.setTransactionSharesFn(rdato.transactionSharesFn);
                        if (midato.getTransactionSharesFn().length()>0 && midato.transactionShares>0) {
                            if (midato.getTransactionYear()>2000 && midato.getTransactionYear()<2024) {
                                milista.add(midato);
                                max++;
                                //if (max % 1000 == 0) System.out.println(max);
                            }
                        }
                    }
                    line = br.readLine();
                }
                br.close();
                //System.out.println("Shares.getDataset: size "+milista.size());
            }
            
            milista.sort(new Comparator<Sharesdata>() {
                @Override
                public int compare(Sharesdata o1, Sharesdata o2) {
                    return o1.getTransactionYearFn().compareTo(o2.getTransactionYearFn());
                }
            });
            
            String xtran="1000";
            Boolean primera=true;
            Sharesdata auxdato=new Sharesdata();
            for (int i=0;i<milista.size();i++) {
                midato=milista.get(i);
                if (!midato.getTransactionYearFn().equals(xtran)) {
                    if (primera) primera=false;
                    else {
                        Sharesdata.listshares.add(auxdato);
                    }
                    auxdato=new Sharesdata();
                    auxdato.transactionDate=midato.transactionDate;
                    auxdato.transactionSharesFn=midato.transactionSharesFn;
                    xtran=midato.getTransactionYearFn();
                }
                auxdato.transactionShares+=midato.transactionShares;
            }
            Sharesdata.listshares.add(auxdato);
            //System.out.println(Sharesdata.listshares.size());
            
            //System.out.println("getDataset: End");
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("getDataset: "+e.getLocalizedMessage());
        }
    }
    
    
}
