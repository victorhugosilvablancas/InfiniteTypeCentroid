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
 * Ownershipt Analysis.
 */
public class Ownership {
    public Ownershipdata data;
    private String filename;
    
    private Tools l=new Tools();
    
    /**
     * Class that defines Ownership.
     */
    public Ownership() {
        data=new Ownershipdata();
        filename=Dataverse.listaarchivos[uaq.dcc.datasets.Dataverse.HARVARD_INSIDERTRADING];
    }
    
    /**
     * Retrieve dynamic list from database
     */
    public void getList() {
        Ownershipdata.listownerships.sort(new Comparator<Ownershipdata>() {
            @Override
            public int compare(Ownershipdata o1, Ownershipdata o2) {
                return o1.getDIOandDate().compareTo(o2.getDIOandDate());
            }
        });
        
        List<Ownershipdata> mylist=new ArrayList<>();
        Ownershipdata mydata=new Ownershipdata();
        
        Double rmax=0.0;
        Double rmin=999999999.0;
        Double dmax=0.0;
        Double dmin=3000.0;
        Double tmax=0.0;
        Double tmin=3000.0;
        
        Ownershipdata aux=new Ownershipdata();
        aux.directOrIndirectOwnership="X";
        int k=1;
        boolean thefirst=true;
        for (int i=0;i<Ownershipdata.listownerships.size();i++) {
            mydata=Ownershipdata.listownerships.get(i);
            if (mydata.exerciseDate>0) {
                if (!aux.getDIOandDate().equals(mydata.getDIOandDate())) {
                    if (thefirst) thefirst=false;
                    else {
                        aux.idowner=k;
                        mylist.add(aux);
                        k++;
                    }
                    aux=mydata;
                }
                aux.transactionShares+=mydata.transactionShares;
                aux.records++;
            }
        }
        if (aux.exerciseDate>0) {
            aux.idowner=k;
            mylist.add(aux);
        }
        //System.out.println(Ownershipdata.listownerships.size()+":"+mylist.size());
        if (mylist.size()>0) {
            Ownershipdata.listownerships=mylist;
            //normalizing
            for (int i=0;i<Ownershipdata.listownerships.size();i++) {
                mydata=Ownershipdata.listownerships.get(i);
                
                if (rmax<mydata.transactionShares.doubleValue()) rmax=mydata.transactionShares.doubleValue();
                if (rmin>mydata.transactionShares.doubleValue()) rmin=mydata.transactionShares.doubleValue();
                if (dmax<mydata.exerciseDate.doubleValue()) dmax=mydata.exerciseDate.doubleValue();
                if (dmin>mydata.exerciseDate.doubleValue()) dmin=mydata.exerciseDate.doubleValue();
                if (tmax<mydata.records.doubleValue()) tmax=mydata.records.doubleValue();
                if (tmin>mydata.records.doubleValue()) tmin=mydata.records.doubleValue();
            }
            for (int i=0;i<Ownershipdata.listownerships.size();i++) {
                mydata=Ownershipdata.listownerships.get(i);
                mydata.apoint=Tools.Normaliza(rmin,rmax,mydata.transactionShares.doubleValue());
                mydata.bpoint=Tools.Normaliza(dmin,dmax,mydata.exerciseDate.doubleValue());
                mydata.cpoint=Tools.Normaliza(dmin,dmax,mydata.records.doubleValue());
                Ownershipdata.listownerships.set(i,mydata);
            }
        }
    }
    /**
     * Draws list in table format
     * 
     * @return a table with all DataList list
     */
    public String[][] DrawTable() {
        String[] ahead=Ownershipdata.Head().split(Tools.TAB);
        
        int r=Ownershipdata.listownerships.size();
        String[][] atable=new String[r][ahead.length];
        
        Ownershipdata midato=new Ownershipdata();
        for (int i=0;i<Ownershipdata.listownerships.size();i++) {
            midato=Ownershipdata.listownerships.get(i);
            atable[i]=midato.string().split(Tools.TAB);
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
        List<Ownershipdata> milista=new ArrayList<>();
        Ownershipdata midato=new Ownershipdata();
        Ownershipdata.SumShares=Long.valueOf(0);
        Ownershipdata.SumRecords=0;
        /*
        try {
            Class.forName(Clusterdata.Driver);
            Connection conn = DriverManager.getConnection(Clusterdata.Url,Clusterdata.InstanceUser,Clusterdata.InstancePwd);
            Statement stmt = conn.createStatement();
            String instruccion="select "
                    + "directOrIndirectOwnership,"
                    + "sum(transactionShares) as transactionShares,"
                    + "sum(registros) as registros "
                    + "from "+Ownershipdata.TABLE+" "
                    + "group by directOrIndirectOwnership "
                    + "order by directOrIndirectOwnership ";
            ResultSet rs=stmt.executeQuery(instruccion);
            while (rs.next()) {
                midato=new Ownershipdata();
                midato.directOrIndirectOwnership=rs.getString("directOrIndirectOwnership");
                midato.transactionShares=Math.abs(rs.getLong("transactionShares"));
                midato.records=rs.getInt("registros");
                
                milista.add(midato);
                
                Ownershipdata.SumShares+=midato.transactionShares;
                Ownershipdata.SumRecords+=midato.records;
            }
            stmt.close();conn.close();
        } catch (Exception e) {
            System.out.println("DrawTable: "+e.getLocalizedMessage());
        }*/
        if (milista.size()>0) {
            cade="""
                \\begin{table}[H]
                \\caption{Direct Or Indirect Ownerships for insider trading.\\label{tabla:directOrIndirectOwnerships}}
                \\begin{tabularx}{\\textwidth}{ l r r r r }
                \\toprule
                """;
            cade+=Ownershipdata.LatexHead();
            cade+=Tools.latexMidrule();
            int r=milista.size();
            for (int i=0;i<r;i++) {
                midato=milista.get(i);

                Double rporshare=midato.transactionShares.doubleValue()/Ownershipdata.SumShares.doubleValue()*100;
                Double rporrecord=midato.records.doubleValue()/Ownershipdata.SumRecords.doubleValue()*100;

                cade+=midato.getDirectOrIndirectOwnership()
                        + " & "+midato.transactionShares
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
        List<Ownershipdata> milista=new ArrayList<>();
        Ownershipdata.listownerships=new ArrayList<>();
        Ownershipdata midato=new Ownershipdata();
        
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
                        
                        midato=new Ownershipdata();
                        midato.directOrIndirectOwnership=rdato.directOrIndirectOwnership;
                        midato.transactionShares=Long.valueOf(rdato.transactionShares);
                        midato.setExerciseDate(rdato.exerciseDate);
                        
                        milista.add(midato);
                        max++;
                        //if (max % 1000 == 0) System.out.println(max);
                    }
                    line = br.readLine();
                }
                br.close();
                //System.out.println("Ownership.getDataset: size "+milista.size());
            }
            
            milista.sort(new Comparator<Ownershipdata>() {
                @Override
                public int compare(Ownershipdata o1, Ownershipdata o2) {
                    return o1.getDIOandDate().compareTo(o2.getDIOandDate());
                }
            });
            
            String xaux="XXX";
            Boolean primera=true;
            Ownershipdata auxdato=new Ownershipdata();
            for (int i=0;i<milista.size();i++) {
                midato=milista.get(i);
                if (!midato.getDIOandDate().equals(xaux)) {
                    if (primera) primera=false;
                    else {
                        Ownershipdata.listownerships.add(auxdato);
                    }
                    xaux=midato.getDIOandDate();
                    auxdato=new Ownershipdata();
                    auxdato.directOrIndirectOwnership=midato.directOrIndirectOwnership;
                    auxdato.exerciseDate=midato.exerciseDate;
                }
                auxdato.transactionShares+=midato.transactionShares;
                auxdato.records++;
            }
            if (auxdato.records>0) {
                Ownershipdata.listownerships.add(auxdato);
            }
            
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("getDataset: "+e.getLocalizedMessage());
        }
    }
    
}
