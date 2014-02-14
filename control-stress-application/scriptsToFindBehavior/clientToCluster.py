# -*- coding: utf-8 -*-
"""
Created on Thu Dec 19 16:47:52 2013

@author: Georgiana
"""

import urllib, urllib2, sys, httplib
#url = "/rSYBL-analysis-engine-0.1-SNAPSHOT/restWS"
url = "/getEstimatedMetricsValues"
#HOST_IP="83.212.112.35"
#HOST_IP="128.130.172.214:8080"
HOST_IP="127.0.0.1:8321"
import json
import csv

if __name__=='__main__':
    connection =  httplib.HTTPConnection(HOST_IP)
    #read composition rules file
    array=[]  
    index=0
    headersOfFile=[]
    with open('test.csv', 'rbU') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            if not row[0].islower():
                values = row
                array.insert(index,values)
                index+=1
            else:
                headersOfFile=row

    metrics=[]
    size = 0

    for values in array:
        index=0
        for v in values:
            print "Trying to insert ",v," at ",index
            if size==0:
                metrics.append([float(v)])
            else:
                metrics[index].append(float(v))
            index+=1
        size+=1    

    modelAction = {
        "toEvaluateMetricsResult":metrics,
        "monitoringFilePath": "EventProcessingServiceTopology_21_25.csv",
        "referenceColumn":5,
        "time": size,
       }
    print json.dumps(modelAction)
    headers={'Content-Type':'application/json; charset=utf-8','Accept':'application/json, multipart/related'}
    connection.request('POST', url,json.dumps(modelAction),headers=headers,)
    result = connection.getresponse()
    print result.read()
 