/*
 * Copyright (C) 2016 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.qaware.chronix.solr.query.analysis.functions.aggregations

import de.qaware.chronix.solr.query.analysis.functions.FunctionType
import de.qaware.chronix.timeseries.MetricTimeSeries
import spock.lang.Specification

/**
 * Unit test for the sum aggregation
 * @author f.lautenschlager
 */
class SumTest extends Specification {


    def "test execute"() {
        given:
        MetricTimeSeries.Builder timeSeries = new MetricTimeSeries.Builder("Sum");
        10.times {
            timeSeries.point(it, it * 10)
        }
        timeSeries.point(11, 9999)
        MetricTimeSeries ts = timeSeries.build()


        when:
        def result = new Sum().execute(ts)
        then:
        result == 10449.0d
    }

    def "test exception behaviour"() {
        when:
        new Sum().execute(new MetricTimeSeries[0])
        then:
        thrown IllegalArgumentException.class
    }

    def "test for empty time series"() {
        when:
        def result = new Sum().execute([new MetricTimeSeries.Builder("Empty").build()] as MetricTimeSeries[])
        then:
        result == Double.NaN
    }

    def "test subquery"() {
        expect:
        !new Sum().needSubquery()
        new Sum().getSubquery() == null
    }

    def "test arguments"() {
        expect:
        new Sum().getArguments().length == 0
    }

    def "test type"() {
        expect:
        new Sum().getType() == FunctionType.SUM
    }

    def "test equals and hash code"() {
        expect:
        def sum = new Sum();
        !sum.equals(null)
        !sum.equals(new Object())
        sum.equals(sum)
        sum.equals(new Sum())
        new Sum().hashCode() == new Sum().hashCode()
    }
}
