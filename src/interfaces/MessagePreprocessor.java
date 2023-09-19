package interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public interface MessagePreprocessor {
    public void start();
    public void stop();
    public Optional<Collection<? extends String>> getResult();
    List<BlockingQueue<String>> getState();
}
