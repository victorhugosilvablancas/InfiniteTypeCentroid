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
 * Exercise analysis.
 */
public class Exercise {
    public Exercisesdata data;
    private String filename;
    
    private Tools l=new Tools();
    
    /**
     * Class that defines Exercises.
     */
    public Exercise() {
        data=new Exercisesdata();
        filename=Dataverse.listaarchivos[uaq.dcc.datasets.Dataverse.HARVARD_INSIDERTRADING];
    }
    
    /**
     * Retrieve dynamic list from database
     */
    public void getList() {
        Exercisesdata.listexercises.sort(new Comparator<Exercisesdata>() {
            @Override
            public int compare(Exercisesdata o1, Exercisesdata o2) {
                return o1.exerciseDateFn.compareTo(o2.exerciseDateFn);
            }
        });
        
        List<Exercisesdata> mylist=new ArrayList<>();
        Exercisesdata mydata=new Exercisesdata();
        
        Double rmax=0.0;
        Double rmin=999999999.0;
        Double dmax=0.0;
        Double dmin=3000.0;
        
        Exercisesdata aux=new Exercisesdata();
        aux.exerciseDateFn="XXX";
        int k=1;
        boolean thefirst=true;
        for (int i=0;i<Exercisesdata.listexercises.size();i++) {
            mydata=Exercisesdata.listexercises.get(i);
            if (mydata.exerciseDate>0) {
                if (!aux.exerciseDateFn.equals(mydata.exerciseDateFn)) {
                    if (thefirst) thefirst=false;
                    else {
                        aux.idexercise=k;
                        mylist.add(aux);
                        k++;
                    }
                    aux=mydata;
                }
                aux.records++;
            }
        }
        if (aux.exerciseDate>0) {
            aux.idexercise=k;
            mylist.add(aux);
        }
        //System.out.println(Exercisesdata.listexercises.size()+":"+mylist.size());
        if (mylist.size()>0) {
            Exercisesdata.listexercises=mylist;
            Exercisesdata.listexercises.sort(new Comparator<Exercisesdata>() {
                @Override
                public int compare(Exercisesdata o1, Exercisesdata o2) {
                    return o1.getExerciseNum().compareTo(o2.getExerciseNum());
                }
            });
            
            //normalizing
            for (int i=0;i<Exercisesdata.listexercises.size();i++) {
                mydata=Exercisesdata.listexercises.get(i);
                
                if (rmax<mydata.getExerciseNum()) rmax=mydata.getExerciseNum().doubleValue();
                if (rmin>mydata.getExerciseNum()) rmin=mydata.getExerciseNum().doubleValue();
                if (dmax<mydata.records) dmax=mydata.records.doubleValue();
                if (dmin>mydata.records) dmin=mydata.records.doubleValue();
            }
            
            for (int i=0;i<Exercisesdata.listexercises.size();i++) {
                mydata=Exercisesdata.listexercises.get(i);
                mydata.apoint=Tools.Normaliza(rmin,rmax,mydata.getExerciseNum().doubleValue());
                mydata.bpoint=Tools.Normaliza(dmin,dmax,mydata.records.doubleValue());
                Exercisesdata.listexercises.set(i,mydata);
            }
        }
        
    }
    /**
     * Draws list in table format
     * @return a table with all DataList list
     */
    public String[][] DrawTable() {
        String[] ahead=Exercisesdata.Head().split(Tools.TAB);
        int r=Exercisesdata.listexercises.size();
        String[][] atable=new String[r][ahead.length]; 
        Exercisesdata midato=new Exercisesdata();
        for (int i=0;i<r;i++) {
            midato=Exercisesdata.listexercises.get(i);
            atable[i]=midato.string().split(Tools.TAB);
        }
        return atable;
    }
    /**
     * Draws list in LaTeX table format
     * @return formatted string to be paste in LaTeX editor.
     */
    public String DrawTableLaTeX() {
        Exercisesdata midato=new Exercisesdata();
        String cade="""
                    \\begin{table}[H]
                    \\caption{Exercise Date Funcion records.\\label{tabla:exerciseDateFn}}
                    \\begin{tabularx}{\\textwidth}{ l r l r}
                    \\toprule
                    """;
        cade+=Exercisesdata.LatexHead();
        cade+=Tools.latexMidrule();
        int r=Exercisesdata.listexercises.size();
        boolean fin=false;
        for (int i=0;i<r;i++) {
            midato=Exercisesdata.listexercises.get(i);
            cade+=midato.latex();
        }
        if (!fin) cade+="\\\\\n";
        //cade+=midato.latexpuntos();
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
        List<Exercisesdata> milista=new ArrayList<>();
        Exercisesdata.listexercises=new ArrayList<>();
        Exercisesdata midato=new Exercisesdata();
        
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
                        
                        midato=new Exercisesdata();
                        midato.setExerciseDate(rdato.exerciseDate);
                        midato.exerciseDateFn=rdato.exerciseDateFn.replace("\"", "");
                        
                        if (midato.exerciseDateFn.contains("-")) {
                            midato.exerciseDateFn=midato.exerciseDateFn.replace("-", ",");
                            String[] partes=midato.exerciseDateFn.split(",");
                            if (partes.length>1) {
                                for (int k=0;k<partes.length;k++) {
                                    Exercisesdata kdato=new Exercisesdata();
                                    kdato.exerciseDate=midato.exerciseDate;
                                    kdato.exerciseDateFn=partes[k];
                                    milista.add(kdato);
                                    max++;
                                    //if (max % 1000 == 0) System.out.println(max);
                                }
                            }
                        } else {
                            milista.add(midato);
                        }
                        //System.out.println(midato.cadena());
                    }
                    line = br.readLine();
                }
                br.close();
                //System.out.println("Exercise.getDataset: size "+milista.size());
            }
            
            milista.sort(new Comparator<Exercisesdata>() {
                @Override
                public int compare(Exercisesdata o1, Exercisesdata o2) {
                    return o1.getExerciseDateFn().compareTo(o2.getExerciseDateFn());
                }
            });
            
            String xfuncion="XXX";
            Boolean primera=true;
            Exercisesdata auxdato=new Exercisesdata();
            for (int i=0;i<milista.size();i++) {
                midato=milista.get(i);
                if (!midato.getExerciseDateFn().equals(xfuncion)) {
                    if (primera) primera=false;
                    else {
                        Exercisesdata.listexercises.add(auxdato);
                    }
                    auxdato=new Exercisesdata();
                    auxdato.exerciseDate=midato.exerciseDate;
                    auxdato.exerciseDateFn=midato.exerciseDateFn;
                    xfuncion=midato.getExerciseDateFn();
                }
                auxdato.records++;
            }
            Exercisesdata.listexercises.add(auxdato);
            
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("getDataset: "+e.getLocalizedMessage());
        }
    }
    
    
}
