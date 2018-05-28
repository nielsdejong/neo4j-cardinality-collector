package org.neo4j.executor;

import me.niels.QueryResult;

import org.neo4j.driver.v1.*;
import org.neo4j.helpers.collection.Iterators;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Hunger @since 22.10.13
 */
public class BoltCypherExecutor implements CypherExecutor
{

    private final org.neo4j.driver.v1.Driver driver;

    public BoltCypherExecutor(String url) {
        this(url, null, null);
    }

    public BoltCypherExecutor(String url, String username, String password) {
        boolean hasPassword = password != null && !password.isEmpty();
        AuthToken token = hasPassword ? AuthTokens.basic(username, password) : AuthTokens.none();
        driver = GraphDatabase.driver(url, token, Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig());
    }

    @Override
    public QueryResult query(String query, Map<String, Object> params) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(query, params);

            List<Map<String, Object>> list = result.list( r -> r.asMap(BoltCypherExecutor::convert));
            return new QueryResult(query, Iterators.asCollection(list.iterator()).size(), result.consume().profile());
        }
    }

    static Object convert(Value value) {
        switch (value.type().name()) {
            case "PATH":
                return value.asList(BoltCypherExecutor::convert);
            case "NODE":
            case "RELATIONSHIP":
                return value.asMap();
        }
        return value.asObject();
    }

}
