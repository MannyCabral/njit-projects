<?php

function cmp($a,$b) {
	$rval = strcasecmp($a['artist'],$b['artist']);
	
	if ($rval != 0) {
		return $rval;
	} else {
		$rval = strcasecmp($a['album'],$b['album']);
		if ($rval != 0){
			return $rval;
		} else {
			return strcasecmp($a['name'],$b['name']);
		}
		
	}
}

function a2t($songs) {
	$test = "<html>\n<body>\n\t<table border=\"1\">\n";

	$test .= "\t\t<tr>\n";
	$test .= "\t\t\t<td><b>ARTIST</b></td>\n";
	$test .= "\t\t\t<td><b>ALBUM</b></td>\n";
	$test .= "\t\t\t<td><b>SONG</b></td>\n";
	$test .= "\t\t</tr>\n";
	
	foreach($songs as $song){
		$test .= "\t\t<tr>\n";
		$test .= "\t\t\t<td>".$song['artist']."</td>\n";
		$test .= "\t\t\t<td>".$song['album']."</td>\n";
		$test .= "\t\t\t<td>".$song['name']."</td>\n";
		$test .= "\t\t</tr>\n";
	}

	$test .= "\t</table>\n</body>\n</html>";
	return $test;
}


$xml=simplexml_load_file("NewLibrary.xml");


$songs = array();
$p4 = array();
$p3 = array();
$p2 = array();
foreach ($xml->dict->dict->dict as $song) {
	$newSong =
		array(
			'name' => $song->name, 
			'artist' => $song->artist,
			'album' => $song->album
		);
	
	$songs[] = $newSong;
	switch ($song->rating){
		case 80:
			$p4[] = $newSong;
			break;
		case 60:
			$p3[] = $newSong;
			break;
		case 40:
			$p2[] = $newSong;
			break;
	}
}

usort($songs, 'cmp');
usort($p4, 'cmp');
usort($p3, 'cmp');
usort($p2, 'cmp');

file_put_contents("test.html", a2t($songs));
file_put_contents("p4.html", a2t($p4));
file_put_contents("p3.html", a2t($p3));
file_put_contents("p2.html", a2t($p2));

?>