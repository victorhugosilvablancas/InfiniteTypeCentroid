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
 * Titles analysis.
 */
public class Titles {
    public Titlesdata data;
    private String filename;
    
    private Tools l=new Tools();
    
    /**
     * Class that defines Titles.
     */
    public Titles() {
        data=new Titlesdata();
        filename=Dataverse.listaarchivos[uaq.dcc.datasets.Dataverse.HARVARD_INSIDERTRADING];
    }
    
    /**
     * Retrieve dynamic list from database
     */
    public void getList() {
        Titlesdata.listtitles.sort(new Comparator<Titlesdata>() {
            @Override
            public int compare(Titlesdata o1, Titlesdata o2) {
                return o1.securityTitle.compareTo(o2.securityTitle);
            }
        });
        
        List<Titlesdata> mylist=new ArrayList<>();
        Titlesdata mydata=new Titlesdata();
        
        Double amax=0.0;
        Double amin=999999999.0;
        Double bmax=0.0;
        Double bmin=999999999.0;
        Double cmax=0.0;
        Double cmin=999999999.0;
        
        Titlesdata aux=new Titlesdata();
        aux.securityTitle="X";
        int k=1;
        boolean thefirst=true;
        for (int i=0;i<Titlesdata.listtitles.size();i++) {
            mydata=Titlesdata.listtitles.get(i);
            if (mydata.transactionShares>100000 && mydata.transactionShares<1000000) {
                if (!aux.securityTitle.equals(mydata.securityTitle)) {
                    if (thefirst) thefirst=false;
                    else {
                        //System.out.println(aux.securityTitle+":"+aux.transactionShares+":"+aux.underlyingSecurityShares);
                        aux.idtitle=k;
                        mylist.add(aux);
                        k++;
                    }
                    aux=mydata;
                }
                aux.transactionShares+=mydata.transactionShares;
                aux.underlyingSecurityShares+=mydata.underlyingSecurityShares;
                aux.records++;
            }
        }
        if (mydata.transactionShares>100000 && mydata.transactionShares<1000000) {
            aux.idtitle=k;
            mylist.add(aux);
        }
        //System.out.println(Titlesdata.listtitles.size()+":"+mylist.size());
        
        if (mylist.size()>0) {
            Titlesdata.listtitles=mylist;
            //normalizing
            for (int i=0;i<Titlesdata.listtitles.size();i++) {
                mydata=Titlesdata.listtitles.get(i);
                
                if (amax<mydata.transactionShares.doubleValue()) amax=mydata.transactionShares.doubleValue();
                if (amin>mydata.transactionShares.doubleValue()) amin=mydata.transactionShares.doubleValue();
                
                if (bmax<mydata.underlyingSecurityShares.doubleValue()) bmax=mydata.underlyingSecurityShares.doubleValue();
                if (bmin>mydata.underlyingSecurityShares.doubleValue()) bmin=mydata.underlyingSecurityShares.doubleValue();
                
                if (cmax<mydata.records.doubleValue()) cmax=mydata.records.doubleValue();
                if (cmin>mydata.records.doubleValue()) cmin=mydata.records.doubleValue();
            }
            for (int i=0;i<Titlesdata.listtitles.size();i++) {
                mydata=Titlesdata.listtitles.get(i);
                mydata.apoint=Tools.Normaliza(amin,amax,mydata.transactionShares.doubleValue());
                mydata.bpoint=Tools.Normaliza(bmin,bmax,mydata.underlyingSecurityShares.doubleValue());
                mydata.cpoint=Tools.Normaliza(bmin,bmax,mydata.records.doubleValue());
                Titlesdata.listtitles.set(i,mydata);
            }
        }
    }
    /**
     * Draws list in table format
     * 
     * @return a table with all DataList list
     */
    public String[][] DrawTable() {
        String[] ahead=Titlesdata.Head().split(Tools.TAB);
        int r=Titlesdata.listtitles.size();
        String[][] atable=new String[r][ahead.length]; 
        
        Titlesdata midato=new Titlesdata();
        for (int i=0;i<r;i++) {
            midato=Titlesdata.listtitles.get(i);
            atable[i]=midato.string().split(Tools.TAB);
        }
        return atable;
    }
    /**
     * Draws list in LaTeX table format
     * @return formatted string to be paste in LaTeX editor.
     */
    public String DrawTableLaTeX() {
        Titlesdata midato=new Titlesdata();
        String cade="""
                    \\begin{table}[H]
                    \\caption{securityTitles for insider trading.\\label{tabla:securityTitles}}
                    \\begin{tabularx}{\\textwidth}{ r l r r}
                    \\toprule
                    """;
        cade+=Titlesdata.LatexHead();
        cade+=Tools.latexMidrule();
        int r=Titlesdata.listtitles.size();
        for (int i=0;i<r;i++) {
            midato=Titlesdata.listtitles.get(i);
            cade+=midato.latex();
            if (i+1 < r)
                cade+=Tools.latexMidrule();
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
        List<Titlesdata> milista=new ArrayList<>();
        Titlesdata.listtitles=new ArrayList<>();
        Titlesdata midato=new Titlesdata();
        
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
                        
                        midato=new Titlesdata();
                        String cade=rdato.securityTitle.toUpperCase();
                        cade=cade.replace("WARRENTS","WARRANTS");
                        cade=cade.replace("WARRNAT","WARRANTS");
                        cade=cade.replace("WARRNATS","WARRANTS");
                        cade=cade.replace("WARRENT","WARRANTS");
                        cade=cade.replace("WARRATS","WARRANTS");
                        cade=cade.replace("WARRATNS","WARRANTS");
                        cade=cade.replace("WARRATMS","WARRANTS");
                        cade=cade.replace("WAARRANT","WARRANTS");
                        cade=cade.replace("WARANT","WARRANTS");
                        cade=cade.replace("WARRANTSS","WARRANTS");
                        cade=cade.replace("\"","");
                        
                        midato.type=Titlesdata.getType(cade);
                        midato.securityTitle=cade;
                        midato.transactionShares=rdato.transactionShares;
                        midato.underlyingSecurityShares=rdato.underlyingSecurityShares;
                        //System.out.println(midato.cadena());
                        
                        if (midato.getSecurityTitle().length()>0)
                            milista.add(midato);
                    }
                    line = br.readLine();
                    max++;
                    //if (max % 1000 == 0) System.out.println(max);
                }
                br.close();
                //System.out.println("Titles.getDataset: size "+milista.size());
            }
            
            milista.sort(new Comparator<Titlesdata>() {
                @Override
                public int compare(Titlesdata o1, Titlesdata o2) {
                    return o1.getSecurityTitle().compareTo(o2.getSecurityTitle());
                }
            });
            
            String xtitle="XXX";
            Boolean primera=true;
            Titlesdata auxtitle=new Titlesdata();
            for (int i=0;i<milista.size();i++) {
                midato=milista.get(i);
                if (!midato.getSecurityTitle().equals(xtitle)) {
                    if (primera) primera=false;
                    else {
                        Titlesdata.listtitles.add(midato);
                    }
                    xtitle=midato.getSecurityTitle();
                    auxtitle=new Titlesdata();
                    auxtitle.type=midato.type;
                    auxtitle.securityTitle=midato.securityTitle;
                }
                auxtitle.transactionShares+=midato.transactionShares;
                auxtitle.underlyingSecurityShares+=midato.underlyingSecurityShares;
            }
            if (!midato.getSecurityTitle().equals(xtitle)) {
                Titlesdata.listtitles.add(midato);
            }
            
            //System.out.println("getDataset: End");
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("getDataset: "+e.getLocalizedMessage());
        }
    }

    /**
     * Calculates total amount from CSV file and stores in Oracle instance
     */
    public void getTotalAmount() {
        Titlesdata midato=new Titlesdata();
        String line="";
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
                        
                        Titlesdata.AddRecordsValue(rdato);
                    }
                    line = br.readLine();
                }
                br.close();
            }
            
            System.out.println("getTotalAmount: End");
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("getTotalAmount: "+e.getLocalizedMessage());
        }
    }
    
    
}
