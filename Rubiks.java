package gameAI.rubiks;

import java.util.ArrayList;
import java.util.Scanner;

import performance.LongIntegerMap;
import performance.LongIntegerSet;

// start time 9:45am Dec24
public class Rubiks 
{
	Position position;
	char[] colors;
	
	Position solved;

	String[] bestinstr = new String[40];
	int bestlen = 20;
	long bestPack = -1;
	
	LongIntegerMap solvable;
	int solvableMax;
	
	
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		System.out.println("[yellow green]");
		System.out.println("[blue   white]");
		System.out.println("type: \"ygbw\"\n");
		
		System.out.print("front: ");
		String f = in.nextLine();
		System.out.print("left: ");
		String l = in.nextLine();
		System.out.print("right: ");
		String r = in.nextLine();
		System.out.print("top: ");
		String t = in.nextLine();
		System.out.print("bottom: ");
		String b = in.nextLine();
		
//		String f = "bbwo";
//		String l = "oygg";
//		String r = "wowb";
//		String t = "wgrr";
//		String b = "rgry";
		
//		System.out.println();
//		
//		System.out.println(f);
//		System.out.println(l);
//		System.out.println(r);
//		System.out.println(t);
//		System.out.println(b);

//		System.out.printf("   %s\n", t.substring(0, 2));
//		System.out.printf("   %s\n", t.substring(2, 4));
//		System.out.printf("%s %s %s\n", l.substring(0, 2), f.substring(0, 2), r.substring(0, 2));
//		System.out.printf("%s %s %s\n", l.substring(2, 4), f.substring(2, 4), r.substring(2, 4));
//		System.out.printf("   %s\n", b.substring(0, 2));
//		System.out.printf("   %s\n", b.substring(2, 4));
		
		System.out.println();
		
		in.close();
		
		Rubiks rub = new Rubiks(f,l,r,t,b);
		
		rub.prep();
		
		rub.solve();
	}
	
	public Rubiks(String f, String l, String r, String t, String b)
	{	
		char x0 = l.charAt(3);
		char y0 = f.charAt(2);
		char z0 = b.charAt(0);
		char x1 = 255;
		char y1 = 255;
		char z1 = 255;
		
		ArrayList<Character> fset = new ArrayList<Character>();
		for(String s : new String[]{f,l,r,t,b})
			for(char c : s.toCharArray())
				if(!fset.contains(c))
					fset.add(c);
//		System.out.println(fset);
		
		if(fset.size() < 5 || fset.size() > 6)
		{
			System.out.println("You done goofed up. Not 6 colors:");
			System.out.println(fset);
			return;
		}
		else if(fset.size() == 5)
		{
			fset.add('*'); // last color all on back, unseen
		}
		
		char[][] pairs = new char[8][];
		pairs[1] = new char[]{f.charAt(0),l.charAt(1),t.charAt(2)};
		pairs[6] = new char[]{f.charAt(1),r.charAt(0),t.charAt(3)};
		pairs[0] = new char[]{f.charAt(2),l.charAt(3),b.charAt(0)};
		pairs[7] = new char[]{f.charAt(3),r.charAt(2),b.charAt(1)};
		pairs[2] = new char[]{l.charAt(0),t.charAt(0),'?'};
		pairs[5] = new char[]{t.charAt(1),r.charAt(1),'?'};
		pairs[3] = new char[]{b.charAt(2),l.charAt(2),'?'};
		pairs[4] = new char[]{r.charAt(3),b.charAt(3),'?'};
		
		int[][] xyz = new int[8][];
		xyz[0] = new int[]{1,0,2};
		xyz[1] = new int[]{1,0,2};
		xyz[2] = new int[]{0,2,1};
		xyz[3] = new int[]{1,2,0};
		xyz[4] = new int[]{0,2,1};
		xyz[5] = new int[]{1,2,0};
		xyz[6] = new int[]{1,0,2};
		xyz[7] = new int[]{1,0,2};
		
//		for(int i=0 ; i<8 ; i++)
//		{
//			System.out.println(new String(pairs[i]));
//		}

		ArrayList<Character> pset = (ArrayList<Character>) fset.clone();
		pset.remove((Character)x0);
		pset.remove((Character)y0);
		pset.remove((Character)z0);
		
		ArrayList<Character> px1 = (ArrayList<Character>) pset.clone();
		ArrayList<Character> py1 = (ArrayList<Character>) pset.clone();
		ArrayList<Character> pz1 = pset;
		for(int i=0 ; i<8 ; i++)
		{
			if(x0 == pairs[i][0] || x0 == pairs[i][1] || x0 == pairs[i][2])
			{
				px1.remove((Character)pairs[i][0]);
				px1.remove((Character)pairs[i][1]);
				px1.remove((Character)pairs[i][2]);
			}
			if(y0 == pairs[i][0] || y0 == pairs[i][1] || y0 == pairs[i][2])
			{
				py1.remove((Character)pairs[i][0]);
				py1.remove((Character)pairs[i][1]);
				py1.remove((Character)pairs[i][2]);
			}
			if(z0 == pairs[i][0] || z0 == pairs[i][1] || z0 == pairs[i][2])
			{
				pz1.remove((Character)pairs[i][0]);
				pz1.remove((Character)pairs[i][1]);
				pz1.remove((Character)pairs[i][2]);
			}
		}

		for(int i=0;i<3;i++)
		{
			if(px1.size() == 1)
			{
				x1 = px1.get(0);
				py1.remove(px1.get(0));
				pz1.remove(px1.get(0));
			}
			if(py1.size() == 1)
			{
				y1 = py1.get(0);
				pz1.remove(py1.get(0));
				px1.remove(py1.get(0));
			}
			if(pz1.size() == 1)
			{
				z1 = pz1.get(0);
				px1.remove(pz1.get(0));
				py1.remove(pz1.get(0));
			}
		}
		
		if(x1==255||y1==255||z1==255)
		{
			System.out.println("ERROR, opposites not found");
			System.out.println(px1);
			System.out.println(py1);
			System.out.println(pz1);
			return;
		}
		
//		System.out.println(x0+" "+x1);
//		System.out.println(y0+" "+y1);
//		System.out.println(z0+" "+z1);

		for(int i=2 ; i<6 ; i++)
		{
			int[] x={0,0},y={0,0},z={0,0};
			for(int j=0;j<2;j++)
			{
				if(x0 == pairs[i][j])
					x[j] = -1;
				if(y0 == pairs[i][j])
					y[j] = -1;
				if(z0 == pairs[i][j])
					z[j] = -1;
				if(x1 == pairs[i][j])
					x[j] = 1;
				if(y1 == pairs[i][j])
					y[j] = 1;
				if(z1 == pairs[i][j])
					z[j] = 1;
			}
			int cx = y[0]*z[1] - z[0]*y[1];
			int cy = z[0]*x[1] - x[0]*z[1];
			int cz = x[0]*y[1] - y[0]*x[1];
			if(cx < 0)
				pairs[i][2] = x0;
			if(cx > 0)
				pairs[i][2] = x1;
			if(cy < 0)
				pairs[i][2] = y0;
			if(cy > 0)
				pairs[i][2] = y1;
			if(cz < 0)
				pairs[i][2] = z0;
			if(cz > 0)
				pairs[i][2] = z1;
//			System.out.println("back "+pairs[i][2]);
		}
		
		colors = new char[]{x0,y0,z0,x1,y1,z1};
		
		int[] pla = new int[8];
		int[] ori = new int[8];
		
		for(int i=0 ; i<8 ; i++)
		{
			int x=0,y=0,z=0;
			if(x1 == pairs[i][0] || x1 == pairs[i][1] || x1 == pairs[i][2])
				x = 1;
			if(y1 == pairs[i][0] || y1 == pairs[i][1] || y1 == pairs[i][2])
				y = 1;
			if(z1 == pairs[i][0] || z1 == pairs[i][1] || z1 == pairs[i][2])
				z = 1;
			
			int[] xyzp = new int[]{pairs[i][xyz[i][0]],pairs[i][xyz[i][1]],pairs[i][xyz[i][2]]};
			for(int j=0 ; j<3 ; j++)
				if(x0 == xyzp[j] || x1 == xyzp[j])
					ori[i] = j;
			
			pla[i] = Position.place(x, y, z);
//			System.out.println(new String(pairs[i])+" "+pla[i]+" "+ori[i]);
		}
		
		Position p = new Position(pla, ori);
		p.print(colors);
		position = p;
	}
	
	public void prep()
	{
		solved = new Position(new int[]{0,1,2,3,4,5,6,7},new int[]{0,0,0,0,0,0,0,0});
		
		LongIntegerMap fullSolved = new LongIntegerMap();
		fullSolved.setVal(solved.pack(), 12);
		
		long tick = System.currentTimeMillis();
		
		Position p = new Position();
		p.unpack(solved.pack());
		
		solvable = new LongIntegerMap();
		solvableMax = 5;
		
		recurse(p, solvable, fullSolved, 0, solvableMax, null, -1);
		
		System.out.println(solvable.length());
		
		long tock = System.currentTimeMillis();
		System.out.println((tock-tick)/1000f+" seonds");
	}
	
	public void solve()
	{
		long tick = System.currentTimeMillis();
		
		LongIntegerMap visit = new LongIntegerMap();
		
		LongIntegerMap fullSolved = new LongIntegerMap();
		fullSolved.setVal(solved.pack(), 12);
		
		String[] instr = new String[40];
		
		bestlen = 12;
		
		recurse(position, visit, solvable, 0, 11-solvableMax, instr,-1);

		if(bestPack < 0)
		{
			System.out.println("No Solution Found.");
		}
		else
		{
			visit.deleteAll();
			position.unpack(bestPack);
			int start = bestlen;
			for(int i=0 ; i<bestlen ; i++)
				instr[i] = bestinstr[i];
			
			System.out.println("Working from: "+bestPack);
			
			bestlen = 12;
			
			recurse(position, visit, fullSolved, start, 12, instr,-1);
			
			System.out.println(bestlen+" Moves:");
			for(int i=0 ; i<bestlen ; i++)
				System.out.println(bestinstr[i]);
		}
		
		long tock = System.currentTimeMillis();
		System.out.println((tock-tick)/1000f+" seonds");
	}
	
	private String[][] mvinstr = {
			{},{},{},
			{"Right Clockwise","Right 180","Right Anti-Clockwise"},
			{"Back Clockwise","Back 180","Back Anti-Clockwise"},
			{"Top Clockwise","Top 180","Top Anti-Clockwise"}
	};
	
	public void recurse(Position p, LongIntegerMap visited, LongIntegerMap goal, int depth, int maxdepth, String[] instr, int lastmove)
	{
		int vis = (int) visited.getVal(p.pack());
		if(depth >= vis && vis > 0)
		{
			return;
		}
		visited.setVal(p.pack(), depth);
		
		if(instr != null && goal.getVal(p.pack()) >= 0)
		{
			if(depth < bestlen)
			{
				System.out.println("SOLVED! "+depth);
				for(int i=0;i<depth;i++)
					bestinstr[i] = instr[i];
				bestlen = depth;
				bestPack = p.pack();
			}
		}
		if(depth >= maxdepth)
		{
			return;
		}
		
		for(int f=3;f<6;f++)
		{
			if(f==lastmove)
				continue;
			for(int i=0;i<3;i++)
			{
				p.cw(f);
				if(instr != null) instr[depth] = mvinstr[f][i];
				recurse(p, visited, goal, depth+1, maxdepth, instr, f);
			}
			p.cw(f);
			if(instr != null) instr[depth] = null;
		}
	}
}
