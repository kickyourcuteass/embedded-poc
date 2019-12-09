package ro.home.job.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
class ChunkCompletionListener implements ChunkListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkCompletionListener.class);

    private AtomicLong chunkCounter = new AtomicLong(0);

    /**
     * Callback that executes before each batch set.
     *
     * @param context
     */
    @Override
    public void beforeChunk(ChunkContext context) {
        LOGGER.debug("Before chunk number {}", chunkCounter.incrementAndGet());
    }

    /**
     * Callback that executes after each batch set.
     *
     * @param context
     */
    @Override
    public void afterChunk(ChunkContext context) {
        LOGGER.debug("After chunk number {}", chunkCounter.get());
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
