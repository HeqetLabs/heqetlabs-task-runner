package com.heqetlabs.tasks;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public interface Task {
    public void setup(CommandLine commandLine);
    public Options getOptions();
    public void run() throws Exception;
}
