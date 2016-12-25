package gameAI.rubiks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class RubikImageParser extends JPanel implements MouseMotionListener, MouseListener
{
	double[][][] histogram = new double[256][256][256];

	JFrame frame;
	
	int slice;
	
	int downX;
	int downY;
	int downSlice;
	
	ArrayList<Rectangle> clickables = new ArrayList<Rectangle>(); 
	
	public RubikImageParser()
	{
	}
	
	public void setup()
	{
		frame = new JFrame();
		frame.setSize(600,600);
		frame.setContentPane(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		frame.setVisible(true);
	}
	
	public void parseImage(BufferedImage img)
	{
		for(int i=0 ; i<(1<<24) ; i++)
		{
			int c = i;
			int b = (c)&255;
			int g = (c>>8)&255;
			int r = (c>>16)&255;
			histogram[r][g][b] = 0;
		}
		for(int x=0 ; x<img.getWidth() ; x++)
		{
			for(int y=0 ; y<img.getHeight() ; y++)
			{
				int c = img.getRGB(x, y);
				
				float[] hsb = new float[3];
				
				int b = (c)&255;
				int g = (c>>8)&255;
				int r = (c>>16)&255;

				Color.RGBtoHSB(r, g, b, hsb);

//				int p2 = (int)(hsb[0]*255);
//				int p1 = (int)(hsb[1]*255);
//				int p3 = (int)(hsb[2]*255);
				
				int p1 = r;
				int p2 = g;
				int p3 = b;
				
				histogram[p1][p2][p3] ++;
			}
		}
		System.out.println("Histogram loaded");
		repaint();
	}
	
	int transfer(double hist)
	{
		return (int) (255 * (Math.tanh(hist/4-1) / 2 + 0.5) );
	}
	
	public void paintComponent(Graphics gr)
	{
		float w = getWidth();
		float h = getHeight();
		
		for(int x=0 ; x<w ; x++)
		{
			for(int y=0 ; y<h ; y++)
			{
				int g = (int)(255 * x / w);
				int b = (int)(255 * (h-y) / h);
				int t = transfer(histogram[slice][g][b]);
				try {
					gr.setColor(new Color(t,t,t));
				} catch(Exception ex) {
					System.out.println("er"+t);
				}
				gr.drawLine(x, y, x, y);
			}
		}
		gr.setColor(Color.WHITE);
		gr.drawLine(0, (int)(h - (slice*h/255)), (int)w, (int)(h- (slice*h/255)));
	}

	public void mouseDragged(MouseEvent e) 
	{
		slice = Math.max(0, Math.min(255, downSlice - (e.getY() - downY)/2));
//		System.out.println(slice);
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(MouseEvent e) 
	{
		
		
	}

	public void mousePressed(MouseEvent e) 
	{
		downX = e.getX();
		downY = e.getY();
		downSlice = slice;
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
