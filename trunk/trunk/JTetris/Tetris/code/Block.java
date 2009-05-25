package code;

import java.awt.Color;

/**More concrete representation of a block.*/
public class Block implements Cloneable
{
	public static final Color[] colors = {
		
		new Color(229,46,56),//red
		new Color(137,129,0),//tan
		new Color(31,54,149),//dark-blue
		new Color(196,93,0),//orange
		new Color(101,39,142),//purple
		new Color(13,113,43),//green
		new Color(97,97,97)//gray
	};
	
	/**Color of an empty block.*/
	public static final Color emptycolor =
		new Color(120,120,190,90);
	
	/**State of the block.*/
	private volatile BlockState state = BlockState.EMPTY;
	
	/**Color of the block.*/
	private volatile Color color = emptycolor;
	
	/**Null constructor.*/
	public Block(){}
	
	/**Initializing constructor.*/
	public Block(BlockState s){state=s;}
	
	/**String representation of this object.*/
	public String toString(){
		return color.toString();
	}
	
	/**Compares the state for equality.*/
	public boolean equals(Object o)
	{
		if(!(o instanceof Block))return false;
		Block b = (Block)o;
		return b.state==state;
	}
	
	/**Implements the Clonable interface.*/
	public Block clone()
	{
		Block ret = new Block(state);
		ret.setColor(color);
		return ret;
	}
	
	public byte toByte()
	{
		switch(state)
		{
		case EMPTY:
			return 0;
		case FILLED:
			return 2;
		case ACTIVE:
			return 1;
		default:
			return -1;
		}
	}

	public BlockState getState()
	{
		return state;
	}

	public void setState(BlockState state)
	{
		this.state = state;
	}
	
	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}
}
