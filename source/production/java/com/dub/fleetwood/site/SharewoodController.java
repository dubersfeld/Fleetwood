package com.dub.fleetwood.site;

import com.dub.fleetwood.config.annotations.WebController;
import com.dub.fleetwood.site.exceptions.NoUploadFileException;
import com.dub.fleetwood.site.exceptions.PhotoUploadException;
import com.dub.fleetwood.site.exceptions.SharewoodException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import javax.inject.Inject;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Controller used for REST requests directed to Sharewood
 * */
@RequestMapping("sharewood")
@WebController
public class SharewoodController
{
    @Inject 
    @Qualifier("sharewoodRestTemplate")
    private OAuth2RestOperations sharewoodRestTemplate;

    @Inject
    private SharewoodService sharewoodService;
    
 	@RequestMapping("photosMy")
	public String photosMy(Model model) throws Exception 
	{	
		try {
			List<Photo> list = sharewoodService.getMyPhotos();
				
			List<Long> photoIds = new ArrayList<>();
			for (Photo photo : list) {				
				photoIds.add(photo.getId());
			}
			model.addAttribute("photoIds", photoIds);
			model.addAttribute("photos", list);
		
			return "sharewoodList";
		} catch (UserDeniedAuthorizationException e) {
			System.out.println(e.getMessage());
			
			return "accessDenied";
		}
	}
	
 	
 	@RequestMapping("photosList/{id}")
	public ResponseEntity<byte[]> photo(@PathVariable String id) throws Exception 
	{
			
		try {
			InputStream photo = sharewoodService.loadPhoto(id);
		
			if (photo == null) {
				return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
			} else {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = photo.read(buffer);
				while (len >= 0) {
					out.write(buffer, 0, len);
					len = photo.read(buffer);
				}
				HttpHeaders headers = new HttpHeaders();
				photo.close();
				headers.set("Content-Type", "image/jpeg");
				return new ResponseEntity<byte[]>(out.toByteArray(), headers, HttpStatus.OK);
			}
		} catch (HttpClientErrorException e) {
			System.out.println(e);
			System.out.println("throwing 404");
			HttpHeaders headers = new HttpHeaders();
			return new ResponseEntity<byte[]>(null, headers, HttpStatus.NOT_FOUND);
		}
	}
 	
 	
 	@RequestMapping(
			value = "createPhotoMulti", 
			method = RequestMethod.GET)
	public ModelAndView createPhotoMulti(ModelMap model) 
	{	
		sharewoodRestTemplate.getAccessToken();
		
		model.addAttribute("photoMulti", new PhotoMultiForm());
		return new ModelAndView("createPhotoMultipart", model);
	}
		
	
	@RequestMapping(
    		value = "createPhotoMulti",
    		method = RequestMethod.POST)      	 
	public String uploadPhoto(
            @Valid @ModelAttribute("photoMulti") PhotoMultiForm form, 
            BindingResult result, ModelMap model) 
	{	 	
		if (result.hasErrors()) {
			System.out.println("errorz " + result.getFieldErrors().get(0));
			return "createPhotoMultipart";
		}
		// Get name of uploaded file.
		MultipartFile uploadedFileRef = null;
		boolean shared = form.isShared();
		String title = form.getTitle();
		  
		uploadedFileRef = form.getUploadedFile();
				
		try {
			long photoId = sharewoodService.createPhoto(uploadedFileRef, title, shared);
			
			model.addAttribute("photoId", photoId);
			
			return "createPhotoSuccess";
		} catch (NoUploadFileException e) {
			System.out.println("NoUploadFileException caught");
			model.addAttribute("cause", "No upload file");
			System.out.println("returning what?");
			return "createPhotoFailure";
		} catch (PhotoUploadException e) {
			System.out.println("PhotoUploadException caught");
			model.addAttribute("cause", "Photo upload error");
			return "createPhotoFailure";
		} catch (IOException e) {
			System.out.println("IOException caught");
			model.addAttribute("cause", "Photo upload error");
			return "createPhotoFailure";
		}
	
	}// uploadPhoto
 	
	@RequestMapping(
			value = "deletePhoto", 
			method = RequestMethod.GET)
	public ModelAndView deletePhoto(ModelMap model) {
		sharewoodRestTemplate.getAccessToken();
		model.addAttribute("getPhoto", new PhotoIdForm());
		return new ModelAndView("deletePhoto", model);
	}
	
	@RequestMapping(
			value = "deletePhoto", 
			method = RequestMethod.POST)
	public String deletePhoto(
					@Valid @ModelAttribute("getPhoto") PhotoIdForm form,
					BindingResult result, 
					ModelMap model) 
	{	
		if (result.hasErrors()) {
			System.out.println("errorz " + result.getFieldErrors().get(0));
			return "deletePhoto";
		}
		
		try {
			sharewoodService.deletePhoto(form.getId());
			return "deletePhotoSuccess";
		} catch (SharewoodException e) {
			String message = e.getMessage();
			if (message.contains("Unauthorized")) {
				model.addAttribute("cause", "unauthorized.request");
			} else {
				model.addAttribute("cause", "unknown.error");
			}
			return "deletePhotoFailure";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("cause", "unknown error");
			return "deletePhotoFailure";
		}
		
	}
	
	
	@RequestMapping(
			value = "updatePhoto", 
			method = RequestMethod.GET)
	public ModelAndView updatePhoto(ModelMap model) 
	{
		sharewoodRestTemplate.getAccessToken();
		model.addAttribute("getPhoto", new PhotoIdForm());
		return new ModelAndView("updatePhoto1", model);
	}
	
	@RequestMapping(
			value = "updatePhoto1", 
			method = RequestMethod.POST)
	public String updatePhoto1(
			@Valid @ModelAttribute("getPhoto") PhotoIdForm form,
			BindingResult result, ModelMap model) 
	{
		if (result.hasErrors()) {
			return "updatePhoto1";
		}
	
		try { 
				Photo photo = sharewoodService.getPhoto(form.getId());				
				model.addAttribute("photo", photo);
			 
				return "updatePhoto2";
		} catch (SharewoodException e) {
			if (e.getMessage().equals("Unauthorized request")) {
				return "accessDenied";
			} else if (e.getMessage().equals("Photo not found")) {
				return "photoNotFound";
			} else {
				model.addAttribute("cause", "error.unknown");
				return "updatePhotoFailure";
			}
		} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("cause", "error.unknown");
				return "updatePhotoFailure";
		}// try			
	}
	
	@RequestMapping(
			value = "updatePhoto2", 
			method = RequestMethod.POST)
	public String updatePhoto2(@Valid @ModelAttribute("photo") PhotoUpdateForm form,
			BindingResult result, ModelMap model) 
	{
		if (result.hasErrors()) {
			return "photos/updatePhoto2";
		}
		try {
			Photo photo = new Photo();
			photo.setId(form.getId());
			photo.setUsername(form.getUsername());
			photo.setTitle(form.getTitle());
			photo.setShared(form.isShared());
			
			sharewoodService.updatePhoto(photo);// actual update			
			return "updatePhotoSuccess";
		} catch (SharewoodException e) {
			if (e.getMessage().equals("Unauthorized request")) {
				model.addAttribute("cause", "unauthorized.request");
			} else {
				model.addAttribute("cause", "error.unknown");
			}
			return "updatePhotoFailure";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("cause", "error.unknown");
			return "updatePhotoFailure";
		}
	}
	
	
	@RequestMapping("sharedPhotos")
	public String sharedPhotos(Model model) throws Exception 
	{	
		try {		
			List<Photo> list = sharewoodService.getSharedPhotos();
			
			model.addAttribute("photos", list);
			
			return "sharewoodShared";
		} catch (UserDeniedAuthorizationException e) {
			System.out.println(e.getMessage());
			
			return "accessDenied";
		}
	}
}
