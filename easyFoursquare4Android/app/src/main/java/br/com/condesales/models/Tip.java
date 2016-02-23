package br.com.condesales.models;

public class Tip {

	private String id;
	private Long createdAt;
	private String text;
	private String canonicalUrl;
	private PhotoItem photo;
	private String photourl;
	private Boolean like;
	private Venue venue;
	private User user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCanonicalUrl() {
		return canonicalUrl;
	}

	public void setCanonicalUrl(String canonicalUrl) {
		this.canonicalUrl = canonicalUrl;
	}

	public PhotoItem getPhoto() {
		return photo;
	}

	public void setPhoto(PhotoItem photo) {
		this.photo = photo;
	}

	public String getPhotourl() {
		return photourl;
	}

	public void setPhotourl(String photourl) {
		this.photourl = photourl;
	}

	public Boolean getLike() {
		return like;
	}

	public void setLike(Boolean like) {
		this.like = like;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
