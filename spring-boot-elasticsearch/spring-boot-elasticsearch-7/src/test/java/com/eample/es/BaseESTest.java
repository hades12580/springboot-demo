package com.eample.es;

import com.eample.BaseTest;
import com.example.dto.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * <p>
 * ??????es??????
 * </p>
 *
 * @author MrWen
 **/
public class BaseESTest extends BaseTest {


    /**
     * 1)????????????
     */
    @Test
    public void indexCreate() throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // ????????????
        CreateIndexRequest request = new CreateIndexRequest("user");
        CreateIndexResponse createIndexResponse =
                esClient.indices().create(request, RequestOptions.DEFAULT);

        // ????????????
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println("???????????? ???" + acknowledged);

        esClient.close();
    }


    /**
     * 2)????????????
     */
    @Test
    public void indexSearch() throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // ????????????
        GetIndexRequest request = new GetIndexRequest("user");

        GetIndexResponse getIndexResponse =
                esClient.indices().get(request, RequestOptions.DEFAULT);

        // ????????????
        System.out.println(getIndexResponse.getAliases());
        System.out.println(getIndexResponse.getMappings());
        System.out.println(getIndexResponse.getSettings());

        esClient.close();
    }


    /**
     * 3)????????????
     */
    @Test
    public void indexDelete() throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // ????????????
        DeleteIndexRequest request = new DeleteIndexRequest("user");

        AcknowledgedResponse response = esClient.indices().delete(request, RequestOptions.DEFAULT);

        // ????????????
        System.out.println(response.isAcknowledged());

        esClient.close();
    }


    /**
     * 1.1 ????????????
     */
    @Test
    public void docInsert() throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // ????????????
        IndexRequest request = new IndexRequest();
        request.index("user").id("1001");

        User user = new User();
        user.setName("zhangsan");
        user.setAge(30);
        user.setSex("???");

        // ???ES???????????????????????????????????????JSON??????
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);
        request.source(userJson, XContentType.JSON);

        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);

        System.out.println(response.getResult());

        esClient.close();
    }

    /**
     * 1.2 ???????????????
     */
    @Test
    public void docInsertBatch() throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // ??????????????????
        BulkRequest request = new BulkRequest();

        request.add(new IndexRequest().index("user").id("1001").source(XContentType.JSON, "name", "zhangsan", "age", 30, "sex", "???"));
        request.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON, "name", "lisi", "age", 30, "sex", "???"));
        request.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON, "name", "wangwu", "age", 40, "sex", "???"));
        request.add(new IndexRequest().index("user").id("1004").source(XContentType.JSON, "name", "wangwu1", "age", 40, "sex", "???"));
        request.add(new IndexRequest().index("user").id("1005").source(XContentType.JSON, "name", "wangwu2", "age", 50, "sex", "???"));
        request.add(new IndexRequest().index("user").id("1006").source(XContentType.JSON, "name", "wangwu3", "age", 50, "sex", "???"));
        request.add(new IndexRequest().index("user").id("1007").source(XContentType.JSON, "name", "wangwu44", "age", 60, "sex", "???"));
        request.add(new IndexRequest().index("user").id("1008").source(XContentType.JSON, "name", "wangwu555", "age", 60, "sex", "???"));
        request.add(new IndexRequest().index("user").id("1009").source(XContentType.JSON, "name", "wangwu66666", "age", 60, "sex", "???"));

        BulkResponse response = esClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.getTook());
        System.out.println(Arrays.toString(response.getItems()));

        esClient.close();
    }


    /**
     * 2)????????????
     */
    @Test
    public void docUpdate() throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // ????????????
        UpdateRequest request = new UpdateRequest();
        request.index("user").id("1001");
        request.doc(XContentType.JSON, "sex", "???");

        UpdateResponse response = esClient.update(request, RequestOptions.DEFAULT);

        System.out.println(response.getResult());

        esClient.close();
    }


    /**
     * 3.1 ????????????-????????????id??????
     */
    @Test
    public void docGet() throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // ????????????
        GetRequest request = new GetRequest();
        request.index("user").id("1001");
        GetResponse response = esClient.get(request, RequestOptions.DEFAULT);

        System.out.println(response.getSourceAsString());

        esClient.close();
    }


    /**
     * 3.2 ??????????????????
     */
    @Test
    public void docGetAll() throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 1. ???????????????????????????
        SearchRequest request = new SearchRequest();
        request.indices("user");

        // ??????????????????
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());

        request.source(builder);

        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

        SearchHits hits = response.getHits();

        System.out.println(response.getTook());
        System.out.println(hits.getTotalHits());
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit hit = iterator.next();
            System.out.println(hit.getSourceAsString());
        }
        esClient.close();
    }


    /**
     * 3.3 ????????????
     */
    @Test
    public void docQuery() throws IOException {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 1. ??????????????????????????????
        System.out.println("\t\n");
        System.out.println("==========================??????????????????????????????==========================");
        SearchRequest request = new SearchRequest();
        request.indices("user");

        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));

        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

        SearchHits hits = response.getHits();

        System.out.println(hits.getTotalHits());
        System.out.println(response.getTook());

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================??????????????????????????????==========================");
        System.out.println("\t\n");

        // 2. ???????????? : termQuery
        System.out.println("==========================???????????? : termQuery==========================");
        SearchRequest request2 = new SearchRequest();
        request2.indices("user");

        request2.source(new SearchSourceBuilder().query(QueryBuilders.termQuery("age", 30)));
        SearchResponse response2 = esClient.search(request2, RequestOptions.DEFAULT);

        SearchHits hits2 = response2.getHits();

        System.out.println(hits2.getTotalHits());
        System.out.println(response2.getTook());

        for (SearchHit hit : hits2) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================???????????? : termQuery==========================");
        System.out.println("\t\n");

        // 3. ????????????
        System.out.println("==========================????????????==========================");
        SearchRequest request3 = new SearchRequest();
        request3.indices("user");

        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        // (????????????-1)*????????????????????????
        builder.from(2);
        builder.size(2);
        request3.source(builder);
        SearchResponse response3 = esClient.search(request3, RequestOptions.DEFAULT);

        SearchHits hits3 = response3.getHits();

        System.out.println(hits3.getTotalHits());
        System.out.println(response3.getTook());

        for (SearchHit hit : hits3) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================????????????==========================");
        System.out.println("\t\n");

        // 4. ????????????
        System.out.println("==========================????????????==========================");
        SearchRequest request4 = new SearchRequest();
        request4.indices("user");

        SearchSourceBuilder builder4 = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());

        builder4.sort("age", SortOrder.DESC);

        request4.source(builder4);
        SearchResponse response4 = esClient.search(request4, RequestOptions.DEFAULT);

        SearchHits hits4 = response4.getHits();

        System.out.println(hits4.getTotalHits());
        System.out.println(response4.getTook());

        for (SearchHit hit : hits4) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================????????????==========================");
        System.out.println("\t\n");


        // 5. ????????????
        System.out.println("==========================????????????==========================");
        SearchRequest request5 = new SearchRequest();
        request5.indices("user");

        SearchSourceBuilder builder5 = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        //
        String[] excludes = {"age"};
        String[] includes = {};
        builder5.fetchSource(includes, excludes);

        request5.source(builder5);
        SearchResponse response5 = esClient.search(request5, RequestOptions.DEFAULT);

        SearchHits hits5 = response5.getHits();

        System.out.println(hits5.getTotalHits());
        System.out.println(response5.getTook());

        for (SearchHit hit : hits5) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================????????????==========================");
        System.out.println("\t\n");

        // 6. ????????????
        System.out.println("==========================????????????==========================");
        SearchRequest request6 = new SearchRequest();
        request6.indices("user");

        SearchSourceBuilder builder6 = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //boolQueryBuilder.must(QueryBuilders.matchQuery("age", 30));
        //boolQueryBuilder.must(QueryBuilders.matchQuery("sex", "???"));
        //boolQueryBuilder.mustNot(QueryBuilders.matchQuery("sex", "???"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("age", 30));
        boolQueryBuilder.should(QueryBuilders.matchQuery("age", 40));

        builder6.query(boolQueryBuilder);

        request6.source(builder6);
        SearchResponse response6 = esClient.search(request6, RequestOptions.DEFAULT);

        SearchHits hits6 = response6.getHits();

        System.out.println(hits6.getTotalHits());
        System.out.println(response6.getTook());

        for (SearchHit hit : hits6) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================????????????==========================");
        System.out.println("\t\n");

        // 7. ????????????
        System.out.println("==========================????????????==========================");
        SearchRequest request7 = new SearchRequest();
        request7.indices("user");

        SearchSourceBuilder builder7 = new SearchSourceBuilder();
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age");

        rangeQuery.gte(30);
        rangeQuery.lt(50);

        builder7.query(rangeQuery);

        request7.source(builder7);
        SearchResponse response7 = esClient.search(request7, RequestOptions.DEFAULT);

        SearchHits hits7 = response7.getHits();

        System.out.println(hits7.getTotalHits());
        System.out.println(response7.getTook());

        for (SearchHit hit : hits7) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================????????????==========================");
        System.out.println("\t\n");

        // 8. ????????????
        System.out.println("==========================????????????==========================");
        SearchRequest request8 = new SearchRequest();
        request8.indices("user");

        SearchSourceBuilder builder8 = new SearchSourceBuilder();
        builder8.query(QueryBuilders.fuzzyQuery("name", "wangwu").fuzziness(Fuzziness.TWO));

        request8.source(builder8);
        SearchResponse response8 = esClient.search(request8, RequestOptions.DEFAULT);

        SearchHits hits8 = response8.getHits();

        System.out.println(hits8.getTotalHits());
        System.out.println(response8.getTook());

        for (SearchHit hit : hits8) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================????????????==========================");
        System.out.println("\t\n");

        // 9. ????????????
        System.out.println("==========================????????????==========================");
        SearchRequest request9 = new SearchRequest();
        request9.indices("user");

        SearchSourceBuilder builder9 = new SearchSourceBuilder();
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("name", "zhangsan");

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("name");

        builder9.highlighter(highlightBuilder);
        builder9.query(termsQueryBuilder);

        request9.source(builder9);
        SearchResponse response9 = esClient.search(request9, RequestOptions.DEFAULT);

        SearchHits hits9 = response9.getHits();

        System.out.println(hits9.getTotalHits());
        System.out.println(response9.getTook());

        for (SearchHit hit : hits9) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================????????????==========================");
        System.out.println("\t\n");

        // 10. ????????????
        System.out.println("==========================????????????==========================");
        SearchRequest request10 = new SearchRequest();
        request10.indices("user");

        SearchSourceBuilder builder10 = new SearchSourceBuilder();

        AggregationBuilder aggregationBuilder = AggregationBuilders.max("maxAge").field("age");
        builder10.aggregation(aggregationBuilder);

        request10.source(builder10);
        SearchResponse response10 = esClient.search(request10, RequestOptions.DEFAULT);

        SearchHits hits10 = response10.getHits();

        System.out.println(hits10.getTotalHits());
        System.out.println(response10.getTook());

        for (SearchHit hit : hits10) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==========================????????????==========================");
        System.out.println("\t\n");

        // 11. ????????????
        System.out.println("==========================????????????==========================");
        SearchRequest request11 = new SearchRequest();
        request11.indices("user");

        SearchSourceBuilder builder11 = new SearchSourceBuilder();

        AggregationBuilder aggregationBuilder11 = AggregationBuilders.terms("ageGroup").field("age");
        builder11.aggregation(aggregationBuilder11);

        request11.source(builder11);
        SearchResponse response11 = esClient.search(request11, RequestOptions.DEFAULT);

        SearchHits hits11 = response11.getHits();

        System.out.println(hits11.getTotalHits());
        System.out.println(response11.getTook());

        for (SearchHit hit : hits11) {
            System.out.println(hit.getSourceAsString());
        }
        esClient.close();
        System.out.println("==========================????????????==========================");
    }


    /**
     * 4.1 ????????????
     */
    @Test
    public void docDelete() throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );


        DeleteRequest request = new DeleteRequest();
        request.index("user").id("1001");

        DeleteResponse response = esClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());

        esClient.close();
    }


    /**
     * 4.2 ???????????????
     */
    @Test
    public void docDeleteBatch() throws IOException {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // ??????????????????
        BulkRequest request = new BulkRequest();

        request.add(new DeleteRequest().index("user").id("1001"));
        request.add(new DeleteRequest().index("user").id("1002"));
        request.add(new DeleteRequest().index("user").id("1003"));

        BulkResponse response = esClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.getTook());
        System.out.println(Arrays.toString(response.getItems()));
        esClient.close();
    }
}
