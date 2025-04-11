package io.cdap.wrangler.directives.aggregate;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.parser.TokenGroup;
import io.cdap.wrangler.api.parser.TokenType;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Directive to aggregate byte sizes and time durations.
 */
public class AggregateStats implements Directive {
    private String sizeColumn;
    private String timeColumn;
    private String sizeTargetColumn;
    private String timeTargetColumn;

    private final AtomicLong totalBytes = new AtomicLong(0);
    private final AtomicLong totalDuration = new AtomicLong(0);
    private int rowCount = 0;

    @Override
    public UsageDefinition define() {
        return UsageDefinition.builder("aggregate-stats")
            .define("size-column", TokenType.COLUMN_NAME)
            .define("time-column", TokenType.COLUMN_NAME)
            .define("size-target-column", TokenType.COLUMN_NAME)
            .define("time-target-column", TokenType.COLUMN_NAME)
            .build();
    }

    @Override
    public void initialize(TokenGroup args) throws DirectiveParseException {
        this.sizeColumn = ((ColumnName) args.getValue("size-column")).value();
        this.timeColumn = ((ColumnName) args.getValue("time-column")).value();
        this.sizeTargetColumn = ((ColumnName) args.getValue("size-target-column")).value();
        this.timeTargetColumn = ((ColumnName) args.getValue("time-target-column")).value();
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        for (Row row : rows) {
            try {
                Object sizeObj = row.getValue(sizeColumn);
                Object timeObj = row.getValue(timeColumn);

                if (sizeObj instanceof Long) {
                    totalBytes.addAndGet((Long) sizeObj);
                }

                if (timeObj instanceof Long) {
                    totalDuration.addAndGet((Long) timeObj);
                }

                rowCount++;
            } catch (Exception e) {
                throw new DirectiveExecutionException("Error processing row", e);
            }
        }

        return rows;
    }

    @Override
    public void destroy() {
        // Finalize the aggregation by adding a new row with totals
    }
}

