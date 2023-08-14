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
	private static int density, iterations, repeats;
	private static int show_visualization, use_file;
	private static int lines_num=50;
	private static int navi_wins=0, col_wins=0, draws=0;
	private static int research_type, parameter;
	static boolean attack_flag, dead_unit_flag; 
	
	private static int navi_counter;
	private static int colonizators_counter;
	
	static ArrayList<Interface> nav = new ArrayList<>();
	static ArrayList<Interface> colo = new ArrayList<>();
	
	private static int spawn_xx, spawn_yy;

	public static void main(String[] args) throws IOException
	{ 
		Scanner scan = new Scanner(System.in);
		PrintWriter out = new PrintWriter("simulation.txt");					//plik do zapisu danych symulacji
		System.out.println("Odczytac parametry wejsciowe z pliku? (1 -> tak):");
		//use_file=scan.nextInt();	//przypisanie wartosci za pomoca Scannera
		use_file=1;				//przypisanie wartosci w celu zadzialania taska run
		if(use_file==1) {
			if(read_data()) {return;}
			create_map(x,y,rider,archer,robot,soldier,iterations,density,show_visualization,out);
		}
		else {	//wywolane jesli dane nie sa odczytywane z gotowego pliku
			type_data(scan);
			research_type_switch(out,scan);
		}
		out.close();
		if(research_type==2)
		{
			Calculate_average CA = new Calculate_average(lines_num,repeats);
			CA.prepare_file();
		}
		scan.close();
		System.out.println("Symulacja ukonczona.");
	}
	public static boolean read_data()
	{
		File file = new File("sample.txt");										//plik do odczytu gotowych parametrow
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
			in.close();
		} catch (IOException e) {
			System.out.println("Błąd odczytu pliku!"); return true;
		}
		return false;
	}
	public static void type_data(Scanner scan)
	{
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
	}
	public static void research_type_switch(PrintWriter out, Scanner scan) throws FileNotFoundException {
		switch(research_type)
		{
			case 2:
				out.println("iteration;navi count;colonizators count;trees;bushes;all map squares");//pierwszy wiersz pliku z danymi zebranymi z symulacji
				for(int i=0;i<repeats;i++) {create_map(x,y,rider,archer,robot,soldier,iterations,density,show_visualization,out);}
				break;
			case 1:
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
					for(int i=0;i<repeats;i++) {create_map(x,y,rider,archer,robot,soldier,iterations,density,show_visualization,out);}
					out.println(j+";"+navi_wins+";"+col_wins+";"+draws);
					navi_wins=0; col_wins=0; draws=0;
					System.out.println("Done "+j+" %");
				}
				break;
			default: create_map(x,y,rider,archer,robot,soldier,iterations,density,show_visualization,out);//wykonanie wymulacji bez badan
				break;
		}
	}
	public static void map_border(char team)
	{
		if(team=='N') 		{spawn_yy++; if(spawn_yy>=y) {spawn_yy=0; spawn_xx++;}}
		else if(team=='C')	{spawn_yy--; if(spawn_yy<0) {spawn_yy=y-1; spawn_xx--;}}
		else {System.out.println("Blad spawnu!");}
	}
	
	public static void create_teams(Map mapa, String unit, int number_of_units, int spawn_x, int spawn_y, char team)
	{
		spawn_xx=spawn_x;
		spawn_yy=spawn_y;
		for(int i=0;i<number_of_units;i++)	//petla for dla jezdzcow i lucznikow
		{
			do{map_border(team);} while(mapa.FieldContent(spawn_xx,spawn_yy)=='T');
			switch(unit)
			{
			case "Soldier":		colo.add(new Soldier(spawn_xx,spawn_yy));	break;
			case "Robot": 		colo.add(new Robot(spawn_xx,spawn_yy));	break;
			case "Rider": 		nav.add(new Rider(spawn_xx,spawn_yy));	break;
			case "Archer": 		nav.add(new Archer(spawn_xx,spawn_yy));	break;
			default:	System.out.println("Blad utworzenia jednostki!"); break;
			}
		}
	}
	
	public static void create_map(int x, int y, int rider, int archer, int robot, int soldier, int iterations, int density,int show_visualization,PrintWriter out) throws FileNotFoundException //metoda z uwzglednieniem parametrow wejsciowych np ze skryptow
	{
		Map mapa = new Map(x,y,density);	//przypisanie mapie podtsawowych wartosci: dlugosc, szerokosc, zageszczenie lasu
		mapa.generate();					//utworzenie mapy
		
		create_teams(mapa,"Soldier",soldier,x-1,y,'C');
		create_teams(mapa,"Robot",robot,spawn_xx,spawn_yy,'C');
		create_teams(mapa,"Rider",rider,0,-1,'N');
		create_teams(mapa,"Archer",archer,spawn_xx,spawn_yy,'N');
		
		Bulldozer bulldozer = new Bulldozer(x/2,y/2);
		colo.add(bulldozer); //utworzenie buldozera i dodanie go do tablicy kolonizatorow na ostatnia pozycje
		
		if(show_visualization==1) {mapa.images(); mapa.Frame(nav,colo);}
		//zaladowanie plikow .png oraz wyswietlenie mapy przed rozpoczeciem symulacji

		switch(simulation(mapa,show_visualization,out))	//wywolanie symulacji
		{
			case 0: draws++; if(research_type==0) {System.out.println("Symulacji zakonczyla sie bez rozstrzygniecia");} break;
			case 1: col_wins++; if(research_type==0) {System.out.println("Symulacji zakonczyla sie zwyciestwem kolonizatorow");} break;
			case 2: navi_wins++; if(research_type==0) {System.out.println("Symulacji zakonczyla sie zwyciestwem navi");}break;
			default: break;
		}
		if(show_visualization==1) {mapa.Frame(nav,colo);} //wyswietlenie zawartosci mapy po zakonczeniu symulacji
	}
	
	public static int simulation(Map mapa,int show_visualization,PrintWriter out) throws FileNotFoundException
	{
		navi_counter=nav.size();			//zapisanie liczby powstalych jednostek navi
		colonizators_counter=colo.size();	//zapisanie liczby powstalych jednostek kolonizatorow
		
		if(show_visualization==1) {
		try {Thread.sleep(3000);}			//odczekanie 3 sekund aby uzytkownik mogl otworzyc mape na czas
		catch (InterruptedException e) {e.printStackTrace();}
		}
		
		Bulldozer bulldozer = (Bulldozer)colo.get(colo.size()-1);
		
		for(int i=0;i<=iterations;i++)	//petla for do wykonania symulacji
		{
			if(show_visualization==1) {
			mapa.Frame(nav,colo);					//wizualizacja mapy podczas danej tury
			try {Thread.sleep(20);}				//odczekanie 0.5 sekundy w celu wyswietlenia pojedynczej iteracji symulacji
			catch (InterruptedException e) {e.printStackTrace();}
			}
			
			if(bulldozer.health>0) {bulldozer.move(mapa);}					//wykonanie metody moveBulldozer dla buldozera
			iteration(mapa,nav,colo,bulldozer);	//metoda iterujaca navi
			iteration(mapa,colo,nav,bulldozer);	//metoda iterujaca kolonizatorow
			if(i%(iterations/lines_num)==0 && research_type==2) {out.println(i+";"+nav.size()+";"+colo.size()+";"+mapa.getTrees_numb()+";"+mapa.getBushes_numb());}
			//zapisanie pozadanych danych
			if(navi_counter==0 && research_type!=2) {return 1;}
			if(colonizators_counter==0 && research_type!=2) {return 2;}
		}
		if(navi_counter==0) {return 1;}
		if(colonizators_counter==0) {return 2;}
		return 0;
	}
	public static void iteration(Map mapa,ArrayList<Interface> unit, ArrayList<Interface> enemy, Bulldozer bulldozer)
	{
		for(Interface UNIT : unit)	//petla foreach dla Navi
		{
			if(((Unit)UNIT).health>0 && ((Unit)UNIT).type!=0) {	//uniemozliwienie wykonania czegokolwiek jesli jednostka jest martwa lub jest buldozerem
			attack_flag=false;
			dead_unit_flag=false;
			if(bulldozer.pos_x==((Unit)UNIT).pos_x && bulldozer.pos_y==((Unit)UNIT).pos_y && bulldozer.health>0 && (((Unit)UNIT).type==3 || ((Unit)UNIT).type==4))
			{bulldozer.attack(UNIT,mapa); navi_counter--;}	//jesli buldozer najechal na dana jednostke Navi to ja zaatakuje (zabije)
			else
			{
				UNIT.find_enemy(enemy,mapa);
				if(dead_unit_flag)
				{
					if(((Unit)UNIT).type==3 || ((Unit)UNIT).type==4) 	{colonizators_counter--;}
					else 												{navi_counter--;}
				}
				if(attack_flag==false) {UNIT.move(mapa);} //wykonanie metody move dla pozostalych jednostek (pod warunkiem ze nie wykonaly ataku)
			}} 						
		}
	}
}
