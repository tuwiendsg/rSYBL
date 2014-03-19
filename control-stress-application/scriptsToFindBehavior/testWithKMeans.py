# -*- coding: utf-8 -*-
"""
Created on Wed Dec 18 10:53:11 2013

@author: Georgiana
"""
import numpy as np
from sklearn.neighbors import KNeighborsClassifier
import scipy.cluster.hierarchy as hcluster
from pandas import *
import collections
import matplotlib.pyplot as plt
import scipy.spatial.distance as ssd
import math
import json
from Point import *
import sys, math, random
# convert the redundant n*n square matrix form into a condensed nC2 array
import json
from flask import request
from flask import Flask
from flask import jsonify

import json
import csv
app = Flask(__name__)
clusters = []
headers = []
 
@app.route('/getEstimatedMetricsValues',methods = ['POST'])
def api_call():
    return parseRequest(request.json)
@app.route('/')
def index():
    return "Hello, World!"   
    
@app.route('/todo/api/v1.0/tasks', methods = ['POST'])
def create_task():    
    task = {
        'id': tasks[-1]['id'] + 1,
        'title': request.json['title'],
        'description': request.json.get('description', ""),
        'done': False
    }
    tasks.append(task)
    return jsonify( { 'task': task } ), 201
   
class MetricsCluster:
    #clustersCentroids
    def __init__(self,clustersCentroids):
        self.clustersCentroids=clustersCentroids
    def __repr__(self):
        return str(self.clustersCentroids)
    def __eq__(self, other):
        if isinstance(other, self.__class__):
            for i in range(len(self.clustersCentroids)):
                if self.clustersCentroids[i]!=other.clustersCentroids[i]:
                    return False
            return True
        else:
            return False
    def __cmp__(self, other):
        assert isinstance(other, A) # assumption for this example
        return cmp(self.clustersCentroids,
                   other.clustersCentroids)
    def __hash__(self):
        return hash(sum(self.clustersCentroids))
    def __ne__(self, other):
        return not self.__eq__(other)
class Node:
    #instance variables
    #other connected nodes
    #relationship to other nodes
    #id
    #history of metrics
    #current metrics
    def __init__(self,id,nodeType,historyOfMetrics,currentMetrics,metricTypes,reference=None):
        self.id=id
        self.nodeType=nodeType
        self.historyOfMetrics=historyOfMetrics
        self.currentMetrics=currentMetrics
        self.metricTypes=metricTypes
        self.connectedNodes=dict()
    def addNodeWithRelationship(relationshipType,node):
        self.connectedNodes[relationshipType].append(node)
    def __repr__(self):
        return self.id
def parseRequest(jsonRequest): 
    """jsonObj= json.loads(jsonRequest)"""
    expectedMetrics= findClustersOnMetricsOfFile(request.json.get("toEvaluateMetricsResult"),request.json.get("monitoringFilePath"),int(request.json.get("time")),int(request.json.get("referenceColumn")))
    resultedMetrics = dict()
    for index in range(len(expectedMetrics)):
        resultedMetrics[expectedMetrics[index]]=expectedMetrics[index][len(expectedMetrics[index])/2:len(expectedMetrics[index])]
    return json.dumps(resultedMetrics)
def readMetricsFromFile(fileName):
    array=[]  
    index=0
    headers=[]
    with open(fileName, 'rbU') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')       
        headers=spamreader.next()
        for row in spamreader:
            values = row
            array.insert(index,values)
            index+=1

    headers=headers[1:(len(headers)-2)]
    metrics=[]
    actions=[]

    size = 0   
    for values in array[1:len(array)]:
        index=0
        for v in values[1:len(values)]:
            if index not in [len(values)-3,len(values)-2]:
                if size==0:
                    if (v!=""):
                        metrics.insert(index,[float(v.strip())])
                    else:
                        metrics.insert(index,[0])
                else:
                    if (v!=""):
                        metrics[index].append(float(v.strip()))
                    else:
                        metrics.insert(index,[0])
                index+=1
        size+=1
    size=0
    for values in array[1:len(array)]:
        for index in [len(values)-2,len(values)-1]:
            if size==0:
               actions.insert(index-(len(values)-2),[values[index].strip()])
            else:
               actions[index-(len(values)-2)].append(values[index].strip())
        size+=1   
    #returning the metrics, the actions and the headers for those metrics
    return (metrics,actions,headers)

def findSignificantIndexesGivenActionAndEntity(actionName, actionEntity,actions):
    index = 0
    significantChangesIndexes=[]
    startIndex=-1
    endIndex=-1    
    sumTime=0
    nbTimes=0
    actionTime=[]  
    
    while (index<len(actions[0])):   
        
        if actionName==actions[0][index] and (actionEntity==actions[1][index] or actionEntity==""):
            if (startIndex==-1):
                startIndex=index
                while (index<len(actions[0]) and actions[0][index]==actionName and (actions[1][index]==actionEntity or actionEntity=="")):
                    index+=1
                endIndex=index-1
                #print "Time necessary is ",(endIndex-startIndex)
                sumTime=sumTime+ (endIndex-startIndex)
                nbTimes+=1
                actionTime.append(endIndex-startIndex)
                significantChangesIndexes.append((endIndex-startIndex)/2+startIndex)
                startIndex=-1
                endIndex=-1
        index+=1
    averageTime = (sumTime/nbTimes)
    timeVariance=0
    for i in range(len(actionTime)-1):
        timeVariance+=(actionTime[i]-averageTime)**2
    
    """print "Average time for this action is ",(sumTime/nbTimes)
    print "Time variance for this action is ", float(timeVariance/(len(actionTime)-1))
    print "Standard deviation for this action is ",math.sqrt(timeVariance/(len(actionTime)-1))"""
    return (significantChangesIndexes,averageTime)
def findSignificantIndexes(metrics,column):    
    index = 0
    significantChangesIndexes=[]
    for nbMachines in metrics[column]:
        if (index+1)!=len(metrics[column]):
            if (metrics[column][index+1]!=nbMachines):
                significantChangesIndexes.append(index)
        index+=1
    return significantChangesIndexes
    
def findSignificantIndexesScaleOut(metrics,column):    
    index = 0
    significantChangesIndexes=[]
    for nbMachines in metrics[column]:
        if (index+1)!=len(metrics[column]):
            if (metrics[column][index+1]>nbMachines):
                significantChangesIndexes.append(index)
        index+=1
    significantChangesIndexes
    return significantChangesIndexes
    
def findSignificantIndexesScaleIn(metrics):    
    index = 0
    significantChangesIndexes=[]
    for nbMachines in metrics[0]:
        if (index+1)!=len(metrics[0]):
            if (metrics[0][index+1]<nbMachines):
                significantChangesIndexes.append(index)
        index+=1
    return significantChangesIndexes    
    
def findSignificantSetsOfMetrics(metrics,significantChangesIndexes,changeInterval):
    significantIntervals=[]
    for significantChange in significantChangesIndexes:
        start=0
        end=len(metrics[0])-1    
        if (significantChange-changeInterval)<0:
            start=significantChange-changeInterval
    
        if (significantChange+changeInterval>len(metrics[0])):
            end=significantChange+changeInterval
        
        if (significantChange-changeInterval)>=0 and (significantChange+changeInterval<=len(metrics[0])):
            significantIntervals.append((significantChange-changeInterval,significantChange+changeInterval))
    return significantIntervals
def computeClustersWithKMeans(metrics,significantIntervals,numberOfClusters,cutoff,headers):
    # choose stable metric like cost/ vm nb/ action enforcement to gather "important" pieces of data
    index=0    
    pointMetrics=[]
    for metricValues in metrics:
        if (metrics[index]!=[] and index<len(headers)):
            i=0
            
            pointMetrics.insert(index,[])
            for (start,end) in significantIntervals:
                if start!=end:
                    p=makePointOfList(end-start,metrics[index][start:end])
                    pointMetrics[index].append(p)
                    i+=1    
            index +=1
                
    clusters=[]
    for i in range(len(headers)):
        clusters.append(kmeans(pointMetrics[i], numberOfClusters, cutoff))
    
    return clusters        
    
def plotClustersAndMetrics(metrics,clusters,significantIntervals,headers):
    index=0
    for metricValues in metrics:
        if (metrics[index]!=[]):
            plt.figure(index+1)   
            i=0
            plt.xlabel("Time")
            plt.ylabel("Metric value")
            plt.title(headers[index])
            for (start,end) in significantIntervals:
                if start!=end:
                    plt.plot(np.arange(end-start),metrics[index][start:end],'b' )
                    i+=1    
            for cl in clusters[index]:
                plt.plot(np.arange(len(cl.centroid.coords)),cl.centroid.coords,'r')
            index +=1 
def findClustersOnMetricsGivenFiles(currentFileName,fileName, changeInterval,actionName,actionEntity):
    array=[]  
    index=0
    headersOfFile=[]
    with open(currentFileName, 'rbU') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            if not row[0].islower() and not row[0].isupper():
                values = row

                array.insert(index,values)
                index+=1
            else:
                headersOfFile=row
                print "Headers of fileeeeeeeeeeee",headersOfFile
    currentMetrics=[]
    size = 0
    
    for values in array:
        index=0
        for v in values:
            if size==0:
                currentMetrics.append([float(v)])
            else:
                currentMetrics[index].append(float(v))
            index+=1
        size+=1    
   
    cutoff =0.2
    (metrics,actions,headers) = readMetricsFromFile(fileName)
    (signIndexes,averageTime) = findSignificantIndexesGivenActionAndEntity(actionName,actionEntity,actions)
    changeInterval+=averageTime/2   

    if (signIndexes[len(signIndexes)-1]+changeInterval/2>len(metrics[0])):
        signIndexes.remove(len(signIndexes)-1)
    signIntervals = findSignificantSetsOfMetrics(metrics,signIndexes,changeInterval)
    numberOfClusters=int(math.sqrt(len(signIntervals)))
    clusters= computeClustersWithKMeans(metrics,signIntervals,numberOfClusters,cutoff,headers)
  
    #newInterval=[] 
    #for metricValues in metrics:
        #newInterval.append(metricValues[signIntervals[0][0]:(signIntervals[0][0]+changeInterval)])
    #print signIntervals[0][0]
    results=evaluateHalfInterval(currentMetrics,signIntervals,metrics,clusters,headers,averageTime)
    plotResults(currentMetrics,results,headers)    
    return (results,headers)
             
def findClustersOnMetricsOfFile(intervalToEvaluate,fileName, changeInterval,actionName,actionEntity):
    cutoff =0.2
    (metrics,actions,headers) = readMetricsFromFile(fileName)
    (signIndexes,averageTime) = findSignificantIndexesGivenActionAndEntity(actionName,actionEntity,actions)
    changeInterval+=averageTime/2   

    if (signIndexes[len(signIndexes)-1]+changeInterval/2>len(metrics[0])):
        signIndexes.remove(len(signIndexes)-1)
    signIntervals = findSignificantSetsOfMetrics(metrics,signIndexes,changeInterval)
    numberOfClusters=int(math.sqrt(len(signIntervals)))
    global clusters    
    clusters= computeClustersWithKMeans(metrics,signIntervals,numberOfClusters,cutoff,headers)
    #plotClustersAndMetrics(metrics,clusters,signIntervals)
    #newInterval=[] 
    #for metricValues in metrics:
        #newInterval.append(metricValues[signIntervals[0][0]:(signIntervals[0][0]+changeInterval)])
    #print signIntervals[0][0]
    return (evaluateHalfInterval(intervalToEvaluate,signIntervals,metrics,clusters,headers,averageTime),headers)
    
    
    
def evaluateHalfInterval(interval,significantIntervals,metrics,clusters,headers,averageTime):
    pointsToEvaluate=[]   
    for metricInterval in interval:
        pointsToEvaluate.append(Point(metricInterval))
    i=0
    closestClusters=[]
    for cl in clusters:
        minDist=9999999
        centroidId=-1
        j=0
        for c1 in cl:
            if (minDist>getMinDistToHalfIntervalPoint(c1,pointsToEvaluate[i])):
                minDist=getMinDistToHalfIntervalPoint(c1,pointsToEvaluate[i])
                centroidId=j
            j+=1              
        closestClusters.append(centroidId)
        i+=1
  
    index=0    
    pointIds=dict()
    for (start,end) in significantIntervals:
        pointIds[start]=dict()
    length=0   
    for metricValues in metrics:
        if (metrics[index]!=[] and index<len(headers)):
            i=0
            for (start,end) in significantIntervals:
                if start!=end:
                    p=makePointOfList(end-start,metrics[index][start:end])
                    pointIds[start][index]=p
                    i+=1    
                    if length==0:
                        length=(end-start)
            index +=1    

    metricsPerIntervals=dict()    
    for startIndex in pointIds:
        metricsPerIntervals[startIndex]=dict()
        for metricIndex in pointIds[startIndex]:
            point=pointIds[startIndex][metricIndex]
            d=10000000
            clusterIndex=-1
            i=0
            for cl in clusters[metricIndex]:
                if(getDistance(cl.centroid,point)<d):
                    clusterIndex=i
                    d=getDistance(cl.centroid,point)
                i+=1
            metricsPerIntervals[startIndex][metricIndex]=clusterIndex
    occurenceOfCombinationsOfCentroids=dict()        
    for startIndex in metricsPerIntervals:
        oc=[]
        for metricIndex in metricsPerIntervals[startIndex]:
            oc.append(metricsPerIntervals[startIndex][metricIndex])
        if not occurenceOfCombinationsOfCentroids.has_key(MetricsCluster(oc)):
            occurenceOfCombinationsOfCentroids[MetricsCluster(oc)]=1
        else:
            occurenceOfCombinationsOfCentroids[MetricsCluster(oc)]+=1

    found=False
    closestFit=0
    closestFitCombination=MetricsCluster([])
    index=0
    for oc in occurenceOfCombinationsOfCentroids.keys():
        nbMatches=0    
        for i in range(len(oc.clustersCentroids)):
            if oc.clustersCentroids[i]==closestClusters[i]:
                nbMatches+=1
        if nbMatches==len(oc.clustersCentroids):
            found=True
        else:
            if closestFit<nbMatches:
                closestFit=nbMatches
                closestFitCombination=oc.clustersCentroids
        index+=1
    if found==False:
        closestClusters= closestFitCombination
    else:
        print closestClusters

    
    """process results"""
    foundCoords=[]
    result = []
  
    for index in range(len(closestClusters)):
        foundCoords.append([])
        result.append([])        
        initialDif= interval[index][0]- clusters[index][closestClusters[index]].centroid.coords[0]      
        for i1 in range(len(clusters[index][closestClusters[index]].centroid.coords)): 
            if (i1>length/2-averageTime/2):
                if (initialDif<-2):
                    foundCoords[index].append(clusters[index][closestClusters[index]].centroid.coords[i1]+initialDif)
                else:
                    foundCoords[index].append(clusters[index][closestClusters[index]].centroid.coords[i1])
                result[index].append(foundCoords[index][i1])
            else:
                foundCoords[index].append(clusters[index][closestClusters[index]].centroid.coords[i1]+initialDif)
        
    for index in range(len(closestClusters)):
        for i1 in range(len(interval[0])):
            foundCoords[index][i1]=interval[index][i1]

    """for index in range(len(closestClusters)):
        plt.figure(index+1)   
        plt.xlabel("Time")
        plt.ylabel(headers[index])
        plt.plot(np.arange(len(interval[index])),interval[index],'b',linewidth=2.0 )
        plt.plot(np.arange(len(foundCoords[index])),foundCoords[index],'r',linewidth=1.0)"""
    return result
def plotResults(initial, expected,headers):
    results=[]

    for index in range(len(initial)):
        results.append([])
        print index
        for r in initial[index]:
            results[index].append(r)
            
    for i in range(len(expected)):
        for r in expected[i]:        
            results[i].append(r)
    print results        
    for t in range(len(headers)):
        plt.figure(t+1)   
        plt.xlabel("Time")
        plt.ylabel(headers[t])
        
        plt.plot(np.arange(len(initial[t])),initial[t],'b',linewidth=3.0 )
        plt.plot(np.arange(len(results[t])),results[t],'g',linewidth=2.0)
   
def plotMatrix(H,labelsx,labelsy):
    """
    H=[]    
    for startIndex in metricsPerIntervals:
        H.append([])
        for metricIndex in metricsPerIntervals[startIndex]:
            H[i].append(metricsPerIntervals[startIndex][metricIndex])
        i+=1
    
    labelsx = []
    labelsy = []
    i=0    
    for startIndex in metricsPerIntervals.iterkeys():
        labelsx.append(startIndex)

    
    i=0
    for startIndex in metrics:
        labelsy.append(i)
        i+=1
    """
    plt.figure(0)  
    plt.imshow(H, interpolation='none')
    plt.xticks(np.arange(len(labelsx)-1), labelsx)    
    plt.yticks(np.arange(len(labelsy)-1), labelsy)
   
    cb = plt.colorbar()
    cb.set_ticks([0,  1])  # force there to be only 3 ticks
    cb.set_ticklabels(['C1', 'Cn'])  # put text labels on them
    plt.show()  

    
def plotChartsForAll():
    array=[]
    index=0
    headers=[]
    with open('test.csv', 'rbU') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            if not row[0].islower() and not row[0].isupper():
                values = row

                array.insert(index,values)
                index+=1
            

    test=[]
    size = 0
    
    for values in array:
        index=0
        for v in values:
            if size==0:
                test.append([float(v)])
            else:
                test[index].append(float(v))
            index+=1
        size+=1   
    array=[]
    results=[]
    
    index=0   
    with open('results.csv', 'rbU') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            if not row[0].islower() and not row[0].isupper():
                values = row
                array.insert(index,values)
                index+=1
            else:
                headers=row

    
    for i in range(len(test)):
        results.append([])
        for j in range(len(test[i])):
            results[i].append(test[i][j])
    for values in array:
        index=0
        for v in values:
            results[index].append(float(v))
            index+=1
        size+=1 
    observed=[]
    array=[]
    size=0
    index=0
    with open('observed.csv', 'rbU') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            if not row[0].islower() and not row[0].isupper():
                values = row
                array.insert(index,values)
                index+=1
    observed=[]
    
    for i in range(len(test)):
        observed.append([])
        for j in range(len(test[i])):
            observed[i].append(test[i][j])
    print len(observed[0])        
    size = 0
    index=0
    for values in array:
        index=0
        for v in values:
            observed[index].append(float(v))
            index+=1
        size+=1   
                 
    index=0
    for i in range(len(headers)):  
        plt.figure(index)   
        plt.xlabel("Time")
        plt.ylabel(headers[index])
        plt.plot(np.arange(len(observed[index])),observed[index],'b',linewidth=2.0)
        plt.plot(np.arange(len(results[index])),results[index],'r',linewidth=1.0)
        index+=1

def computeStdDeviation(res, observed,varianceSoFar):
    stdDev=0
    for index in range(len(res)):
        
        for i in range(len(res[0])):
            """stdDev+=math.fabs(res[index][i]-observed[index][i])/(max(max(res[index]),max(observed[index])))"""
            varianceSoFar[index]+=((res[index][i]-observed[index][i])/(max(max(res[index]),max(observed[index]))))**2
    return varianceSoFar
def computeStatistics(metrics):
    observedMetrics=[]
    array=[]  
    index=0

    with open('observed.csv', 'rbU') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            if not row[0].islower() and not row[0].isupper():
                values = row

                array.insert(index,values)
                index+=1
            else:
                headersOfFile=row
    
    size = 0
    
    for values in array:
        index=0
        for v in values:
            if size==0:
                observedMetrics.append([float(v)])
            else:
                observedMetrics[index].append(float(v))
            index+=1
        size+=1  
    varianceSoFar=[]
    for i in range(len(observedMetrics)):
        varianceSoFar.append(0)
    headers=[]
    for i in range(100):
        (res,headers) = findClustersOnMetricsOfFile(metrics,'CloudService_16_2.csv',len(metrics[0]),"DataNodeServiceUnit_scaleIn","DataNodeServiceUnit")
        varianceSoFar = computeStdDeviation(res,observedMetrics,varianceSoFar)
    for i in range(len(observedMetrics)):
        print headers[i],"Variance= ",varianceSoFar[i]/100,"Standard deviation = ",math.sqrt(varianceSoFar[i]/100)
def computeExpectedValuesForGivenSP(currentMetrics,historicalMetrics,metricNames,actions,actionName,targetSP,changeInterval):
    cutoff =0.2
    (signIndexes,averageTime) = findSignificantIndexesGivenActionAndEntity(actionName,targetSP,actions)
    changeInterval+=averageTime/2   

    if (signIndexes[len(signIndexes)-1]+changeInterval/2>len(metrics[0])):
        signIndexes.remove(len(signIndexes)-1)
    signIntervals = findSignificantSetsOfMetrics(historicalMetrics,signIndexes,changeInterval)
    numberOfClusters=int(math.sqrt(len(signIntervals)))   
    clusters= computeClustersWithKMeans(historicalMetrics,signIntervals,numberOfClusters,cutoff,headers)
    #plotClustersAndMetrics(metrics,clusters,signIntervals)
    #newInterval=[] 
    #for metricValues in metrics:
        #newInterval.append(metricValues[signIntervals[0][0]:(signIntervals[0][0]+changeInterval)])
    #print signIntervals[0][0]
    return evaluateHalfInterval(intervalToEvaluate,signIntervals,historicalMetrics,clusters,metricNames,averageTime),metricNames
    
    
def main(args):
    #plotChartsForAll()
    #app.debug = True
    """app.run(port=8321,debug=True)"""
    array=[]  
    index=0
    headersOfFile=[]
    with open('test.csv', 'rbU') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            if not row[0].islower() and not row[0].isupper():
                values = row

                array.insert(index,values)
                index+=1
            else:
                headersOfFile=row
                print "Headers of fileeeeeeeeeeee",headersOfFile
    metrics=[]
    size = 0
    
    for values in array:
        index=0
        for v in values:
            if size==0:
                metrics.append([float(v)])
            else:
                metrics[index].append(float(v))
            index+=1
        size+=1    
    #computeStatistics(metrics)
    (res,headers)=findClustersOnMetricsOfFile(metrics,'CloudService_16_2.csv',len(metrics[0]),"DataNodeServiceUnit_scaleIn","DataNodeServiceUnit")        
    plotResults(metrics,res,headers)    
    print "Length of the metrics to be evaluated is " ,len(metrics[0])," length of the result ", len(res[0])
    with open('results.csv', 'wb') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=',',
                                quotechar=' ', quoting=csv.QUOTE_MINIMAL)
        
        spamwriter.writerow(headers)
        for index in range(len(res[0])):
            line=[]
            for l in range (len(res)):
                line.append(res[l][index])
            spamwriter.writerow(line)
    
if __name__ == "__main__": main(sys.argv)