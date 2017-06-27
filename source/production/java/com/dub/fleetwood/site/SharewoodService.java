package com.dub.fleetwood.site;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dub.fleetwood.site.Photo;
import com.dub.fleetwood.site.exceptions.SharewoodException;


/**
 * @author Dominique Ubersfeld
 * main interface to Sharewood REST provider
 */
public interface SharewoodService {
	
	List<Photo> getMyPhotos() throws SharewoodException;
	
	List<Photo> getSharedPhotos() throws SharewoodException;
	
	Photo getPhoto(long id) throws SharewoodException;
	
	void deletePhoto(long id) throws SharewoodException;
			
	long createPhoto(MultipartFile uploadedFileRef, String title, boolean shared) 
			throws SharewoodException, IOException;
	
	void updatePhoto(Photo photo) throws SharewoodException, IOException;
	
	InputStream loadPhoto(String id) throws SharewoodException;
	
}
