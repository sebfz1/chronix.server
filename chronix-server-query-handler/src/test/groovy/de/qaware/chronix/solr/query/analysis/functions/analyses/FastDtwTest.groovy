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
package de.qaware.chronix.solr.query.analysis.functions.analyses

import de.qaware.chronix.solr.query.analysis.functions.FunctionType
import de.qaware.chronix.timeseries.MetricTimeSeries
import spock.lang.Specification

/**
 * Unit test for the fast dtw analysis
 * @author f.lautenschlager
 */
class FastDtwTest extends Specification {
    def "test execute"() {
        given:
        MetricTimeSeries.Builder timeSeries = new MetricTimeSeries.Builder("FastDTW");
        10.times {
            timeSeries.point(it, it + 10)
        }
        timeSeries.point(11, 9999)
        MetricTimeSeries ts1 = timeSeries.build()
        MetricTimeSeries ts2 = timeSeries.build()


        when:
        def result = new FastDtw("", 5, 20).execute(ts1, ts2)
        then:
        result
    }

    def "test time series with equal timestamps"() {
        MetricTimeSeries.Builder timeSeries = new MetricTimeSeries.Builder("FastDTW-1");
        timeSeries.point(0, 2)
        3.times {
            timeSeries.point(1, it)
        }
        timeSeries.point(2, 2)

        def ts1 = timeSeries.build()

        when:
        def result = new FastDtw("", 5, 0).execute(ts1, ts1)

        then:
        result
    }

    def "test execute for with -1 as result"() {
        given:
        MetricTimeSeries.Builder timeSeries = new MetricTimeSeries.Builder("FastDTW-1");
        MetricTimeSeries.Builder secondTimeSeries = new MetricTimeSeries.Builder("FastDTW-2");
        10.times {
            timeSeries.point(it, it * 10)
            secondTimeSeries.point(it, it * -10)
        }
        def ts1 = timeSeries.build()
        def ts2 = secondTimeSeries.build()


        when:
        def result = new FastDtw("", 5, 0).execute(ts1, ts2)
        then:
        !result

    }

    def "test exception behaviour"() {
        when:
        new FastDtw("", 5, 20).execute(new MetricTimeSeries[0])
        then:
        thrown IllegalArgumentException.class
    }

    def "test subquery"() {
        expect:
        new FastDtw("", 5, 20).needSubquery()
        new FastDtw("", 5, 20).getArguments().size() == 3
        new FastDtw("(query)", 5, 20).getSubquery().equals("(query)")
    }

    def "test arguments"() {
        expect:
        new Outlier().getArguments().length == 0
    }

    def "test type"() {
        expect:
        new FastDtw("", 5, 20).getType() == FunctionType.FASTDTW
    }

    def "test equals and hash code"() {
        when:
        def equals = dtw1.equals(dtw2)
        def dtw1Hash = dtw1.hashCode()
        def dtw2Hash = dtw2.hashCode()

        then:
        dtw1.equals(dtw1)
        !dtw1.equals(new Object())
        !dtw1.equals(null)
        equals == result
        dtw1Hash == dtw2Hash == result

        where:
        dtw1 << [new FastDtw("", 5, 20), new FastDtw("metric:a", 5, 20), new FastDtw("metric:a", 5, 20), new FastDtw("metric:a", 5, 20)]
        dtw2 << [new FastDtw("", 5, 20), new FastDtw("metric:b", 5, 20), new FastDtw("metric:a", 6, 20), new FastDtw("metric:a", 5, 21)]

        result << [true, false, false, false]
    }

    def "test to string"() {
        when:
        def stringRepresentation = new FastDtw("metric:a", 5, 20).toString()
        then:
        stringRepresentation.contains("metric:a")
        stringRepresentation.contains("5")
        stringRepresentation.contains("20")
    }
}
