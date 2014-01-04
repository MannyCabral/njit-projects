<?php
	/*
		FOR OPEN ENDED GRADING
	*/
	$OBOOLTRUE = '"1"';
	
	/*
		Given [abis] and a string variable, returns the correct Java conversion to that type
	*/
	function expected($format, $str)
	{
		global $OBOOLTRUE;
		
		switch ($format)
		{
			case 'a':
				return "helper.stia(".$str.")";
			case 'b':
				return $OBOOLTRUE.".equals(".$str.")";
			case 'i':
				return "Integer.parseInt(".$str.")";
			case 's':
				return $str;
		}
	}
	
	/*
		return java function args
	*/
	function argsString($format)
	{
		$len = count($format);
		$args = "";
		for ($i = 0; $i < $len; $i++) {
			if ($i != 0)
			{
				$args .= ', ';
			}
			
			$args .= expected($format[$i],"args[".$i."]");
		}
		
		return $args;
	}
	
	/*
		returns the comparison boolean for a,b,i,s
		ie.
		a -> helper.ae( helper.stia(args[1]), result)
		b -> System.out.printf("%b", ( result ));
		i -> System.out.printf("%b ", ( Integer.parseInt(args[2]) == result ));
		s -> System.out.printf("%b ", ( args[1].equals(result) )); 
	*/
	function issame($format)
	{
		$i = count($format[0]);
		
		switch ($format[1])
		{
			case 'a';
				return "helper.ae( ".expected($format[1],"args[".$i."]").", result)";
			case 'b';
				return expected($format[1],"args[".$i."]")." == result";
			case 'i';
				return expected($format[1],"args[".$i."]")." == result";
			case 's';
				return expected($format[1],"args[".$i."]").".equals(result)";
		}
		return;
	}
	
	/*
		returns output type
		a,b,i,s -> int[],boolean,int,String
	*/
	function output($format)
	{
		switch ($format)
		{
			case "a":
				return "int[]";
			case "b":
				return "boolean";
			case "i":
				return "int";
			case "s":
				return "String";
		}
		return;
	}
	
	/*
		return args for printing result
		a -> "%s", helper.iats(result)
		b -> "%b", result
		i -> "%i", result
		s -> "%s", result
	*/
	function result($format)
	{
		switch ($format)
		{
			case "a":
				return "\"%s\", helper.iats(result)";
			case "b":
				return "\"%b\", result";
			case "i":
				return "\"%d\", result";
			case "s":
				return "\"%s\", result";
		}
		return;
	}
	
	/*
		returns what will go into the .java file
	*/
	function javaFileContents($format, $fn, $stud, $filename)
	{
		$str = "public class ".$filename." {\r\n";
		$str .= "\r\n\t// START STUDENT CODE\r\n";
		$str .= $stud;
		$str .= "\r\n\t// END OF STUDENT CODE\r\n";
		$str .= "\r\n\tpublic static void main(String[] args){\r\n";
		
		//tester declaration
		$str .= "\t\t".$filename." tester = new ".$filename."();\r\n";
		
		//result
		$str .= "\t\t".output($format[1])." result = tester.".$fn."(".argsString($format[0]).");\r\n";
		
		//print comparison
		$str .= "\t\tSystem.out.printf(\"%b \", ( ";
		$str .= issame($format,$fn);
		$str .= " ));\r\n";
		
		//print result
		$str .= "\t\tSystem.out.printf(".result($format[1]).");\r\n";
		
		
		$str .= "\t}\r\n";
		$str .= "}\r\n";
		
		$str .= "class helper{\r\n";
		$str .= "\tfinal static String REGEX = \",\";\r\n";
		$str .= "\tpublic static int[] stia(String str){\r\n";
		$str .= "\t\tString[] s = str.split(REGEX);\r\n";
		$str .= "\t\tint[] i = new int[s.length];\r\n";
		$str .= "\t\tfor (int j = 0; j < s.length;j++){\r\n";
		$str .= "\t\t\ti[j] = Integer.parseInt(s[j]);\r\n";
		$str .= "\t\t}\r\n";
		$str .= "\t\treturn i;\r\n";
		$str .= "\t}\r\n\r\n";
			
		$str .= "\tpublic static boolean ae(int[] a1,int[] a2){\r\n";
		$str .= "\t\tif (a1.length != a2.length){\r\n";
		$str .= "\t\t\treturn false;\r\n";
		$str .= "\t\t}\r\n";
		$str .= "\t\tfor (int i = 0; i < a1.length; i++){\r\n";
		$str .= "\t\t\tif (a1[i] != a2[i]){\r\n";
		$str .= "\t\t\t\treturn false;\r\n";
		$str .= "\t\t\t}\r\n";
		$str .= "\t\t}\r\n";
		$str .= "\t\treturn true;\r\n";
		$str .= "\t}\r\n\r\n";
		
		$str .= "\tpublic static String iats(int[] a){\r\n";
		$str .= "\t\tStringBuilder str = new StringBuilder();\r\n";
		$str .= "\t\tfor (int i = 0; i < a.length; i++){\r\n";
		$str .= "\t\t\tif (i != 0){\r\n";
		$str .= "\t\t\t\tstr.append(\",\");\r\n";
		$str .= "\t\t\t}\r\n";
		$str .= "\t\t\tstr.append(Integer.toString(a[i]));\r\n";
		$str .= "\t\t}\r\n";
		$str .= "\t\treturn str.toString();\r\n";
		$str .= "\t}\r\n";
		$str .= "}";
		
		return $str;
	}
	
	/*
		creates file then runs javaexec
	*/
	function createCompileRunJava($filename,$ucid,$filecontents, $format, $cases, $answers)
	{
		// CREATE .java file
		if (! file_put_contents($filename.$ucid.".java",$filecontents))
		{
			die("Couldn't create file.");
		}
		
		// CREATE .class file
		$descriptorspec = array(
		   0 => array("pipe", "r"),  // stdin is a pipe that the child will read from
		   1 => array("pipe", "w"),  // stdout is a pipe that the child will write to
		   2 => array("pipe", "w")
		);
		$cwd = getcwd();
		//$cwd = '.';
		$env = array('some_option' => 'aeiou');
		$cmd = 'javac '.$filename.'.java'; // ie. HelloWorld.java';
		
		$result = javaexec($cmd,$descriptorspec,$cwd,$env,$filename.$ucid, $format, $cases, $answers);
		
		//DELETE .java
		unlink($filename.$ucid.'.java');
		
		return $result;
	}
	
	/*
		runs each test case and returns results
	*/
	function javaexec($cmd,$descriptorspec,$cwd,$env,$filename, $format, $cases, $answers)
	{
		$results = array();
		$process = proc_open('javac '.$filename.'.java', $descriptorspec, $pipes, $cwd, $env);
		if (is_resource($process)) 
		{
			fclose($pipes[0]);

			$results['c1'] = stream_get_contents($pipes[1]);
			fclose($pipes[1]);

			$results['c2'] = stream_get_contents($pipes[2]);
			fclose($pipes[2]);

			$return_value = proc_close($process);

			$results['crv'] = $return_value;
		}
	
		if ($return_value == 0)
		{
			$results['results'] = array();
			$len = count($cases);
			for ($i = 0; $i < $len; $i++)
			{
				$result = array();
				$process = proc_open("java ".$filename." ".getParams($format,$cases[$i],$answers[$i]), $descriptorspec, $pipes, $cwd, $env);
				
				if (is_resource($process)) 
				{
					fclose($pipes[0]);

					$result['e1'] = explode(" ",stream_get_contents($pipes[1]),2);
					fclose($pipes[1]);

					$result['e2'] = stream_get_contents($pipes[2]);
					fclose($pipes[2]);

					$return_value = proc_close($process);

					$result['erv'] = $return_value;
					
					array_push($results['results'],$result);
				}
			}
			
			//DELETE .class
			unlink($filename.'.class');
		}
		return $results;
	}
	
	/*
		convert test case to command line arguments
	*/
	function getParams($format, $case, $answer)
	{
		$case = explode('_', $case);
		
		$str = "";
		$len = count($format[0]);
		for ($i = 0; $i < $len; $i++)
		{
			if ($i != 0)
			{
			$str .= " ";
			}
			
			if ($format[0][$i] === "s" ){
				$str .= "\"".$case[$i]."\"";
			}
			else
			{
				$str .= $case[$i];
			}
		}
		
		if ($format[1] === "s" ){
			$str .= " \"".$answer."\"";
		}
		else
		{
			$str .= ' '.$answer;
		}
		
		return $str;
	}
	
	/*
		takes array of strings and converts them to:
		str1;str2;str3;str4;...
	*/
	function convert($strings)
	{
		$conversion = "";
		$len = count($strings);
		for ($i = 0; $i < $len; $i++)
		{
			if ($i != 0)
			{
				$conversion .= ";";
			}
			$conversion .= $strings[$i];
		}
		return $conversion;
	}
	
	/*
		grades open ended question; returns % and json encoded results
	*/
	function gradeOE($ucid,$filename,$format,$cases,$answer,$stud)
	{
		$ioformat = explode(';',$format);
		$ioformat[0] = explode('_',$ioformat[0]);
		$cases = explode(';',$cases);
		$answers = explode(';',$answer);
		
		$results =
			createCompileRunJava(
				$filename,
				$ucid,
				javaFileContents(
					$ioformat,
					$filename,
					$stud,
					$filename.$ucid
				),
				$ioformat,
				$cases,
				$answers
			);
			
		$score = 0;
		$total = 0;
		if ( isset($results['results'])){
			foreach ($results['results'] as $result)
			{
				$total++;
				if ($result['erv'] == 0 && $result['e1'][0] === 'true')
				{
					$score++;
				}
			}
		}
		
		
		// return array($score/$total,convert($conversion));
		if ($total != 0)
		{
			return array($score/$total,json_encode($results));
		}
		else {
			return array(0,json_encode($results));
		}
	}
?>