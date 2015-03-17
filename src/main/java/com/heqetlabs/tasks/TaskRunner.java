package com.heqetlabs.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import org.apache.commons.cli.*;

import java.util.List;
import java.util.Map;

public abstract class TaskRunner {
    Map<String, TaskWrapper> taskMap = Maps.newHashMap();
    Options options = new Options();

    private static class TaskWrapper {
        String command;
        String description;
        Class klass;

        public TaskWrapper(String command, String description, Class klass) {
            this.command = command;
            this.description = description;
            this.klass = klass;
        }
    }

    protected abstract Injector getInjector();

    protected abstract void configure();

    protected void register(String command, String description, Class klass) {
        if (taskMap.containsKey(command)) {
            throw new RuntimeException(String.format("Command %s was already registered", command));
        } else {
            taskMap.put(command, new TaskWrapper(command, description, klass));
            options.addOption(command, false, description);
        }
    }

    public void run(String [] args) throws Exception {
        configure();
        if (args.length == 0) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("Task Runner by HeqetLabs", options);
        } else {
            try {
                BasicParser basicParser = new BasicParser();
                CommandLine commandLine = basicParser.parse(options, args);

                Injector injector = getInjector();

                String command = findCommandStrings(commandLine);
                Class taskClass = taskMap.get(command).klass;

                Object obj = injector.getInstance(taskClass);
                Task task = (Task) obj;
                Options taskOptions = task.getOptions();
                for (Object option : options.getOptions()) {
                    taskOptions.addOption((Option)option);
                }
                task.setup(basicParser.parse(taskOptions, args));
                task.run();
            } catch (UnrecognizedOptionException exception) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("task runner", options);
                System.err.println(exception.getMessage());
            }
        }
    }

    private String findCommandStrings(CommandLine commandLine) {
        List<String> commands = Lists.newArrayList();
        for (TaskWrapper taskWrapper : taskMap.values()) {
            if (commandLine.hasOption(taskWrapper.command)) {
                commands.add(taskWrapper.command);
            }
        }

        if (commands.size() > 1) {
            throw new RuntimeException("Please only specify a single command to run");
        } else if (commands.size() == 0) {
          HelpFormatter helpFormatter = new HelpFormatter();
          helpFormatter.printHelp("task runner", options);
          throw new RuntimeException("No valid command found.");
        }

        return commands.get(0);
    }

}
