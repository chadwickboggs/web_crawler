package com.chadwickboggs.interview.wipro.buildit.webcrawler;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class CommandLine {

    private Map<String, Arg> registeredArgs = new HashMap<>();
    private Set<Arg> parsedArgs = new HashSet<>();
    private int requiredCount;


    public void setRequiredCount(int requiredCount) {

        this.requiredCount = requiredCount;
    }


    public int getRequiredCount() {
        return requiredCount;
    }


    public static class Arg {

        protected final Character symbol;
        protected final String name;
        protected final boolean required;


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         */
        public Arg(@Nonnull final Character symbol) {

            this.symbol = symbol;
            this.name = null;
            this.required = false;
        }


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         * @param required true if this command line option is a required option.
         */
        public Arg(@Nonnull final Character symbol, boolean required) {

            this.symbol = symbol;
            this.name = null;
            this.required = required;
        }


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         * @param name the name.
         */
        public Arg(@Nonnull final Character symbol, @Nonnull final String name) {

            this.symbol = symbol;
            this.name = name;
            this.required = false;
        }


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         * @param name the name.
         * @param required true if this command line option is a required option.
         */
        public Arg(@Nonnull final Character symbol, @Nonnull final String name, boolean required) {

            this.symbol = symbol;
            this.name = name;
            this.required = required;
        }


        @Nullable
        public Character getSymbol() {
            return symbol;
        }


        @Nullable
        public String getName() {
            return name;
        }


        public boolean isRequired() {
            return required;
        }


        /**
         * Returns the command line format representation of this option.
         *
         * @return the command line format representation of this option.
         */
        @Nullable
        public String toValue() {

            StringBuilder buf = new StringBuilder();
            if (symbol != null) {
                buf.append("-").append(symbol);
            }
            else if (StringUtils.isNoneBlank(name)) {
                buf.append("--").append(name);
            }

            return buf.toString();
        }


        @Override
        @Nonnull
        public String toString() {

            return "Arg{"
                + "symbol='" + symbol + '\''
                + ", name='" + name + '\''
                + ", required='" + required + '\''
                + "}";
        }


        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Arg arg = (Arg) o;
            return symbol.equals(arg.symbol);
        }


        @Override
        public int hashCode() {

            return Objects.hash(symbol);
        }
    }


    public static final class ArgWithArgument extends Arg {

        private String argument;


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         */
        public ArgWithArgument(@Nonnull final Character symbol) {

            super(symbol);
        }


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         * @param required true if this command line option is a required option.
         */
        public ArgWithArgument(@Nonnull final Character symbol, boolean required) {

            super(symbol, required);
        }


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         * @param argument the argument.
         */
        public ArgWithArgument(@Nonnull final Character symbol, @Nonnull final String argument) {

            super(symbol);

            this.argument = null;
        }


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         * @param argument the argument.
         * @param required true if this command line option is a required option.
         */
        public ArgWithArgument(@Nonnull final Character symbol, boolean required, @Nonnull final String argument) {

            super(symbol, required);

            this.argument = null;
        }


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         * @param argument the argument.
         * @param name the name.
         */
        public ArgWithArgument(@Nonnull final Character symbol, String name, @Nonnull final String argument) {

            super(symbol, name);

            this.argument = null;
        }


        /**
         * Construct instance.
         *
         * @param symbol the symbol.
         * @param name the name.
         * @param argument the argument.
         * @param required true if this command line option is a required option.
         */
        public ArgWithArgument(
            @Nonnull final Character symbol, @Nonnull final String name, boolean required,
            @Nonnull final String argument) {

            super(symbol, name, required);

            this.argument = argument;
        }


        @Nullable
        public String getArgument() {
            return argument;
        }


        public void setArgument(@Nonnull final String argument) {
            this.argument = argument;
        }


        /**
         * Returns the command line format representation of this option.
         *
         * @return the command line format representation of this option.
         */
        @Nonnull
        public String toValue() {

            StringBuilder buf = new StringBuilder();
            if (symbol != null) {
                buf.append("-").append(symbol);
            }
            else if (StringUtils.isNoneBlank(name)) {
                buf.append("--").append(name);
            }

            if (StringUtils.isNoneBlank(argument)) {
                buf.append(" ").append(argument);
            }

            return buf.toString();
        }


        @Nonnull
        @Override
        public String toString() {

            return "Arg{"
                + "symbol='" + symbol + '\''
                + ", name='" + name + '\''
                + ", argument='" + argument + '\''
                + '}';
        }
    }


    /**
     * Register an a command line argument.
     *
     * @param arg the command line argument.
     * @return this command line instance.
     */
    @Nonnull
    public final CommandLine registerArg(@Nonnull final Arg arg) {

        registeredArgs.put("-" + arg.getSymbol(), arg);

        return this;
    }


    /**
     * Parse command line arguments into a collection of Arg instances.
     *
     * @param args the command line arguments.
     * @return the parsed Arg instances.
     * @throws ArgsInvalidException on invalid command line arguments.
     */
    @Nonnull
    public final Set<Arg> parseArgs(@Nonnull final String... args) throws ArgsInvalidException {

        Set<Arg> commandLineArgs = new HashSet<>();
        if (requiredCount > 0 && (args == null || args.length < requiredCount)) {

            throw new ArgsInvalidException(String.format(
                "Insufficient number of arguments parsed.  Required Count: %d",
                requiredCount
            ));
        }

        final ArgWithArgument[] lastArgWithArguments = new ArgWithArgument[1];
        Arrays.stream(args).forEach(arg -> {
            Optional<Arg> argOpt = parseArg(arg);
            if (argOpt.isPresent()) {
                Arg argObj = argOpt.get();
                commandLineArgs.add(argObj);

                if (argObj instanceof ArgWithArgument) {
                    lastArgWithArguments[0] = (ArgWithArgument) argObj;
                }
            }
            else if (lastArgWithArguments[0] != null) {
                lastArgWithArguments[0].setArgument(arg);
            }
        });

        if (commandLineArgs.size() < requiredCount) {
            throw new ArgsInvalidException(String.format(
                "Insufficient number of arguments parsed.  Required Count: %d",
                requiredCount
            ));
        }

        return parsedArgs = commandLineArgs;
    }


    @Nonnull
    private final Optional<Arg> parseArg(@Nonnull final String argString) {

        final String argStringTrimmed = argString.trim();
        if (argStringTrimmed.length() == 0) {
            return Optional.empty();
        }

        Arg arg = registeredArgs.get(argStringTrimmed);
        if (arg == null) {
            return Optional.empty();
        }

        return Optional.of(arg);
    }


    @Nonnull
    public Map<String, Arg> getRegisteredArgs() {
        return new HashMap(registeredArgs);
    }


    @Nonnull
    public Set<Arg> getParsedArgs() {

        return new HashSet<>(parsedArgs);
    }
}
