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
 * Transaction type analysis.
 */
public class Transactionstype {
    public Transactionstypedata data;
    private String filename;
    
    private Tools l=new Tools();
    
    /**
     * Class that defines Transaction.
     */
    public Transactionstype() {
        data=new Transactionstypedata();
        filename=Dataverse.listaarchivos[uaq.dcc.datasets.Dataverse.HARVARD_INSIDERTRADING];
    }
    
    /**
     * Retrieve dynamic list from database
     */
    public void getList() {
        Transactionstypedata.listtransactions.sort(new Comparator<Transactionstypedata>() {
            @Override
            public int compare(Transactionstypedata o1, Transactionstypedata o2) {
                return o1.transactionDate.compareTo(o2.transactionDate);
            }
        });
        
        List<Transactionstypedata> mylist=new ArrayList<>();
        Transactionstypedata mydata=new Transactionstypedata();
        
        Double amax=0.0;
        Double amin=0.0;
        Double bmax=0.0;
        Double bmin=0.0;
        
        Transactionstypedata aux=new Transactionstypedata();
        aux.transactionDate=-1;
        int k=1;
        boolean thefirst=true;
        for (int i=0;i<Transactionstypedata.listtransactions.size();i++) {
            mydata=Transactionstypedata.listtransactions.get(i);
            if (mydata.transactionDate>0) {
                if (!aux.transactionDate.equals(mydata.transactionDate)) {
                    if (thefirst) thefirst=false;
                    else {
                        aux.idtransaction=k;
                        mylist.add(aux);
                        k++;
                    }
                    aux=mydata;
                }
                aux.records++;
            }
        }
        if (aux.records>99999 && aux.records<999999) {
            aux.idtransaction=k;
            mylist.add(aux);
        }

        System.out.println(Transactionstypedata.listtransactions.size()+":"+mylist.size());
        if (mylist.size()>0) {
            Transactionstypedata.listtransactions=mylist;
            
            //normalizing
            for (int i=0;i<Transactionstypedata.listtransactions.size();i++) {
                mydata=Transactionstypedata.listtransactions.get(i);
                
                if (amax<mydata.records.doubleValue()) amax=mydata.records.doubleValue();
                if (amin>mydata.records.doubleValue()) amin=mydata.records.doubleValue();
                
                if (bmax<mydata.transactionDate.doubleValue()) bmax=mydata.transactionDate.doubleValue();
                if (bmin>mydata.transactionDate.doubleValue()) bmin=mydata.transactionDate.doubleValue();
            }
            
            for (int i=0;i<Transactionstypedata.listtransactions.size();i++) {
                mydata=Transactionstypedata.listtransactions.get(i);
                mydata.apoint=Tools.Normaliza(amin,amax,mydata.records.doubleValue());
                mydata.bpoint=Tools.Normaliza(bmin,bmax,mydata.transactionDate.doubleValue());
                Transactionstypedata.listtransactions.set(i,mydata);
            }
        }
    }
    
    
    /**
     * Draws list in table format
     * @return a table with all DataList list
     */
    public String[][] DrawTable() {
        String[] ahead=Transactionstypedata.Head().split(Tools.TAB);
        int r=Transactionstypedata.listtransactions.size();
        String[][] atable=new String[r][ahead.length];
        Transactionstypedata midato=new Transactionstypedata();
        for (int i=0;i<r;i++) {
            midato=Transactionstypedata.listtransactions.get(i);
            atable[i]=midato.string().split(Tools.TAB);
        }
        return atable;
    }
    /**
     * Draws list in LaTeX table format
     * @return formatted string to be paste in LaTeX editor.
     */
    public String DrawTableLaTeX() {
        Transactionstypedata midato=new Transactionstypedata();
        String cade="""
                    \\begin{table}[H]
                    \\caption{transactionTypes for insider trading.\\label{tabla:transactionTypes}}
                    \\begin{tabularx}{\\textwidth}{ r l r r}
                    \\toprule
                    """;
        cade+=Transactionstypedata.LatexHead();
        cade+=Tools.latexMidrule();
        int r=Transactionstypedata.listtransactions.size();
        for (int i=0;i<r;i++) {
            midato=Transactionstypedata.listtransactions.get(i);
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
     */
    public void getDataset() {
        List<Transactionstypedata> milista=new ArrayList<>();
        Transactionstypedata.listtransactions=new ArrayList<>();
        Transactionstypedata midato=new Transactionstypedata();
        
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
                        midato=new Transactionstypedata();
                        midato.transactionType=rdato.transactionType;
                        midato.setTransactionDate(rdato.transactionDate);
                        if (midato.transactionDate<2024) {
                            milista.add(midato);
                            max++;
                            //if (max % 1000 == 0) System.out.println(max);
                        }
                    }
                    line = br.readLine();
                }
                br.close();
                //System.out.println("Transactions.getDataset: size "+milista.size());
            }
            
            milista.sort(new Comparator<Transactionstypedata>() {
                @Override
                public int compare(Transactionstypedata o1, Transactionstypedata o2) {
                    return o1.getTypeAndDate().compareTo(o2.getTypeAndDate());
                }
            });
            
            Transactionstypedata aux=new Transactionstypedata();
            String xtran="XXX";
            Boolean primera=true;
            Integer isuma=0;
            for (int i=0;i<milista.size();i++) {
                midato=milista.get(i);
                if (!midato.getTypeAndDate().equals(xtran)) {
                    if (primera) primera=false;
                    else {
                        aux.records=isuma;
                        Transactionstypedata.listtransactions.add(aux);
                        //System.out.println(aux.string());
                    }
                    isuma=0;
                    xtran=midato.getTypeAndDate();
                    aux=midato;
                }
                isuma++;
            }
            if (isuma>0) Transactionstypedata.listtransactions.add(aux);
            
            //System.out.println("Transactionstype.getDataset: Proceso Terminado "+milista.size());
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("getDataset: "+e.getLocalizedMessage());
        }
    }

    /**
     * Calculates total amount from CSV file and stores in Oracle instance
     */
    public void getTotalAmount() {
        Transactionstypedata midato=new Transactionstypedata();
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
                        
                        Transactionstypedata.AddRecordsValue(rdato);
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
