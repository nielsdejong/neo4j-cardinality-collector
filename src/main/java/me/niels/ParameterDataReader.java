package me.niels;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterDataReader
{

    private List<String[]> parameterNames = new ArrayList<>();
    private List<List<Object[]>> parameterValuesList = new ArrayList<>();

    public List<String[]> getParameterNames()
    {
        return parameterNames;
    }

    public List<List<Object[]>> getParameterValuesList()
    {
        return parameterValuesList;
    }

    public void readParameterData( List<String> queries, String parameterFolder, List<String> parameterFiles )
    {
        // Populate params list

        for ( int a = 0; a < queries.size(); a++ )
        {
            String query = queries.get( a );
            Map<String,List<String>> params = new HashMap();
            BufferedReader br = null;
            try
            {
                br = new BufferedReader( new FileReader( "src/main/resources/" + parameterFolder + "/" + parameterFiles.get( queries.indexOf( query ) ) ) );
                String[] param_names_string = br.readLine().split( "\\|" );
                String[] param_names_string_parsed = new String[param_names_string.length];
                String[] class_names = new String[param_names_string.length];
                for ( int x = 0; x < param_names_string.length; x++ )
                {
                    param_names_string_parsed[x] = param_names_string[x].split( ":" )[0];
                    if ( param_names_string[x].split( ":" ).length > 1 )
                    {
                        class_names[x] = param_names_string[x].split( ":" )[1];
                    }
                }

                parameterNames.add( param_names_string_parsed );
                String line = "";
                List<Object[]> values = new ArrayList<>();
                line = br.readLine();
                while ( line != null )
                {
                    String[] paramValuesString = line.split( "\\|" );
                    Object[] paramValuesActual = new Object[paramValuesString.length];
                    for ( int pID = 0; pID < paramValuesString.length; pID++ )
                    {
                        String[] paramValueArray = paramValuesString[pID].split( "," );
                        if ( paramValueArray.length == 1 )
                        {

                            paramValuesActual[pID] = paramValueArray[0];
                            if ( class_names[pID] != null )
                            {
                                if ( class_names[pID].equals( "Long" ) )
                                {
                                    paramValuesActual[pID] = Long.parseLong( (String) paramValuesActual[pID] );
                                }
                                if ( class_names[pID].equals( "Int" ) )
                                {
                                    paramValuesActual[pID] = Integer.parseInt( (String) paramValuesActual[pID] );
                                }
                            }
                        }
                        else
                        {
                            paramValuesActual[pID] = paramValueArray;
                        }
                    }
                    values.add( paramValuesActual );
                    line = br.readLine();
                }
                parameterValuesList.add( values );
                br.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }
}
