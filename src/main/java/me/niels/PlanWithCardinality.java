package me.niels;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.summary.ProfiledPlan;
import org.neo4j.graphdb.ExecutionPlanDescription;

public class PlanWithCardinality
{

    public final double realCardinality;
    public double estimatedCardinality;
    public final List<PlanWithCardinality> children;

    public PlanWithCardinality( ProfiledPlan plan )
    {

        realCardinality = plan.arguments().get( "Rows" ).asDouble();
        estimatedCardinality = plan.arguments().get( "EstimatedRows" ).asDouble();
        children = new ArrayList<>();
        for ( ProfiledPlan child : plan.children() )
        {
            children.add( new PlanWithCardinality( child ) );
        }
    }

    public PlanWithCardinality( ExecutionPlanDescription plan )
    {
        realCardinality = (long) plan.getArguments().get( "Rows" );
        estimatedCardinality = (double) plan.getArguments().get( "EstimatedRows" );
        children = new ArrayList<>();
        for ( ExecutionPlanDescription child : plan.getChildren() )
        {
            children.add( new PlanWithCardinality( child ) );
        }
    }
}
