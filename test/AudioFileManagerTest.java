import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.junit.*;

import models.Album;
import models.Audio;
import play.libs.Yaml;
import play.mvc.Result;
import play.test.Helpers;

import static play.test.Helpers.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class AudioFileManagerTest {


	private Album testAlbum1;
	private Album testAlbum2;
	private Album testAlbum3;
	
	
	@Before
	public void setUp() {
		HashMap<String, String> config = new HashMap<>();
		config.putAll(inMemoryDatabase());
		config.put("audio.directory", "testdata/Music");
		start(fakeApplication(config));


		List list = (List) Yaml.load("testData/AudioData.yml");
		
		//Ebean.save(list);

		testAlbum1 = (Album) list.get(6);
		testAlbum2 = (Album) list.get(7);
		testAlbum3 = (Album) list.get(8);

		for(Audio a : testAlbum1.audios){
			a.album = testAlbum1;
		}
		for(Audio a : testAlbum2.audios){
			a.album = testAlbum2;
		}
		for(Audio a : testAlbum3.audios){
			a.album = testAlbum3;
		}
		
		testAlbum1.save();
		testAlbum2.save();
		testAlbum3.save();
	}

	/**
	 * mp3ファイルをidで取得する
	 */
	@Test
	public void getByAudioID()
			throws IOException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException {

		Result result = route(fakeRequest(GET, "/audio/3.mp3"));
		
		assertThat(result.status(), is(OK));
		assertThat(result.contentType(), is("audio/mpeg"));

		testCorrectMP3File(Helpers.contentAsBytes(result), "testArtist1", "testAlbum2", "testAudio3");
	}
	

	/**
	 * mp3ファイルを曲名で取得する
	 */
	@Test
	public void getByAudioTitle()
			throws IOException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		String title = "testAudio5";
		String album = "testAlbum3";
		String artist = "testArtist2";

		Result result = route(fakeRequest(GET, String.format("/audio/%s/%s/%s.mp3", artist, album, title)));

		assertThat(result.status(), is(OK));
		assertThat(result.contentType(), is("audio/mpeg"));

		testCorrectMP3File(Helpers.contentAsBytes(result), artist, album, title);
	}

	/**
	 * 存在しないIDでmp3ファイルを取得する
	 */
	@Test
	public void getNotExistsFileByID() {
		Result result = route(fakeRequest(GET, "/audio/100.mp3"));

		assertThat(result.status(), is(NOT_FOUND));
	}

	/**
	 * 存在しないタイトルでmp3ファイルを取得する
	 */
	@Test
	public void getNotExistsFileByTitle() {
		String title = "a";
		String album = "b";
		String artist = "c";

		Result result = route(fakeRequest(GET, String.format("/audio/%s/%s/%s.mp3", artist, album, title)));

		assertThat(result.status(), is(NOT_FOUND));
	}
	


	private void testCorrectMP3File(byte[] file, String artist, String album, String title)
			throws IOException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		Path path = Files.createTempFile("test", ".mp3");
		BufferedOutputStream buf = new BufferedOutputStream(Files.newOutputStream(path));
		buf.write(file);
		buf.close();

		Tag tag = AudioFileIO.read(path.toFile()).getTag();

		assertThat(tag.getFirst(FieldKey.ALBUM_ARTIST), is(artist));
		assertThat(tag.getFirst(FieldKey.TITLE), is(title));
		assertThat(tag.getFirst(FieldKey.ALBUM), is(album));
	}
}
