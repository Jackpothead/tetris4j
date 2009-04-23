package code;

import java.awt.Color;

/**More concrete representation of a block.*/
public class Block implements Cloneable
{
	/**State of the block.*/
	private BlockState state = BlockState.EMPTY;
	
	/**Color of the block.*/
	private Color color = null;
	
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
	
	public Block clone()
	{
		Block ret = new Block(state);
		ret.color = color;
		return ret;
	}

	public BlockState getState()
	{
		return state;
	}

	public void setState(BlockState state)
	{
		this.state = state;
	}
}
