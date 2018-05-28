package org.neo4j.executor;

import me.niels.QueryResult;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Michael Hunger @since 22.10.13
 */
public interface CypherExecutor {
    QueryResult query(String statement, Map<String,Object> params);
}
