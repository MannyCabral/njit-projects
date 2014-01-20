Converts Itunes XML output into an HTML table.

The python file converts lines like this:
<key>Track ID</key><integer>830</integer>
into:
<trackid>830</trackid>
which are more easily parsed by SimpleXML. Then from there it's pretty straightforward to convert to an HTML file.

Currently creates 4 html files with a table of the artist, album and title:
1) entire library
2) rating of 2
3) rating of 3
4) rating of 4