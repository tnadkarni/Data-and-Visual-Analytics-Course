package edu.gatech.cse6242;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.lang.Object;


public class Q4 extends Configured implements Tool {

 private static final String temp = "temp.tsv";

 public static class Mapper1 extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      String line = value.toString();
      String[] tokens = line.split("\t");
      for (int i=0; i<tokens.length; i++) {
        context.write(new Text(tokens[0]), one);
      }

    }
   }

    public static class Mapper2 extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      String line = value.toString();
      String[] tokens = line.split("\t");
      context.write(new Text(tokens[1]), one);


    }
  }
  
  public static class Reducer1 extends Reducer<Text, IntWritable, Text, IntWritable> {

    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
          sum += val.get();
        }
      context.write(key, new IntWritable(sum));
      }
  
  }

 @Override
 public int run(String[] args) throws Exception {
  /*
   * Job 1
   */
  Configuration conf = this.getConf();

  FileSystem fs = FileSystem.get(conf);
  //Path tempPath = new Path(temp);
  
  Job job = new Job(conf, "Job1");
  job.setJarByClass(Q4.class);

  job.setMapOutputKeyClass(Text.class);
  job.setMapOutputValueClass(IntWritable.class);
  job.setOutputKeyClass(Text.class);
  job.setOutputValueClass(IntWritable.class);

  job.setMapperClass(Mapper1.class);
  job.setReducerClass(Reducer1.class);

  FileInputFormat.addInputPath(job, new Path(args[0]));
  FileOutputFormat.setOutputPath(job, new Path(temp));

  job.waitForCompletion(true);

  /*
   * Job 2
  */
  
  
  Job job2 = new Job(conf, "Job2");
  job2.setJarByClass(Q4.class);

  job2.setMapperClass(Mapper2.class);
  job2.setReducerClass(Reducer1.class);

  job2.setMapOutputKeyClass(Text.class);
  job2.setMapOutputValueClass(IntWritable.class);
  job2.setOutputKeyClass(Text.class);
  job2.setOutputValueClass(IntWritable.class);

  FileInputFormat.addInputPath(job2, new Path(temp));
  FileOutputFormat.setOutputPath(job2, new Path(args[1]));

 // fs.delete(tempPath, true);

  return job2.waitForCompletion(true) ? 0 : 1;
 }

 public static void main(String[] args) throws Exception {
  ToolRunner.run(new Configuration(), new Q4(), args);
 }
}