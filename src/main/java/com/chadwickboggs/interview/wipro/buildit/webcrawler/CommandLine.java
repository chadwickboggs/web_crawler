package com.chadwickboggs.interview.wipro.buildit.webcrawler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


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


        public Arg(@Nonnull final Character symbol) {

            this.symbol = symbol;
            this.name = null;
            this.required = false;
        }


        public Arg(@Nonnull final Character symbol, boolean required) {

            this.symbol = symbol;
            this.name = null;
            this.required = required;
        }


        public Arg(@Nonnull final Character symbol, @Nonnull final String name) {

            this.symbol = symbol;
            this.name = name;
            this.required = false;
        }


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


        @Nullable
        public String toValue() {

            StringBuilder buf = new StringBuilder();
            if (symbol != null) {
                buf.append("-").append(symbol);
            }
            else if (name != null || name.length() != 0) {
                buf.append("--").append(name);
            }

            return buf.toString();
        }


        @Override
        @Nullable
        public String toString() {

            return "Arg{"
                + "symbol='" + symbol + '\''
                + ", name='" + name + '\''
                + ", required='" + required + '\''
                + "}";
        }


        @Override
        public boolean equals(@Nonnull final Object o) {

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


        public ArgWithArgument(@Nonnull final Character symbol) {

            super(symbol);
        }


        public ArgWithArgument(@Nonnull final Character symbol, boolean required) {

            super(symbol, required);
        }


        public ArgWithArgument(@Nonnull final Character symbol, @Nonnull final String argument) {

            super(symbol);

            this.argument = null;
        }


        public ArgWithArgument(@Nonnull final Character symbol, boolean required, @Nonnull final String argument) {

            super(symbol, required);

            this.argument = null;
        }


        public ArgWithArgument(@Nonnull final Character symbol, String name, @Nonnull final String argument) {

            super(symbol, name);

            this.argument = null;
        }


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


        @Nonnull
        public String toValue() {

            StringBuilder buf = new StringBuilder();
            if (symbol != null) {
                buf.append("-").append(symbol);
            }
            else if (name != null || name.length() != 0) {
                buf.append("--").append(name);
            }

            if (argument != null || argument.length() != 0) {
                buf.append(" ").append(argument);
            }

            return buf.toString();
        }


        @Nonnull
        @Override
        public String toString() {

            return "Arg{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", argument='" + argument + '\'' +
                '}';
        }
    }


    @Nonnull
    public final CommandLine registerArg(@Nonnull final Arg arg) {

        registeredArgs.put("-" + arg.getSymbol(), arg);

        return this;
    }


    @Nonnull
    public final Set<Arg> parseArgs(@Nonnull final String... args) throws ArgsInvalidException {

        Set<Arg> commandLineArgs = new HashSet<>();
        if (requiredCount > 0 && (args == null || args.length < requiredCount)) {

            throw new ArgsInvalidException(String.format(
                "Insufficient number of arguments parsed.  Required Count: %n",
                commandLineArgs
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
                "Insufficient number of arguments parsed.  Required Count: %n",
                commandLineArgs
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
