package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import models.Audio;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class AudioFileManager extends Controller {
	public Result get(String audioId) {
		int idx = audioId.lastIndexOf('.');
		if (idx == -1) {
			return badRequest();
		}

		String id = audioId.substring(0, idx);
		String ext = audioId.substring(idx + 1);

		try {
			Audio a = Audio.find.byId(Long.parseLong(id));
			if (a == null) {
				return notFound();
			}
			File f = a.createFile(ext);
			if (!f.exists()) {
				return notFound();
			}
			return ok(f);
		} catch (NumberFormatException e) {
			return badRequest();
		}
	}

	public Result getOfName(String artist, String album, String title) {

		int idx = title.lastIndexOf('.');
		if (idx == -1) {
			return badRequest();
		}

		String id = title.substring(0, idx);
		String ext = title.substring(idx + 1);

		List<Audio> a = Audio.find(artist, album, id);
		if (a.size() == 0) {
			return notFound();
		}
		if (a.size() > 1) {
			return badRequest();
		}

		File f = a.get(0).createFile(ext);
		if (!f.exists()) {
			throw new RuntimeException(f.getAbsolutePath());
			//return notFound();
		}
		return ok(f);
	}

	@Transactional
	public Result upload() {
		play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
		play.mvc.Http.MultipartFormData.FilePart music = body.getFile("music");
		if (music != null) {
			try {
				File nf = new File("./tmp", music.getFilename());
				music.getFile().renameTo(nf);
				storeFile(nf, music.getFilename());
				return ok("File uploaded");
			} catch (CannotReadException | IOException | TagException | ReadOnlyFileException
					| InvalidAudioFrameException e) {
				e.printStackTrace();
				return badRequest();
			}
		} else {
			flash("error", "Missing file");
			return badRequest();
		}
	}

	private void storeFile(File file, String fileName)
			throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		/*
		 * AudioFile af = AudioFileIO.read(file); Tag tag = af.getTag();
		 * 
		 * 
		 * Audio audio = new Audio(); audio.title =
		 * tag.getFirst(FieldKey.TITLE); audio.trackNumber =
		 * Integer.valueOf(tag.getFirst(FieldKey.TRACK));
		 * 
		 * String albumTitle = tag.getFirst(FieldKey.ALBUM); String artist =
		 * tag.getFirst(FieldKey.ALBUM_ARTIST);
		 * 
		 * 
		 * 
		 * TypedQuery<Album> query = JPA.em().createQuery(
		 * "SELECT a FROM Album a WHERE a.title='" + albumTitle +
		 * "' and a.artist='" + artist + "'", Album.class);
		 * 
		 * List<Album> albumList = query.getResultList(); if(albumList.size() ==
		 * 0){ audio.album = new Album(); audio.album.artist = artist;
		 * audio.album.title = albumTitle; }else{ audio.album =
		 * albumList.get(0); TypedQuery<Audio> query2 = JPA.em().createQuery(
		 * "SELECT a FROM Audio a WHERE a.album='" + audio.album.id +
		 * "' and a.trackNumber=" + audio.trackNumber , Audio.class);
		 * List<Audio> rlist = query2.getResultList(); if(rlist.size() != 0){
		 * Audio nAudio = rlist.get(0); nAudio.title = audio.title;
		 * JPA.em().merge(nAudio); return; } }
		 * 
		 * JPA.em().persist(audio);
		 */
	}
}
