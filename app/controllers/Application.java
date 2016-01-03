package controllers;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import play.*;
import play.db.jpa.JPA;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

	public Result index() {
		return ok(index.render("music sync", "Your new application is ready."));
	}

	public Result uploadPage() {
		return ok();
	}

	@play.db.jpa.Transactional
	public Result upload() {
		return ok();
	}
	


	public Result uploadAjax() {
		return ok();
	}

}
