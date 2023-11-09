package com.ursolutions.dataflow.beam.pipeline;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.ValueProvider;

public interface LogParserOptions extends DataflowPipelineOptions {
    @Description("The clud pub/sub subscription")
    @Default.String("projects/freedom-ukraine/topics/apache-log-out")
    ValueProvider<String> getLogInputTopic();

    void setLogInputTopic(ValueProvider<String> logInputTopic);

    @Description("Table where the log data will be stored")
    @Default.String("log_objects")
    public ValueProvider<String> getBigQueryTable();

    void setBigQueryTable(ValueProvider<String> bigQueryTable);

    @Description("Static filename contains the example log")
    @Default.String("gs://log-samples-data/data/sample_apache.log")
    ValueProvider<String> getLogFileData();

    void setLogFileData(ValueProvider<String> logFileData);

    @Description("Database of BigQuery table")
    @Default.String("db")
    ValueProvider<String> getBigQueryDb();

    void setBigQueryDb(ValueProvider<String> bigQueryDb);

}