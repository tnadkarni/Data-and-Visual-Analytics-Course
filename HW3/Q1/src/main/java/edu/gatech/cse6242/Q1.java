package edu.gatech.cse6242;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Q1 {

  	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      String line = value.toString();
      String[] tokens = line.split("\t");
      int weight = Integer.parseInt(tokens[2]);
      context.write(new Text(tokens[1]), new IntWritable(weight));

    }
  }
	
	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int max_val = 0
			;
			for(IntWritable value : values) {
				int current = value.get();

				if (max_val < current) {
					max_val = current;
				}
			}
			if (max_val != 0) {
				context.write(key, new IntWritable(max_val));
			}
		}
	}

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = new Job(conf, "Q1");
    job.setJarByClass(Q1.class);

	job.setMapOutputKeyClass(Text.class);
	job.setMapOutputValueClass(IntWritable.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);

    /* TODO: Needs to be implemented */

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
