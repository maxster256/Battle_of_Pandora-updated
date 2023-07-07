package pandora;

public class Archer extends Unit{
	
	public Archer(int pos_x, int pos_y)
	{
		super(pos_x,pos_y);
		type=4;
		health=100;
		speed=1;
		strength=40;
		strength_bonus=1.7;
		defense_bonus=0.25;
		can_far_attack=true;
		view_range=5;
	}
}
