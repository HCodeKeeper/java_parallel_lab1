package interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public interface MessagePreprocessor {
    void start();
    void stop();
    Optional<Collection<? extends String>> getResult();
    List<BlockingQueue<String>> getState();
}
