package models;

import java.util.List;

import javax.persistence.*;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"title", "artist"}))
public class Album extends Model{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long album_id;
	
	public String title;
	
	public String artist;
	
	public Integer year;
	
	public String artwork_format;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="album", fetch=FetchType.EAGER)
	@JsonManagedReference
	public List<Audio> audios;
	

	public static Finder<Long, Album> find = new Finder<>(Album.class);
	
	public Album(){}

	public Album(Long id, String title, String artist, Integer year, String artwork_format, List<Audio> audios) {
		this.album_id = id;
		this.title = title;
		this.artist = artist;
		this.year = year;
		this.artwork_format = artwork_format;
		this.audios = audios;
	}
	
	public static List<Album> find(String artist, String album, String title){
		if(artist == null && album == null && title == null){
			return find.all();
		}else{
			ExpressionList<Album> exp = find.fetch("audios").where();
			if(artist != null){
				exp = exp.eq("artist", artist);
			}
			if(album != null){
				exp = exp.eq("t0.title", album);
			}
			if(title != null){
				exp = exp.eq("t1.title", title);
			}
			
			return exp.findList();
		}
	}
}
