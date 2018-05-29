package me.niels.executor.bolt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.neo4j.driver.v1.summary.ProfiledPlan;

import static org.neo4j.helpers.collection.MapUtil.map;

public class BoltGraphQueryer
{

    private final BoltCypherExecutor cypher;

    public BoltGraphQueryer(String uri) {
        cypher = createCypherExecutor(uri);
    }

    private BoltCypherExecutor createCypherExecutor(String uri) {
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

    public ProfiledPlan profileQuery(String queryText, Map<String,Object> params ){
        return cypher.query("PROFILE " + queryText, params);
    }
}
