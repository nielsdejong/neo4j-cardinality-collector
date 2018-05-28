package me.niels;

import java.util.Iterator;
import java.util.Map;

import org.neo4j.driver.v1.summary.ProfiledPlan;

public class QueryResult
{
    public final int rowCount;
    public final ProfiledPlan plan;
    public final String query;

    public QueryResult(String query, int rowCount, ProfiledPlan plan){
        this.rowCount = rowCount;
        this.query = query;
        this.plan = plan;
    }

    @Override
    public String toString(){
        return query + "\n"
                + "    estimated rows: " +  plan.arguments().get( "EstimatedRows" ) + "\n"
                + "    actual rows: " + rowCount;
    }
}
