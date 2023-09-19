import implementation.ConcreteMessagePreprocessor;
import interfaces.MessagePreprocessor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class Demo {
    public static UnaryOperator<String> trimSpaces = message -> message.replaceAll("\\s+", " ");
    public static UnaryOperator<String> removeLinks = message -> message.replaceAll("http://\\S+|https://\\S+", "");
    public static UnaryOperator<String> toLower = String::toLowerCase;
    public static UnaryOperator<String> removePunctuation = message -> message.replaceAll("\\p{Punct}", "");
    public static void main(String[] args) {
        // Sample input messages
        Collection<String> messages = Arrays.asList(
                "Logback-classic will automatically ask the web-server to install a LogbackServletContainerInitializer (https://logback.qos.ch/apidocs/ch/qos/logback/classic/servlet/LogbackServletContainerInitializer.html) implementing the ServletContainerInitializer interface (available in servlet-api 3.x and later) .",
                "This initializer will in turn install and instance of LogbackServletContextListener (https://logback.qos.ch/apidocs/ch/qos/logback/classic/servlet/LogbackServletContextListener.html) .",
                "This listener will stop the current logback-classic context when the web-app is stopped or reloaded.",
                "empty"
        );

        // Create a list of processing functions
        List<UnaryOperator<String>> functions = Arrays.asList(trimSpaces, removeLinks, toLower, removePunctuation);

        // Create a MessagePreprocessor
        MessagePreprocessor preprocessor = new ConcreteMessagePreprocessor(messages, functions);

        // Start processing
        preprocessor.start();

        // Wait for processing to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop processing
        preprocessor.stop();
        Optional<Collection<? extends String>> result = preprocessor.getResult();
        if (result.isPresent()) {
            for (String processedMessage : result.get()) {
                System.out.println(processedMessage);
            }
        } else {
            System.out.println("Message processing was not completed.");
        }
    }
}