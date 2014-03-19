# -*- coding: utf-8 -*-
"""
Created on Tue Mar 18 10:45:48 2014

@author: Georgiana
"""
import sys, math, random
import requests
import xml.etree.cElementTree as et
import testWithKMeans as kMeans
from Tkinter import *
from math import *
import tkMessageBox
MELA_URL="http://128.130.172.214:8080/MELA/REST_WS/"
recentData="historicalmonitoringdataXML/lastX?count="
historicalData="historicalmonitoringdataXML/all"




sxml="""
<MonitoredElementSnapshot>
<MonitoredElement level="SERVICE" id="CloudService">
        <MonitoredElement level="SERVICE_TOPOLOGY" id="DataEndServiceTopology">
            <MonitoredElement level="SERVICE_UNIT" id="DataControllerServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.44"/>
            </MonitoredElement>
            <MonitoredElement level="SERVICE_UNIT" id="DataNodeServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.50"/>
            </MonitoredElement>
        </MonitoredElement>
        <MonitoredElement level="SERVICE_TOPOLOGY" id="EventProcessingServiceTopology">
            <MonitoredElement level="SERVICE_UNIT" id="EventProcessingServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.24"/>
            </MonitoredElement>
            <MonitoredElement level="SERVICE_UNIT" id="LoadBalancerServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.39"/>
            </MonitoredElement>
        </MonitoredElement>
</MonitoredElement>
<Metrics>
        <entry>
            <metric type="RESOURCE" measurementUnit="$" name="cost/client/h"/>
            <value ValueType="NUMERIC">
                <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.48</Value>
            </value>
        </entry>
</Metrics>
    <Action action="SCALING OUT" targetEntityID="EventProcessingServiceUnit"/>
    <MonitoredElementSnapshot>
        <MonitoredElement level="SERVICE_TOPOLOGY" id="DataEndServiceTopology">
            <MonitoredElement level="SERVICE_UNIT" id="DataControllerServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.44"/>
            </MonitoredElement>
            <MonitoredElement level="SERVICE_UNIT" id="DataNodeServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.50"/>
            </MonitoredElement>
        </MonitoredElement>
        <Metrics>
            <entry>
                <metric type="RESOURCE" measurementUnit="%" name="cpuUsage"/>
                <value ValueType="NUMERIC">
                    <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.0999984741210938</Value>
                </value>
            </entry>
            <entry>
                <metric type="RESOURCE" measurementUnit="$" name="cost"/>
                <value ValueType="NUMERIC">
                    <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.24</Value>
                </value>
            </entry>
            <entry>
                <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                <value ValueType="NUMERIC">
                    <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">2.0</Value>
                </value>
            </entry>
        </Metrics>
        <MonitoredElementSnapshot>
            <MonitoredElement level="SERVICE_UNIT" id="DataControllerServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.44"/>
            </MonitoredElement>
            <Metrics>
                <entry>
                    <metric type="RESOURCE" measurementUnit="%" name="cpuUsage"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.5999984741210938</Value>
                    </value>
                </entry>
                <entry>
                    <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.0</Value>
                    </value>
                </entry>
                <entry>
                    <metric type="RESOURCE" measurementUnit="$" name="cost"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.12</Value>
                    </value>
                </entry>
            </Metrics>
            <MonitoredElementSnapshot>
                <MonitoredElement level="VM" id="10.99.0.44"/>
                <Metrics>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="%" name="cpu_idle"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:float">99.4</Value>
                        </value>
                    </entry>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.0</Value>
                        </value>
                    </entry>
                </Metrics>
            </MonitoredElementSnapshot>
        </MonitoredElementSnapshot>
        <MonitoredElementSnapshot>
            <MonitoredElement level="SERVICE_UNIT" id="DataNodeServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.50"/>
            </MonitoredElement>
            <Metrics>
                <entry>
                    <metric type="RESOURCE" measurementUnit="%" name="cpuUsage"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.5999984741210938</Value>
                    </value>
                </entry>
                <entry>
                    <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.0</Value>
                    </value>
                </entry>
                <entry>
                    <metric type="RESOURCE" measurementUnit="$" name="cost"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.12</Value>
                    </value>
                </entry>
            </Metrics>
            <MonitoredElementSnapshot>
                <MonitoredElement level="VM" id="10.99.0.50"/>
                <Metrics>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="%" name="cpu_idle"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:float">98.4</Value>
                        </value>
                    </entry>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.0</Value>
                        </value>
                    </entry>
                </Metrics>
            </MonitoredElementSnapshot>
        </MonitoredElementSnapshot>
    </MonitoredElementSnapshot>
    <MonitoredElementSnapshot>
        <MonitoredElement level="SERVICE_TOPOLOGY" id="EventProcessingServiceTopology">
            <MonitoredElement level="SERVICE_UNIT" id="EventProcessingServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.24"/>
            </MonitoredElement>
            <MonitoredElement level="SERVICE_UNIT" id="LoadBalancerServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.39"/>
            </MonitoredElement>
        </MonitoredElement>
        <Metrics>
            <entry>
                <metric type="RESOURCE" measurementUnit="no" name="numberOfClients"/>
                <value ValueType="NUMERIC">
                    <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.0</Value>
                </value>
            </entry>
            <entry>
                <metric type="RESOURCE" measurementUnit="ms" name="responseTime"/>
                <value ValueType="NUMERIC">
                    <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.0</Value>
                </value>
            </entry>
            <entry>
                <metric type="RESOURCE" measurementUnit="operations/s" name="throughput"/>
                <value ValueType="NUMERIC">
                    <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.0</Value>
                </value>
            </entry>
            <entry>
                <metric type="RESOURCE" measurementUnit="$" name="cost"/>
                <value ValueType="NUMERIC">
                    <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.24</Value>
                </value>
            </entry>
            <entry>
                <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                <value ValueType="NUMERIC">
                    <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">2.0</Value>
                </value>
            </entry>
        </Metrics>
        <MonitoredElementSnapshot>
            <MonitoredElement level="SERVICE_UNIT" id="EventProcessingServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.24"/>
            </MonitoredElement>
            <Metrics>
                <entry>
                    <metric type="RESOURCE" measurementUnit="ms" name="responseTime"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.0</Value>
                    </value>
                </entry>
                <entry>
                    <metric type="RESOURCE" measurementUnit="operations/s" name="throughput"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.0</Value>
                    </value>
                </entry>
                <entry>
                    <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.0</Value>
                    </value>
                </entry>
                <entry>
                    <metric type="RESOURCE" measurementUnit="$" name="cost"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.12</Value>
                    </value>
                </entry>
            </Metrics>
            <Action action="SCALING OUT" targetEntityID="EventProcessingServiceUnit"/>
            <MonitoredElementSnapshot>
                <MonitoredElement level="VM" id="10.99.0.24"/>
                <Metrics>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="operations/s" name="throughput"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:int">0</Value>
                        </value>
                    </entry>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="%" name="cpu_idle"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:float">87.8</Value>
                        </value>
                    </entry>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="milliseconds" name="responseTime"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:int">0</Value>
                        </value>
                    </entry>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.0</Value>
                        </value>
                    </entry>
                </Metrics>
            </MonitoredElementSnapshot>
        </MonitoredElementSnapshot>
        <MonitoredElementSnapshot>
            <MonitoredElement level="SERVICE_UNIT" id="LoadBalancerServiceUnit">
                <MonitoredElement level="VM" id="10.99.0.39"/>
            </MonitoredElement>
            <Metrics>
                <entry>
                    <metric type="RESOURCE" measurementUnit="no" name="numberOfClients"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.0</Value>
                    </value>
                </entry>
                <entry>
                    <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.0</Value>
                    </value>
                </entry>
                <entry>
                    <metric type="RESOURCE" measurementUnit="$" name="cost"/>
                    <value ValueType="NUMERIC">
                        <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">0.12</Value>
                    </value>
                </entry>
            </Metrics>
            <MonitoredElementSnapshot>
                <MonitoredElement level="VM" id="10.99.0.39"/>
                <Metrics>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="no" name="activeConnections"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:int">0</Value>
                        </value>
                    </entry>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="%" name="cpu_idle"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:float">87.4</Value>
                        </value>
                    </entry>
                    <entry>
                        <metric type="RESOURCE" measurementUnit="no" name="numberOfVMs"/>
                        <value ValueType="NUMERIC">
                            <Value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">1.0</Value>
                        </value>
                    </entry>
                </Metrics>
            </MonitoredElementSnapshot>
        </MonitoredElementSnapshot>
    </MonitoredElementSnapshot>
</MonitoredElementSnapshot>
"""
def recursivelyFind(currentSnapshots,findId):
    if len(currentSnapshots)>0:   
        if (currentSnapshots[0].find("MonitoredElement").get("id").strip()==findId.strip()):
            return currentSnapshots[0]
        else:
            for e in currentSnapshots[0].findall("MonitoredElementSnapshot"):
                current=e.find("MonitoredElement") 
                currentSnapshots.append(e)
                if current.get("id").strip()==findId.strip():
                   return e
            currentSnapshots.pop(0)
            return recursivelyFind(currentSnapshots,findId)
def recursivelyFindIds(currentEl,currentIds):
    for e in currentEl.findall("MonitoredElement"):
        if (e.get("level")!="VM"):  
            currentIds.append(e.get("id"))
    for e in currentEl.findall("MonitoredElement"):
        if (e.get("level")!="VM"):      
            recursivelyFindIds(e,currentIds)
    return currentIds
def getRecentSnapshots(items):
    s = requests.Session()
    histURL="%s%s%i"%(MELA_URL,recentData,items)
    resp = s.get(histURL, stream=True)
    content = resp.content
    return content
def getAllHistory():
    s = requests.Session()
    histURL="%s%s"%(MELA_URL,historicalData)
    resp = s.get(histURL, stream=True)
    content = resp.content
    return content
def initializeKnownMetrics():
    #initialize the known metrics for the different IDs existent
    historicalMetrics = dict()
    content = getAllHistory()
    tree = et.fromstring(content)
    currentItems = 0 
    for el in tree.findall("MonitoredElementSnapshot"):
        timestamp = el.find("Timestamp").text
        ids= recursivelyFindIds(el,[])
        if (len(historicalMetrics)==0):
            for x in ids:
                historicalMetrics[x]=dict()
        for x in ids:
            historicalMetrics[x]["Action"]=[]
            current= recursivelyFind ([el], x)         
            if("MetricsHeaders" not in historicalMetrics[x]):      
                historicalMetrics[x]["MetricsHeaders"]=[]
                currentEl=current.find("MonitoredElement")  
                index=0     
                for e in currentEl.findall('Metrics'):
                    for m in e.getchildren():            
                        for metric in m.findall("metric"):
                            historicalMetrics[x]["MetricsHeaders"].append(metric.get("name"))
                            index+=1
                for m in historicalMetrics[x]["MetricsHeaders"]:
                    historicalMetrics[x][m]=[]
            if (current.find("Action")!=None):
                if (len(historicalMetrics[x]["Action"])>0):
                    historicalMetrics[x]["Action"][0].append(current.find("Action").get("action"))
                    historicalMetrics[x]["Action"][1].append(current.find("Action").get("targetEntityID"))
                else:
                    historicalMetrics[x]["Action"][0]=current.find("Action").get("action")
                    historicalMetrics[x]["Action"][1]=current.find("Action").get("targetEntityID")
            for e in current.findall('Metrics'):
                for m in e.getchildren(): 
                    name=m.find("metric").get("name") 
                    if (name in historicalMetrics[x]):
                        historicalMetrics[x][name].append(m.find("value").find("Value").text)
                    else:
                        historicalMetrics[x]["MetricsHeaders"].append(name)                        
                        historicalMetrics[x][name]=[m.find("value").find("Value").text]
            currentItems +=1
    return historicalMetrics
def getCurrentMetrics(items):
    #initialize the known metrics for the different IDs existent
    historicalMetrics = dict()
    content = getRecentSnapshots(items)
    tree = et.fromstring(content)
    currentItems = 0 
    for el in tree.findall("MonitoredElementSnapshot"):
        timestamp = el.find("Timestamp").text
        print timestamp
        
        ids= recursivelyFindIds(el,[])
        if (len(historicalMetrics)==0):
            for x in ids:
                historicalMetrics[x]=dict()
        for x in ids:
            historicalMetrics[x]["Action"]=[]
            current= recursivelyFind ([el], x)         
            if("MetricsHeaders" not in historicalMetrics[x]):      
                historicalMetrics[x]["MetricsHeaders"]=[]
                currentEl=current.find("MonitoredElement")  
                index=0     
                for e in currentEl.findall('Metrics'):
                    for m in e.getchildren():            
                        for metric in m.findall("metric"):
                            historicalMetrics[x]["MetricsHeaders"].append(metric.get("name"))
                            index+=1

                for m in historicalMetrics[x]["MetricsHeaders"]:
                    historicalMetrics[x][m]=[]
            if (current.find("Action")!=None):
                if (len(historicalMetrics[x]["Action"])>0):
                    historicalMetrics[x]["Action"][0].append(current.find("Action").get("action"))
                    historicalMetrics[x]["Action"][1].append(current.find("Action").get("targetEntityID"))
                else:
                    historicalMetrics[x]["Action"][0]=current.find("Action").get("action")
                    historicalMetrics[x]["Action"][1]=current.find("Action").get("targetEntityID")
            for e in current.findall('Metrics'):
                for m in e.getchildren(): 
                    name=m.find("metric").get("name") 
                    if (name in historicalMetrics[x]):
                        historicalMetrics[x][name].append(m.find("value").find("Value").text)
                    else:
                        historicalMetrics[x]["MetricsHeaders"].append(name)                        
                        historicalMetrics[x][name]=[m.find("value").find("Value").text]
            currentItems +=1
    return historicalMetrics
def fetch(entries):
   for entry in entries:
      field = entry[0]
      text  = entry[1].get()
      print('%s: "%s"' % (field, text)) 

def makeform(root, fields):
   entries = []
   for field in fields:
      row = Frame(root)
      lab = Label(row, width=20, text=field, anchor='w')
      ent = Entry(row)
      row.pack(side=TOP, fill=X, padx=5, pady=5)
      lab.pack(side=LEFT)
      ent.pack(side=RIGHT, expand=YES, fill=X)
      entries.append((field, ent))
   return entries
MELA_fields = 'Service Part', 'Action Name', 'Targeted Service Part'
File_Fields = 'Historical Metrics','Current Metrics File', 'Action Name','Targeted Service Part'
def mela_based_test(entries):
    currentId=""
    actionToEstimate=""
    targetSP=""
    for entry in entries:
      if (MELA_fields[0]==entry[0]):
          currentId  = entry[1].get()
      if (MELA_fields[1]==entry[0]):
          actionToEstimate  = entry[1].get()
      if (MELA_fields[2]==entry[0]):
          targetSP  = entry[1].get()
      
    test(currentId, actionToEstimate, targetSP,6,10)
def files_based_test(entries):
    historicalMetrics=""
    currentMetrics=""
    actionToEstimate=""
    targetSP=""
    for entry in entries:
      if (File_Fields[0]==entry[0]):
          historicalMetrics  = entry[1].get()
      if (File_Fields[1]==entry[0]):
          currentMetrics  = entry[1].get()
      if (File_Fields[2]==entry[0]):
          actionToEstimate  = entry[1].get()
      if (File_Fields[3]==entry[0]):
          targetSP  = entry[1].get()
      
    (results,headers)=kMeans.findClustersOnMetricsGivenFiles(currentMetrics,historicalMetrics, 10,actionToEstimate,targetSP)
    
def gui(fields,commandToBeExecuted):
   root = Tk()
   ents = makeform(root, fields)
   root.bind('<Return>', (lambda event, e=ents: fetch(e)))   
   b1 = Button(root, text='Show',
          command=(lambda e=ents: commandToBeExecuted(e)))
   b1.pack(side=LEFT, padx=5, pady=5)
   b2 = Button(root, text='Quit', command=root.quit)
   b2.pack(side=LEFT, padx=5, pady=5)
   root.mainloop()
def test(currentId, actionToEstimate, targetSP,items):
    historicalMetrics=initializeKnownMetrics()
    currentMetrics=getCurrentMetrics(items)
    if (len(currentMetrics[currentId]["Action"])>0):    
        kMeans.computeExpectedValuesForGivenSP(currentMetrics[currentId],historicalMetrics[currentId],currentMetrics[currentId]["MetricsHeaders"],currentMetrics[currentId]["Action"],actionToEstimate,targetSP)
    else:
        print "Not processing the data, it has been no action in history"
def main(args):
   #test("CloudService", "SCALING OUT","DataNodeServiceUnit",5)
   #test("EventProcessingServiceTopology","",5)
   root = Tk()
   
   root.withdraw()
   result=tkMessageBox.askquestion("What type of data are you using? Real-time MELA or introducing Files?", "What type of data are you using? Real-time MELA or introducing Files? Choose Yes if you choose to use MELA", icon='question')
   if result == 'yes':
        print "Using MELA"
        root.withdraw()
        gui(MELA_fields,mela_based_test)
   else:
        print "Using Files as Input"   
        gui(File_Fields,files_based_test)
if __name__ == "__main__": main(sys.argv)