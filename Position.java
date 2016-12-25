package gameAI.rubiks;

public class Position 
{
	// fixed slots, describe content
	private int[] pla; // val 0-7
	private int[] ori; // val 0-2
	private long pack = -1;
	
	private static int[] plaxyz = new int[]{0,1,3,2,6,7,5,4};
	private static int[] xyzpla = new int[]{0,1,3,2,7,6,4,5};
	
	//pla xyz 000 001 011 010 110 111 101 100 

	//ori (x=0) 0 = (xcol = xdir), 1 = cw, 2 = ccw
	
	
	//fac 0 = x0, 1 = y0, 2 = z0, 3 = x1, 4 = y1, 5 = z1
	
	public Position()
	{
		pla = new int[]{0,1,2,3,4,5,6,7};
		ori = new int[]{0,0,0,0,0,0,0,0};
	}
	
	public Position(int[] pla, int[] ori)
	{
		this.pla = pla;
		this.ori = ori;
	}
	
	public static int place(int x, int y, int z)
	{
		return xyzpla[(x<<2)|(y<<1)|(z)];
	}
	
	public long pack()
	{
		if(pack == -1)
		{
			pack = 0;
			for(int i=0;i<8;i++)
				pack=(pack<<5)|(pla[i]<<2)|(ori[i]);
		}
		return pack;
	}
	
	public void unpack(long pack)
	{
		for(int i=7;i>=0;i--)
		{
			ori[i] = (int)(pack&3);
			pack >>= 2;
			pla[i] = (int)(pack&7);
			pack >>= 3;
		}
		this.pack = pack;
	}
	
	public void print(char[] colors)
	{
//		for(int i=0;i<8;i++)
//			System.out.println(pla[i]+" "+ori[i]);

		char[][] map = new char[][]{
				{colors[0],colors[1],colors[2]},
				{colors[0],colors[5],colors[1]},
				{colors[0],colors[4],colors[5]},
				{colors[0],colors[2],colors[4]},
				{colors[3],colors[4],colors[2]},
				{colors[3],colors[5],colors[4]},
				{colors[3],colors[1],colors[5]},
				{colors[3],colors[2],colors[1]},
		};
		
		char[][] project = new char[8][3];
		for(int i=0 ; i<8 ; i++)
		{
			int dir = ((i%2)*-2)+1;
			project[i][ori[i]] = map[pla[i]][0];
			project[i][(ori[i]+3+dir)%3] = map[pla[i]][1];
			project[i][(ori[i]+3+2*dir)%3] = map[pla[i]][2];
		}

		System.out.printf("     %s %s\n", project[2][2],project[5][2]);
		System.out.printf("     %s %s\n", project[1][2],project[6][2]);
		System.out.printf("%s %s  %s %s  %s %s  %s %s\n",
				project[2][0],project[1][0],
				project[1][1],project[6][1],
				project[6][0],project[5][0],
				project[5][1],project[2][1]
				);
		System.out.printf("%s %s  %s %s  %s %s  %s %s\n",
				project[3][0],project[0][0],
				project[0][1],project[7][1],
				project[7][0],project[4][0],
				project[4][1],project[3][1]);
		System.out.printf("     %s %s\n", project[0][2],project[7][2]);
		System.out.printf("     %s %s\n\n", project[3][2],project[4][2]);
	}
	
	//pla xyz 000 001 011 010 110 111 101 100 
	//ori 0 = (wh/ye = x), 1 = (wh/ye = y), 2 = (wh/ye = z)
	//fac 0 = x0, 1 = y0, 2 = z0, 3 = x1, 4 = y1, 5 = z1

	public void cw(int face)
	{
		pack = -1;
		int[][] affected = new int[][]{
				{0,1,2,3},
				{7,6,1,0},
				{0,3,4,7},
				{4,5,6,7},
				{5,4,3,2},
				{6,5,2,1}
		};
		int[][] rot = new int[][]{
				{0,2,1},
				{2,1,0},
				{1,0,2},
				{0,2,1},
				{2,1,0},
				{1,0,2}
		};
		
		int tmp = pla[affected[face][0]];
		pla[affected[face][0]] = pla[affected[face][1]];
		pla[affected[face][1]] = pla[affected[face][2]];
		pla[affected[face][2]] = pla[affected[face][3]];
		pla[affected[face][3]] = tmp;
		
		tmp = rot[face][ori[affected[face][0]]];
		ori[affected[face][0]] = rot[face][ori[affected[face][1]]];
		ori[affected[face][1]] = rot[face][ori[affected[face][2]]];
		ori[affected[face][2]] = rot[face][ori[affected[face][3]]];
		ori[affected[face][3]] = tmp;
	}
	public void ccw(int face)
	{
		pack = -1;
		int[][] affected = new int[][]{
				{0,1,2,3},
				{7,6,1,0},
				{0,3,4,7},
				{4,5,6,7},
				{5,4,3,2},
				{6,5,2,1}
		};
		int[][] rot = new int[][]{
				{0,2,1},
				{2,1,0},
				{1,0,2},
				{0,2,1},
				{2,1,0},
				{1,0,2}
		};
		
		int tmp = pla[affected[face][3]];
		pla[affected[face][3]] = pla[affected[face][2]];
		pla[affected[face][2]] = pla[affected[face][1]];
		pla[affected[face][1]] = pla[affected[face][0]];
		pla[affected[face][0]] = tmp;
		
		tmp = rot[face][ori[affected[face][3]]];
		ori[affected[face][3]] = rot[face][ori[affected[face][2]]];
		ori[affected[face][2]] = rot[face][ori[affected[face][1]]];
		ori[affected[face][1]] = rot[face][ori[affected[face][0]]];
		ori[affected[face][0]] = tmp;
	}
}
