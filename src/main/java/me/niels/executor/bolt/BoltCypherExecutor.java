package me.niels.executor.bolt;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.summary.ProfiledPlan;
import org.neo4j.helpers.collection.Iterators;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Hunger @since 22.10.13
 */
public class BoltCypherExecutor
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

    public ProfiledPlan query(String query, Map<String, Object> params) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(query, params);
            return result.consume().profile();
        }
    }
}
