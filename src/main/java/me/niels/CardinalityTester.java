package me.niels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.shell.util.json.JSONArray;
import org.neo4j.shell.util.json.JSONObject;
import org.neo4j.util.Util;

import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * @author Michael Hunger @since 22.10.13
 */
public class CardinalityTester
{

    public static void main(String[] args) throws IOException
    {
        GraphQueryer queryer = new GraphQueryer( "bolt://neo4j:niels@localhost" );
        String queryJSONFile = "queries.json";
        String parameterFolder = "entities";

        // Populate query list
        String queriesJSON = new String(Files.readAllBytes(Paths.get("src/main/resources/"+queryJSONFile)));
        List<String> queries = new ArrayList<String>();
        List<String> queryDescriptions = new ArrayList<String>();
        List<String> parameterFiles = new ArrayList<String> ();
        try
        {
            JSONArray arr = new JSONArray( queriesJSON );
            for ( int i = 0; i < arr.length(); i++ )
            {
                String queryString = arr.getJSONObject( i ).getString( "queryString" );
                queries.add( queryString );
                queryDescriptions.add( arr.getJSONObject( i ).getString( "name" ) );
                parameterFiles.add( arr.getJSONObject( i ).getString( "parameterFile" ) );
            }
        }catch(Exception e){
            e.printStackTrace();
        }


        // Populate params list
        Map<String, Map<String, List<String>>> queryToParamsMap = new HashMap();
        for(String query : queries){
            Map<String, List<String>> params = new HashMap();
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/"+parameterFolder + "/" +parameterFiles.get( queries.indexOf( query ) )));
            try {
                String key = br.readLine();
                String line = "";
                List<String> param_values = new ArrayList<String>();
                while (line != null) {
                    line = br.readLine();
                    param_values.add( line );
                }
                params.put(key, param_values);
            } finally {
                br.close();
            }
            queryToParamsMap.put( query, params );
        }


        // Create output file
        File output = new File("src/main/resources/output.csv");
        output.createNewFile();
        FileWriter writer = new FileWriter(output);

        writer.write("query, params, est_card, real_card\n");
        for(String q : queries){
            Map<String, List<String>> currentQueryParams = queryToParamsMap.get( q );
            for(String paramKey : currentQueryParams.keySet()){
                for(String paramValue : currentQueryParams.get(paramKey))
                {
                    QueryResult qr = queryer.profileQuery( q, map(paramKey, paramValue) );
                    // Writes the content to the file
                    String outputLine = queryDescriptions.get(queries.indexOf( q )) + "," + map(paramKey, paramValue) + "," + qr.plan.arguments().get( "EstimatedRows" ) + "," + qr.rowCount + "\n";
                    System.out.print( outputLine );
                    writer.write( outputLine );
                }
            }
        }
        writer.flush();
        writer.close();

    }
}
