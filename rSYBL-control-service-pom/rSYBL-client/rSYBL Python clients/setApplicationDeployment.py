import urllib, urllib2, sys, httplib

#url = "/rSYBL-analysis-engine-0.1-SNAPSHOT/restWS"
url = "/rSYBL-analysis-engine-0.1-SNAPSHOT/restWS"
#HOST_IP="83.212.112.35"
HOST_IP="83.212.117.112"
#HOST_IP="localhost:8080"

 

if __name__=='__main__':
	connection =  httplib.HTTPConnection(HOST_IP)
        #read composition rules file
        composition_file = open("./cassandraDeploymentDescription.xml", "r")
        body_content =  composition_file.read()
       
        headers={
	        'Content-Type':'application/xml; charset=utf-8',
                'Accept':'application/xml, multipart/related'
	}
 
	connection.request('PUT', url+'/setApplicationDeploymentDescriptionCELAR', body=body_content,headers=headers,)
	result = connection.getresponse()
        print result.read()
 

 

