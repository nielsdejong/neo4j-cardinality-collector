package me.niels;

import me.niels.executor.bolt.BoltCardinalityDataCollector;
import me.niels.executor.bolt.BoltGraphQueryer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.summary.ProfiledPlan;
import org.neo4j.shell.util.json.JSONArray;

import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * @author Michael Hunger @since 22.10.13
 */
public class CardinalityAnalyzer
{
    static final String QUERY_JSON_FILE = "social_network_queries.json";
    static final String PARAMETERS_FOLDER = "socialnetwork";
    static final int PARAMETER_COMBINATIONS_LIMIT = 5;
    public static void main(String[] args) throws IOException
    {
        BoltCardinalityDataCollector bcd = new BoltCardinalityDataCollector();
        BoltGraphQueryer boltQueryer = new BoltGraphQueryer( "bolt://neo4j:niels@localhost" );

        QueryReader queryReader = new QueryReader();
        queryReader.readQueries( QUERY_JSON_FILE );




        // Create output file
        File output = new File("src/main/resources/output.csv");
        output.createNewFile();
        FileWriter writer = new FileWriter(output);

        writer.write("version, query, params, operator_id, operator_name, est_card, real_card, rel_err_percentage \n");
        String version = "3.4.0 Enterprise";
        DecimalFormat df = new DecimalFormat("#.##");
        for(int q = 0; q < queries.size(); q++){
            String[] current_param_names = param_names.get( q );
            List<Object[]> current_param_values = param_values.get( q );
            for(int paramValue = 0; paramValue < Math.min(parameterCombinationsLimit, current_param_values.size()); paramValue++)
            {
                Map<String, Object> params = new HashMap<>();
                for( int paramID = 0; paramID < current_param_names.length; paramID++ ){
                    params.put( current_param_names[paramID], current_param_values.get( paramValue )[paramID] );
                }
                // Run query
                ProfiledPlan queryPlan = boltQueryer.profileQuery( queries.get( q ), params );
                // Print results
                LinkedList<ProfiledPlan> queue = new LinkedList<ProfiledPlan>();
                queue.add( queryPlan );
                int operatorID = 0;
                // Writes the content to the file
                while( !queue.isEmpty() ){

                    ProfiledPlan plan =  queue.pop();
                    String operatorType = plan.operatorType();
                    double realCardinality = plan.arguments().get( "Rows" ).asDouble();
                    double estimatedCardinality = plan.arguments().get( "EstimatedRows" ).asDouble();
                    double fractionError = (estimatedCardinality/realCardinality);
                    if(fractionError < 1){
                        fractionError = 1.0/fractionError;
                    }

                    String outputLine = version +","+ q + "," + paramValue+ "," + operatorID + "," + operatorType + "," + estimatedCardinality + "," + realCardinality + "," + df.format(fractionError*100.0-100.0)+"%" + "\n";
                    System.out.print( outputLine );
                    System.out.println( plan );
                    writer.write( outputLine );
                    queue.addAll( plan.children() );
                    operatorID++;
                }
            }
        }
        writer.flush();
        writer.close();
    }
}
