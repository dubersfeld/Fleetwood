package com.dub.fleetwood.site;


import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/** Helper class */
@XmlRootElement(name = "photos")
public class PhotoWebServiceList
{
    private List<Photo> photos;
    
    @XmlElement(name = "photo")
	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

   
}
