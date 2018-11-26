package com.chadwickboggs.interview.wipro.buildit.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


public final class WebCrawler implements Runnable {

    private static final String USAGE_FILENAME = "usage.txt";
    public static final Pattern URL_PATTERN = Pattern.compile(
        " href=['\"]([^']|[^\"]*)['\"]", Pattern.CASE_INSENSITIVE
    );

    private CommandLine commandLine;
    private String domainName;


    public static final void main(String... args) {

        try {
            // TODO: Usage a configurable threadpool.

            new Thread(new WebCrawler(parseCommandLineArguments(args))).start();
        }
        catch (ArgsInvalidException e) {
            System.err.println(getUsage());

            System.exit(2);
        }
    }


    public static CommandLine parseCommandLineArguments(String[] args) throws ArgsInvalidException {
        CommandLine commandLine = new CommandLine();
        commandLine.registerArg(new CommandLine.Arg('h', "help", false));
        commandLine.registerArg(new CommandLine.Arg('u', "usage", false));
        commandLine.registerArg(new CommandLine.ArgWithArgument(
            't', "target", false, ""
        ));
        commandLine.setRequiredCount(1);

        commandLine.parseArgs(args);

        return commandLine;
    }


    private static final String getUsage() {

        StringBuilder buf = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                WebCrawler.class.getClassLoader().getResourceAsStream(USAGE_FILENAME)
            ));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buf.append(line).append("\n");
            }
        }
        catch (IOException e) {
            System.err.println(String.format(
                "Error reading file.  Filename: \"%s\", Error Message: \"%s\"",
                USAGE_FILENAME, e.getMessage()
            ));
            e.printStackTrace();
        }

        return buf.toString();
    }


    public WebCrawler(final CommandLine commandLine) {

        this.commandLine = commandLine;
    }


    @Override
    public void run() {

        Set<CommandLine.Arg> parsedArgs = commandLine.getParsedArgs();

        if (parsedArgs.contains(new CommandLine.Arg('h')) ||
            parsedArgs.contains(new CommandLine.Arg('u'))) {

            System.out.println(getUsage());

            System.exit(0);
        }

        if (parsedArgs.contains(new CommandLine.ArgWithArgument('t'))) {
            Optional<CommandLine.Arg> argOpt = parsedArgs.stream()
                .filter(arg -> Character.valueOf('t').equals(arg.getSymbol()))
                .filter(arg -> (arg instanceof CommandLine.ArgWithArgument))
                .findFirst();
            if (!argOpt.isPresent()) {
                System.err.println(getUsage());

                System.exit(3);
            }

            String urlString = ((CommandLine.ArgWithArgument) argOpt.get()).getArgument();
            try {
                URL startUrl = new URL(urlString);
                String domainNameLimit = extractDomainName(startUrl);
                webCrawl(startUrl, domainNameLimit, 0, new HashSet<URL>(), System.out);
            }
            catch (MalformedURLException e) {
                System.err.println(String.format("Unable to parse target URL.  Target URL: %s", urlString));

                System.exit(4);
            }
            catch (IOException e) {
                System.err.println(String.format("Error reading URL.  Target URL: %s", urlString));

                System.exit(5);
            }
        }

    }


    private String extractDomainName(final URL startUrl) {

        String[] split = startUrl.getHost().split("\\.");
        StringBuilder buf = new StringBuilder();
        buf.append(split[split.length - 2]).append(".").append(split[split.length - 1]);

        return  buf.toString().toLowerCase();
    }


    private void webCrawl(
        final URL startUrl, final String domainNameLimit, int depth, final Set<URL> siteMapUrls,
        final PrintStream printStream
    ) throws IOException {

        if (siteMapUrls.contains(startUrl)) {
            return;
        }

        IntStream.range(0, depth).mapToObj(i -> "\t").forEachOrdered(printStream::print);
        printStream.println(startUrl);

        siteMapUrls.add(startUrl);

        // TODO: Add a configurable threadpool for concurrency.

        listUrls(startUrl).stream()
            .filter(url -> !startUrl.equals(url))
            .filter(url -> domainNameLimit.equals(extractDomainName(url)))
            .forEach(url -> {
            try {
                webCrawl(url, domainNameLimit, depth + 1, siteMapUrls, printStream);
            }
            catch (Exception e) {
                System.err.println(String.format("Error crawling url.  URL: %s", url.toString()));
            }
        });
    }


    private Set<URL> listUrls(final URL url) throws IOException {

        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        Set<URL> urls = new HashSet<>();
        while ((line = reader.readLine()) != null) {
            Matcher matcher = URL_PATTERN.matcher(line);
            while (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String group = "";
                    try {
                        group = matcher.group(i);
                        urls.add(new URL(group));
                    }
                    catch (MalformedURLException e) {
                        try {
                            group = url + "/" + group;
                            urls.add(new URL(group));
                        }
                        catch (MalformedURLException muE) {
                            System.err.println(String.format(
                                "Error parsing URL in page.  Page: \"%s\", URL: \"%s\"",
                                url, group
                            ));
                        }
                    }
                }
            }
        }

        return urls;
    }
}
