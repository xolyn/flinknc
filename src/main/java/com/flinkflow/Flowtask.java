package com.flinkflow;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Flowtask {
    public static void main(String[] args) throws Exception {
        
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // nc -l 2810 

        DataStream<String> input = env.socketTextStream("localhost", 2810);

        DataStream<Tuple2<String, Integer>> wordCounts = input
                .flatMap(new Tokenizer())
                .keyBy(0)
                .sum(1);

        wordCounts.print();
        
        env.execute("Flow task");
    }

    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] words = value.toLowerCase().split("\\W+");

            for (String word : words) {
                if (word.length() > 0) {
                    out.collect(new Tuple2<>(word, 1));
                }
            }
        }
    }
}