package pandora;

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	
	private static int x,y;
	private static int rider, archer, robot, soldier;
	private static int density;
	private static int iterations;
	private static int repeats;
	private static int show_visualization;
	private static int use_file;
	private static int lines_num=50;
	private static int navi_wins=0, col_wins=0, draws=0;
	private static int research_type, parameter;
	static boolean attack_flag, dead_unit_flag; 

	public static void main(String[] args) throws IOException
	{ 
		Scanner scan = new Scanner(System.in);
		File file = new File("sample.txt");										//plik do odczytu gotowych parametrow
		PrintWriter out = new PrintWriter("simulation.txt");					//plik do zapisu danych symulacji
		System.out.println("Odczytac parametry wejsciowe z pliku? (1 -> tak):");
		use_file=scan.nextInt();	//przypisanie wartosci za pomoca Scannera
		//use_file=1;					//przypisanie wartosci w celu zadzialania taska run
		if(use_file==1) {
			try {										//odczytanie zawartosci pliku sample.txt o ile istnieje
				FileReader reader = new FileReader(file);
				BufferedReader in = new BufferedReader(reader);
			    String sample;
			    int[] T= new int[9]; int i=0;
			    while((sample=in.readLine())!=null)
			    {
			    	T[i]=Integer.parseInt(sample); i++;	//zamiana sample z typu String na int i przypisanie do tablicy
			    }
			    x=T[0]; y=T[1]; rider=T[2]; archer=T[3]; robot=T[4]; soldier=T[5]; iterations=T[6]; density=T[7]; show_visualization=T[8];
			    create_teams_and_map(x,y,rider,archer,robot,soldier,iterations,density,show_visualization,out);
			    in.close();
			} catch (IOException e) {
				System.out.println("Błąd odczytu pliku!");
			} 
		}
		else {	//wywolane jesli dane nie sa odczytywane z gotowego pliku
		System.out.println("Podaj rozmiary mapy:");
		x = scan.nextInt();
		y = scan.nextInt();
		
		System.out.println("Podaj liczebnosc jezdzcow:");
		rider=scan.nextInt();
		System.out.println("Podaj liczebnosc lucznikow:");
		archer=scan.nextInt();
		System.out.println("Podaj liczebnosc robotow:");
		robot=scan.nextInt();
		System.out.println("Podaj liczebnosc zolnierzy:");
		soldier=scan.nextInt();
		System.out.println("Podaj zageszczenie lasu:");
		density=scan.nextInt();
		System.out.println("Podaj liczbe iteracji:");
		iterations=scan.nextInt();
		System.out.println("Przedstawic wizualizacje symulacji? (1 -> tak):");
		show_visualization=scan.nextInt();
		System.out.println("Podaj typ badania: (brak badan -> 0, wplyw danego parametru na wynik symulacji -> 1, srednia liczebnosc druzyn w trakcie trwania symulacji ->2)");
		research_type=scan.nextInt();
		System.out.println("Podaj liczbe powtorzen symulacji (dla wlaczonej wizualizacji zalecane jest 1):");
		repeats=scan.nextInt();
		
		if(research_type==1)	//wykonanie symulacji dla typu 1 badania
		{
			System.out.println("Podaj parametr\nzageszczenie lasu -> 1\nliczba lucznikow -> 2\nliczba jezdzcow -> 3\nliczba robotow -> 4\nliczba zolnierzy -> 5");
			parameter=scan.nextInt();
			out.println(parameter+";navi_wins;col_wins;draws");
			for(int j=0;j<101;j+=2)
			{
				switch (parameter)
				{
				case 1: density=j; break;
				case 2: archer=j; break;
				case 3: rider=j; break;
				case 4: robot=j; break;
				case 5: soldier=j; break;
				}					//przekazanie niezbednych parametrow do wykonania symulacji
			for(int i=0;i<repeats;i++) {create_teams_and_map(x,y,rider,archer,robot,soldier,iterations,density,show_visualization,out);}
			out.println(j+";"+navi_wins+";"+col_wins+";"+draws);
			navi_wins=0; col_wins=0; draws=0;
			System.out.println("Done "+j+" %");
			}
		}
		else if(research_type==2)	//wykonanie symulacji dla typu 2 badan
		{
			out.println("iteration;navi count;colonizators count;trees;bushes;all map squares");//pierwszy wiersz pliku z danymi zebranymi z symulacji
			for(int i=0;i<repeats;i++) {create_teams_and_map(x,y,rider,archer,robot,soldier,iterations,density,show_visualization,out);}
		}
		else {create_teams_and_map(x,y,rider,archer,robot,soldier,iterations,density,show_visualization,out);}//wykonanie wymulacji bez badan
		}
		out.close();
		if(research_type==2)
		{
		File file1 = new File("simulation.txt");
		PrintWriter outA = new PrintWriter("average.txt");	//plik w ktorym zamieszczone beda srednie z wykonanych symulacji
		average(file1,outA);	//wywolanie metody do obliczenia srednich
		outA.close();
		}
		scan.close();
		System.out.println("Symulacja ukonczona.");
	}
	
	public static void create_teams_and_map(int x, int y, int rider, int archer, int robot, int soldier, int iterations, int density,int show_visualization,PrintWriter out) throws FileNotFoundException //metoda z uwzglednieniem parametrow wejsciowych np ze skryptow
	{
		Map mapa = new Map(x,y,density);	//przypisanie mapie podtsawowych wartosci: dlugosc, szerokosc, zageszczenie lasu
		mapa.generate();					//utworzenie mapy
		int spawn_x=x-1;					//umiejscowienie spawnu kolonizatorow w dolnym prawym rogu mapy
		int spawn_y=y-1;		
		
		Interface[] navi = new Interface[archer+rider];		//tablica typu Interface dla Navi
		Interface[] col = new Interface[robot+soldier+1];	//tablica typu Interface dla kolonizatorow
		//ponizsze pętle for tworza podana wczesniej liczbe jednostek poszczegolnych klas - podana liczbe razy tworza jednostke i dodaja ja do tablicy typu Interface
		//parametry konstruktorow danych klas uszeregowane sa w nastepujacy sposob:
		//(type,health,speed,pos_x,pos_y,strength,strength_bonus,defense_bonus,can_far_attack)
		
		//dla kolonizatorow i navi jednostki spawnuja sie tak aby zadne jednostki nie dzielily ze soba pola
		//dla kolonizatorow spawn zaczyna sie od prawej krawedzi mapy
		//dla navi spawn zaczyna sie od lewej krawedzi mapy
		//w przypadku wypelnienia ktores z krawedzi, do spawnu przydzielana jest nastepna kolumna w kierunku centrum mapy 
		for(int i=0;i<robot+soldier;i++) //petla for dla robotow i zolnierzy
		{
			if(spawn_y<0) {spawn_y=y-1; spawn_x--;}	// komentarz 92
			while(mapa.FieldContent(spawn_x,spawn_y)=='T'){spawn_y--; if(spawn_y<0) {spawn_y=y-1; spawn_x--;}}	//jesli pole jest drzewem to jednostka nie moze na nim sie pojawic (pojawi sie na pierwszym dostepnym polu 'B' lub '_')
			if(i<robot)	{col[i+1] = new Robot(spawn_x,spawn_y);}
			else		{col[i+1] = new Soldier(spawn_x,spawn_y);}
			spawn_y--; //przesuniecie spawnu tak aby kazda jednostka miala wlasne miejsce spawnu
		}
		spawn_x=0;			//umiejscowienie spawnu navi w gornym lewym rogu mapy
		spawn_y=0;
		for(int i=0;i<rider+archer;i++)	//petla for dla jezdzcow i lucznikow
		{
			if(spawn_y>=y) {spawn_y=0; spawn_x++;} // komentarz 92
			while(mapa.FieldContent(spawn_x,spawn_y)=='T'){spawn_y++; if(spawn_y>=y) {spawn_y=0; spawn_x++;}}
			if(i<rider) {navi[i] = new Rider(spawn_x,spawn_y);}
			else 		{navi[i] = new Archer(spawn_x,spawn_y);}
			spawn_y++;
		}
		Bulldozer bulldozer = new Bulldozer(x/2,y/2);
		col[0] = bulldozer;	//(150-151)utworzenie buldozera i dodanie go do tablicy kolonizatorow na pierwsza pozycje
		
		if(show_visualization==1){
		mapa.images();	//zaladowanie plikow .png
		mapa.Frame(navi,col); //wyswietlenie mapy przed rozpoczeciem symulacji
		}	
		
		switch(simulation(mapa,navi,col,show_visualization,out))	//wywolanie symulacji
		{
			case 0: draws++; if(research_type==0) {System.out.println("Symulacji zakonczyla sie bez rozstrzygniecia");} break;
			case 1: col_wins++; if(research_type==0) {System.out.println("Symulacji zakonczyla sie zwyciestwem kolonizatorow");} break;
			case 2: navi_wins++; if(research_type==0) {System.out.println("Symulacji zakonczyla sie zwyciestwem navi");}break;
			default: break;
		}
		if(show_visualization==1) {mapa.Frame(navi,col);} //wyswietlenie zawartosci mapy po zakonczeniu symulacji
	}
	
	public static int simulation(Map mapa, Interface[] navi, Interface[] col,int show_visualization,PrintWriter out) throws FileNotFoundException
	{
		int navi_counter=navi.length;			//zapisanie liczby powstalych jednostek navi
		int colonizators_counter=col.length;	//zapisanie liczby powstalych jednostek kolonizatorow
		
		if(show_visualization==1) {
		try {Thread.sleep(7000);}			//odczekanie 3 sekund aby uzytkownik mogl otworzyc mape na czas
		catch (InterruptedException e) {e.printStackTrace();}
		}
		
		Bulldozer bulldozer = (Bulldozer)col[0];
		
		for(int i=0;i<=iterations;i++)	//petla for do wykonania symulacji
		{
			if(show_visualization==1) {
			mapa.Frame(navi,col);					//wizualizacja mapy podczas danej tury
			try {Thread.sleep(10);}				//odczekanie 0.5 sekundy w celu wyswietlenia pojedynczej iteracji symulacji
			catch (InterruptedException e) {e.printStackTrace();}
			}
			
			if(bulldozer.health>0) {bulldozer.moveBulldozer(mapa);}					//wykonanie metody moveBulldozer dla buldozera
			for(Interface NAVI : navi)	//petla foreach dla Navi
			{
				if(((Unit)NAVI).health>0) {	//uniemozliwienie wykonania czegokolwiek jesli jednostka jest martwa
				attack_flag=false;
				dead_unit_flag=false;
				if(bulldozer.pos_x==((Unit)NAVI).pos_x && bulldozer.pos_y==((Unit)NAVI).pos_y && bulldozer.health>0) {bulldozer.attack(NAVI,mapa); navi_counter--;}	//jesli buldozer najechal na dana jednostke Navi to ja zaatakuje (zabije)
				else
				{
					NAVI.find_enemy(col,mapa);
					if(dead_unit_flag) {colonizators_counter--;}
					if(attack_flag==false) {NAVI.move(mapa);} //wykonanie metody move dla pozostalych jednostek (pod warunkiem ze nie wykonaly ataku)
				}} 						
			}
			for(Interface COL : col)	//petla foreach dla kolonizatorow
			{
				if(((Unit)COL).health>0 && ((Unit)COL).type!=0) { //uniemozliwienie wykonania czegokolwiek jesli jednostka jest martwa lub jest buldozerem (we wlasny sposob wykonuje mozliwe ruchy)
				attack_flag=false;
				dead_unit_flag=false;
					COL.find_enemy(navi,mapa);
					if(dead_unit_flag) {navi_counter--;}
					if(attack_flag==false) {COL.move(mapa);} //wykonanie metody move dla pozostalych jednostek (pod warunkiem ze nie wykonaly ataku)
				}
			}
			if(i%(iterations/lines_num)==0 && research_type==2) {out.println(i+";"+navi_counter+";"+colonizators_counter+";"+mapa.getTrees_numb()+";"+mapa.getBushes_numb());}
			//zapisanie pozadanych danych
			if(navi_counter==0 && research_type!=2) {return 1;}
			if(colonizators_counter==0 && research_type!=2) {return 2;}
		}
		if(navi_counter==0) {return 1;}
		if(colonizators_counter==0) {return 2;}
		return 0;
	}
	//metody average i average_calc do obliczenia sredniej z wykonanych symulacji (tylko dla typu 2 badan)
	public static double average_calc(ArrayList<ArrayList<Double>> T, int I, int J)
	{
		double temp=0;
		for(int k=0;k<T.size();k+=(lines_num+1))
		{
			temp+=T.get(I+k).get(J);	//sumowanie danego parametru z danej iteracji ze wszystkich powtorzen symulacji
		}
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
		    ArrayList<ArrayList<Double>> T= new ArrayList<>(), final_Array= new ArrayList<>();	//lista T przechowania calej zawartosci simulation.txt oraz lista final_array z cala zawartoscia do pliku average.txt
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
