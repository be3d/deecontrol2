package com.ysoft.dctrl.log;

import java.io.File;
import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import com.ysoft.dctrl.utils.files.FilePathResource;

/**
 * Created by pilar on 24.7.2017.
 */
@Plugin(name = "DeeControlConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(50)
public class DeeControlConfigurationFactory extends ConfigurationFactory {
    private final static String FILE_NAME = "dctrl.log";
    private final static String ARCHIVE_FILE_NAME = "dctrl-%d{MM-dd-yy}.log";

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        String logFileDir = FilePathResource.getLoggerDir();
        builder.setStatusLevel(Level.DEBUG);
        builder.setConfigurationName("RollingBuilder");

        AppenderComponentBuilder consoleAppenderBuilder = builder.newAppender("Stdout", "CONSOLE");
        consoleAppenderBuilder.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);

        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout");
        layoutBuilder.addAttribute("pattern", "%d [%t] %-5level: %msg%n");

        ComponentBuilder policyBuilder = builder.newComponent("Policies");
        policyBuilder.addComponent(builder.newComponent("CronTriggeringPolicy").addAttribute("schedule", "0 0 0 * * ?"));
        policyBuilder.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "100M"));

        AppenderComponentBuilder fileAppenderBuilder = builder.newAppender("rolling", "RollingFile");
        fileAppenderBuilder.addAttribute("fileName", logFileDir + File.separator + FILE_NAME);
        fileAppenderBuilder.addAttribute("filePattern", logFileDir + File.separator + ARCHIVE_FILE_NAME);
        fileAppenderBuilder.add(layoutBuilder);
        fileAppenderBuilder.addComponent(policyBuilder);

        RootLoggerComponentBuilder rootBuilder = builder.newRootLogger(Level.DEBUG);
        rootBuilder.add(builder.newAppenderRef("Stdout"));
        rootBuilder.add(builder.newAppenderRef("rolling"));

        builder.add(consoleAppenderBuilder);
        builder.add(fileAppenderBuilder);
        builder.add(rootBuilder);

        return builder.build();
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, String name, URI configLocation) {
        return getConfiguration(loggerContext, null);
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, String name, URI configLocation, ClassLoader loader) {
        return getConfiguration(loggerContext, null);
    }
}
