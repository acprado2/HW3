import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class SockMatching 
{
	public static void main( String args[] )
	{
		// Create the threads
		SockWasher washer = new SockWasher(); // Washer 
		SockMatcher matcher = new SockMatcher( washer ); // Matcher
		
		SockGenerator generatorRed = new SockGenerator( "Red", matcher ); // Generators
		SockGenerator generatorGreen = new SockGenerator( "Green", matcher );
		SockGenerator generatorBlue = new SockGenerator( "Blue", matcher );
		SockGenerator generatorOrange = new SockGenerator( "Orange", matcher );
		
		// Get the sock total for the matcher
		int sockTotal = generatorRed.GetCount() + generatorGreen.GetCount() + generatorBlue.GetCount() + generatorOrange.GetCount();
		matcher.setSockTotal( sockTotal );
		
		// Run the threads
		generatorRed.start();
		generatorGreen.start();
		generatorBlue.start();
		generatorOrange.start();
		matcher.start();
		washer.start();
		
		// Don't kill the process until the washer is done
		while ( true )
		{
			// Has the washer finished?
			if ( !washer.isAlive() )
				break;
		}
		
		System.out.println( "Job done" );
		return;
	}
}

class SockGenerator extends Thread
{
	private String color;
	private int count;
	private ArrayList<Sock> list;
	private SockMatcher matchingThread;
	
	public SockGenerator( String color, SockMatcher matchingThread )
	{		
		// Set the color
		this.color = color;
		
		// Generate a random number of socks
		this.count = new Random().nextInt( 100 ) + 1;
		
		this.matchingThread = matchingThread;
	}
	
	public void run() 
	{
		// Add socks to the list
		this.list = new ArrayList<>();
		for ( int i = 0; i < count; i++ )
		{
			// Create the sock
			Sock sock = new Sock( color, this );
			list.add( sock );			
			System.out.println( color + " Sock: Produced " + ( i + 1 ) + " of " + count + " " + color + " Socks" );
			
			matchingThread.addToQueue( sock );
		}
		
		// Don't die until our list is empty
		while ( true )
		{
			if ( count == 0 )
				break;
			
			// Sleep a bit
			try
			{
				sleep( 5 );
			}
			catch ( InterruptedException e )
			{
				System.out.println( "Thread interrupted! Terminating..." );
			}
		}
		
		// Let the matcher know we're done
		matchingThread.incrementThreadsFinished();
	}
	
	public int GetCount()
	{
		return count;
	}
	
	public void Remove( Sock sock )
	{
		if ( list.contains( sock ) )
			list.remove( sock );
		
		count--;
	}
}

class SockMatcher extends Thread
{
	private LinkedList<Sock> sockQueue;
	private SockWasher washerThread;
	private int threadsFinished;
	private int sockTotal;
	
	public SockMatcher( SockWasher washerThread )
	{
		this.washerThread = washerThread;
		
		this.sockQueue = new LinkedList<>();
		this.threadsFinished = 0;
		this.sockTotal = 0;
	}
	
	public void run() 
	{
		int curPos = 0;
		Sock sockToPair = null;
		
		// Don't kill the matcher until the generators are done
		while ( threadsFinished < 4 )
		{			
			if ( curPos < sockQueue.size() )
			{
				if ( sockToPair == null )
				{
					sockToPair = sockQueue.get( curPos );
				}
				else
				{
					Sock s = sockQueue.get( curPos );
					
					// Check if the sock color match
					if ( sockToPair.GetColor().equals( s.GetColor() ) )
					{
						// Don't continue until the washer is empty
						while ( washerThread.isWasherEmpty() == false )
						{
							try 
							{
								// Sleep for a bit
								sleep( 5 );
							} 
							catch ( InterruptedException e ) 
							{
								System.out.println( "Thread interrupted! Terminating..." );
							}
						}
						
						System.out.println( "Send " + s.GetColor() + "Socks to Washer. Total socks " + sockTotal + ". Total inside queue: " + sockQueue.size() );
						
						// Send to the washer
						washerThread.addSocks( s, sockToPair );
						
						sockQueue.remove( s );
						sockQueue.remove( sockToPair );
						sockToPair = null;
					}
				}
				
				curPos++;
			}
			else
			{
				curPos = 0;
				
				// Check if we're currently holding a sock
				if ( sockToPair != null )
				{
					// Check if this is the last sock in the generator
					if ( sockToPair.GetCreatorSockCount() == 1 )
					{
						System.out.println( "Send Unmatched " + sockToPair.GetColor() + "Sock to Washer. Total socks " + sockTotal + ". Total inside queue: " + sockQueue.size() );
						
						// Send to the washer
						washerThread.addUnmatchedSock( sockToPair );
						
						sockQueue.remove( sockToPair );
						sockToPair = null;
					}
				}
			}
			
			// Sleep a bit
			try
			{
				sleep( 5 );
			}
			catch ( InterruptedException e )
			{
				System.out.println( "Thread interrupted! Terminating..." );
			}
		}
		
		// Let the washer know we're done
		washerThread.setMatcherFinished( true );
	}
	
	public void addToQueue( Sock sock )
	{
		sockQueue.add( sock );
	}
	
	public void incrementThreadsFinished()
	{
		threadsFinished++;
	}
	
	public void setSockTotal( int total )
	{
		sockTotal = total;
	}
}

class SockWasher extends Thread
{
	private boolean bMatcherFinished;
	private boolean bUnmatchedSock;
	private Sock firstSock;
	private Sock secondSock;
	
	public SockWasher()
	{
		bMatcherFinished = false;
		firstSock = null;
		secondSock = null;
	}
	
	public void run()
	{
		while ( !bMatcherFinished )
		{
			if ( isWasherEmpty() == false )
			{
				System.out.println( "Washer Thread: Destroyed " + firstSock.GetColor() + " Socks " );
				
				// Remove the socks
				firstSock.RemoveSock();
				firstSock = null;
				
				// Unmatched socks don't have pair
				if ( bUnmatchedSock )
				{
					bUnmatchedSock = false;
				}
				else
				{
					secondSock.RemoveSock();				
					secondSock = null;
				}
			}
			
			// Sleep a bit
			try
			{
				sleep( 5 );
			}
			catch ( InterruptedException e )
			{
				System.out.println( "Thread interrupted! Terminating..." );
			}
		}
	}
	
	public void setMatcherFinished( boolean bFinished )
	{
		bMatcherFinished = bFinished;
	}
	
	public boolean isWasherEmpty()
	{
		return ( ( firstSock == null && secondSock == null ) && bUnmatchedSock == false );
	}
	
	public void addSocks( Sock first, Sock second )
	{
		firstSock = first;
		secondSock = second;
	}
	
	public void addUnmatchedSock( Sock sock )
	{
		firstSock = sock;
		bUnmatchedSock = true;
	}
}

final class Sock
{
	private String color;
	private SockGenerator creator;
	
	public Sock( String color, SockGenerator creator )
	{
		this.color = color;
		this.creator = creator;
	}
	
	public String GetColor()
	{
		return color;
	}
	
	public void RemoveSock()
	{
		creator.Remove( this );
	}
	
	public int GetCreatorSockCount() 
	{
		return creator.GetCount();
	}
}
