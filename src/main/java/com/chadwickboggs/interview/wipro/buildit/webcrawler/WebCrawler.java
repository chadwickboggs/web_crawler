package com.chadwickboggs.interview.wipro.buildit.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Web Crawler scans a site building a sitemap.  It takes the root homepage to
 * start at as a command line parameter.  Its scans for links on that page within
 * the same domain and scans them.  Its output is a tab indented one page per
 * line.
 */
public final class WebCrawler implements Runnable {

    public static final Pattern URL_PATTERN = Pattern.compile(
        " href=['\"]([^']|[^\"]*)['\"]", Pattern.CASE_INSENSITIVE
    );

    private static final String USAGE_FILENAME = "usage.txt";

    private CommandLine commandLine;
    private String domainName;


    /**
     * Command line run method.
     *
     * @param args command line arguments.
     */
    public static void main(@Nullable final String... args) {

        try {
            // TODO: Usage a configurable threadpool.

            new Thread(new WebCrawler(parseCommandLineArguments(args))).start();
        }
        catch (ArgsInvalidException e) {
            System.err.println(getUsage());

            System.exit(2);
        }
    }


    /**
     * Parse command line arguments into a CommandLine instance.
     *
     * @param args the command line arguments.
     * @return A new CommandLine instances for the parsed arguments.
     * @throws ArgsInvalidException on invalid arguments.
     */
    @Nonnull
    public static CommandLine parseCommandLineArguments(@Nullable final String... args)
        throws ArgsInvalidException {

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


    @Nonnull
    private static final String getUsage() {

        StringBuilder buf = new StringBuilder();
        try (BufferedReader bufferedReader =
                 new BufferedReader(new InputStreamReader(
                     WebCrawler.class.getClassLoader().getResourceAsStream(USAGE_FILENAME), Charset.defaultCharset())
                 )) {

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


    /**
     * Create instance.
     *
     * @param commandLine the command line.
     */
    public WebCrawler(@Nonnull final CommandLine commandLine) {

        this.commandLine = commandLine;
    }


    @Override
    public void run() {

        Set<CommandLine.Arg> parsedArgs = commandLine.getParsedArgs();

        if (parsedArgs.contains(new CommandLine.Arg('h'))
            || parsedArgs.contains(new CommandLine.Arg('u'))) {

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
                System.err.println(String.format(
                    "Unable to parse target URL.  Target URL: \"%s\"", urlString
                ));

                System.exit(4);
            }
            catch (IOException e) {
                System.err.println(String.format(
                    "Error reading URL.  Target URL: \"%s\"", urlString
                ));

                System.exit(5);
            }
        }

    }


    @Nonnull
    private String extractDomainName(@Nonnull final URL startUrl) {

        String[] split = startUrl.getHost().split("\\.");
        if (split.length < 2) {
            throw new RuntimeException(String.format(
                "URL must contain at least one '.' character.  URL: \"%s\"", startUrl
            ));
        }

        StringBuilder buf = new StringBuilder();
        buf.append(split[split.length - 2]).append(".").append(split[split.length - 1]);

        return buf.toString().toLowerCase();
    }


    private void webCrawl(
        @Nonnull final URL startUrl, @Nonnull final String domainNameLimit, int depth,
        @Nonnull final Set<URL> siteMapUrls, @Nonnull final PrintStream printStream
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
                    System.err.println(String.format(
                        "Error crawling url.  URL: \"%s\"", url.toString()
                    ));
                }
            });
    }


    @Nonnull
    private Set<URL> listUrls(@Nonnull final URL url) throws IOException {

        Set<URL> urls = new HashSet<>();

        String line;
        try (BufferedReader reader =
                 new BufferedReader(new InputStreamReader(url.openStream(), Charset.defaultCharset()))) {

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
        }

        return urls;
    }
}
