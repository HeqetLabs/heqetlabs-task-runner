HeqetLabs Task Runner
=====================

This provides a super simple interface to allow writing oneoff or cron job like tasks in Java easier.

It lets you use Guice to inject and create your runner classes.

```java
package com.heqetlabs.tasks.example;

import com.heqetlabs.tasks.Task;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class TestTask implements Task {

    private final Integer value;

    @Inject
    public TestTask(@Named("testValue") Integer value) {
        this.value = value;
    }

    @Override
    public void setup(CommandLine commandLine) {
        System.out.println("Setting up test task");
    }

    @Override
    public Options getOptions() {
        Options options = new Options();
        options.addOption("testing", false, "Test option to a test task");
        return options;
    }

    @Override
    public void run() throws Exception {
        System.out.println("I am alive and I was passed " + value + " from guice :D");
    }
}
```

```java
package com.heqetlabs.tasks.example;

import com.heqetlabs.tasks.TaskRunner;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class TestTaskRunner extends TaskRunner {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Integer.class).annotatedWith(Names.named("testValue")).toInstance(1337);
            }
        });
    }

    @Override
    protected void configure() {
        register("test", "Run a test task", TestTask.class);
    }

    public static void main(String [] args) throws Exception {
        new TestTaskRunner().run(args);
    }
}
```

Then you simply do the following:

```bash
java -jar Tasks.jar -test
```

At this point the TestTask will be setup and run with the provided Guice settings.

