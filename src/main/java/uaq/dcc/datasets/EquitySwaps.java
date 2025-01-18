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
 * Equity Swaps analysis.
 */
public class EquitySwaps {
    /**
     * EquitySwapsdata DataList type.
     */
    public EquitySwapsdata data;
    /**
     * source CSV file.
     */
    private String filename;
    
    /**
     * Tools instance
     */
    private Tools l=new Tools();
    
    /**
     * Class that defines EquitySwaps.
     */
    public EquitySwaps() {
        data=new EquitySwapsdata();
        filename=Dataverse.listaarchivos[uaq.dcc.datasets.Dataverse.HARVARD_INSIDERTRADING];
    }
    /**
     * Retrieve dynamic list from database
     */
    public void getList() {
        EquitySwapsdata.listequityswaps.sort(new Comparator<EquitySwapsdata>() {
            @Override
            public int compare(EquitySwapsdata o1, EquitySwapsdata o2) {
                return o1.transactionCode.compareTo(o2.transactionCode);
            }
        });
        List<EquitySwapsdata> mylist=new ArrayList<>();
        EquitySwapsdata mydata=new EquitySwapsdata();
        
        Double rmax=0.0;
        Double rmin=999999999.0;
        Double dmax=0.0;
        Double dmin=999999999.0;
        Double tmax=0.0;
        Double tmin=999999999.0;
        
        EquitySwapsdata aux=new EquitySwapsdata();
        aux.transactionCode="XXX";
        int k=1;
        boolean thefirst=true;
        for (int i=0;i<EquitySwapsdata.listequityswaps.size();i++) {
            mydata=EquitySwapsdata.listequityswaps.get(i);
            if (mydata.equitySwapInvolved>0) {
                if (!aux.transactionCode.equals(mydata.transactionCode)) {
                    if (thefirst) thefirst=false;
                    else {
                        //rounding because to mainting focus on transactionCode
                        //at the moment of normalizing
                        if (mydata.records>99999) mydata.records=100000;
                        if (mydata.records<5000) mydata.records=5000;
                        aux.idequity=k;
                        mylist.add(aux);
                        k++;
                    }
                    aux=mydata;
                }
                aux.equitySwapInvolved+=mydata.equitySwapInvolved;
                aux.records++;
            }
        }
        if (aux.equitySwapInvolved>0) {
            if (mydata.records>99999) mydata.records=100000;
            if (mydata.records<5000) mydata.records=5000;
            aux.idequity=k;
            mylist.add(aux);
        }
        //System.out.println(EquitySwapsdata.listequityswaps.size()+":"+mylist.size());
        if (mylist.size()>0) {
            EquitySwapsdata.listequityswaps=mylist;
            //normalizing
            for (int i=0;i<EquitySwapsdata.listequityswaps.size();i++) {
                mydata=EquitySwapsdata.listequityswaps.get(i);
                
                if (rmax<mydata.transactioncodefloat()) rmax=mydata.transactioncodefloat();
                if (rmin>mydata.transactioncodefloat()) rmin=mydata.transactioncodefloat();
                if (dmax<mydata.equitySwapInvolved.doubleValue()) dmax=mydata.equitySwapInvolved.doubleValue();
                if (dmin>mydata.equitySwapInvolved.doubleValue()) dmin=mydata.equitySwapInvolved.doubleValue();
                if (tmax<mydata.records.doubleValue()) tmax=mydata.records.doubleValue();
                if (tmin>mydata.records.doubleValue()) tmin=mydata.records.doubleValue();
            }
            for (int i=0;i<EquitySwapsdata.listequityswaps.size();i++) {
                mydata=EquitySwapsdata.listequityswaps.get(i);
                mydata.apoint=Tools.Normaliza(rmin,rmax,mydata.transactioncodefloat());
                mydata.bpoint=Tools.Normaliza(dmin,dmax,mydata.equitySwapInvolved.doubleValue());
                mydata.cpoint=Tools.Normaliza(dmin,dmax,mydata.records.doubleValue());
                EquitySwapsdata.listequityswaps.set(i,mydata);
            }
        }
    }
    /**
     * Draws list in table format
     * 
     * @return a table with all DataList list
     */
    public String[][] DrawTable() {
        String[] ahead=EquitySwapsdata.Head().split(Tools.TAB);
        
        int r=EquitySwapsdata.listequityswaps.size();
        String[][] atable=new String[r][ahead.length];
        
        EquitySwapsdata midato=new EquitySwapsdata();
        
        for (int i=0;i<EquitySwapsdata.listequityswaps.size();i++) {
            midato=EquitySwapsdata.listequityswaps.get(i);
            atable[i]=midato.string().split("\t");
        }
        return atable;
    }
    /**
     * 
     * Draws list in LaTeX table format
     * @return formatted string to be paste in LaTeX editor.
     */
    public String DrawTableLaTeX() {
        String cade="";
        List<EquitySwapsdata> milista=new ArrayList<>();
        EquitySwapsdata midato=new EquitySwapsdata();
        EquitySwapsdata.SumSwaps=0;
        EquitySwapsdata.SumRecords=0;/*
        try {
            Class.forName(Clusterdata.Driver);
            Connection conn = DriverManager.getConnection(Clusterdata.Url,Clusterdata.InstanceUser,Clusterdata.InstancePwd);
            Statement stmt = conn.createStatement();
            String instruccion="select "
                    + "transactionCode,"
                    + "sum(equitySwapInvolved) as equitySwapInvolved,"
                    + "sum(registros) as registros "
                    + "from "+EquitySwapsdata.TABLE+" "
                    + "group by transactionCode "
                    + "order by transactionCode ";
            ResultSet rs=stmt.executeQuery(instruccion);
            while (rs.next()) {
                midato=new EquitySwapsdata();
                midato.transactionCode=rs.getString("transactionCode");
                midato.equitySwapInvolved=rs.getInt("equitySwapInvolved");
                midato.records=rs.getInt("registros");
                
                milista.add(midato);
                
                EquitySwapsdata.SumSwaps+=midato.equitySwapInvolved;
                EquitySwapsdata.SumRecords+=midato.records;
            }
            stmt.close();conn.close();
        } catch (Exception e) {
            System.out.println("DrawTable: "+e.getLocalizedMessage());
        }*/
        if (milista.size()>0) {
            Double rporshare=0.0;
            Double rporrecord=0.0;

                cade="""
                    \\begin{table}[H]
                    \\caption{Equity Swaps Involved by Transaction Code.\\label{tabla:equitySwapInvolved}}
                    \\begin{tabularx}{\\textwidth}{ l r r r r }
                    \\toprule
                    """;
                cade+=EquitySwapsdata.LatexHead();
                cade+=Tools.latexMidrule();
                int r=milista.size();
                for (int i=0;i<r;i++) {
                    midato=milista.get(i);
                    
                    rporshare=midato.equitySwapInvolved.doubleValue()/EquitySwapsdata.SumSwaps.doubleValue()*100;
                    rporrecord=midato.records.doubleValue()/EquitySwapsdata.SumRecords.doubleValue()*100;
                    
                    cade+=midato.getTransactionCode()
                            + " & "+midato.equitySwapInvolved
                            + " & "+String.format("%.2f",rporshare)+"\\%"
                            + " & "+midato.records
                            + " & "+String.format("%.2f",rporrecord)+"\\%"
                            + "\\\\\n";
                    if (i+1 < r)
                        cade+=Tools.latexMidrule();
                }
                cade+="""
                      \\bottomrule
                      \\end{tabularx}
                      \\noindent{\\footnotesize{Source: Proposed work.}}
                      \\end{table}""";
            
        }
        return cade;
    }
    
    /**
     * Retrieve dinamic list from CSV file
     * @param maxdata
     */
    public void getDataset(int maxdata) {
        List<EquitySwapsdata> milista=new ArrayList<>();
        EquitySwapsdata.listequityswaps=new ArrayList<>();
        EquitySwapsdata midato=new EquitySwapsdata();
        
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
                        
                        midato=new EquitySwapsdata();
                        midato.transactionCode=rdato.transactionCode;
                        midato.equitySwapInvolved=rdato.equitySwapInvolved;
                        //System.out.println(midato.cadena());
                        milista.add(midato);
                    }
                    line = br.readLine();
                    max++;
                    //if (max % 1000 == 0) System.out.println(max);
                }
                br.close();
                //System.out.println("Swaps.getDataset: size "+milista.size());
            }
            
            milista.sort(new Comparator<EquitySwapsdata>() {
                @Override
                public int compare(EquitySwapsdata o1, EquitySwapsdata o2) {
                    return o1.getTransactionCode().compareTo(o2.getTransactionCode());
                }
            });
            
            String xaux="XXX";
            Boolean primera=true;
            EquitySwapsdata auxdato=new EquitySwapsdata();
            for (int i=0;i<milista.size();i++) {
                midato=milista.get(i);
                if (!midato.getTransactionCode().equals(xaux)) {
                    if (primera) primera=false;
                    else {
                        //System.out.println(auxdato.cadena());
                        EquitySwapsdata.listequityswaps.add(auxdato);
                    }
                    xaux=midato.getTransactionCode();
                    auxdato=new EquitySwapsdata();
                    auxdato.transactionCode=midato.transactionCode;
                }
                auxdato.equitySwapInvolved+=midato.equitySwapInvolved;
                auxdato.records++;
            }
            if (auxdato.records>0) {
                //System.out.println(auxdato.cadena());
                EquitySwapsdata.listequityswaps.add(auxdato);
            }
            
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("getDataset: "+e.getLocalizedMessage());
        }
    }
    
    
}
