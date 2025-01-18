package uaq.infinitetypecentroid;

/**
 *
 * Miscelaneus tools.
 */
public class Tools {
    public static final String ABOUT_TEXT="""
                                          InfiniteTypeCentroid.jar
                                          This is a research work by the
                                          Doctorate in Data Science Program (DCC), of the
                                          Informatics Faculty (FIF), from the
                                          Universidad Autónoma de Querétaro (UAQ)
                                          https://www.uaq.mx/
                                          2025. México.
                                          """;
    
    /**
     * TAB character
     */
    public static final String TAB="\t";
    /**
     * Class that provides tools
     */
    public Tools() {
    }

    
    /**
     * 
     * @return midrule tag for LaTeX
     */
    public static String latexMidrule() {
        return "\\midrule\n";
    }
    /**
     * 
     * @param arow data in string format
     * @return only numeric values (0..9,-,.)
     */
    public static Double OnlyNumbers(String arow) {
        Double esnegativo=1.0;
        if (arow.contains("(")) {
            arow=arow.replace("(", "");
            arow=arow.replace(")", "");
            esnegativo=-1.0;
        }
        if (arow.contains("-")) {
            arow=arow.replace("-", "");
            esnegativo=-1.0;
        }
        int r=arow.length();
        String letra="";
        for (int i=0;i<r;i++) {
            switch (arow.substring(i, i+1)) {
                case "0":
                    letra+=arow.substring(i, i+1);
                    break;
                case "1":
                    letra+=arow.substring(i, i+1);
                    break;
                case "2":
                    letra+=arow.substring(i, i+1);
                    break;
                case "3":
                    letra+=arow.substring(i, i+1);
                    break;
                case "4":
                    letra+=arow.substring(i, i+1);
                    break;
                case "5":
                    letra+=arow.substring(i, i+1);
                    break;
                case "6":
                    letra+=arow.substring(i, i+1);
                    break;
                case "7":
                    letra+=arow.substring(i, i+1);
                    break;
                case "8":
                    letra+=arow.substring(i, i+1);
                    break;
                case "9":
                    letra+=arow.substring(i, i+1);
                    break;
                case ".":
                    letra+=arow.substring(i, i+1);
                    break;
                default:
                    break;
            }
        }
        if (letra.length()>0) return Double.valueOf(letra)*esnegativo;
        else return 0.0;
    }
    
    /**
     * return normalized value according to:
     * x = (x-xmin)/(xmax-xmin)
     * 
     * @param xmin min value.
     * @param xmax max value.
     * @param adata data to be normalized.
     * @return 
     */
    public static Double Normaliza(Double xmin, Double xmax, Double adata) {
        Double xdivisor=xmax-xmin;
        if (xdivisor!=0) return (adata-xmin)/(xmax-xmin);
        else return 0.0;
    }

    /**
     * Rounds to the upper next number.
     * @param rnumber
     * @return rounded number.
     */
    public static Double Redondea(Double rnumber) {
        String anumber=String.format("%.0f", rnumber);
        return Double.valueOf(anumber);
    }
 
}