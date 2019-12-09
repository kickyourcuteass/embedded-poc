package ro.home.job.step.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import org.springframework.core.io.FileSystemResource;
import ro.home.job.model.FxMarketEvent;

public class FxMarketEventReader extends FlatFileItemReader<FxMarketEvent> {

    public FxMarketEventReader(String resourcePath) {
        super();
        this.setResource(new FileSystemResource(resourcePath));
        this.setLinesToSkip(1);
        this.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"stock", "time", "price", "shares"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(FxMarketEvent.class);
            }});
        }});
    }

    @Override
    public FxMarketEvent read() throws Exception {
        return super.read();
    }
}
