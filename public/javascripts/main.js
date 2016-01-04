
$(function(){
	query("","","");
	$("#query_button").click(function(){
		var artist = $("#form_artist").val();
		var album = $("#from_album").val();
		var title = $("#form_title").val();	
		query(artist, album, title);
	});
});

function query(artist, album, title){
	var param = new Object();
	if(artist != "" && artist != null) param["artist"] = artist;
	if(album != "" && album != null) param["album"] = album;
	if(title != "" && title != null) param["title"] = title;
	console.log(param);
	$.ajax({
		type:"GET",
		url:"list",
		data : param, 
		success : function(data, textStatus, jqXHR){
			console.log(data);
			setQueryResult(data );		

		},
		error : function(jqXHR, textStatus, errorThrown){
			
		},
		complete : function(jqXHR, textStatus){
		}
	});
}

function setQueryResult(data){
	$('#query_table').html("");
	$('#query_table').append("<tr><th>アーティスト</th><th>アルバム</th><th>発表年</th><th></th><th>曲名</th></tr>");

	var albumList = data['list'];
	for(var i = 0; i < albumList.length; i++){
	
		var album_id = albumList[i]["album_id"];
		var album_title = albumList[i]["title"];
		var artist = albumList[i]["artist"];
		var year = albumList[i]["year"];
		var audios = albumList[i]["audios"];
		for(var j = 0; j < audios.length; j++){
			var audio_title = audios[j]["title"];
			var audio_id = audios[j]["audio_id"];
			var trackNumber = audios[j]["trackNumber"];
			console.log(audio_title);
			insertQueryRow(album_id, album_title, artist, year, audio_title, audio_id, trackNumber);
		}
	}
}

function insertQueryRow(album_id, album_title, artist, year, audio_title, audio_id, trackNumber){
	var row = "<tr><td>"
			+ album_title 
			+ "</td><td>"
			+ artist 
			+ "</td><td>"
			+ year 
			+ "</td><td>"
			+ trackNumber 
			+ "</td><td><a href=\"audio/" + audio_id + ".mp3\">"
			+ audio_title 
			+ "</a></td></tr>"
			;
	console.log(row);
	$('#query_table').append(row);
	
}
