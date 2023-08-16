package pandora;

import java.io.*;
import java.util.ArrayList;

public class Calculate_average {
	
	//klasa sluzaca do obliczenia sredniej arytmetycznej z n wykonanych symulacji (dla typu 2 badan)
    private static int lines_num, repeats;
    
    public Calculate_average(int lines_num, int repeats)
    {
        this.lines_num=lines_num;
        this.repeats=repeats;
    }
    public void prepare_file() throws FileNotFoundException {
        File file1 = new File("simulation.txt");
        PrintWriter outA = new PrintWriter("average.txt");	//plik w ktorym zamieszczone beda srednie z wykonanych symulacji
        average(file1,outA);	//wywolanie metody do obliczenia srednich
        outA.close();
    }
    //metody average i average_calc do obliczenia sredniej z wykonanych symulacji
    public static double average_calc(ArrayList<ArrayList<Double>> T, int I, int J)
    {
        double temp=0;
        for(int k=0;k<T.size();k+=(lines_num+1))	{temp+=T.get(I+k).get(J);} //sumowanie danego parametru z danej iteracji ze wszystkich powtorzen symulacji
        return temp/repeats;	//zwrocenie wartosci sredniej
    }
    public static void average(File file1, PrintWriter outA)
    {
        outA.println("A-iteration;A-navi count;A-colonizators count;A-trees;A-bushes");
        try {
            FileReader reader = new FileReader(file1);
            BufferedReader in = new BufferedReader(reader);
            String line;
            ArrayList<Double> num, sub_final;								//lista num sluzaca do przechowania pojedynczej linii z pliku simulation.txt oraz lista sub_final bedaca pojedyncza linia do pliku average.txt
            ArrayList<ArrayList<Double>> T= new ArrayList<>(), final_Array= new ArrayList<>();	//lista T do przechowania calej zawartosci simulation.txt oraz lista final_array z cala zawartoscia do pliku average.txt
            line=in.readLine();					//odczyt linii tytulowej
            while((line=in.readLine())!=null)	//odczyt pozostaych linii
            {
                num = new ArrayList<>();
                String[] numbers = line.split(";");	//wyodrebnienie wszystkich liczb z pojedynczej linii
                for(int j=0;j<numbers.length;j++)
                {
                    num.add(Double.parseDouble(numbers[j]));	//dodanie liczb do listy num
                }
                T.add(num);		//dodanie list num do listy T
            }
            for(int i=0;i<(lines_num+1);i++)	//petla for do zapelnienia listy final_array
            {
                sub_final = new ArrayList<>();
                for(int j=0;j<T.get(i).size();j++)
                {
                    sub_final.add(average_calc(T,i,j));	//dodanie usrednionych wartosci do listy sub_final
                }
                final_Array.add(sub_final);	//dodanie list sub_final do final_array
            }
            for(int i=0;i<final_Array.size();i++) //petla for do przepisania zawartosci listy final_array do pliku average.txt
            {
                for(int j=0;j<final_Array.get(0).size();j++)
                {
                    outA.print(String.format("%.3f;",final_Array.get(i).get(j)));
                }
                outA.println();
            }
            in.close();

        } catch (IOException e) {
            System.out.println("Błąd odczytu pliku!");
        }
    }
}
