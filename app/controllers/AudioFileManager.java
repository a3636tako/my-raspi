package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import models.Album;
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
			a.album = Album.find.byId(a.album.album_id);
			
			File f = a.createFile(ext);

			System.out.println(f.toString());
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
			// throw new RuntimeException(f.getAbsolutePath());
			return notFound();
		}
		return ok(f);
	}

	
	public Result upload() {
		File file = request().body().asRaw().asFile();
		File nf = new File(file.getAbsolutePath() + ".mp3");
		file.renameTo(nf);
		try {
			storeFile(nf);
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException
				| InvalidAudioFrameException e) {
			e.printStackTrace();
			return badRequest();
		}
		return ok();
	}

	
	public Result uploadForm() {
		play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
		play.mvc.Http.MultipartFormData.FilePart music = body.getFile("music");
		if (music != null) {
			try {
				File nf = new File("./tmp", music.getFilename());
				music.getFile().renameTo(nf);
				storeFile(nf);
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

	private void storeFile(File file)
			throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {

		AudioFile af = AudioFileIO.read(file);
		Tag tag = af.getTag();

		String title = tag.getFirst(FieldKey.TITLE);
		Integer trackNumber = null;
		try{
			trackNumber = Integer.valueOf(tag.getFirst(FieldKey.TRACK));
		}catch(NumberFormatException e){
			trackNumber = 1;
		}
		String albumTitle = tag.getFirst(FieldKey.ALBUM);
		String artist = tag.getFirst(FieldKey.ALBUM_ARTIST);
		Integer year = null;
		try{
			year = Integer.valueOf(tag.getFirst(FieldKey.YEAR));
		}catch(NumberFormatException e){
			year = 0;
		}

		List<Audio> audioList = Audio.find(artist, albumTitle, title);

		Audio audio;
		if (audioList.size() > 0) {
			audio = audioList.get(0);
		} else {
			audio = new Audio();
		}

		audio.title = title;
		audio.trackNumber = trackNumber;

		List<Album> albumList = Album.find(artist, albumTitle, null);
		if (albumList.size() > 0) {
			audio.album = albumList.get(0);
		} else {
			audio.album = new Album();
			audio.album.artist = artist;
			audio.album.artwork_format = "mp3";
			audio.album.audios = new ArrayList<>();
			audio.album.audios.add(audio);
			audio.album.title = albumTitle;
			audio.album.year = year;
		}
		audio.album.save();
		audio.save();
		File saveFile = audio.createFile("mp3");
		saveFile.getParentFile().mkdirs();
		file.renameTo(saveFile);
		System.out.println("Audio ID :" + audio.audio_id);
	}
}
