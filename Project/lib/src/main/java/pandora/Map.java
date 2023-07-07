package pandora;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Map {
	
    Main Main = new Main();
	
	private int width;
    private int height;
    private int density; // zagęszczenie roślin w procentach
    private int Trees_numb;
    private int Bushes_numb;
    private char[][] map;
    private int w=1800;				//stale w i z uzyte w celu automatycznego dostosowania +- mapy do wymiarow ekranu (1920x1080)
    private int z=900;
    private int p_w; //szerokosc pojedynczego piksela na mapie
    private int p_h; //wysokosc pojedynczego piksela na mapie
    
    private Image []image = new Image[7];	//tablica typu Image do przechowywania plikow .png
    private JFrame frame = new JFrame();
    private JPanel panel;
    Color color;
    	
    public Map()	{}
	public Map(int width, int height, int plantDensity) {
        this.width = width;
        this.height = height;
        this.density = plantDensity;
        this.map = new char[height][width];
        this.Trees_numb = 0;
        this.Bushes_numb = 0;
        
        p_w=w/width;
        p_h=z/height;
    }
	// metoda generująca mapę
    public void generate() {
        Random rand = new Random();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // losowe umieszczenie roślin na mapie
                if (rand.nextInt(100) < density) {
                    // losowy wybór typu rośliny
                    if (rand.nextInt(5) == 0) {
                        map[row][col] = 'T'; // T-drzewo
                        Trees_numb++;
                    } else {
                        map[row][col] = 'B'; // B-krzak
                        Bushes_numb++;
                    }
                } else {
                    map[row][col] = '_'; // pole puste
                }
            }
        }
    }
    public void images()	//metoda do zaladowania plikow .png
    {
    	this.image[0]=getImage("bull.png");
        this.image[1]=getImage("bush.png");
        this.image[2]=getImage("tree.png");
        this.image[3]=getImage("archer.png");
        this.image[4]=getImage("rider.png");
        this.image[5]=getImage("soldier.png");
        this.image[6]=getImage("robot.png");
    }
    private Image getImage(String file)
    {
        ImageIcon icon = new ImageIcon(file);
        return icon.getImage();
    }
    public void Frame(Interface[] navi,Interface[] col)
    {
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(w+width,z+2*height));	//dostosowywanie szerokosci i wysokosci panelu +- do wymiarow ekranu (1920,1080)
        panel = new JPanel() {							//panel do wyswietlania mapy i jednostek
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                display(g);
                displayUnits(g,navi,col);
            }
        };
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    // metoda wyświetlająca mapę na ekranie
    public void display(Graphics g)
    { 
    	color = new Color(74,190,100); g.setColor(color);	//kolor mapy
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
            	g.fillRect(col*p_w,row*p_h,p_w,p_h);	//wypelnienie danego pola mapy kolorem	
            	switch(map[row][col])
            	{
            	case 'T': g.drawImage(image[2],col*p_w,row*p_h,p_w,p_h,null); break; //wyswietlenie tree.png jesli pole jest drzewem
            	case 'B': g.drawImage(image[1],col*p_w,row*p_h,p_w,p_h,null); break; //wyswietlenie bush.png jesli pole jest krzakiem
            	default: break;
            	}
            }
        }
    }
    public void displayUnits(Graphics g,Interface[] navi,Interface[] col)
    {
    	for(int index=0;index<navi.length;index++)
    	{
    		if(((Unit)navi[index]).health>0)	//unikniecie wyswietlania jednostek juz martwych
    		{
    			switch(((Unit)navi[index]).type)
    			{
    			case 3: g.drawImage(image[4],((Unit)navi[index]).pos_x*p_w,((Unit)navi[index]).pos_y*p_h,p_w,p_h,null); break; //wyswietlenie rider.png na zajmowanej pozycji
    			case 4: g.drawImage(image[3],((Unit)navi[index]).pos_x*p_w,((Unit)navi[index]).pos_y*p_h,p_w,p_h,null); break; //wyswietlenie archer.png na zajmowanej pozycji
    			}
    		}
    	}
    	for(int index=0;index<col.length;index++)
    	{
    		if(((Unit)col[index]).health>0)		//unikniecie wyswietlania jednostek juz martwych
    		{
    			switch(((Unit)col[index]).type)
    			{
    			case 0: g.drawImage(image[0],((Unit)col[index]).pos_x*p_w,((Unit)col[index]).pos_y*p_h,p_w,p_h,null); break; //wyswietlenie bull.png na zajmowanej pozycji
    			case 1: g.drawImage(image[5],((Unit)col[index]).pos_x*p_w,((Unit)col[index]).pos_y*p_h,p_w,p_h,null); break; //wyswietlenie soldier.png na zajmowanej pozycji
    			case 2: g.drawImage(image[6],((Unit)col[index]).pos_x*p_w,((Unit)col[index]).pos_y*p_h,p_w,p_h,null); break; //wyswietlenie robot.png na zajmowanej pozycji
    			}
    		}
    	}
    }
    
    // metoda zwracająca wartośc pola
    public char FieldContent(int x, int y)	{return map[y][x];}
    // metoda zwracająca liczbę T-drzew na mapie
    public int getTrees_numb() 				{return Trees_numb;}
    // metoda zwracająca liczbę B-krzaków na mapie
    public int getBushes_numb()				{return Bushes_numb;}
    // metoda zwracająca dlugosc mapy
    public int getX() 						{return width;}
    // metoda zwracająca szerokosc mapy
	public int getY() 						{return height;}
	// metoda zamieniajaca zawartosc danego pola mapy
    public void change_map(int x, int y)
    {
    	switch(map[y][x])
    	{
    		case 'T': Trees_numb--; break;
    		case 'B': Bushes_numb--; break;
    	}
    	map[y][x] = '_'; // Zamień zawartość pola na '_'
    }
}
