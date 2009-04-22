package tetris.core;

import java.awt.Color;

/**More concrete representation of a block.*/
public class Block
{
	/**State of the block.*/
	public BlockState state = null;
	
	/**Color of the block.*/
	public Color color = null;
	
	/**Null constructor.*/
	public Block(){}
	
	/**Initializing constructor.*/
	public Block(BlockState s){state=s;}
}
