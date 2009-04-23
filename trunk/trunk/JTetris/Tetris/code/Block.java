package code;

import java.awt.Color;

/**More concrete representation of a block.*/
public class Block
{
	/**State of the block.*/
	public volatile BlockState state = BlockState.EMPTY;
	
	/**Color of the block.*/
	public volatile Color color = null;
	
	/**Null constructor.*/
	public Block(){}
	
	/**Initializing constructor.*/
	public Block(BlockState s){state=s;}
	
	/**String representation of this object.*/
	public String toString(){
		switch(state)
		{
		case EMPTY:
			return "0";
		case FILLED:
			return "1";
		case ACTIVE:
			return "2";
		default:
			return "-1";
		}
	}
	
	/**Compares the state for equality.*/
	public boolean equals(Object o)
	{
		if(!(o instanceof Block))return false;
		Block b = (Block)o;
		return b.state==state;
	}
}
