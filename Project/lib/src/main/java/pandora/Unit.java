package pandora;

import java.util.ArrayList;
import java.util.Random;

public class Unit implements Interface{
	
	int type;				//typ (rodzaj) jednostki
	int health;
	double speed;
	int pos_x;
	int pos_y;
	int strength;
	double strength_bonus;
	double defense_bonus;
	boolean can_far_attack;
	int view_range;			//zasieg ataku
	
	double moves;			//liczba ruchow w metodzie move
	
	Random random = new Random();
	
	public boolean number_of_moves(double move)
	{
		if(moves>1) {return true;}
		if(random.nextFloat(1)<move) {return true;} //jesli predkosc jest wyrazona jako liczba niecalkowita to instrukcja if wylosuje czy ma wykonac dodatkowy ruch gdy liczba ruchow spadnie ponizej 1
		return false;
	}
	@Override
	public void move(Map mapa)
	{
		int direction;
		for(moves=speed;moves>0;moves--)	//petla wykonuje przewidziana liczbe ruchow dla jednostki np: dla speed=2 petla wykona się 2 razy
		{
			// gdy liczba ruchow wyniesie wiecej niz 0 ale mniej niz 1 instrukcja wykona ponizszy krok.
			if(number_of_moves(moves)==false) {break;}
		direction = random.nextInt(4); // GGeneruj losową liczbę między 0-3
		switch(direction)
		{
		case 0: //Poruszaj się w górę
			if (pos_y - 1 >= 0) { // Sprawdź, czy nowa pozycja mieści się w granicach siatki
       			if (mapa.FieldContent(pos_x, pos_y - 1)!='T'||type==0) {pos_y--;} // Sprawdz czy pole jest drzewem. Jesli nie, przejdź na nową pozycję
			}break;
		case 1: // Poruszaj się w dół
			if (pos_y + 1 < mapa.getY()) {
    			if (mapa.FieldContent(pos_x, pos_y + 1)!='T'||type==0) {pos_y++;}
			}break;
		case 2: // Poruszaj się w lewo
			if (pos_x - 1 >= 0) {
      			if (mapa.FieldContent(pos_x - 1, pos_y)!='T'||type==0) {pos_x--;}
			}break;
		case 3: // Poruszaj się w prawo
			if (pos_x + 1 < mapa.getX()) {
       			if (mapa.FieldContent(pos_x + 1, pos_y)!='T'||type==0) {pos_x++;}
			}break;
		}
		if (type==0 && mapa.FieldContent(pos_x, pos_y)!='_') {mapa.change_map(pos_x,pos_y);} // Sprawdź, czy pole jest puste i zniszcz pole, jeśli nie jest puste
		}
	}
	@Override
	public void attack(Interface enemy, Map mapa)
	{
		if(random.nextFloat(1)>((Unit)enemy).defense_bonus)	//losowanie szansy na obrone ataku
		{
			if(mapa.FieldContent(pos_x,pos_y)=='_') {((Unit)enemy).health-=strength;}	//atak z pustego pola
			else {((Unit)enemy).health-=strength*strength_bonus;}						//atak z krzakow
		}
	}
	public boolean block_attack(Interface enemy, Map mapa)
	{
		int i;	//zmienna do okreslenia kierunku ataku
		//ponizsze instrukcje if zakladaja ze drzewo jest w stanie zablokowac atak tylko w poziomie lub pionie
		if(((Unit)enemy).pos_x-pos_x==0)
		{
			if(((Unit)enemy).pos_y-pos_y>0) {i=1;}
			else 							{i=-1;}
			if(mapa.FieldContent(pos_x,pos_y+i)=='T'||mapa.FieldContent(pos_x,pos_y+2*i)=='T') {return true;}
			else if ((type==3||type==4) && pos_y+4*i>0 && pos_y+4*i<mapa.getY())
			{if(mapa.FieldContent(pos_x,pos_y+3*i)=='T'||mapa.FieldContent(pos_x,pos_y+4*i)=='T') {return true;}}
		}
		else if (((Unit)enemy).pos_y-pos_y==0)
		{
			if(((Unit)enemy).pos_x-pos_x>0) {i=1;}
			else 							{i=-1;}
			if(mapa.FieldContent(pos_x+i,pos_y)=='T'||mapa.FieldContent(pos_x+2*i,pos_y)=='T') {return true;}
			else if ((type==3||type==4) && pos_x+4*i>0 && pos_x+4*i<mapa.getX())
			{if(mapa.FieldContent(pos_x+3*i,pos_y)=='T'||mapa.FieldContent(pos_x+4*i,pos_y)=='T') {return true;}}
		}
		return false;
	}
	@Override
	public void far_attack(Interface enemy, Map mapa)
	{
		boolean attack_blocked=block_attack(enemy,mapa); //flaga do okreslenia czy atak zostal zablokowany przez drzewo
		if(attack_blocked==false)	//jesli atak nie zostal zablokowany wykonac atak
		{
			double defense;
			if(((Unit)enemy).type==3||((Unit)enemy).type==4) 	{defense = ((Unit)enemy).defense_bonus;}
			else 												{defense = ((Unit)enemy).defense_bonus*((Unit)enemy).defense_bonus;}
																//przy dalekim ataku ze strony navi jednostki kolonizatorow maja zmniejszona szanse na obrone
			if(random.nextFloat(1)>defense)		//losowanie szansy na obrone ataku
			{
				if(mapa.FieldContent(pos_x,pos_y)=='_') {((Unit)enemy).health-=strength;}	//atak z pustego pola
				else {((Unit)enemy).health-=strength*strength_bonus*strength_bonus;}		//atak z krzakow
			}
		}
   	}
	@Override
	public void find_enemy(ArrayList<Interface> enemy, Map mapa)
	{
		int X, Y, Xe, Ye, distance;
		X=pos_x;
		Y=pos_y;
		for(Interface ENEMY : enemy) //petla foreach do znalezienia przeciwnika do zaatakowania
		{
			if(((Unit)ENEMY).health>0) {	//uniemozliwienie zaatakowania jednostki juz martwej
			Xe=((Unit)ENEMY).pos_x;
			Ye=((Unit)ENEMY).pos_y;
			distance = Math.abs(X-Xe)+Math.abs(Y-Ye);
			if(distance<=1) {attack(ENEMY,mapa); pandora.Main.attack_flag=true;} //wykonanie ataku jesli wroga jednostka jest w bezposrednim sasiedztwie (do 1 kratki odleglosci)
			else if(distance<=view_range && can_far_attack) {far_attack(ENEMY,mapa); pandora.Main.attack_flag=true;} //wykonanie ataku jesli wroga jednostka jest w odleglosci do 3 (lub 5) kratek
			if(((Unit)ENEMY).health<=0)
			{pandora.Main.dead_unit_flag=true; ((Unit)ENEMY).pos_x=mapa.getX()+10; ((Unit)ENEMY).pos_y=mapa.getY()+10;}
			if(pandora.Main.attack_flag==true) {break;}} //jesli atak zostal wykonany to zakonczyc szukanie przeciwnika
		}
	}
	
	public Unit(int pos_x, int pos_y)
	{
		this.pos_x=pos_x;
		this.pos_y=pos_y;
	}
}
