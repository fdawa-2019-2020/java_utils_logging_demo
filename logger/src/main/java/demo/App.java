package demo;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import static java.util.stream.Collectors.*;
import java.util.stream.IntStream;

/**
 * Demo application
 */
public final class App {
    private List<Logger> loggers;
    private Logger rootHandler;


    /**
     */
    public static void main(String[] args) {
        App app = new App();
        app.init();
        app.run();
    }

    /**
     * Init the demonstration
     */
    private void init() {
        initLoggers();
        initConsoleHandler();
    }

    /**
     * Init the console Handler
     */
    private void initConsoleHandler() {
        rootHandler = Logger.getLogger("");
        Handler handler = rootHandler.getHandlers()[0];
        initFormat(handler);
        allowConsoleToShowAllLevel(handler);
    }

    private void allowConsoleToShowAllLevel(Handler handler) {
        handler.setLevel(Level.ALL);
    }


    private void initFormat(Handler handler) {
        handler.setFormatter(new SimpleFormatter(){
            public String format(LogRecord rec) {
                return String.format("[%1$-7s] [%2$-12s] %3$s %n", rec.getLevel().getName(), rec.getLoggerName(), rec.getMessage());
            }
        });
    }

    private void initLoggers() {
        this.loggers = Arrays.asList(
                new String[] { "org", "org.foo", "org.foo.bar" })
                .stream()
                .map(Logger::getLogger)
                .collect(toList());

    }

    /**
     * Runs the demo
     */
    private void run() {

        showAllLoggers();

        doAllLogMessageAtLevel(Level.INFO, "1st message");

        switchLoggerToLevel( Logger.getLogger(""), Level.SEVERE);

        doAllLogMessageAtLevel(Level.INFO, "2nd message, you won't see me :)");

        doAllLogMessageToSeveralLevels("3rd message", Level.FINE, Level.INFO, Level.SEVERE);


        switchLoggerToLevel( loggers.get(1), Level.INFO);

        doAllLogMessageToSeveralLevels("4th message", Level.FINE, Level.INFO, Level.SEVERE);



        switchLoggerToLevel( loggers.get(2), Level.FINE);

        doAllLogMessageToSeveralLevels("5th message", Level.FINE, Level.INFO, Level.SEVERE);

        showAllLoggers();

    }

    private void switchLoggerToLevel(Logger logger, Level level) {
        System.out.println(String.format("------------------------------ Switch [%12s] to [%12s] %n",logger.getName(), level));
        logger.setLevel(level);        
    }

    private void showAllLoggers() {
        System.out.println("******************* SHOW all loggers *****************");
        this.loggers.stream().forEach( logger -> {
            System.out.println(String.format("Logger [%1$-12s], Level [%2$-12s]", logger.getName(), logger.getLevel()));
        });
    }

    /**
     * Makes all loggers emit the same message at the same level
     * 
     * @param level
     * @param msg
     */
    private void doAllLogMessageAtLevel(Level level, String msg) {
        IntStream.range(0, this.loggers.size())
            .forEach(idx -> {
                loggers.get(idx).log(level, msg);
            });
    }


    /**
     * Makes a logger logs a message on all given levels
     */
    private void makeLogMessageAtSeveralLevel(Logger logger, String msg, Level...levels) {
        for (Level level: levels) {
            logger.log(level, msg);
        }
    }

    private void doAllLogMessageToSeveralLevels(String msg, Level...levels) {
        System.out.println("~~~~~~~~~~~~~~~~ Log "+msg+" on levels "+Arrays.asList(levels).stream().map(Level::getName).collect(joining(" ")));
        for (Logger logger : loggers) {            
            makeLogMessageAtSeveralLevel(logger, msg, levels);
        }
    }

}
