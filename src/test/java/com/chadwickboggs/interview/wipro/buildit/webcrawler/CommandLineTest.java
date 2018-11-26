package com.chadwickboggs.interview.wipro.buildit.webcrawler;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class CommandLineTest {


    private CommandLine commandLine;


    @Before
    public void setup() {

        commandLine = new CommandLine();

    }


    @Test
    public void testRegisterArg() {

        registerArgs();

        Map<String, CommandLine.Arg> registeredArgs = commandLine.getRegisteredArgs();

        assertNotNull(registeredArgs);
        assertTrue(registeredArgs.size() > 0);

    }


    @Test( expected = ArgsInvalidException.class )
    public void testMissingArgs() throws ArgsInvalidException {

        registerArgs();
        Set<CommandLine.Arg> args = commandLine.parseArgs("");

    }


    @Test
    public void testValidArgsNoArguments() throws ArgsInvalidException {

        registerArgs();
        Set<CommandLine.Arg> args = commandLine.parseArgs("-h");

        assertNotNull(args);
        assertEquals(1, args.size());

    }


    @Test
    public void testValidArgsWithArguments() throws ArgsInvalidException {

        registerArgs();
        Set<CommandLine.Arg> args = commandLine.parseArgs("-t", "http://chadwickboggs.com");

        assertNotNull(args);
        assertEquals(1, args.size());
        CommandLine.Arg arg = args.iterator().next();
        assertTrue(arg instanceof CommandLine.ArgWithArgument);
        String argument = ((CommandLine.ArgWithArgument) arg).getArgument();
        assertNotNull(argument);
        assertTrue(argument.length() > 0);


    }


    private void registerArgs() {

        commandLine.registerArg(new CommandLine.Arg('h', "help", false));
        commandLine.registerArg(new CommandLine.Arg('u', "usage", false));
        commandLine.registerArg(new CommandLine.ArgWithArgument(
            't', "target", false, ""
        ));
        commandLine.setRequiredCount(1);
    }

}
