package implementation;

import interfaces.MessagePreprocessor;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.UnaryOperator;

public class ConcreteMessagePreprocessor implements MessagePreprocessor {
    private final List<BlockingQueue<String>> queues;
    private final List<Transformer> transformers;
    private boolean isCompleted = false;

    public ConcreteMessagePreprocessor(Collection<String> initial,
                                       List<UnaryOperator<String>> functions){
        if (initial == null || functions == null || initial.isEmpty() || functions.isEmpty()){
            throw new IllegalArgumentException("Some of specified arguments were either null or empty");
        }

        int numQueues = functions.size() + 2; // source + destination + rest intermediate
        int capacity = initial.size() / 2;

        queues = new ArrayList<>(numQueues);
        transformers = new ArrayList<>(functions.size());

        prepareQueues(capacity, numQueues, initial);
        prepareTransformers(functions);
    }

    private void prepareQueues(int capacity, int numQueues, Collection<String> messages){
        for (int i=0; i < numQueues; i++){
            if (i == 0){
                queues.add(new LinkedBlockingQueue<>(messages));
            } else if (i == numQueues - 1) {
                queues.add(new LinkedBlockingQueue<>(messages.size()));
            } else{
                queues.add(new LinkedBlockingQueue<>(capacity));
            }
            BlockingQueue<String> input_queue = queues.get(0);
            input_queue.addAll(messages);
        }
    }

    private void prepareTransformers(List<UnaryOperator<String>> functions){
        for (int i=0; i < functions.size(); i++){
            UnaryOperator<String> op = functions.get(i);
            //i+1 on the last iteration is resulting queue (i=0 is input messages queue)
            //the last queue is used as storage for last operation results and thus is the resulting queue
            Transformer transformer = new Transformer(op, queues.get(i), queues.get(i+1));
            transformers.add(transformer);
        }
    }

    @Override
    public void start() {
        for (Transformer transformer : transformers){
            transformer.start();
        }
    }

    @Override
    public void stop() {
        for (Transformer transformer : transformers){
            transformer.interrupt();
        }
        isCompleted = true;
    }

    @Override
    public Optional<Collection<? extends String>> getResult() {
        if (!isCompleted) {
            return Optional.empty();
        }

        List<String> result = new ArrayList<>();
        BlockingQueue<String> destinationQueue = queues.get(queues.size() - 1);
        try {
            while (true) {
                String message = destinationQueue.take();
                System.out.println(message);
                if (message.equals("empty")) {
                    break;
                }
                result.add(message);
            }
        } catch (InterruptedException e) {
        }
        return Optional.of(Collections.unmodifiableCollection(result));
    }

    @Override
    public List<BlockingQueue<String>> getState() {
        return Collections.unmodifiableList(queues);
    }
}
