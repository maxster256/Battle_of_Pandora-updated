package pandora;

import java.util.Random;

public class Bulldozer extends Unit{
	
	public void moveBulldozer(Map mapa)
	{
		Random random = new Random();
		int direction;
		moves = speed;
		for(;moves>0;moves--)	//petla wykonuje przewidziana liczbe ruchow dla jednostki np: dla speed=2 petla wykona się 2 razy
		{
			if(number_of_moves(moves)==false) {break;}
		direction = random.nextInt(4); // GGeneruj losową liczbę między 0-3
		switch(direction)
		{
		case 0: // Poruszaj się w gore
			if (pos_y - 1 >= 0)			 {pos_y--;} break;// Sprawdź, czy nowa pozycja mieści się w granicach siatki. Jesli tak to przejdź na nową pozycję
		case 1: // Poruszaj się w dol
			if (pos_y + 1 < mapa.getY()) {pos_y++;} break;
		case 2: // Poruszaj się w lewo
			if (pos_x - 1 >= 0) 		 {pos_x--;} break;
		case 3: // Poruszaj się w prawo
			if (pos_x + 1 < mapa.getX()) {pos_x++;} break;
		}
		if (mapa.FieldContent(pos_x, pos_y)!='_') {mapa.change_map(pos_x,pos_y);} // Sprawdź, czy pole jest puste i zniszcz pole, jeśli nie jest puste
		}
	}
	public Bulldozer(int type,int health, double speed, int pos_x, int pos_y, int strength,double strength_bonus,double defense_bonus, boolean can_far_attack,int view_range)
	{
		super(type,health,speed,pos_x,pos_y,strength,strength_bonus,defense_bonus,can_far_attack,view_range);
	}
}