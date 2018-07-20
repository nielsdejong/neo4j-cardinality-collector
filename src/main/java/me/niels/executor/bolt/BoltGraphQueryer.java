package me.niels.executor.bolt;

import me.niels.PlanWithCardinality;
import me.niels.executor.GraphQueryer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class BoltGraphQueryer extends GraphQueryer
{

    private final BoltCypherExecutor cypher;

    public BoltGraphQueryer( String uri )
    {
        cypher = createCypherExecutor( uri );
    }

    private BoltCypherExecutor createCypherExecutor( String uri )
    {
        try
        {
            String auth = new URL( uri.replace( "bolt", "http" ) ).getUserInfo();
            if ( auth != null )
            {
                String[] parts = auth.split( ":" );
                return new BoltCypherExecutor( uri, parts[0], parts[1] );
            }
            return new BoltCypherExecutor( uri );
        }
        catch ( MalformedURLException e )
        {
            throw new IllegalArgumentException( "Invalid Neo4j-ServerURL " + uri );
        }
    }

    public PlanWithCardinality getQueryPlan( String queryText, Map<String,Object> params )
    {
        System.out.println( queryText );
        System.out.println( params );
        return new PlanWithCardinality( cypher.query( "PROFILE " + queryText, params ) );
    }
}
