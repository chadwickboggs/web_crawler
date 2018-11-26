package com.chadwickboggs.interview.wipro.buildit.webcrawler;

import org.junit.Test;


public class WebCrawlerTest {

    @Test
    public void testRun() throws ArgsInvalidException {

        String[] runArgs = new String[] {"-t", "http://chadwickboggs.com"};
        new WebCrawler(WebCrawler.parseCommandLineArguments(runArgs)).run();

    }

}
