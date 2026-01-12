package uk.ac.ed.inf;

import uk.ac.ed.inf.dataTypes.OrderValidationResult;

public record PairwiseCase(OrderTestBuilder builder, OrderValidationResult expected) {
}
