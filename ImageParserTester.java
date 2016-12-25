package gameAI.rubiks;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ImageParserTester
{

	public static void main(String[] args)
	{
		try
		{
			
			RubikImageParser p = new RubikImageParser();
			
			p.setup();
			p.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			BufferedImage img = ImageIO.read(new File("/tmp/derp.jpg"));
			
			p.parseImage(img);
		}
		catch(Exception ex)
		{
			
		}
	}
}
