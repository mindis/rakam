package org.rakam.analysis;

import org.junit.Test;
import org.rakam.analysis.query.simple.SimpleFieldScript;
import org.rakam.analysis.rule.aggregation.MetricAggregationRule;
import org.rakam.constant.AggregationAnalysis;
import org.rakam.constant.AggregationType;
import org.rakam.util.json.JsonArray;
import org.rakam.util.json.JsonObject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.rakam.constant.AggregationType.AVERAGE_X;
import static org.rakam.constant.AggregationType.MINIMUM_X;
import static org.rakam.util.TimeUtil.UTCTime;

/**
 * Created by buremba <Burak Emre Kabakcı> on 23/09/14 21:58.
 */
public class GroupingMetricAnalysisTest extends AnalysisBaseTest {

    @Test
    public void testCountAggregation() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AggregationType.COUNT, null, null, new SimpleFieldScript<String>("key"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 0; i < 10000; i++)
            eventAggregator.aggregate(projectId, new JsonObject().put("key", "value" + i % 5), "actor" + i, UTCTime());

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.COUNT.name());

        JsonObject data = eventAnalyzer.analyze(query);

        JsonObject json = new JsonObject();
        for (int i = 0; i < 5; i++) {
            json.put("value"+i, 2000L);
        }
        assertEquals(new JsonObject().put("result", json), data);
    }

    @Test
    public void testCountXAggregation() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AggregationType.COUNT_X, new SimpleFieldScript<String>("key2"), null, new SimpleFieldScript<String>("key1"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 0; i < 10000; i++) {
            eventAggregator.aggregate(projectId, new JsonObject().put("key1", "value" + i % 5).put("key2", "value" + (i % 5)), "actor" + i, UTCTime());
            eventAggregator.aggregate(projectId, new JsonObject().put("key1", "value" + i % 5), "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.COUNT_X.name());
        JsonObject data = eventAnalyzer.analyze(query);

        JsonObject json = new JsonObject();
        for (int i = 0; i < 5; i++) {
            json.put("value"+i, 2000);
        }
        assertEquals(new JsonObject().put("result", json), data);    }


    @Test
    public void testSumXAggregation() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AggregationType.SUM_X, new SimpleFieldScript<String>("key2"), null, new SimpleFieldScript<String>("key1"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 0; i < 10000; i++) {
            JsonObject m = new JsonObject().put("key1", "value" + i % 5).put("key2", (i + 1) % 5);
            eventAggregator.aggregate(projectId, m, "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.SUM_X.name());
        JsonObject data = eventAnalyzer.analyze(query);

        JsonObject jsonObject = new JsonObject()
                .put("value0", 2000L)
                .put("value2", 6000L)
                .put("value1", 4000L)
                .put("value4", 0L)
                .put("value3", 8000L);
        assertEquals(data, new JsonObject().put("result",jsonObject));
    }


    @Test
    public void testMaxXAggregation() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AggregationType.MAXIMUM_X, new SimpleFieldScript<String>("test"),null, new SimpleFieldScript<String>("key1"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 0; i < 10000; i++) {
            eventAggregator.aggregate(projectId, new JsonObject().put("test", i).put("key1", "value"+i%5), "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.MAXIMUM_X.name());
        JsonObject data = eventAnalyzer.analyze(query);

        JsonObject jsonObject = new JsonObject();
        for (long i = 0; i < 5; i++) {
            jsonObject.put("value"+i, 9995+i);
        }

        assertEquals(data, new JsonObject().put("result",jsonObject));
    }


    @Test
    public void testMinXAggregation() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, MINIMUM_X, new SimpleFieldScript<String>("test"), null, new SimpleFieldScript<String>("key1"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 10000; i < 20000; i++) {
            JsonObject m = new JsonObject().put("test", 100+(i % 10)).put("key1", "value" + i % 10);
            eventAggregator.aggregate(projectId, m, "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.MINIMUM_X.name());
        JsonObject data = eventAnalyzer.analyze(query);
        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < 10; i++) {
            jsonObject.put("value"+i, 100+i);
        };
        assertEquals(data, new JsonObject().put("result",jsonObject));
    }


    @Test
    public void testAverageXAggregation() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AVERAGE_X, new SimpleFieldScript<String>("test"), null, new SimpleFieldScript<String>("key1"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 0; i < 20000; i++) {
            JsonObject m = new JsonObject().put("test", i).put("key1", "value" + i % 10);;
            eventAggregator.aggregate(projectId, m, "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.AVERAGE_X.name());
        JsonObject data = eventAnalyzer.analyze(query);

        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < 10; i++) {
            jsonObject.put("value"+i, 9995+i);
        }

        assertEquals(data, new JsonObject().put("result", jsonObject));
    }


    @Test
    public void testAverageXAggregation_whenSumX() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AVERAGE_X, new SimpleFieldScript<String>("test"), null, new SimpleFieldScript<String>("key1"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 0; i < 10000; i++) {
            JsonObject m = new JsonObject().put("test", i).put("key1", "value" + i % 10);
            eventAggregator.aggregate(projectId, m, "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.SUM_X.name());
        JsonObject data = eventAnalyzer.analyze(query);
        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < 10; i++) {
            jsonObject.put("value"+i, 4995000L+(i*1000));
        }
        assertEquals(data, new JsonObject().put("result", jsonObject));
    }

    @Test
    public void testAverageXAggregation_whenCountX() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AVERAGE_X, new SimpleFieldScript<String>("test"), null, new SimpleFieldScript<String>("key1"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 0; i < 10000; i++) {
            JsonObject m = new JsonObject().put("test", i).put("key1", "value" + i % 10);
            eventAggregator.aggregate(projectId, m, "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.COUNT_X.name());
        JsonObject data = eventAnalyzer.analyze(query);
        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < 10; i++) {
            jsonObject.put("value"+i, 1000L);
        }
        assertEquals(data, new JsonObject().put("result", jsonObject));
    }

    @Test
    public void testUniqueXAggregation_countUniqueX() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AggregationType.UNIQUE_X, new SimpleFieldScript<String>("test"), null, new SimpleFieldScript<String>("key1"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 0; i < 10000; i++) {
            JsonObject m = new JsonObject().put("test", "value"+i).put("key1", "value" + i % 10);
            eventAggregator.aggregate(projectId, m, "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.COUNT_UNIQUE_X.name());
        JsonObject data = eventAnalyzer.analyze(query);
        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < 10; i++) {
            jsonObject.put("value"+i, 1000L);
        }
        assertEquals(data, new JsonObject().put("result", jsonObject));
    }

    @Test
    public void testUniqueXAggregation_selectUniqueX() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AggregationType.UNIQUE_X, new SimpleFieldScript<String>("test"), null, new SimpleFieldScript<String>("key1"));
        analysisRuleMap.add(projectId, rule);

        for (long i = 0; i < 10000; i++) {
            JsonObject m = new JsonObject().put("test", "value"+i % 100).put("key1", "value" + i % 10);
            eventAggregator.aggregate(projectId, m, "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.SELECT_UNIQUE_X.name());
        JsonObject data = eventAnalyzer.analyze(query);
        JsonObject result = data.getJsonObject("result");
        assertNotNull(result);

        for (long i = 0; i < 10; i++) {
            JsonArray array = result.getJsonArray("value" + i);
            assertNotNull(array);
            for (int i1 = 0; i1 < 10; i1++) {
                assertTrue(array.contains("value" + (i1 * 10 + i)));
            }
            assertEquals(array.size(), 10);
        }
        assertEquals(result.size(), 10);
    }

    @Test
    public void testUniqueXAggregation_attributeBelongsUser() {
        String projectId = randomString(10);
        MetricAggregationRule rule = new MetricAggregationRule(projectId, AggregationType.UNIQUE_X, new SimpleFieldScript<String>("_user.test"), null, new SimpleFieldScript<String>("_user.key1"));
        analysisRuleMap.add(projectId, rule);

        for (int i = 0; i < 100; i++) {
            JsonObject map = new JsonObject();
            map.put("test", "value" + (i % 20));
            map.put("key1", "value" + (i % 10));
//            databaseAdapter.createActor(projectId, "actor" + i, map);
        }

        for (long i = 0; i < 10000; i++) {
            JsonObject actor = new JsonObject().put("actor", "actor" + (i % 100));
            eventAggregator.aggregate(projectId, actor, "actor" + i, UTCTime());
        }

        collector.process(analysisRuleMap.entrySet());

        JsonObject query = new JsonObject().put("tracker", projectId).mergeIn(rule.toJson())
                .put("analysis_type", AggregationAnalysis.SELECT_UNIQUE_X.name());
        JsonObject data = eventAnalyzer.analyze(query);
        JsonObject result = data.getJsonObject("result");
        assertNotNull(result);

        for (long i = 0; i < 10; i++) {
            JsonArray array = result.getJsonArray("value" + i);
            assertNotNull(array);
            for (int i1 = 0; i1 < 2; i1++) {
                assertTrue(array.contains("value" + (i1 * 10 + i)));
            }
            assertEquals(array.size(), 2);
        }
        assertEquals(result.size(), 10);
    }
}
