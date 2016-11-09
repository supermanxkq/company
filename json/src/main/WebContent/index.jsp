<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="http://tu.hangtian123.net/12306SearchMonitor/js/jquery.min.js"></script>
<title>Alibaba FastJson</title>
<script type="text/javascript">
var rootPath=getRootPath();
	//html结构载入执行
	$(function(){
		$.ajax({
			url:rootPath+"/FastJsonServlet",
			method:'post',
			data:'',
			success:function(jsonData){
				var html="";
				for (var i = 0; i < jsonData.length; i++) {
					html+=jsonData[i].name+"<br/>";
				}
				$(".a").html(html);
			}
		});
	})
	//获取rootPath
	function getRootPath(){
		  //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
        var curWwwPath = window.document.location.href;
        //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        //获取主机地址，如： http://localhost:8083
        var localhostPaht = curWwwPath.substring(0, pos);
        //获取带"/"的项目名，如：/uimcardprj
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        return (localhostPaht + projectName);
	}
</script>
</head>
<body>
	<div class="a"></div>
</body>
</html>