package io.github.eddie999.minesweepergame.mine;

import java.util.ArrayList;
import java.util.Random;

public class MineMatrix implements java.io.Serializable{
	private static final long serialVersionUID = -2619513574222981696L;

	public class Pos {
		private Integer x;
		private Integer y;
		public Pos(Integer x, Integer y) {
			this.x = x;
			this.y = y;
		}		
		public Integer getX() {return x;}
		public Integer getY() {return y;}
		public String toString() {return ("(" + x + ", " + y + ")");}
		public boolean equals(Pos pos) {return ((pos.x==x) && (pos.y==y));}
	}

	private final Integer[][] matrix;
	private final Integer rows;
	private final Integer cols;
	private final Integer mines;
	
	public MineMatrix(Integer rows, Integer cols, Integer mines) {
		this.rows = (rows >= 2)?rows:2;
		this.cols = (cols >= 2)?cols:2;
		this.mines = (mines <= (this.rows*this.cols))?mines:(this.rows*this.cols)/4;
		matrix = new Integer[this.rows][this.cols];
		reset();
	}
	
	public Integer getRows() {return rows;}
	public Integer getCols() {return cols;}
	public Integer getMines() {return mines;}
	public Integer get(Pos pos) {return get(pos.getX(), pos.getY());}
	public Integer get(Integer i, Integer j) {
		if( i<0 || i>=rows || j<0 || j>=cols) return 0;
		return matrix[i][j];
	}
	
	public String toString() {
		String dump = "MineMatrix ("+ rows +"," + cols + ")\n";
		for(int i=0; i<rows; i++) {
			for(int j=0; j<cols; j++) {
				if( matrix[i][j] == 0) dump = dump.concat( "-");
				else if( matrix[i][j] == 9) dump = dump.concat( "X");
				else dump = dump.concat( String.format("%d", matrix[i][j]));
			}
			dump = dump.concat("\n");
		}		
		return dump;
	}
	
	public void reset() {
		for(int i=0; i<rows; i++) {
			for(int j=0; j<cols; j++) {
				matrix[i][j] = 0;
			}
		}
	}
	
	public void populate() {
		int count = 0;
		Random rand = new Random();
		do {
			int range = 6;
			ArrayList<Pos> list = new ArrayList<Pos>(); 
			do {
				list.clear();
				int max = rand.nextInt(range)+(7-range);
				minePositions( list, max);
				if( range <= 1) break;
				range--;
			}while(list.size() <= 0);
			int pos = rand.nextInt(list.size());
			Pos mine = list.get(pos);
			placeMine(mine);
			count++;
		}while(count < mines);
		cleanUp();
		setupCounters();
	}
	
	public void uncoverNullBlocks(Pos pos, Integer[][] flags) {
		flags[pos.getX()][pos.getY()] = 2;
		ArrayList<Pos> list = getAround(pos, flags);
		if(list.isEmpty()) return;
		for(Pos item : list) {
			flags[item.getX()][item.getY()] = 1;
		}
		for(Pos item : list) {
			if(matrix[item.getX()][item.getY()] == 0) uncoverNullBlocks(item, flags);
		}
	}
	
	private ArrayList<Pos> getAround(Pos pos, Integer[][] flags){
		Pos item;
		ArrayList<Pos> list = new ArrayList<>();
		if(pos.getY()>=1) {
			item = new Pos(pos.getX(), pos.getY()-1);
			if(flags[item.getX()][item.getY()] == 0) list.add(item);							
		}
		if(pos.getY()<(cols-1)) {
			item = new Pos(pos.getX(), pos.getY()+1);
			if(flags[item.getX()][item.getY()] == 0) list.add(item);							
		}
		if(pos.getX()>=1) {
			item = new Pos(pos.getX()-1, pos.getY());
			if(flags[item.getX()][item.getY()] == 0) list.add(item);
			if(pos.getY()>=1) {
				item = new Pos(pos.getX()-1, pos.getY()-1);
				if(flags[item.getX()][item.getY()] == 0) list.add(item);
			}
			if(pos.getY()<(cols-1)) {
				item = new Pos(pos.getX()-1, pos.getY()+1);
				if(flags[item.getX()][item.getY()] == 0) list.add(item);				
			}
		}
		if(pos.getX()<(rows-1)) {
			item = new Pos(pos.getX()+1, pos.getY());
			if(flags[item.getX()][item.getY()] == 0) list.add(item);			
			if(pos.getY()>=1) {
				item = new Pos(pos.getX()+1, pos.getY()-1);
				if(flags[item.getX()][item.getY()] == 0) list.add(item);				
			}
			if(pos.getY()<(cols-1)) {
				item = new Pos(pos.getX()+1, pos.getY()+1);
				if(flags[item.getX()][item.getY()] == 0) list.add(item);								
			}
		}
		return list;
	}
	
	private void minePositions( ArrayList<Pos> list, Integer max) {
		for(Integer i=0; i<rows; i++) {
			for(Integer j=0; j<cols; j++) {
				if( matrix[i][j] <= max) {
					list.add(new Pos(i, j));
				}
			}
		}		
	}

	private void cleanUp() {
		for(Integer i=0; i<rows; i++) {
			for(Integer j=0; j<cols; j++) {
				if( matrix[i][j] == 7) {
					matrix[i][j] = 9;
				}else if(matrix[i][j] < 6) {
					matrix[i][j] = 0;					
				}
			}
		}				
	}

	private void placeMine( Pos mine) {
		matrix[mine.getX()][mine.getY()] = 7;
		if( mine.getX() > 0) {
			int val = 6;
			for( int i=mine.getX()-1;i>=0;i--) {
				if( matrix[i][mine.getY()] < val) matrix[i][mine.getY()] = val;
				int min = mine.getY() - (7 - val);
				if( min < 0) min = 0;
				int max = mine.getY() + (7 - val);
				if( max >= cols) max = cols-1;
				for( int j=min; j<=max; j++) {
					if( matrix[i][j]<val) matrix[i][j] = val;
				}
				val--;
				if( val<1) break;
			}
		}
		if( mine.getX() < rows-1) {
			int val = 6;
			for( int i=mine.getX()+1;i<rows;i++) {
				if(matrix[i][mine.getY()] < val) matrix[i][mine.getY()] = val;
				int min = mine.getY() - (7 - val);
				if( min < 0) min = 0;
				int max = mine.getY() + (7 - val);
				if( max >= cols) max = cols-1;
				for( int j=min; j<=max; j++) {
					if( matrix[i][j]<val) matrix[i][j] = val;
				}				
				val--;
				if( val<1) break;
			}
		}

		if( mine.getY() > 0) {
			int val = 6;
			for( int j=mine.getY()-1;j>=0;j--) {
				if(matrix[mine.getX()][j]<val) matrix[mine.getX()][j] = val;
				int min = mine.getX() - (7 - val);
				if( min < 0) min = 0;
				int max = mine.getX() + (7 - val);
				if( max >= rows) max = rows-1;
				for( int i=min; i<=max; i++) {
					if( matrix[i][j]<val) matrix[i][j] = val;
				}
				val--;
				if( val<1) break;
			}
		}
		if( mine.getY() < cols-1) {
			int val = 6;
			for( int j=mine.getY()+1;j<cols;j++) {
				if(matrix[mine.getX()][j]<val) matrix[mine.getX()][j] = val;
				int min = mine.getX() - (7 - val);
				if( min < 0) min = 0;
				int max = mine.getX() + (7 - val);
				if( max >= rows) max = rows-1;
				for( int i=min; i<=max; i++) {
					if( matrix[i][j]<val) matrix[i][j] = val;
				}				
				val--;
				if( val<1) break;
			}
		}
	}
		
	private void setupCounters() {
		for(Integer i=0; i<rows; i++) {
			for(Integer j=0; j<cols; j++) {
				if( matrix[i][j] == 6) {
					matrix[i][j] = countBombs(i,j);
				}
			}
		}						
	}

	private int countBombs( int x, int y) {
		Integer count = 0;
		Integer x_min = x - 1;
		if( x_min < 0) x_min = 0;
		Integer y_min = y - 1;
		if( y_min < 0) y_min = 0;
		Integer x_max = x + 1;
		if( x_max >= rows) x_max = rows-1;
		Integer y_max = y + 1;
		if( y_max >= cols) y_max = cols-1;
		for(Integer i=x_min; i<=x_max; i++) {
			for(Integer j=y_min; j<=y_max; j++) {
				if( matrix[i][j] == 9) count++;
			}			
		}
		
		return count;
	}


}
