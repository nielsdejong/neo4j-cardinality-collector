package me.niels;

import me.niels.executor.GraphQueryer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QueryDataCollector
{
    private String version;
    private int parameterCombinationsLimit;
    private GraphQueryer graphQueryer;
    private DecimalFormat df = new DecimalFormat("#.##");

    public QueryDataCollector(String version, int parameterCombinationsLimit, GraphQueryer graphQueryer ){
        this.graphQueryer = graphQueryer;
        this.version = version;
        this.parameterCombinationsLimit = parameterCombinationsLimit;
    }

    public List<String> getCardinalityDataForQueries(List<String> queries, List<String[]> param_names, List<List<Object[]>> param_values){
        List<String> output = new ArrayList<>();

        output.add("est_root_"+version+", real_root_"+version+", rel_err_root_"+version+", q_error_root_"+version+", max_q_error_"+version+", avg_q_error_"+version+", est_sum_"+version+", real_sum_"+version+", est_geom_mean_"+version+", real_geom_mean_"+version+",q_error_geom_mean"+version+",rel_error_geom_mean"+version);
        for(int q = 0; q < queries.size( ); q++){
       //for(int q = 3; q < 4; q++){

        String[] current_param_names = param_names.get( q );
            List<Object[]> current_param_values = param_values.get( q );
            for(int paramValue = 0; paramValue < Math.min(parameterCombinationsLimit, current_param_values.size()); paramValue++)
            {
                Map<String, Object> params = new HashMap<>();
                for( int paramID = 0; paramID < current_param_names.length; paramID++ ){
                    params.put( current_param_names[paramID], current_param_values.get( paramValue )[paramID] );
                }
                // Run getQueryPlan
                PlanWithCardinality queryPlan = graphQueryer.getQueryPlan( queries.get( q ), params );
                // Print results
                LinkedList<PlanWithCardinality> queue = new LinkedList<>();
                queue.add( queryPlan );
                int count = 0;
                double realCardinalitySum = 0;
                double avgQError = 0;
                double estCardinalitySum = 0;
                double realCardinalityProduct = 1;
                double estimatedCardinalityProduct = 1;
                double qErrorProduct = 1;
                double maxQError = 1;
                double relErrorProduct = 1;

                // Get info from plan.
                while( !queue.isEmpty() ){
                    count++;
                    PlanWithCardinality plan =  queue.pop();

//                    if( plan.realCardinality == 1.0 )
//                    {
//                        plan.estimatedCardinality = plan.realCardinality;
//                    }

                    // Sums
                    realCardinalitySum +=  plan.realCardinality;
                    estCardinalitySum += plan.estimatedCardinality;

                    // Products
                    realCardinalityProduct *= plan.realCardinality;
                    estimatedCardinalityProduct *= plan.estimatedCardinality;
                    if( getQError( plan.realCardinality, plan.estimatedCardinality ) != 0 )
                        qErrorProduct *= getQError( plan.realCardinality, plan.estimatedCardinality );
                    relErrorProduct *= getRelError( plan.realCardinality, plan.estimatedCardinality );
                    // AVG AND MAX
                    avgQError += getQError( plan.realCardinality, plan.estimatedCardinality );
                    maxQError = Math.max(maxQError, getQError( plan.realCardinality, plan.estimatedCardinality ));


                    queue.addAll( plan.children );
                }

                double realCardinalityRoot = queryPlan.realCardinality;
                double estimatedCardinalityRoot = queryPlan.estimatedCardinality;

                double realGeometricMean = Math.pow( realCardinalityProduct, 1.0/count );
                double estimatedGeometricMean = Math.pow( estimatedCardinalityProduct, 1.0/count );
                double qErrorGeometricMean = Math.pow( qErrorProduct, 1.0/count );
                double relErrorGeometricMean = Math.pow( relErrorProduct, 1.0/count );
                avgQError /= count;

                String outputLine =
                        estimatedCardinalityRoot + "," +
                        realCardinalityRoot + "," +
                        getRelError( realCardinalityRoot, estimatedCardinalityRoot ) + "," +
                        getQError( realCardinalityRoot, estimatedCardinalityRoot ) + "," +
                        maxQError + "," +
                        avgQError + "," +
                        estCardinalitySum + "," +
                        realCardinalitySum + "," +
                        estimatedGeometricMean + "," +
                        realGeometricMean + "," +
                        qErrorGeometricMean + "," +
                        relErrorGeometricMean;
                System.out.println( outputLine );
                output.add( outputLine );
            }
        }
        return output;
    }

    private Double getQError(double realCardinality, double estCardinality){
        if(realCardinality == 0 || estCardinality == 0){
            return 1.0;
        }
        double qError = (estCardinality/realCardinality);
        if(qError < 1){
            qError = 1.0/qError;
        }
        return qError;
    }

    private Double getRelError(double realCardinality, double estCardinality){
        return (realCardinality / estCardinality) - 1;
    }
}
