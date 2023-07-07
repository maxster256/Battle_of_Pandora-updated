package pandora;

public class Rider extends Unit{
	
	public Rider(int pos_x, int pos_y)
	{
		super(pos_x,pos_y);
		type=3;
		health=100;
		speed=2;
		strength=60;
		strength_bonus=2;
		defense_bonus=0.3;
	}
}
