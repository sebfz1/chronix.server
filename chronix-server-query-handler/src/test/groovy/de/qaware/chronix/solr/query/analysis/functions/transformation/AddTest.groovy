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
package de.qaware.chronix.solr.query.analysis.functions.transformation

import de.qaware.chronix.solr.query.analysis.functions.FunctionType
import de.qaware.chronix.timeseries.MetricTimeSeries
import spock.lang.Specification

/**
 * Unit test for the add transformation
 * @author f.lautenschlager
 */
class AddTest extends Specification {
    def "test transform"() {
        given:
        def timeSeriesBuilder = new MetricTimeSeries.Builder("Add")
        10.times {
            timeSeriesBuilder.point(it * 100, it + 10)
        }
        timeSeriesBuilder.point(10 * 100, -10)
        def timeSeries = timeSeriesBuilder.build()

        def add = new Add(4);
        when:
        add.transform(timeSeries)
        then:
        timeSeries.size() == 11
        timeSeries.getValue(1) == 1 + 10 + 4

        timeSeries.getValue(10) == -6
    }

    def "test getType"() {
        expect:
        new Add(4).getType() == FunctionType.ADD
    }

    def "test getArguments"() {
        expect:
        new Add(4).getArguments()[0] == "value=4.0"
    }

    def "test equals and hash code"() {
        expect:
        def function = new Add(4);
        !function.equals(null)
        !function.equals(new Object())
        function.equals(function)
        function.equals(new Add(4))
        new Add(4).hashCode() == new Add(4).hashCode()
        new Add(4).hashCode() != new Add(2).hashCode()
    }

    def "test string representation"() {
        expect:
        def string = new Add(4).toString()
        string.contains("value")
    }
}
