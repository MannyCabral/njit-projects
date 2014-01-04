<?php
	function json2xml($jsonObj,$flag = NULL, $header="req")
	{
		// echo $flag."\r\n";
		$xmlObj = '<?xml version="1.0" encoding="ISO-8859-1"?>'."\r\n<".$header.">";
		$xmlObj .= json2xmlR($jsonObj,1,$header);
		$xmlObj .= "</".$header.">";
		
		return $xmlObj;
	}

	function json2xmlR($Obj, $tabs,$prev)
	{
		$str = "";
		// $containsArray = False;
		
		if (is_array($Obj)) 
		{
			foreach (array_keys($Obj) as $key)
			{
				$head = replaceXMLName($key,$prev);
				// echo $head;
				$str .= "\r\n".str_repeat("\t",$tabs)."<".$head.">";
				
				$str .= json2xmlR($Obj[$key], $tabs + 1, $head);
				if (is_array($Obj[$key]))
				{
					$str .= str_repeat("\t",$tabs);
				}
				
				$str .= "</".$head.">";
			}
			
			$str .= "\r\n";
			// if ($containsArray)
			// {
				// $str .= str_repeat("\t",$tabs-1);
			// }
		} else 
		{
			$str = $Obj;
		}
		
		return $str;
	}
	
	function replaceXMLName($str,$flag)
	{
		if (is_numeric($str) && strcmp($flag,"courses") == 0) {
			return "course";
		} else if (is_numeric($str) && strcmp($flag,"quizzes") == 0) {
			return "quiz";
		} else if (is_numeric($str) && strcmp($flag,"courseposts") == 0) {
			return "coursepost";
		} else if (is_numeric($str) && strcmp($flag,"questions") == 0) {
			return "question";
		} else if (is_numeric($str) 
			&& ( strcmp($flag,"OE") == 0 || strcmp($flag,"MC") == 0 || strcmp($flag,"TF") == 0))
		{
			return "qinfo";
		} else if (is_numeric($str) && strcmp($flag,"grades") == 0) {
			return "student";
		} else if (is_numeric($str) && strcmp($flag,"quizzesinfo") == 0) {
			return "quizinfo";
		} else if (is_numeric($str) && strcmp($flag,"cases") == 0) {
			return "case";
		}
		return $str;
	}
?>