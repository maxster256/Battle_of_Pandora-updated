package pandora;

public class Soldier extends Unit{

	public Soldier(int pos_x, int pos_y)
	{
		super(pos_x,pos_y);
		type=1;
		health=100;
		speed=1;
		strength=40;
		strength_bonus=0.5;
		defense_bonus=0.2;
		can_far_attack=true;
		view_range=3;
	}
}
