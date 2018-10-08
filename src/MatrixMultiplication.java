import java.util.Random;

public class MatrixMultiplication 
{
	public static void main( String args[] )
	{
		Random rand = new Random();
		
		int m = 2160;
		int n = 4560;
		int p = 6400;
		
		float A[][] = new float[m][n];
		float B[][] = new float[n][p];
		float C[][] = new float[m][p]; 
		
		// Fill A with random numbers
		for ( int i = 0; i < m; i++ )
		{
			for ( int j = 0; j < n; j++ )
			{
				A[i][j] = rand.nextFloat() * 100;
			}
		}
		
		// Fill B with random numbers
		for ( int i = 0; i < n; i++ )
		{
			for ( int j = 0; j < p; j++ )
			{
				B[i][j] = rand.nextFloat() * 100;
			}
		}
		
		matmult( A, B, C, m, n, p );
	}
	
	public static void matmult( float A[][], float B[][], float C[][], int m, int n, int p )
	{
		long startTime = 0, stopTime = 0;
		Thread t[] = new Thread[8];
		
		// 1 Thread
		System.out.println( "1 Thread:\n" );
		
		// 4 Different array sizes to test
		for ( int i = 4; i > 0; i-- )
		{
			// Start time
			startTime = System.nanoTime();
			
			// Create 1 thread to do all the work
			t[0] = new Thread( new WorkerThread( A, B, C, 0, ( n / i) , ( m / i ), ( p / i ) ) );	
			t[0].start();
			
			try 
			{
				t[0].join();
			} 
			catch ( InterruptedException e ) 
			{
				e.printStackTrace();
			}
			
			// Stop time
			stopTime = System.nanoTime();
			System.out.println( "Time with 1 thread: " + ( ( double ) stopTime - ( double ) startTime ) / 1000000000 + "sec; m = " + ( m / i ) + ", n = " + ( n / i ) + ", p = " + ( p / i ) ); // print array size + elapsed time
		}
		
		
		// 2 Threads
		System.out.println( "\n2 Threads:\n" );
		
		// 4 Different array sizes to test
		for ( int i = 4; i > 0; i-- )
		{
			// Each thread should get 50% of the work
			int split = ( n / i ) / 2;
			
			// Start time
			startTime = System.nanoTime();
			
			// Split the work up
			for ( int j = 0; j < 2; j++)
			{
				int start = 0, end = 0;
				
				if ( j == 0)
				{
					end = split;
				}
				else
				{
					start = split + 1;
					end = ( n / i );
				}
				
				t[j] = new Thread( new WorkerThread( A, B, C, start, end, ( m / i ), ( p / i ) ) );
				t[j].start();
			}
			
			try 
			{
				// Don't move on until every thread is finished
				for ( int j = 0; j < 2; j++ )
				{
					t[j].join();
				}
			} 
			catch ( InterruptedException e ) 
			{
				e.printStackTrace();
			}
			
			// Stop time
			stopTime = System.nanoTime();
			System.out.println( "Time with 2 threads: " + ( ( double ) stopTime - ( double ) startTime ) / 1000000000 + "sec; m = " + ( m / i ) + ", n = " + ( n / i ) + ", p = " + ( p / i ) ); // print array size + elapsed time
		}
		
		
		// 4 Threads
		System.out.println( "\n4 Threads:\n" );
		
		// 4 Different array sizes to test
		for ( int i = 4; i > 0; i-- )
		{
			// Each thread should get 25% of the work
			int split = ( n / i ) / 4;
			
			// Start time
			startTime = System.nanoTime();
			
			// Split the work up
			for ( int j = 0; j < 4; j++)
			{
				int start = 0, end = 0;
				
				if ( j == 0)
				{
					end = split;
				}
				else
				{
					start = ( split * j ) + 1; 
					
					if ( j + 1 == 4 )
					{
						end = ( n / i );
					}
					else
					{
						end = split * ( j + 1 );
					}
				}
				
				t[j] = new Thread( new WorkerThread( A, B, C, start, end, ( m / i ), ( p / i ) ) );
				t[j].start();
			}
			
			try 
			{
				// Don't move on until every thread is finished
				for ( int j = 0; j < 4; j++ )
				{
					t[j].join();
				}
			} 
			catch ( InterruptedException e ) 
			{
				e.printStackTrace();
			}
			
			// Stop time
			stopTime = System.nanoTime();
			System.out.println( "Time with 4 threads: " + ( ( double ) stopTime - ( double ) startTime ) / 1000000000 + "sec; m = " + ( m / i ) + ", n = " + ( n / i ) + ", p = " + ( p / i ) ); // print array size + elapsed time
		}
		
		
		// 8 Threads
		System.out.println( "\n8 Threads:\n" );
		
		// 4 Different array sizes to test
		for ( int i = 4; i > 0; i-- )
		{
			// Each thread should get 12.5% of the work
			int split = ( n / i ) / 8;
			
			// Start time
			startTime = System.nanoTime();
			
			// Split the work up
			for ( int j = 0; j < 8; j++)
			{
				int start = 0, end = 0;
				
				if ( j == 0)
				{
					end = split;
				}
				else
				{
					start = ( split * j ) + 1; 
					
					if ( j + 1 == 8 )
					{
						end = ( n / i );
					}
					else
					{
						end = split * ( j + 1 );
					}
				}
				
				t[j] = new Thread( new WorkerThread( A, B, C, start, end, ( m / i ), ( p / i ) ) );
				t[j].start();
			}
			
			try 
			{
				// Don't move on until every thread is finished
				for ( int j = 0; j < 8; j++ )
				{
					t[j].join();
				}
			} 
			catch ( InterruptedException e ) 
			{
				e.printStackTrace();
			}
			
			// Stop time
			stopTime = System.nanoTime();
			System.out.println( "Time with 8 threads: " + ( ( double ) stopTime - ( double ) startTime ) / 1000000000 + "sec; m = " + ( m / i ) + ", n = " + ( n / i ) + ", p = " + ( p / i ) ); // print array size + elapsed time
		}
	}
}

// WorkerThread does the work partitioned to it
class WorkerThread implements Runnable
{
	// Bounds for thread
	private int nStart;
	private int nEnd;
	private int m;
	private int p;
	private float A[][];
	private float B[][];
	private float C[][];
	
	
	public WorkerThread( float A[][], float B[][], float C[][], int nStart, int nEnd, int m, int p )
	{
		this.A = A;
		this.B = B;
		this.C = C;
		this.nStart = nStart;
		this.nEnd = nEnd;
		this.m = m;
		this.p = p;
	}
	
	public void run() 
	{	
		// Calculate for the givens bounds
		for ( int i = nStart; i < nEnd; i++ )
		{
			for ( int j = 0; j < m; j++ )
			{
				for ( int k = 0; k < p; k++ )
				{
					C[j][k] += ( A[j][i] * B[i][k] );
				}
			}
		}
	}	
}
