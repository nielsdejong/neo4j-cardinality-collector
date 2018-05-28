package me.niels;

import org.neo4j.cypher.internal.frontend.v2_3.ast.Query;
import org.neo4j.executor.BoltCypherExecutor;
import org.neo4j.executor.CypherExecutor;
import org.neo4j.helpers.collection.Iterators;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.neo4j.helpers.collection.MapUtil.map;

public class GraphQueryer {

    private final CypherExecutor cypher;

    public GraphQueryer(String uri) {
        cypher = createCypherExecutor(uri);
    }

    private CypherExecutor createCypherExecutor(String uri) {
        try {
            String auth = new URL(uri.replace("bolt","http")).getUserInfo();
            if (auth != null) {
                String[] parts = auth.split(":");
                return new BoltCypherExecutor(uri,parts[0],parts[1]);
            }
            return new BoltCypherExecutor(uri);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Neo4j-ServerURL " + uri);
        }
    }

    public QueryResult profileQuery(String queryText, Map<String,Object> params ){
        return cypher.query("PROFILE " + queryText, params);
    }
}
