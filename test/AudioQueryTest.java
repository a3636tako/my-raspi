
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import play.libs.Json;
import play.libs.Yaml;
import play.mvc.Result;
import play.test.Helpers;

import static play.test.Helpers.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.Album;
import models.Audio;


public class AudioQueryTest {

	private Album testAlbum1;
	private Album testAlbum2;
	private Album testAlbum3;
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		
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
	
	/*
{
"list" : [
	{
	"album_id" : 1111,
	"album_title" : "abc",
	"year" : 2015,
	"artwork_format" : ""
	"audios" : [
		{
		"audio_id" : 1111,
		"title" : "abc",
		"track_number" : 1
		}
	]
	},
]
}
	 */
	@Test
	public void loadYaml(){
		assertThat(testAlbum1.title, is("testAlbum1"));
		assertThat(testAlbum2.title, is("testAlbum2"));
		assertThat(testAlbum3.title, is("testAlbum3"));
		assertThat(testAlbum1.audios.size(), is(2));
		assertThat(testAlbum2.audios.size(), is(1));
		assertThat(testAlbum3.audios.size(), is(3));
		assertThat(testAlbum1.audios.get(0).title, is("testAudio1"));
		assertThat(testAlbum1.audios.get(1).title, is("testAudio2"));
		assertThat(testAlbum2.audios.get(0).title, is("testAudio3"));
		assertThat(testAlbum3.audios.get(0).title, is("testAudio4"));
		assertThat(testAlbum3.audios.get(1).title, is("testAudio5"));
		assertThat(testAlbum3.audios.get(2).title, is("testAudio6"));
	}

	@Test
	public void testQuery1() throws JsonParseException, JsonMappingException, IOException {
		List<Album> albums = query(String.format("?artist=%s&title=%s&album=%s", "testArtist1", "testAudio1", "testAlbum1"));
		
		assertThat(albums.size(), is(1));
		Album a = albums.get(0);
		assertThat(a.title, is("testAlbum1"));
		assertThat(a.artist, is("testArtist1"));
		assertThat(a.artwork_format, is("jpg"));
		assertThat(a.audios.get(0).title, is("testAudio1"));
		assertThat(a.audios.get(0).trackNumber, is(1));
		assertThat(a.audios.get(0).audio_id, is(1L));
	}
	
	@Test
	public void testQuery2() throws JsonParseException, JsonMappingException, IOException {
		List<Album> albums = query(String.format("?artist=%s", "testArtist1"));
		
		assertThat(albums.size(), is(2));
		Album a1 = albums.get(0);
		Album a2 = albums.get(1);
		
		assertThat(a1.title, is("testAlbum1"));
		assertThat(a1.artist, is("testArtist1"));
		
		assertThat(a2.title, is("testAlbum2"));
		assertThat(a2.artist, is("testArtist1"));

		assertThat(a1.audios.get(0).title, is("testAudio1"));
		assertThat(a1.audios.get(0).trackNumber, is(1));
		
		assertThat(a1.audios.get(1).title, is("testAudio2"));
		assertThat(a1.audios.get(1).trackNumber, is(2));
		
		assertThat(a2.audios.get(0).title, is("testAudio3"));
		assertThat(a2.audios.get(0).trackNumber, is(1));
	}
	
	public void testQuery3() throws JsonParseException, JsonMappingException, IOException {
		List<Album> albums = query(String.format("?album=%s", "no"));
		assertThat(albums.size(), is(0));
	}
	
	private List<Album> query(String query) throws JsonParseException, JsonMappingException, IOException{
		Result result = route(fakeRequest().method(GET).uri("/list" + query));
		
		JsonNode node = Json.parse(Helpers.contentAsString(result));
		ObjectMapper om = new ObjectMapper();
		List<Album> albums = om.readValue(node.get("list").traverse(), new TypeReference<List<Album>>(){});
		return albums;
	}

}
