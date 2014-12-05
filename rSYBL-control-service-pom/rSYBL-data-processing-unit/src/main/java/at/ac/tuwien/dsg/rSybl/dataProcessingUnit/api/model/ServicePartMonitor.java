/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Georgiana
 */
public class ServicePartMonitor {
    private HashMap<String,Double> metrics=new HashMap<>();
    private String servicePart;
    

    /**
     * @return the metrics
     */
    public HashMap<String,Double> getMetrics() {
        return metrics;
    }

    /**
     * @param metrics the metrics to set
     */
    public void setMetrics(HashMap<String,Double> metrics) {
        this.metrics = metrics;
    }
 /**
     * @param metrics the metrics to set
     */
    public void addMetricValue(String metric,Double metricValue) {
        this.metrics.put(metric, metricValue);
    }

    /**
     * @return the servicePart
     */
    public String getServicePart() {
        return servicePart;
    }

    /**
     * @param servicePart the servicePart to set
     */
    public void setServicePart(String servicePart) {
        this.servicePart = servicePart;
    }



 
}
