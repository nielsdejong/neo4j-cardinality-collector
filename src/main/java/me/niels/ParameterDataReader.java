package me.niels;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterDataReader
{


    private  List<String[]> param_names = new ArrayList<>(  );
    private List<List<Object[]>> param_values = new ArrayList<>(  );

    public void readParameterData(List<String> queries, String parameterFolder, List<String> parameterFiles){
        // Populate params list

        for(int a = 0; a < queries.size(); a++){
            String query = queries.get( a );
            Map<String, List<String>> params = new HashMap();
            BufferedReader br = null;
            try
            {
                br = new BufferedReader(new FileReader("src/main/resources/"+parameterFolder + "/" +parameterFiles.get( queries.indexOf( query ) )));
                String[] param_names_string = br.readLine().split( "\\|" );
                String[] param_names_string_parsed = new String[param_names_string.length];
                for(int x = 0; x < param_names_string.length; x++){
                    param_names_string_parsed[x] = param_names_string[x].split( ":" )[0];
                }
                param_names.add( param_names_string_parsed );
                String line = "";
                List<Object[]> values = new ArrayList<>();
                line = br.readLine();
                while (line != null) {
                    String[] paramValuesString = line.split( "\\|" );
                    Object[] paramValuesActual = new Object[paramValuesString.length];
                    for(int pID = 0; pID < paramValuesString.length; pID ++){
                        String[] paramValueArray = paramValuesString[pID].split( "," );
                        if(paramValueArray.length == 1){
                            paramValuesActual[pID] = paramValueArray[0];
                        }else{
                            paramValuesActual[pID] = paramValueArray;
                        }
                    }
                    values.add( paramValuesActual );
                    line = br.readLine();
                }
                param_values.add( values );
                br.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }
}
