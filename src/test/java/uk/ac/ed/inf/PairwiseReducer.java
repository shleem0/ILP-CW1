package uk.ac.ed.inf;

import uk.ac.ed.inf.dataTypes.OrderValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class PairwiseReducer {

    static Stream<PairwiseCase> pairwiseOrders() {
        List<PairwiseCase> cases = new ArrayList<>();

        for (var card : OrderTestBuilder.CardNum.values())
            for (var cvv : OrderTestBuilder.CVV.values())
                for (var expiry : OrderTestBuilder.Expiry.values())
                    for (var count : OrderTestBuilder.PizzaCount.values())
                        for (var rest : OrderTestBuilder.Restaurant.values())
                            for (var def : uk.ac.ed.inf.OrderTestBuilder.PizzaDef.values())
                                for (var price : OrderTestBuilder.PizzaPrice.values())
                                    for (var total : OrderTestBuilder.Total.values())
                                        for (var time : OrderTestBuilder.OrderTime.values()) {

                                            OrderTestBuilder b = new OrderTestBuilder(card, cvv, expiry,
                                                    count, rest, def, price, total, time);

                                            OrderValidationResult expected = ResultOracle.evaluate(b);

                                            cases.add(new PairwiseCase(b, expected));
                                        }

        return PairwiseReducer.reduce(cases).stream();
    }

    static List<PairwiseCase> reduce(List<PairwiseCase> all) {

        Set<Pair> uncovered = PairUniverse.allPairs();
        List<PairwiseCase> result = new ArrayList<>();

        while (!uncovered.isEmpty()) {

            PairwiseCase best = null;
            int bestCount = 0;

            for (var c : all) {
                Set<Pair> candidatePairs = PairUniverse.coveredBy(c.builder());
                candidatePairs.retainAll(uncovered);   // only count the uncovered ones
                int hits = candidatePairs.size();

                if (hits > bestCount) {
                    best = c;
                    bestCount = hits;
                }
            }

            result.add(best);
            uncovered.removeAll(PairUniverse.coveredBy(best.builder()));
            all.remove(best);
        }

        return result;
    }
}
