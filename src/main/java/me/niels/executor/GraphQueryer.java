package me.niels.executor;

import me.niels.PlanWithCardinality;
import java.util.Map;

public abstract class GraphQueryer
{
    public abstract PlanWithCardinality getQueryPlan( String query, Map<String,Object> params );
}
