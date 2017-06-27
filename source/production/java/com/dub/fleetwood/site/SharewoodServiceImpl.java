package com.dub.fleetwood.site;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import com.dub.fleetwood.site.SharewoodService;
import com.dub.fleetwood.site.exceptions.NoUploadFileException;
import com.dub.fleetwood.site.exceptions.SharewoodException;
import com.dub.fleetwood.site.Photo;
import com.dub.fleetwood.site.PhotoWebServiceList;

/**
 * @author Dominique Ubersfeld
 * All properties are externally configured
 */
@Service
@PropertySource("classpath:sharewood.properties")
public class SharewoodServiceImpl implements SharewoodService {

	@Value("${sharewoodPhotoListURL}")
	private String sharewoodPhotoListURL;
	
	@Value("${sharewoodPhotoURLPattern}")
	private String sharewoodPhotoURLPattern;
		
	@Value("${sharewoodPhotoBaseURL}")
	private String sharewoodPhotoBaseURL; 
	
	private String sharewoodPhotoURL;
	
	
    @Inject 
    @Qualifier("sharewoodRestTemplate")
    private OAuth2RestOperations sharewoodRestTemplate;
    
   
	public void setSharewoodRestTemplate(OAuth2RestTemplate sharewoodRestTemplate) {
		this.sharewoodRestTemplate = sharewoodRestTemplate;
	}

	
	public String getSharewoodPhotoListURL() {
		return this.sharewoodPhotoListURL;
	}

	
	@Override
	public List<Photo> getMyPhotos() throws SharewoodException 
	{				
		sharewoodRestTemplate.getAccessToken();
		
		HttpHeaders headers = new HttpHeaders();
		List<MediaType> amt = new ArrayList<>();
		amt.add(MediaType.APPLICATION_JSON);
		headers.setAccept(amt);
		
		HttpEntity<PhotoWebServiceList> request = new HttpEntity<>(null, headers);
		
		ResponseEntity<PhotoWebServiceList> response 
						= sharewoodRestTemplate.exchange(
								sharewoodPhotoListURL, HttpMethod.GET, request, PhotoWebServiceList.class);

		try {
			if (response.getStatusCode() == HttpStatus.OK) {
				return response.getBody().getPhotos();
			} else {
				throw new SharewoodException("Error");
			}
		} catch (HttpStatusCodeException e) {
			System.out.println("exception caught " + e);
			String message;	
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				message = "Photo not found";
			} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED 
								|| e.getStatusCode() == HttpStatus.FORBIDDEN) {
				message = "Unauthorized request";
			} else {
				message = "Unknown server error";
			}
			throw new SharewoodException(message);		
		}
	}
	
	
	@Override
	public InputStream loadPhoto(String id) throws SharewoodException 
	{
		return new ByteArrayInputStream(sharewoodRestTemplate.getForObject(
				URI.create(String.format(sharewoodPhotoURLPattern, id)), byte[].class));	
	}
	

	@Override
	public void deletePhoto(long id) throws SharewoodException 
	{	
		String url = sharewoodPhotoBaseURL + "/deletePhoto/" + id; 
				
    	try {
    		sharewoodRestTemplate.delete(url);	 		
    	} catch (HttpStatusCodeException e) {
    		System.out.println("exception caught " + e);
    		String message;	
    		if (e.getMessage().contains("404")) {
    			message = "Photo not found";
    		} else if (e.getMessage().contains("401") 
    								|| e.getMessage().contains("403")) {
    			message = "Unauthorized request";
    		} else {
    			message = "Unknown server error";
    		}
    		throw new SharewoodException(message);	
    	}
	}


	private File uploadPhoto(MultipartFile uploadedFileRef) 
			throws NoUploadFileException, IOException 
	{
		/**
		 * Helper method for photo upload using MultipartFile 
		 */
		
		if (uploadedFileRef.isEmpty()) {
			System.out.println("throwing NoUploadFileException");
			throw new NoUploadFileException();
		}
		String fileName = uploadedFileRef.getOriginalFilename();
    	String path = "/home/dominique/Pictures/client/tmp/" + fileName; 
    	File outputFile = new File(path);
    	
    	InputStream is = null;     
    	OutputStream os = null;
         
    	byte[] buffer = new byte[1000];
    	int bytesRead = -1;
    	int totalBytes = 0;
    		
    	is = (ByteArrayInputStream) uploadedFileRef.getInputStream();
        os = new FileOutputStream(outputFile);
        	
        while ((bytesRead = is.read(buffer)) != -1) {           	
        	os.write(buffer);        
            totalBytes += bytesRead;
        }        	
              	 		
        is.close();		
        os.close();
        		
        return outputFile;
	}

	public String getSharewoodPhotoBaseURL() {
		return sharewoodPhotoBaseURL;
	}

	@Override
	public long createPhoto(MultipartFile uploadedFileRef, 
										String title, boolean shared)
			throws SharewoodException, IOException 
	{
		File tempFile = null;	    
		String pattern = sharewoodPhotoBaseURL + "/(\\d+)";	   
		Pattern r = Pattern.compile(pattern);
			
		// first store uploaded file to temporary location 
        tempFile = uploadPhoto(uploadedFileRef);
		       	
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("uploadedFile", new FileSystemResource(tempFile));
        map.add("title", title);
        map.add("shared", shared);
        
        List<MediaType> amt = new ArrayList<>();
        amt.add(MediaType.APPLICATION_JSON);
        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(amt);
			
    	HttpEntity<MultiValueMap<String, Object>> requestEntity = 
				new HttpEntity<MultiValueMap<String, Object>>(map, headers);
    	
    	String url = sharewoodPhotoBaseURL + "/addPhoto";
    		
    	ResponseEntity<String> response = sharewoodRestTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, String.class);
    				
    	// parse response
    	Matcher m = r.matcher(response.getHeaders().toString());			
    				
    	if (m.find()) {
    		return Long.parseLong(m.group(1));		
    	} else {
   			return 0;
    	}
    
	}

	@Override
	public Photo getPhoto(long id) throws SharewoodException {
	 
	  	sharewoodPhotoURL = sharewoodPhotoBaseURL + "/" + id;
	  	
		HttpHeaders headers = new HttpHeaders();
		
		List<MediaType> list = new ArrayList<>();
		list.add(MediaType.APPLICATION_JSON);
		headers.setAccept(list);
		
		HttpEntity<Photo> request = new HttpEntity<>(null, headers);
		
		try {
			ResponseEntity<Photo> response = sharewoodRestTemplate.exchange(
                sharewoodPhotoURL, HttpMethod.GET, request, Photo.class);
	
			if (response.getStatusCode() == HttpStatus.OK) {
				return response.getBody();
			} else {
				throw new SharewoodException("Error");
			}
		} catch (HttpStatusCodeException e) {
			System.out.println("exception caught " + e);
    		String message;	
    		if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
    			message = "Photo not found";
    		} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED 
    								|| e.getStatusCode() == HttpStatus.FORBIDDEN) {
    			message = "Unauthorized request";
    		} else {
    			message = "Unknown server error";
    		}
    		throw new SharewoodException(message);		
		}
	}


	@Override
	public void updatePhoto(Photo photo) throws SharewoodException, IOException 
	{
		String url = sharewoodPhotoBaseURL + "/updatePhoto";
	  	
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		List<MediaType> list = new ArrayList<>();
		list.add(MediaType.APPLICATION_JSON);
		headers.setAccept(list);
		
		HttpEntity<Photo> request = new HttpEntity<>(photo, headers);
			
		try {
			ResponseEntity<String> response = sharewoodRestTemplate.exchange(
                url, HttpMethod.PUT, request, String.class);
	  	
		} catch (HttpStatusCodeException e) {
			System.out.println("exception caught " + e);
			String message;	
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				message = "Photo not found";
			} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED 
								|| e.getStatusCode() == HttpStatus.FORBIDDEN) {
				message = "Unauthorized request";
			} else {
				message = "Unknown server error";
			}
			throw new SharewoodException(message);	
		}
	}


	@Override
	public List<Photo> getSharedPhotos() throws SharewoodException {	
		sharewoodPhotoListURL = sharewoodPhotoBaseURL + "/sharedPhotos";
		
		sharewoodRestTemplate.getAccessToken();
		
		HttpHeaders headers = new HttpHeaders();
		List<MediaType> amt = new ArrayList<>();
		amt.add(MediaType.APPLICATION_JSON);
		headers.setAccept(amt);
		
		HttpEntity<PhotoWebServiceList> request = new HttpEntity<>(null, headers);
		
		ResponseEntity<PhotoWebServiceList> response 
								= sharewoodRestTemplate.exchange(
                sharewoodPhotoListURL, HttpMethod.GET, request, PhotoWebServiceList.class);
		
		try {
			if (response.getStatusCode() == HttpStatus.OK) {			
				return response.getBody().getPhotos();
			} else {
				throw new SharewoodException("Error");
			}
		} catch (HttpStatusCodeException e) {
			System.out.println("exception caught " + e);
			String message;	
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				message = "Photo not found";
			} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED 
								|| e.getStatusCode() == HttpStatus.FORBIDDEN) {
				message = "Unauthorized request";
			} else {
				message = "Unknown server error";
			}
			throw new SharewoodException(message);	
		}
	}
}
