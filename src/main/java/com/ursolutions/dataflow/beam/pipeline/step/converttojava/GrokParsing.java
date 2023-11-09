package com.ursolutions.dataflow.beam.pipeline.step.converttojava;

import com.ursolutions.dataflow.beam.pipeline.step.dto.LogGrabbedData;
import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GrokParsing extends DoFn<String, LogGrabbedData> implements Serializable {

    private int number;

    @ProcessElement
    public void processElement(ProcessContext context) {
        String logLine = context.element();
        Match logMatch = getGrok().match(logLine);
        Map<String, Object> paramsMatcher = logMatch.capture();
        LogGrabbedData logGrabbedData = fromParseResult(paramsMatcher);
        context.output(logGrabbedData);
//        log.info("data -> {}", logGrabbedData);
    }

    private Grok getGrok() {
        GrokCompiler compiler = GrokCompiler.newInstance();
        compiler.registerDefaultPatterns();

        final Grok grok = compiler.compile("%{IPORHOST:clientip} - - " +
                "\\[%{HTTPDATE:timestamp}\\] " +
                "\"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})\" " +
                "%{NUMBER:response} " +
                "(?:%{NUMBER:bytes})");
        return grok;
    }

    private LogGrabbedData fromParseResult(Map<String, Object> parseResult) {
        LogGrabbedData logGrabbedData = new LogGrabbedData();
        logGrabbedData.setMethod(parseResult.getOrDefault("verb", "").toString());
        logGrabbedData.setRequest(parseResult.getOrDefault("request", "").toString());
        logGrabbedData.setResponseStatus(Integer.parseInt(parseResult.getOrDefault("response", "-1").toString()));
        logGrabbedData.setClientIp(parseResult.getOrDefault("clientip", "-").toString());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss xxxx");
        Optional.ofNullable(parseResult.get("timestamp"))
                .map(it -> ZonedDateTime.parse(it.toString(), dateTimeFormatter))
                .ifPresent(logGrabbedData::setDateTime);

        return logGrabbedData;
    }
}
