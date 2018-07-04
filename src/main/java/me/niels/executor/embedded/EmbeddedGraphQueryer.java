package me.niels.executor.embedded;

import me.niels.PlanWithCardinality;
import me.niels.executor.GraphQueryer;
import java.io.File;
import java.util.Map;

import org.neo4j.graphdb.ExecutionPlanDescription;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.factory.EnterpriseGraphDatabaseFactory;

public class EmbeddedGraphQueryer extends GraphQueryer
{
    GraphDatabaseService graphDB;

    public EmbeddedGraphQueryer( File DBStore ){
        graphDB = new EnterpriseGraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder( DBStore )
                .newGraphDatabase();
        System.out.println("Started Embedded DB");
        System.out.println(graphDB);
    }

    public PlanWithCardinality getQueryPlan( String query, Map<String, Object> params) {
        System.out.println( query + ":" + params );
        Result r =  graphDB.execute( "PROFILE "+ query, params);
        //Result r =  graphDB.execute( "EXPLAIN match (a:`0`)-[x:`0`]->(b:`1`)-[y:`1`]->(c:`2`)-[z:`2`]->(d:`3`)-[q:`3`]->(e:`4`) return *", params);

        r.resultAsString();
        ExecutionPlanDescription epd = r.getExecutionPlanDescription();
        System.out.println(epd);
        r.close();
        return new PlanWithCardinality ( epd );
    }

    public void shutdownDB(){
        graphDB.shutdown();
    }
}
