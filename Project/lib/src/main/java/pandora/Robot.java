package pandora;

public class Robot extends Unit{
	
	public Robot(int pos_x, int pos_y)
	{
		super(pos_x,pos_y);
		type=2;
		health=200;
		speed=2;
		strength=60;
		strength_bonus=0.6;
		defense_bonus=0.5;
		can_far_attack=true;
		view_range=3;
	}
}
