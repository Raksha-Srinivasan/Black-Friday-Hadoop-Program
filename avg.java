import java.io.IOException; 
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
public class avg 
{
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {     //Mapper class
	 //private final static IntWritable one = new IntWritable(1);
	 IntWritable one = new IntWritable(1);

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
	        String[] line = value.toString().split(",");
	        	int n=Integer.parseInt(line[7]);
	        	if(n==1)
	        	{
	        		try
	        		{
	        			context.write(new Text(line[2]), new IntWritable(Integer.parseInt(line[4])));
	        		}
	        		catch(Exception e)
	        		{
	        			context.write(new Text(line[2]), new IntWritable(Integer.parseInt("0")));
	        		}
	        	}
	        	
	        }  
	     } 

	  public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {     //Reducer Class

		  int max_s=0,max=0;
		  Text max_key=new Text("Max Average Occupation: ");
		  Text m_key=new Text();
		    public void reduce(Text key, Iterable<IntWritable> values, Context context) 
		    throws IOException, InterruptedException {	        
		    	int s=0,c=0,avg=0;
		        for (IntWritable val : values) {
		        	c+=1;
		            s+= val.get();
		        }
		        avg=s/c;
		        if(max<avg)
		        {
		        	max=avg;
		        	m_key.set(key);
		        }
		  context.write(key, new IntWritable(avg));
		  
		   }
		    @Override
		    public void cleanup(Context context) throws IOException, InterruptedException 
		    {
		    context.write(max_key, new IntWritable(max));
            }
		  }
	  public static void main(String[] args) throws Exception
	  {                                                              //Driver Class
		   Configuration conf = new Configuration();
		   Job job = new Job(conf, "BlackFriday");
		   job.setJarByClass(avg.class);
		   job.setOutputKeyClass(Text.class);
	       job.setOutputValueClass(IntWritable.class);
		   job.setMapperClass(Map.class);
		   job.setReducerClass(Reduce.class);
	       job.setInputFormatClass(TextInputFormat.class);	    
	       job.setOutputFormatClass(TextOutputFormat.class);    
	       FileInputFormat.addInputPath(job, new Path(args[0]));
	       FileOutputFormat.setOutputPath(job, new Path(args[1]));
           job.waitForCompletion(true);
		}        
}