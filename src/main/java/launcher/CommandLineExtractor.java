package launcher;

import org.apache.commons.cli.*;

import static spark.Spark.port;

class CommandLineExtractor {

    private String[] args;
    private Options CommandsLauncherOptions = new Options();
    private CommandLineParser commandLineParser = new DefaultParser();
    private HelpFormatter helpFormatter = new HelpFormatter();


    CommandLineExtractor(String[] args) {
        this.args = args;
    }


    void setUpPort() {
        Option portOption = new Option("p", "port", true, "Port for working 'Spark'");
        portOption.setRequired(true);
        portOption.setArgs(1);
        CommandsLauncherOptions.addOption(portOption);

        try {
            CommandLine commandLine = commandLineParser.parse(CommandsLauncherOptions, args);
            int port = Integer.parseInt(commandLine.getOptionValue("port"));

            if (port >= 1024 && port <= 65535) {
                port(port);
                System.out.println("'Spark' port is: " + port);
            } else {
                System.err.println("Invalid port");
                System.exit(1);
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());

            helpFormatter.printHelp("utility-name", CommandsLauncherOptions);
            System.exit(1);
        }
    }


}