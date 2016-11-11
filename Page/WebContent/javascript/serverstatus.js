$(function() {
	//声明
	var ServerStatus = {};
	//获取rootPath
	var rootPath=getRootPath();
	//初始化加载所有dom元素的点击事件
	ServerStatus.init=function(){
		//重构内存
		$('#alibabaRefresh').on('click', function(){
			ServerStatus.alibabaAndMeiTuan();
		});
		//过期扫描服务
		$('#invalidRefresh').on('click', function(){
			ServerStatus.invalidService();
		});
		//抢票内存
		$('#memoryRefresh').on('click', function(){
			ServerStatus.memoryRefresh();
		});
		//淘宝抢票查询列队
		$('#taoBaoRefresh').on('click', function(){
			ServerStatus.taoBaoRefresh();
		});
		//rep
		$('#repRefresh').on('click', function(){
			ServerStatus.repRefresh();
		});
		//下单消费者
		$('#placeOrderCustomerRefresh').on('click', function(){
			ServerStatus.placeOrderCustomerRefresh();
		});
		//抢票内存
		$('#ticketMemoryRefresh').on('click', function(){
			ServerStatus.ticketMemoryRefresh();
		});
		//服务器
		$("#serverRefresh").on('click',function(){
			ServerStatus.serverRefresh();
		});
	}
	
	
	
	
	//阿里内存重构
	ServerStatus.alibabaAndMeiTuan=function(){
		$.ajax({
			url : rootPath+ "/KPITrainOrderBeBespeakServlet?type=302",
			data : '',
			type : 'post',
			success:function(jsonData){
				var  html="<table width='100%' height='100%' class='am-table'><thead><tr><th>名称</th><th>IP</th><th>端口</th><th>状态</th></tr></thead> <tbody>";
						for(var i = 0 ; i < jsonData.length; i ++){
							html+="<tr><td>"+jsonData[i].name+"</td><td>"+jsonData[i].ip+"</td><td>"+jsonData[i].port+"</td>" ;
							if(jsonData[i].result!='异常'){
								html+="<td><font color='green' style='font-weight:bold;'>"+jsonData[i].result;
							}else{
								html+="<td><font color='red' style='font-weight:bold;'>"+jsonData[i].result;
							}
						}
						html+="</font></td></tr></tbody></table>";
				$("#alibabaAndMeiTuan").html(html);
			}
		});
	}
	//过期扫描服务
	ServerStatus.invalidService=function(){
		$.ajax({
			url : rootPath+ "/KPITrainOrderBeBespeakServlet?type=303",
			data : '',
			type : 'post',
			success:function(jsonData){
				var  html="<table width='100%' height='100%' class='am-table'><thead><tr><th>IP</th><th>端口</th><th>状态</th></tr></thead> <tbody>";
				for (var i = 0; i < jsonData.length; i++) {
					html+="<tr><td>"+jsonData[i].ip+"</td><td>"+jsonData[i].port+"</td>";
					if(jsonData[i].result!='异常'){
						html+="<td><font color='green' style='font-weight:bold;'>"+jsonData[i].result+"</font></td>";
					}else{
						html+="<td><font color='red' style='font-weight:bold;'>"+jsonData[i].result+"</font></td>";
					}
					html+="</tr>";
				}
				html+= "</tbody></table>";
				$("#invalid").html(html);
			}
		});
	}
	
	//抢票内存
	ServerStatus.memoryRefresh=function(){
		$.ajax({
			url : rootPath+ "/KPITrainOrderBeBespeakServlet?type=306",
			data : '',
			type : 'post',
			success:function(jsonData){
				var  html="<table width='100%' height='100%' class='am-table'><thead><tr><th>名称</th><th>IP</th><th>端口</th><th colspan='4' style='text-align:center;' >参数</th></tr></thead> <tbody>";
				for (var i = 0; i < jsonData.length; i++) {
					html+="<tr><td>"+jsonData[i].name+"</td>"
					html+="<td>"+jsonData[i].ip+"</td><td>"+jsonData[i].port+"</td>";
					if(jsonData[i].result!='异常'){
						var info=jsonData[i].result.split(',');
						html+="<td ><font color='green' style='font-weight:bold;'>"+ info[0] + "</font></td>";
						html+="<td ><font color='green' style='font-weight:bold;'>"+ info[1] + "</font></td>";
						html+="<td ><font color='green' style='font-weight:bold;'>"+ info[2] + "</font></td>";
						html+="<td ><font color='green' style='font-weight:bold;'>"+ info[3] + "</font></td>";
					}else{
						html+="<td colspan='4'><font color='red' style='font-weight:bold;'>"+ jsonData[i].result + "</font></td>";
					}
					html+="</tr>";
				}
				html+="</tbody></table>";
				$("#memoryResult").html(html);
			}
		});
	}
	
	
	
	//抢票消费者
	ServerStatus.ticketMemoryRefresh=function(){
		$.ajax({
			url : rootPath+ "/KPITrainOrderBeBespeakServlet?type=304",
			data : '',
			type : 'post',
			success:function(jsonData){
				var  html="<table width='100%' height='100%' class='am-table'><thead><tr><th>IP</th><th>端口</th><th>状态</th></tr></thead> <tbody>";
				for (var i = 0; i < jsonData.length; i++) {
					html+="<tr><td>"+jsonData[i].ip+"</td>" + "<td>"+jsonData[i].port+"</td>";
					if(jsonData[i].result!='异常'){
						html+="<td ><font color='green' style='font-weight:bold;'>"+jsonData[i].result;
					}else{
						html+="<td ><font color='red' style='font-weight:bold;'>"+jsonData[i].result;
					}
					html+="</font></td></tr>"
				}
				html+="</tbody></table>";
				$("#ticketMemory").html(html);
			}
		});
	}
	//淘宝抢票查询列队
	ServerStatus.taoBaoRefresh=function(){
		$.ajax({
			url : rootPath+ "/KPITrainOrderBeBespeakServlet?type=305",
			data : '',
			type : 'post',
			success:function(jsonData){
				var  html="<table width='100%' height='100%' class='am-table'><thead><tr><th>IP</th><th>端口</th><th>状态</th></tr></thead> <tbody>";
					for (var i = 0; i < jsonData.length; i++) {
						html+="<tr> <td>"+jsonData[i].ip+"</td>" + "<td>"+jsonData[i].port+"</td>"
						if(jsonData[i].result!='异常'){
							html+= "<td ><font color='green' style='font-weight:bold;'>"
								+ jsonData[i].result + "</font></td>";
						}else{
							html+= "<td ><font color='red' style='font-weight:bold;'>"
								+ jsonData[i].result + "</font></td>";
						}
						html+= "</tr>" ;
					}
					html+="</tbody></table>";
				$("#taoBao").html(html);
			}
		});
	}
	//rep
	ServerStatus.repRefresh=function(){
		$.ajax({
			url : rootPath+ "/KPITrainOrderBeBespeakServlet?type=308",
			data : '',
			type : 'post',
			success:function(jsonData){
				var  html="<table width='100%' height='100%' class='am-table'><thead><tr><th>IP</th><th>端口</th><th>状态</th></tr></thead> <tbody>";
				for (var i = 0; i < jsonData.length; i++) {
					html+= "<tr>"
						+ "<td>"+jsonData[i].ip+"</td>" + "<td>"+jsonData[i].port+"</td>"
						+ "<td >" ;
					if(jsonData[i].result=='异常'){
						html+="<font color='red' style='font-weight:bold;'>"
							+ jsonData[i].result + "</font></td>";
					}else{
						html+="<font color='green' style='font-weight:bold;'>"
							+ jsonData[i].result + "</font></td>";
					}
					 html+= "</tr>"
				}
				 html+= "</tbody></table>";
				$("#rep").html(html);
			}
		});
	}
	//下单消费者
	ServerStatus.placeOrderCustomerRefresh=function(){
		$.ajax({
			url : rootPath+ "/KPITrainOrderBeBespeakServlet?type=307",
			data : '',
			type : 'post',
			success:function(jsonData){
				var  html="<table width='100%' height='100%' class='am-table'><thead><tr><th>下单消费者</th><th>IP</th><th>端口</th><th>状态</th></tr></thead> <tbody>";
				for (var i = 0; i < jsonData.length; i++) {
					if(i<2){
						html+="<tr><td>"+jsonData[i].name+"</td>";
					}else{
						html+="<tr><td>"+jsonData[i].name+"</td>";
					}
					html+= "<td>"+jsonData[i].ip+"</td>";
					html+= "<td>"+jsonData[i].port+"</td>"
					if(jsonData[i].result=='异常'){
						html+= "<td><font color='red' style='font-weight:bold;'>";
							html+= jsonData[i].result + "</font></td>";
					}else{
						html+= "<td><font color='green' style='font-weight:bold;'>";
						html+= jsonData[i].result + "</font></td>";
					}
					 html+="</tr>";
				}
				html+="</tbody></table>";
				$("#placeOrderCustomer").html(html);
			}
		});
	}
	//服务器
	ServerStatus.serverRefresh=function(){
		$.ajax({
			url:rootPath+"/KPITrainOrderBeBespeakServlet?type=309",
			data:'',
			type:'post',
			success:function(jsonData){
				$(".server").html("");
				var color=0;
				for (var i = 0; i < jsonData.length; i++) {
					var  array=['am-panel-danger','am-panel-success','am-panel-secondary'];
					var  html='<div class="am-panel    '+array[color]+'    panel-width-height server-float">';
					html+='<div class="am-panel-hd">'+jsonData[i].IP+'</div>';
					html+='<div class="am-panel-bd"><table width="100%" height="100%" class="am-table"><thead><tr><th>IP</th><th>端口</th><th>状态</th></tr></thead> <tbody>';
					for (var j = 0; j < jsonData[i].urls.length; j++) {
						html+='<tr><td>'+jsonData[i].urls[j].ip+'</td><td>'+jsonData[i].urls[j].port+'</td><td><font color="red" style="font-weight:bold;">'+jsonData[i].urls[j].value+'</font></td></tr>';
					}
					html+='</tbody></table></div>';
					html+= '</div>';
					color++;
					if(color==3){
						color=0;
					}
					$("#server").append(html);
				}
			}
		});
	}
	
	//获取rootPath
	function getRootPath(){
		  //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
        var curWwwPath = window.document.location.href;
        //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        //获取主机地址，如： http://localhost:8083
        var localhostPath = curWwwPath.substring(0, pos);
        //获取带"/"的项目名，如：/uimcardprj
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        return (localhostPath + projectName);
	}
	// 声明
	window.ServerStatus = ServerStatus;
	$(document).ready(function() {
		ServerStatus.init();
	});
	
});