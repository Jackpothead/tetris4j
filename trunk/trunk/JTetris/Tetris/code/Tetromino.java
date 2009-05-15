package code;

import java.awt.Color;

/**Object representation of a tetromino.*/
public class Tetromino
{
	/**Constructor.*/
	public Tetromino(){}
	
	
	/**Contents (Block array)*/
	public Block[][] array;
	
	
	/**Position, rotation, type, etc*/
	public int x, y, rot, type;
	
	
	/**Color.*/
	public Color color;
}