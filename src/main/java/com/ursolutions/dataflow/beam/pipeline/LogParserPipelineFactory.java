package com.ursolutions.dataflow.beam.pipeline;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.ursolutions.dataflow.beam.pipeline.step.converttojava.GrokParsing;
import com.ursolutions.dataflow.beam.pipeline.step.dto.LogGrabbedData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.transforms.ParDo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class LogParserPipelineFactory implements Serializable {
    public Pipeline build(LogParserOptions options) {
        Pipeline pipeline = Pipeline.create(options);

        pipeline
//                .apply("Fetch the data", PubsubIO.readStrings()
//                    .withTimestampAttribute("timestamp_ms")
//                    .fromSubscription(options.getLogInputTopic()))
                .apply("Fetch the data", TextIO.read().from("gs://log-samples-data/data/sample_apache.log"))
//                .apply("Fetch the data", TextIO.read().from("/home/vegura/Documents/sample_apache.log"))
//                .apply("Apply windowing", Window.<String>into(
//                                FixedWindows.of(Duration.standardMinutes(1)))
//                        .triggering(AfterWatermark.pastEndOfWindow())
//                        .withAllowedLateness(Duration.ZERO)
//                        .accumulatingFiredPanes())
                .apply("convert to Grok entity format", ParDo.of(new GrokParsing()))
                .apply("save to bigquery", BigQueryIO.<LogGrabbedData>write()
                        .to(String.format("%s.%s.%s", "freedom-ukraine", "test_dataset", "test-table"))
                        .withFormatFunction(this::composeFrom)
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                        .withSchema(createSchema())
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
                        .withMethod(BigQueryIO.Write.Method.STORAGE_WRITE_API));


        return pipeline;
    }

    private TableSchema createSchema() {
        return new TableSchema()
                .setFields(
                        Arrays.asList(
                                new TableFieldSchema().setName("clientIp").setType("STRING").setMode("REQUIRED"),
                                new TableFieldSchema().setName("dateTime").setType("STRING"),
                                new TableFieldSchema().setName("method").setType("STRING"),
                                new TableFieldSchema().setName("responseStatus").setType("STRING"),
                                new TableFieldSchema().setName("request").setType("STRING")
                        )
                );
    }

    @SneakyThrows
    private TableRow composeFrom(LogGrabbedData data) {
        TableRow tableRow = new TableRow();
        tableRow.set("clientIp", data.getClientIp());
        tableRow.set("dateTime", Optional.ofNullable(data.getDateTime()).map(ZonedDateTime::toString).orElse("not specified"));
        tableRow.set("method", data.getMethod());
        tableRow.set("responseStatus", data.getResponseStatus().toString());
        tableRow.set("request", data.getRequest());

        return tableRow;
    }
}
