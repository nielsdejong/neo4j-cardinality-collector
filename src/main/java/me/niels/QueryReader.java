package me.niels;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.shell.util.json.JSONArray;
import org.neo4j.shell.util.json.JSONException;

public class QueryReader
{
    private List<String> queries = new ArrayList<String> ();
    private List<String> queryDescriptions = new ArrayList<String> ();
    private List<String> parameterFiles = new ArrayList<String> ();


    public List<String> getQueries()
    {
        return queries;
    }

    public List<String> getQueryDescriptions()
    {
        return queryDescriptions;
    }

    public List<String> getParameterFiles()
    {
        return parameterFiles;
    }


    public void readQueries(String queryJSONFile){
        String queriesJSON = null;
        try
        {
            queriesJSON = new String(Files.readAllBytes(Paths.get(queryJSONFile)));
            JSONArray arr = new JSONArray( queriesJSON );
            for ( int i = 0; i < arr.length(); i++ )
            {
                String queryString = arr.getJSONObject( i ).getString( "queryString" );
                queries.add( queryString );
                queryDescriptions.add( arr.getJSONObject( i ).getString( "name" ) );
                parameterFiles.add( arr.getJSONObject( i ).getString( "parameterFile" ) );
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        catch ( JSONException e )
        {
            e.printStackTrace();
        }
    }
}
