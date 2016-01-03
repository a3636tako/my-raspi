package models;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.*;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import play.Play;

@Entity
public class Audio extends Model{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long audio_id;
	
	public String title;
	
	@ManyToOne
	@JoinColumn(name="album_id")
	@JsonBackReference
	public Album album;
	
	public Integer trackNumber;
	
	public static Finder<Long, Audio> find = new Finder<>(Audio.class);
	
	public Audio(){}
	
	public Audio(Long id, String title, Album album, Integer trackNumber){
		this.audio_id = id;
		this.title = title;
		this.album = album;
		this.trackNumber = trackNumber;
	}

	public File createFile(String format){
		String path = Play.application().configuration().getString("audio.directory");
		return Paths.get(path, album.artist, album.title, String.format("%02d-%s.%s", trackNumber, title , format)).toFile();
	}
	
	public static List<Audio> find(String artist, String album, String title){
		if(artist == null && album == null && title == null){
			return find.all();
		}else{
			ExpressionList<Audio> exp = find.fetch("album").where();
			if(artist != null){
				exp = exp.eq("t1.artist", artist);
			}
			if(album != null){
				exp = exp.eq("t1.title", album);
			}
			if(title != null){
				exp = exp.eq("t0.title", title);
			}
			
			return exp.findList();
		}
	}
}
