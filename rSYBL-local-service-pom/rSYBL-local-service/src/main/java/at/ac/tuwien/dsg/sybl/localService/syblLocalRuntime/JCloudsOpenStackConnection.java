/** 
   Copyright 2013 Technische Universität Wien (TUW), Distributed Systems Group E184

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package at.ac.tuwien.dsg.sybl.localService.syblLocalRuntime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;



/*Georgiana Copil Vienna University of Technology
 *          ==============================
 */

public class JCloudsOpenStackConnection {
	
	NovaApi client;
	public JCloudsOpenStackConnection(){
		Properties overrides = new Properties();

		overrides.setProperty(Constants.PROPERTY_ENDPOINT,
				"http://openstack.infosys.tuwien.ac.at:5000/v2.0");

		Iterable<Module> modules = ImmutableSet
				.<Module> of(new SLF4JLoggingModule());

		ComputeServiceContext context = ContextBuilder
				.newBuilder("openstack-nova")
				.credentials("CELAR:ecopil", "Aeb2Piec")
				.endpoint("http://openstack.infosys.tuwien.ac.at:5000/v2.0")
				// .overrides(overrides) 128.131.172.226
				// .modules(modules)
				.buildView(ComputeServiceContext.class);

		ComputeService computeService = context.getComputeService();

		 client = (NovaApi) context
				.unwrap(NovaApiMetadata.CONTEXT_TOKEN).getApi();
	}
	public void scaleUpInstance(String name){
		FlavorApi flavorApi = client.getFlavorApiForZone("myregion");
		ServerApi serverApi = client.getServerApiForZone("myregion");
		Iterator<? extends IterableWithMarker<? extends Server>> iterator = client
				.getServerApiForZone("myregion").listInDetail().iterator();
		
		for (Server myServer : iterator.next()) {
		
		 if (myServer.getName().equalsIgnoreCase(name))
		if( myServer.getStatus()==Status.ACTIVE || myServer.getStatus()==Status.PAUSED){
		String initialFlavor = flavorApi.get(myServer.getFlavor().getId()).getName();
		String largerFlavor = getFlavorForBiggerInstance(flavorApi,flavorApi.get(myServer.getFlavor().getId()));
		if (!initialFlavor.equals(flavorApi.get(largerFlavor))){
		serverApi.resize(myServer.getId()+"", largerFlavor);
		System.out.println("Scaled up from "+ initialFlavor+" to "+largerFlavor);
		}
		 }
		}
	}
	public void scaleDownInstance(String name){
	    FlavorApi flavorApi = client.getFlavorApiForZone("myregion");
		ServerApi serverApi = client.getServerApiForZone("myregion");
		
		Iterator<? extends IterableWithMarker<? extends Server>> iterator = client
				.getServerApiForZone("myregion").listInDetail().iterator();
		
		for (Server myServer : iterator.next()) {
		
		 if (myServer.getName().equalsIgnoreCase(name))
				if( myServer.getStatus()==Status.ACTIVE || myServer.getStatus()==Status.PAUSED){
					
		String initialFlavor = flavorApi.get(myServer.getFlavor().getId()).getName();
		String smaller = getFlavorForSmallerInstance(flavorApi,flavorApi.get(myServer.getFlavor().getId()));
		if (!initialFlavor.equals(flavorApi.get(smaller))){
		
		serverApi.resize(myServer.getId(), smaller);
		
//		while ( serverApi.get(myServer.getId()).getStatus()!= Status.VERIFY_RESIZE && serverApi.get(myServer.getId()).getStatus() != Status.ERROR)
//		
//			try {
//				System.out.println(serverApi.get(myServer.getId()).getFlavor());
//				System.out.println("Waiting for verify resize or error, current status "+ serverApi.get(myServer.getId()).getStatus());
//				
//				Thread.sleep(20);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		if (serverApi.get(myServer.getId()).getStatus()== Status.VERIFY_RESIZE)
//		{
//			serverApi.confirmResize(myServer.getId());
//		}
//		
//		if (myServer.getStatus()== Status.ERROR){
//			System.out.println("We had an error");
//		}
		//serverApi.confirmResize(myServer.getId());
		System.out.println("Scaled down from "+ initialFlavor +" to "+flavorApi.get(smaller));
		 }
		}
		}
	}
	public static void main(String[] args) {
		
		JCloudsOpenStackConnection jCloudsInstance= new JCloudsOpenStackConnection();
		jCloudsInstance.scaleDownInstance("instance");
	}

	public void test() {

		
		FlavorApi flavorApi = client.getFlavorApiForZone("myregion");
		ImageApi imageApi = client.getImageApiForZone("myregion");
		FloatingIPApi floatingIPApi = client.getFloatingIPExtensionForZone(
				"myregion").get();

		for (FloatingIP floatingIP : floatingIPApi.list()) {
			System.out.println(floatingIP.getId() + " "
					+ floatingIP.getInstanceId());
		}

		ServerApi serverApi = client.getServerApiForZone("myregion");

		Iterator<? extends IterableWithMarker<? extends Server>> iterator = client
				.getServerApiForZone("myregion").listInDetail().iterator();

		for (Server myServer : iterator.next()) {
			// Resource r = myServer.getFlavor();
			// Multimap<String, Address> multi =myServer.getAddresses();

			// for (Entry<String,Address> e: multi.entries()){
			// System.out.println(e.getKey()+" "+e.getValue()+" ");
			// }
			// Flavor flavor = flavorApi.get(r.getId());
			// Image image = imageApi.get(myServer.getImage().getId());
			// System.out.println("Flavor "
			// +flavor.getDisk()+" "+flavor.getRam()+" "+flavor.getVcpus()+" ");

			// System.out.println("Image "+image.getMinDisk()+" "+image.getMinRam()+" ");
			// System.err.println(flavor.getName());
			// System.out.println("Flavor ID is "+myServer.getFlavor().getId());

			if (!myServer.getName().equalsIgnoreCase("instance2")) {
				// serverApi.delete(myServer.getId());
				// serverApi.stop(myServer.getId());

				// serverApi.resize(myServer.getId(),(flavor.getId()+1)+"" );
			}
			// System.out.println("AAAAfter .... " +
			// "\n Flavor "
			// +flavor.getDisk()+" "+flavor.getRam()+" "+flavor.getVcpus()+" ");

			// System.out.println("Image "+image.getMinDisk()+" "+image.getMinRam()+" ");
		}

		for (Resource res : flavorApi.list().concat()) {
			System.out.println(res);

		}

		// Flavor flavor = flavorApi.get("10");
		// System.out.println(flavor.getRam());

	}

	private String getFlavorForBiggerInstance(FlavorApi flavorApi, Flavor currentFlavor) {
		System.out.println(currentFlavor);
		ArrayList<String> list = new ArrayList<String>();
		list.add("tiny");
		list.add("small");
		list.add("medium");
		list.add("large");
		list.add("xlarge");
		list.add("2xlarge");
		list.add("xxlarge");
		String s[] = currentFlavor.getName().split("\\.");
		int currentIndex = 0;
		int closestIndex = 100;
		String flavorId = currentFlavor.getId();
		if (s.length == 3) {
			currentIndex = list.indexOf(s[1]);
			// search for flavors which have 2 points and set the smaller one
			for (Resource res : flavorApi.list().concat()) {
				if (res.getName().split("\\.").length==3){
					if (res.getName().split("\\.")[2].equals(s[2])) {
						int i = list.indexOf(res.getName().split("\\.")[1]);
						if (i!=currentIndex)
						if (i!=-1 && i - currentIndex < closestIndex - currentIndex) {
							closestIndex = i;
							flavorId = res.getId();
						}
					}
				
				}
			}
		} else {
			if (s.length == 2) {
				currentIndex = list.indexOf(s[1]);

				for (Resource res : flavorApi.list().concat()) {
					if (res.getName().split("\\.").length==2){
					int i = list.indexOf(res.getName().split("\\.")[1]);
					if (i!=currentIndex)
					if (i!=-1 && i - currentIndex < closestIndex - currentIndex) {

						closestIndex = i;
						flavorId = res.getId();
					}
					}
				}
			} else {
				if (s.length == 1) {
					currentIndex = list.indexOf(s[0]);

					for (Resource res : flavorApi.list().concat()) {
						if (res.getName().split("\\.").length==1){

						int i = list.indexOf(res.getName().split("\\.")[0]);
						if (i!=currentIndex)
						if (i!=-1 && i - currentIndex < closestIndex - currentIndex) {

							closestIndex = i;
							flavorId = res.getId();
						}
					}
				}
			}
		}
		}

//		for (Resource res : flavorApi.list().concat()) {
//			System.out.println(res.toString());
//		}
		//System.err.println(currentFlavor.getName() + " "+ flavorApi.get(flavorId));
		return flavorId;
	}

	private String getFlavorForSmallerInstance(FlavorApi flavorApi,
			Flavor currentFlavor) {
		System.out.println(currentFlavor);
		ArrayList<String> list = new ArrayList<String>();
		list.add("tiny");
		list.add("small");
		list.add("medium");
		list.add("large");
		list.add("xlarge");
		list.add("xxlarge");
		String s[] = currentFlavor.getName().split("\\.");
		int currentIndex = 0;
		int closestIndex = -100;
		String flavorId = currentFlavor.getId();
		if (s.length == 3) {
			currentIndex = list.indexOf(s[1]);
			// search for flavors which have 2 points and set the smaller one
			for (Resource res : flavorApi.list().concat()) {
				if (res.getName().split("\\.").length > 2) {
					if (res.getName().split("\\.")[2].equals(s[2])) {
						int i = list.indexOf(res.getName().split("\\.")[1]);
						
						if (i!=-1 && currentIndex-i>=0 &&currentIndex - i < currentIndex - closestIndex) {
							closestIndex = i;
							flavorId = res.getId();
						}
					}
				}
			}
		} else {
			if (s.length == 2) {

				for (Resource res : flavorApi.list().concat()) {
					if (res.getName().split("\\.").length==2){
					int i = list.indexOf(res.getName().split("\\.")[1]);
					if ( i!=-1 && currentIndex-i>=0 && currentIndex - i < currentIndex - closestIndex) {

						closestIndex = i;
						flavorId = res.getId();
					}
				}
				}
			} else {
				if (s.length == 1) {
					currentIndex = list.indexOf(s[0]);

					for (Resource res : flavorApi.list().concat()) {
						if (res.getName().split("\\.").length==1){

						int i = list.indexOf(res.getName().split("\\.")[0]);
						if ( i!=-1 && currentIndex-i>=0 && currentIndex - i < currentIndex - closestIndex) {

							closestIndex = i;
							flavorId = res.getId();
						}
					}
				}}
			}
		}


		return flavorId;
	}

	public void tryWithEC2() {

		Iterable<Module> modules = ImmutableSet
				.<Module> of(new SLF4JLoggingModule());
		RestContext<EC2Client, EC2AsyncClient> context = ContextBuilder
				.newBuilder("openstack-nova")
				.credentials("CELAR:ecopil", "Aeb2Piec")
				.endpoint("http://openstack.infosys.tuwien.ac.at:5000/v2.0")
				.modules(modules).build();
		EC2Client client = context.getApi();

		Set<? extends Reservation<? extends RunningInstance>> reservations = client
				.getInstanceServices().describeInstancesInRegion(null);
	}
}
