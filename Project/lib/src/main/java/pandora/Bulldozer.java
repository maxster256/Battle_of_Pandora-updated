package pandora;

public class Bulldozer extends Unit{
	
	public Bulldozer(int pos_x, int pos_y)
	{
		super(pos_x,pos_y);
		type=0;
		health=2000;
		speed=0.25;
		defense_bonus=0.85;
	}
}
