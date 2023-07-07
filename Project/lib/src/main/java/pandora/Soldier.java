package pandora;

public class Soldier extends Unit{
	
	public Soldier(int type,int health, double speed, int pos_x, int pos_y, int strength,double strength_bonus,double defense_bonus, boolean can_far_attack,int view_range)
	{
		super(type,health,speed,pos_x,pos_y,strength,strength_bonus,defense_bonus,can_far_attack,view_range);
	}
}