package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.Vector;

import at.ac.tuwien.dsg.sybl.model.annotations.SYBL_CodeRegionDirective;
import at.ac.tuwien.dsg.sybl.model.annotations.SYBL_CodeRegionDirective.AnnotType;

import com.yahoo.ycsb.Client;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.DBFactory;
import com.yahoo.ycsb.UnknownDBException;
import com.yahoo.ycsb.Utils;
import com.yahoo.ycsb.Workload;
import com.yahoo.ycsb.WorkloadException;
import com.yahoo.ycsb.measurements.Measurements;
import com.yahoo.ycsb.measurements.exporter.MeasurementsExporter;
import com.yahoo.ycsb.measurements.exporter.TextMeasurementsExporter;

public class WorkloadStarter {
	public WorkloadStarter() {
		

	}
		public void runWorkloadFromFile(String workloadFile){
		String [] st = new String[5];
		st[0]= "-P";
		st[1]=workloadFile;
		st[2]="-P";
		st[3]="config.dat";
		st[4]="-s";
		Client.main(st);
		}
	 

	 public void runYCSBWorkload(){
		 runWorkloadFromFile("workloads/workloada");
	 }
	 
	 @SYBL_CodeRegionDirective(annotatedEntityID="methodrunWorkload",type=AnnotType.DURING,
			 constraints="Co1:CONSTRAINT cpuUsageData < 65;" +
					 "Co2:CONSTRAINT cpuUsageData > 30; " +
					 "Co3:CONSTRAINT cpuUsageData < 85 WHEN cost > 70", 
			  monitoring ="Mo1:MONITORING cost = cost.instant;" +
			  		"Mo2: MONITORING dataThroughput = throughput.datasource;" +
			  		"Mo3: MONITORING cpuAllocatedData = cpu.size.datasource;" +
			  		"Mo4: MONITORING cpuUsage = cpu.usage;" +
			  		"Mo5: MONITORING cpuUsageData = cpu.usage.datasource",
			  strategies="St1:STRATEGY CASE Violated(Co2): scaleInDataSource; " +
			  		"St2:STRATEGY CASE Enabled(Co1) AND Violated(Co1): scaleOutDataSource;" +
			  		"St3:STRATEGY CASE Enabled(Co3) AND Violated(Co3): scaleOutDataSource;",
			  priorities="Priority(Co3) > Priority(Co1)")
	 public void runWorkload(){
		Properties props=new Properties();
     	props.setProperty("readallfields", "true");
		props.setProperty("readproportion", "0.5");
		props.setProperty("updateproportion", "0.1");
		props.setProperty("scanproportion", "0.1");
		props.setProperty("insertproportion", "0.3");
		props.setProperty("requestdistribution", "zipfian");
		props.setProperty("db", "com.yahoo.ycsb.db.CassandraClient10");
		props.setProperty("hosts","128.130.172.213");
		props.setProperty("workload", "com.yahoo.ycsb.workloads.CoreWorkload");
		props.setProperty("exportFile","resultsIntensiveWorkload.csv");
		String dbname="cassandra-10";
		props.setProperty("threadcount","100");
		props.setProperty("operationcount","100000");
		props.setProperty("recordcount","100000");
		String label="";
		Workload workload = null;
		boolean dotransactions=true;
		int threadcount=1;
		int target=0;
		boolean status=true;

		if (!checkRequiredProperties(props))
			{
				System.exit(0);
			}
			//set up measurements
			Measurements.setProperties(props);
			
			ClassLoader classLoader = Client.class.getClassLoader();
			Class workloadclass = null;
			try {
				workloadclass = classLoader.loadClass(props.getProperty("workload"));
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//get number of threads, target and db
			threadcount=Integer.parseInt(props.getProperty("threadcount","1"));
			dbname=props.getProperty("db","com.yahoo.ycsb.BasicDB");
			target=Integer.parseInt(props.getProperty("target","0"));
			
			try {
				workload=(Workload)workloadclass.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
			

			try
			{
				workload.init(props);
			}
			catch (WorkloadException e)
			{
				e.printStackTrace();
				e.printStackTrace(System.out);
				System.exit(0);
			}
			
			//warningthread.interrupt();
			Vector<Thread> threads=new Vector<Thread>();

			for (int threadid=0; threadid<threadcount; threadid++)
			{
				DB db=null;
				try
				{
					db=DBFactory.newDB(dbname,props);
				}
				catch (UnknownDBException e)
				{
					System.out.println("Unknown DB "+dbname);
					System.exit(0);
				}
				int opcount=Integer.parseInt(props.getProperty("operationcount","0"));
				double targetperthreadperms=-1;
				if (target>0)
				{
					double targetperthread=((double)target)/((double)threadcount);
					targetperthreadperms=targetperthread/1000.0;
				}	 

				Thread t=new ClientThread(db,dotransactions,workload,threadid,threadcount,props,opcount/threadcount,targetperthreadperms);

				threads.add(t);
				//t.start();
			}

			StatusThread statusthread=null;

			if (status)
			{
				boolean standardstatus=false;
				if (props.getProperty("measurementtype","").compareTo("timeseries")==0) 
				{
					standardstatus=true;
				}	
				statusthread=new StatusThread(threads,label,standardstatus);
				statusthread.start();
			}

			long st=System.currentTimeMillis();

			for (Thread t : threads)
			{
				t.start();
			}
			
	    Thread terminator = null;
	    
	   
	    int opsDone = 0;

			for (Thread t : threads)
			{
				try
				{
					t.join();
					opsDone += ((ClientThread)t).getOpsDone();
				}
				catch (InterruptedException e)
				{
				}
			}

			long en=System.currentTimeMillis();
			
			if (terminator != null && !terminator.isInterrupted()) {
	      terminator.interrupt();
	    }

			if (status)
			{
				statusthread.interrupt();
			}

			try
			{
				workload.cleanup();
			}
			catch (WorkloadException e)
			{
				e.printStackTrace();
				e.printStackTrace(System.out);
				System.exit(0);
			}

			try
			{
				exportMeasurements(props, opsDone, en - st);
			} catch (IOException e)
			{
				System.err.println("Could not export measurements, error: " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
			try {
				System.err.println("Now sleeping ...");
				Thread.sleep(270000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}


	public static boolean checkRequiredProperties(Properties props)
	{
		if (props.getProperty("workload")==null)
		{
			System.out.println("Missing property: "+"workload");
			return false;
		}

		return true;
	}


	/**
	 * Exports the measurements to either sysout or a file using the exporter
	 * loaded from conf.
	 * @throws IOException Either failed to write to output stream or failed to close it.
	 */
	private static void exportMeasurements(Properties props, int opcount, long runtime)
			throws IOException
	{
		MeasurementsExporter exporter = null;
		try
		{
			// if no destination file is provided the results will be written to stdout
			OutputStream out;
			String exportFile = props.getProperty("exportfile");
			if (exportFile == null)
			{
				out = System.out;
			} else
			{
				out = new FileOutputStream(exportFile);
			}

			// if no exporter is provided the default text one will be used
			String exporterStr = props.getProperty("exporter", "com.yahoo.ycsb.measurements.exporter.TextMeasurementsExporter");
			try
			{
				exporter = (MeasurementsExporter) Class.forName(exporterStr).getConstructor(OutputStream.class).newInstance(out);
			} catch (Exception e)
			{
				System.err.println("Could not find exporter " + exporterStr
						+ ", will use default text reporter.");
				e.printStackTrace();
				exporter = new TextMeasurementsExporter(out);
			}

			exporter.write("OVERALL", "RunTime(ms)", runtime);
			double throughput = 1000.0 * ((double) opcount) / ((double) runtime);
			exporter.write("OVERALL", "Throughput(ops/sec)", throughput);

			Measurements.getMeasurements().exportMeasurements(exporter);
		} finally
		{
			if (exporter != null)
			{
				exporter.close();
			}
		}
	}
}
class StatusThread extends Thread
{
	Vector<Thread> _threads;
	String _label;
	boolean _standardstatus;
	
	/**
	 * The interval for reporting status.
	 */
	public static final long sleeptime=10000;

	public StatusThread(Vector<Thread> threads, String label, boolean standardstatus)
	{
		_threads=threads;
		_label=label;
		_standardstatus=standardstatus;
	}

	/**
	 * Run and periodically report status.
	 */
	public void run()
	{
		long st=System.currentTimeMillis();

		long lasten=st;
		long lasttotalops=0;
		
		boolean alldone;

		do 
		{
			alldone=true;

			int totalops=0;

			//terminate this thread when all the worker threads are done
			for (Thread t : _threads)
			{
				if (t.getState()!=Thread.State.TERMINATED)
				{
					alldone=false;
				}

				ClientThread ct=(ClientThread)t;
				totalops+=ct.getOpsDone();
			}

			long en=System.currentTimeMillis();

			long interval=en-st;
			//double throughput=1000.0*((double)totalops)/((double)interval);

			double curthroughput=1000.0*(((double)(totalops-lasttotalops))/((double)(en-lasten)));
			
			lasttotalops=totalops;
			lasten=en;
			
			DecimalFormat d = new DecimalFormat("#.##");
			
			if (totalops==0)
			{
				System.err.println(_label+" "+(interval/1000)+" sec: "+totalops+" operations; "+Measurements.getMeasurements().getSummary());
			}
			else
			{
				System.err.println(_label+" "+(interval/1000)+" sec: "+totalops+" operations; "+d.format(curthroughput)+" current ops/sec; "+Measurements.getMeasurements().getSummary());
			}

			if (_standardstatus)
			{
			if (totalops==0)
			{
				System.out.println(_label+" "+(interval/1000)+" sec: "+totalops+" operations; "+Measurements.getMeasurements().getSummary());
			}
			else
			{
				System.out.println(_label+" "+(interval/1000)+" sec: "+totalops+" operations; "+d.format(curthroughput)+" current ops/sec; "+Measurements.getMeasurements().getSummary());
			}
			}

			try
			{
				sleep(sleeptime);
			}
			catch (InterruptedException e)
			{
				//do nothing
			}

		}
		while (!alldone);
	}
}

/**
 * A thread for executing transactions or data inserts to the database.
 * 
 * @author cooperb
 *
 */
class ClientThread extends Thread
{
	DB _db;
	boolean _dotransactions;
	Workload _workload;
	int _opcount;
	double _target;

	int _opsdone;
	int _threadid;
	int _threadcount;
	Object _workloadstate;
	Properties _props;


	/**
	 * Constructor.
	 * 
	 * @param db the DB implementation to use
	 * @param dotransactions true to do transactions, false to insert data
	 * @param workload the workload to use
	 * @param threadid the id of this thread 
	 * @param threadcount the total number of threads 
	 * @param props the properties defining the experiment
	 * @param opcount the number of operations (transactions or inserts) to do
	 * @param targetperthreadperms target number of operations per thread per ms
	 */
	public ClientThread(DB db, boolean dotransactions, Workload workload, int threadid, int threadcount, Properties props, int opcount, double targetperthreadperms)
	{
		//TODO: consider removing threadcount and threadid
		_db=db;
		_dotransactions=dotransactions;
		_workload=workload;
		_opcount=opcount;
		_opsdone=0;
		_target=targetperthreadperms;
		_threadid=threadid;
		_threadcount=threadcount;
		_props=props;
		//System.out.println("Interval = "+interval);
	}

	public int getOpsDone()
	{
		return _opsdone;
	}

	public void run()
	{
		try
		{
			_db.init();
		}
		catch (DBException e)
		{
			e.printStackTrace();
			e.printStackTrace(System.out);
			return;
		}

		try
		{
			_workloadstate=_workload.initThread(_props,_threadid,_threadcount);
		}
		catch (WorkloadException e)
		{
			e.printStackTrace();
			e.printStackTrace(System.out);
			return;
		}

		//spread the thread operations out so they don't all hit the DB at the same time
		try
		{
		   //GH issue 4 - throws exception if _target>1 because random.nextInt argument must be >0
		   //and the sleep() doesn't make sense for granularities < 1 ms anyway
		   if ( (_target>0) && (_target<=1.0) ) 
		   {
		      sleep(Utils.random().nextInt((int)(1.0/_target)));
		   }
		}
		catch (InterruptedException e)
		{
		  // do nothing.
		}
		
		try
		{
			if (_dotransactions)
			{
				long st=System.currentTimeMillis();

				while (((_opcount == 0) || (_opsdone < _opcount)) && !_workload.isStopRequested())
				{

					if (!_workload.doTransaction(_db,_workloadstate))
					{
						break;
					}

					_opsdone++;

					//throttle the operations
					if (_target>0)
					{
						//this is more accurate than other throttling approaches we have tried,
						//like sleeping for (1/target throughput)-operation latency,
						//because it smooths timing inaccuracies (from sleep() taking an int, 
						//current time in millis) over many operations
						while (System.currentTimeMillis()-st<((double)_opsdone)/_target)
						{
							try
							{
								sleep(1);
							}
							catch (InterruptedException e)
							{
							  // do nothing.
							}

						}
					}
				}
			}
			else
			{
				long st=System.currentTimeMillis();

				while (((_opcount == 0) || (_opsdone < _opcount)) && !_workload.isStopRequested())
				{

					if (!_workload.doInsert(_db,_workloadstate))
					{
						break;
					}

					_opsdone++;

					//throttle the operations
					if (_target>0)
					{
						//this is more accurate than other throttling approaches we have tried,
						//like sleeping for (1/target throughput)-operation latency,
						//because it smooths timing inaccuracies (from sleep() taking an int, 
						//current time in millis) over many operations
						while (System.currentTimeMillis()-st<((double)_opsdone)/_target)
						{
							try 
							{
								sleep(1);
							}
							catch (InterruptedException e)
							{
							  // do nothing.
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		}

		try
		{
			_db.cleanup();
		}
		catch (DBException e)
		{
			e.printStackTrace();
			e.printStackTrace(System.out);
			return;
		}

	}
	
}

