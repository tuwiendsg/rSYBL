import urllib, urllib2, sys, httplib

url = "/rSYBL-analysis-engine-0.1-SNAPSHOT/restWS"
#HOST_IP="83.212.112.35"
#HOST_IP="128.130.172.214:8080"
HOST_IP="128.130.172.214:8081"

 

if __name__=='__main__':
	connection =  httplib.HTTPConnection(HOST_IP)
        #read composition rules file
        composition_file = open("./serviceDescription.xml", "r")
        body_content =  composition_file.read()
       
        headers={
	        'Content-Type':'application/xml; charset=utf-8',
                'Accept':'application/xml, multipart/related'
	}
 
	connection.request('PUT', url+'/setApplicationDescriptionCELAR', body=body_content,headers=headers,)
	result = connection.getresponse()
        print result.read()
 

 

