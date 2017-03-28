package org.la.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class XmlToJson {

    private static boolean modeVerbose;

    public static void main(String[] args) {

        int exitStatus = 0;
        modeVerbose = false;

        // Build command line options
        Options clOptions = new Options();
        clOptions.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Show this help")
                .build());
        clOptions.addOption(Option.builder("f")
                .longOpt("file")
                .desc("XML file to convert")
                .hasArg()
                .argName("filename")
                .build());
        clOptions.addOption(Option.builder("o")
                .longOpt("output")
                .desc("output file")
                .hasArg()
                .argName("filename")
                .build());
        clOptions.addOption(Option.builder("v")
                .longOpt("verbose")
                .desc("show processing messages")
                .build());

        if(args.length == 0) {
            showCommandHelp(clOptions);
        }
        else {
            exitStatus = processCommandLine(args, clOptions);
        }

        System.exit(exitStatus);

    }


    private static int processCommandLine(String[] args, Options clOptions) {
        int executeStatus = 0;
        String inputXml = "";
        String outputJson = "";

        CommandLineParser clParser = new DefaultParser();


        try {
            CommandLine line = clParser.parse(clOptions, args, true);

            // Remaining command line parameter, if any, is XML string to convert
            List<String> cmdLineXml = line.getArgList();
            if(cmdLineXml.size() > 0) {
                inputXml = cmdLineXml.get(0); // Get only the first parameter, ignore others
            }

            if (line.hasOption("help")) {
                showCommandHelp(clOptions);
            }
            else {

                if (line.hasOption("verbose")) {
                    modeVerbose = true;
                }

                // Read XML from file and convert
                if (line.hasOption("file")) {
                    outputJson = convertToJsonFaster(new File(line.getOptionValue("file")));
                }
                // Read XML from command line and convert
                else {
                    if (inputXml != null && inputXml.length() > 0) {
                        outputJson = convertToJsonFaster(inputXml);
                    }
                    else {
                        System.out.println("No XML to process.");
                    }
                }

                if (outputJson != null && outputJson.length() > 0) {
                    // Write JSON to file
                    if (line.hasOption("output")) {
                        executeStatus = writeStringToFile(line.getOptionValue("output"), outputJson);
                    }
                    // Write JSON to command line
                    else {
                        System.out.println(outputJson);
                    }
                }


            }
        }
        catch (ParseException e) {
            System.err.println("Command line parsing failed. Error: " + e.getMessage() + "\n");
            showCommandHelp(clOptions);
            executeStatus = 1;
        }

        return executeStatus;
    }


    private static String convertToJson(File xmlFile) {
        String json = "";

        return json;
    }


    private static String convertToJsonFaster(File xmlFile) {
        String json = "";
        XmlMapper xmlMapper = new XmlMapper();

        if (modeVerbose) {
            System.out.println("Input file (XML): " + xmlFile.toString());
        }

        try {
            JsonNode jsonNode = xmlMapper.readTree(xmlFile);
            System.out.println("node size: " + jsonNode.size());
            System.out.println("node data: " + jsonNode.toString());

            ObjectMapper jsonMapper = new ObjectMapper();
            json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        }
        catch (IOException e) {
            System.err.println("Problem mapping XML to JSON. Error: " + e.getMessage());
        }
        return json;
    }


    private static String convertToJsonFaster(String xmlIn) {
        String json = "";
        XmlMapper xmlMapper = new XmlMapper();

        if (modeVerbose) {
            System.out.println("XML to convert: " + xmlIn);
        }

        try {
            JsonNode jsonNode = xmlMapper.readTree(xmlIn);

            ObjectMapper jsonMapper = new ObjectMapper();
            json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        }
        catch (IOException e) {
            System.err.println("Problem mapping XML to JSON. Error: " + e.getMessage());
        }
        return json;
    }


    private static int writeStringToFile(String outputFilename, String outputString) {
        int status = 0;
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        if (modeVerbose) {
            System.out.println("Output file (JSON): " + outputFilename);
        }

        try {
            fileWriter = new FileWriter(outputFilename);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(outputString);

        }
        catch (IOException e) {
            System.out.println("Problem writing to file. Error: " + e.getMessage());
            status = 1;
        }
        finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
            catch (IOException ioErr) {
                System.out.println("Problem closing file. Error: " + ioErr.getMessage());
                status = 1;
            }
        }

        return status;
    }


    private static void showCommandHelp(Options options) {
        String commandHelpHeader = "\nConvert XML to JSON\n\n";
        String commandHelpFooter = "\nExamples:\n\n" +
                "  java -jar xml2json.jar -f movie.xml\n\n" +
                "  java -jar xml2json.jar -f movie.xml -o movie.json\n\n" +
                "  java -jar xml2json.jar -o movie.json \'<movie><title>Ant Man</title></movie>\'\n\n" +
                "  java -jar xml2json.jar \'<?xml version=\"1.0\"?><movie><title>Ant Man</title></movie>\'\n\n";

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(88,"java -jar xml2json.jar ['XML to convert']", commandHelpHeader, options, commandHelpFooter, true);
    }


}
