package me.niels;

import me.niels.executor.bolt.BoltGraphQueryer;
import me.niels.executor.embedded.EmbeddedGraphQueryer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.neo4j.helpers.collection.MapUtil.map;

public class CardinalityAnalyzer
{

    static final String QUERY_JSON_FILE = "C:/Users/Niels de Jong/Desktop/neo4j-estimator-analyzer/src/main/resources/accesscontrol.json";
    static final String PARAMETERS_FOLDER = "parameters";
    static final int PARAMETER_COMBINATIONS_LIMIT = 10;

    public static void main( String[] args ) throws IOException
    {
        System.out.println( "analyzer" );
        QueryReader queryReader = new QueryReader();
        queryReader.readQueries( QUERY_JSON_FILE );
        ParameterDataReader pdm = new ParameterDataReader();
        pdm.readParameterData( queryReader.getQueries(), PARAMETERS_FOLDER, queryReader.getParameterFiles() );
        EmbeddedGraphQueryer embeddedGraphQueryer =
                new EmbeddedGraphQueryer( new File( "C:/Users/Niels de Jong/Downloads/accesscontrol/accesscontrol~/graph.db" ) );
        QueryDataCollector embeddedDataCollector = new QueryDataCollector( "_neo4j_BGCE", PARAMETER_COMBINATIONS_LIMIT, embeddedGraphQueryer );
        List<String> embeddedCardinalityData =
                embeddedDataCollector.getCardinalityDataForQueries( queryReader.getQueries(), pdm.getParameterNames(), pdm.getParameterValuesList() );
        embeddedGraphQueryer.shutdownDB();
        BoltGraphQueryer boltQueryer = new BoltGraphQueryer( "bolt://neo4j:niels@localhost" );

        QueryDataCollector boltDataCollector = new QueryDataCollector( "_neo4j", PARAMETER_COMBINATIONS_LIMIT, boltQueryer );
        List<String> boltCardinalityData =
                boltDataCollector.getCardinalityDataForQueries( queryReader.getQueries(), pdm.getParameterNames(), pdm.getParameterValuesList() );

        // Create output file
        File output = new File( "src/main/resources/output.csv" );
        output.createNewFile();
        FileWriter writer = new FileWriter( output );
        writer.write( "getQueryPlan, params," + boltCardinalityData.get( 0 ) + "," + embeddedCardinalityData.get( 0 ) + "\n" );
        int count = 1;
        for ( int q = 0; q < queryReader.getQueries().size(); q++ )
        {
            List<Object[]> currentParamValues = pdm.getParameterValuesList().get( q );
            for ( int paramValue = 0; paramValue < Math.min( PARAMETER_COMBINATIONS_LIMIT, currentParamValues.size() ); paramValue++ )
            {
                String prefix = (q + 1) + "," + paramValue + ",";
                String output1 = boltCardinalityData.get( count );
                String output2 = embeddedCardinalityData.get( count );
                writer.write( prefix + output1 + "," + output2 + "\n" );
                count++;
            }
        }

        writer.flush();
        writer.close();
    }
}
