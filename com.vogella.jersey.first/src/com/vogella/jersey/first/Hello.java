package com.vogella.jersey.first;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;



// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/hello")
public class Hello {
String bundlesPath = "c://bundles/";
	 
  // This method is called if TEXT_PLAIN is request


  // This method is called if HTML is request
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayHtmlHello() {
    return 
    		"com.acme.proximity"
    		+"\n"+
    		 "com.acme.gyroscope1"
    		+"\n"+
    		"com.acme.light";


  }
  

@POST
  @Path("/downloadbundle")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getFile3(InputStream incomingData) {
	  
		StringBuilder crunchifyBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		
	  
	  
	  
      File file = new File(bundlesPath+crunchifyBuilder.toString().substring(15)+".apk");
      ResponseBuilder response = Response.ok((Object) file);
      response.header("Content-Disposition",
          "attachment; filename="+crunchifyBuilder.toString().substring(15)+".apk");
      return response.build();

  }
  
	@POST
	@Path("/crunchifyService")
	@Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response crunchifyREST(InputStream incomingData) {
		StringBuilder crunchifyBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
			return Response.status(200).entity(e.getMessage()).build();
		}
		System.out.println("Data Received: " + crunchifyBuilder.toString());
		
		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()+"successfull build").build();
	}

	@GET
	@Path("/verify")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyRESTService(InputStream incomingData) {
		String result = "CrunchifyRESTService Successfully started..";

		// return HTTP response 200 in case of success
		return Response.status(200).entity(result).build();
	}

  
} 