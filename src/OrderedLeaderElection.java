import java.util.ArrayList;
import java.util.Random;

public class OrderedLeaderElection 
{
	public static void main( String args[] )
	{
		Random rand = new Random();
		
		// Generate a random amount of officials
		int n = rand.nextInt( 100 );
		
		rankThread ranker = new rankThread( n );
		Thread threadRank = new Thread( ranker );
		threadRank.start();
		
		for ( int i = 1; i <= n; i++ )
		{
			// Create a new elected official
			electedOfficial official = new electedOfficial( rand.nextInt(), i, ranker);
			Thread t = new Thread( official );
			t.start();
			
			// Let the rank thread know a new thread has been created
			ranker.addOfficial( official, t );
			threadRank.interrupt();
		}
		
		// Wait until the rank thread is done
		try 
		{
			threadRank.join();
		} 
		catch ( InterruptedException e ) 
		{
			e.printStackTrace();
		}
	}
}

class electedOfficial implements Runnable
{
	private int rank;
	private String name;
	private boolean bFinished;
	private electedOfficial leader;
	private rankThread ranker;
	
	public electedOfficial( int rank, int number, rankThread ranker )
	{
		this.rank = rank;
		this.name = "Elected Official " + number;
		this.leader = this;
		this.ranker = ranker;
		
		System.out.println( this.name + " is being created with a rank of " + this.rank + ": I think " + leader.getName() + " is the leader" );
	}
	
	public void run()
	{
		while ( !bFinished )
		{
			try 
			{
				Thread.sleep( 1000 );
			} 
			catch ( InterruptedException e ) 
			{
				leader = ranker.getLeader();
				System.out.println( this.name + ": I think " + leader.getName() + " is the leader" );
			}
		}
	}
	
	public int getRank()
	{
		return rank;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setFinished( boolean bFinished )
	{
		this.bFinished = bFinished;
	}
}

class rankThread implements Runnable
{
	private int threadCount;
	private ArrayList<electedOfficial> officials;
	private ArrayList<Thread> officialThreads;
	private electedOfficial leader;
	
	public rankThread( int numThreads )
	{
		this.threadCount = numThreads;
		officials = new ArrayList<>();
		officialThreads = new ArrayList<>();
		leader = null;
	}
	
	public void run()
	{
		while ( officials.size() < threadCount )
		{
			try 
			{
				Thread.sleep( 1000 );
			} 
			catch ( InterruptedException e ) 
			{
				System.out.println( "New Thread Created. Calculating new leader..." );
				
				// Determine who the leader is
				int index = 0;
				for ( int i = 0; i < officials.size(); i++ )
				{
					// Check if the current official is of a higher rank than the current known highest
					if ( officials.get( i ).getRank() > officials.get( index ).getRank() )
					{
						index = i;
					}
				}
				
				// Check if the calculated leader is different from the previous leader
				if ( officials.get( index ) != leader )
				{
					leader = officials.get( index );
					
					System.out.println( "\nNew Leader Appointed! Notifying elected officials...\n" );
					
					// Wake up the threads
					for ( int i = 0; i < officialThreads.size(); i++ )
					{
						officialThreads.get( i ).interrupt();
					}
				}
			}
		}
		
		// Let the threads know we've finished
		for ( int i = 0; i < officials.size(); i++ )
		{
			officials.get( i ).setFinished( true );
		}
	}
	
	public void addOfficial( electedOfficial official, Thread thread )
	{
		officials.add( official );
		officialThreads.add( thread );
	}
	
	public electedOfficial getLeader()
	{
		return leader;
	}
}
