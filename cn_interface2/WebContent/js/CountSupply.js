Date.prototype.format = function(format) {
	var o = {
		"M+" : this.getMonth() + 1, // month
		"d+" : this.getDate(), // day
		"h+" : this.getHours(), // hour
		"m+" : this.getMinutes(), // minute
		"s+" : this.getSeconds(), // second
		"q+" : Math.floor((this.getMonth() + 3) / 3), // quarter
		"S" : this.getMilliseconds()
	// millisecond
	}

	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	}

	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
}

function GetRequest() {
	var url = location.search; // 获取url中"?"符后的字串
	var theRequest = new Object();
	if (url.indexOf("?") != -1) {
		var str = url.substr(1);
		strs = str.split("&");
		for (var i = 0; i < strs.length; i++) {
			theRequest[strs[i].split("=")[0]] = (strs[i].split("=")[1]);
		}
	}
	return theRequest;
}

var ViewLogic = {
	TimeInterval : 8000,
	Timer : null,
	GetRequest : function() {
		var url = location.search; // 获取url中"?"符后的字串
		var theRequest = new Object();
		if (url.indexOf("?") != -1) {
			var str = url.substr(1);
			strs = str.split("&");
			for (var i = 0; i < strs.length; i++) {
				theRequest[strs[i].split("=")[0]] = (strs[i].split("=")[1]);
			}
		}
		return theRequest;
	},
	ChangeViewType : function() {
		var type = $("#ddlViewType").val();
		if (type == 1) {
			$("#divViewOptions").hide();
			$("#divRealTimeStatus").show();
			ViewLogic.Init();
		} else {
			$("#divViewOptions").show();
			$("#divRealTimeStatus").hide();
		}
	},
	Stop : function() {
		if (ViewLogic.Timer != null) {
			clearInterval(ViewLogic.Timer);
			ViewLogic.Timer = null;
		}
	},
	GetInitData : function() {
		var data = [], time = new Date((new Date())
				.format("yyyy/MM/dd hh:mm:00")).getTime(), i;
		for (i = -39; i <= 0; i += 1) {
			data.push({
				x : time + i * 1000 * 60,
				y : 0
			});
		}
		return data;
	},
	RenderData1 : function(targetSeries, data) {

		var flag = false;
		for (var i = 0; i < targetSeries.data.length; i++) {
			if (targetSeries.data[i].x == data.x) {
				flag = true;
				if (targetSeries.data[i].y != data.y) {
					targetSeries.data[i].update(parseFloat(data.y));
				}
				break;
			}
		}
		if (!flag && data.x > targetSeries.data[0].x) {
			targetSeries.addPoint([ data.x, parseFloat(data.y) ], true, true);
		}
	},
	RenderData : function(targetSeries, data) {
		var flag = false;

		for (var i = 0; i < targetSeries.points.length; i++) {
			if (targetSeries.points[i].x == data.x) {

				flag = true;
				if (targetSeries.points[i].y != data.y) {
					targetSeries.points[i].update(data.y, true, true);
				}
				break;
			}
		}

		if (!flag && data.x > targetSeries.data[0].x) {
			targetSeries.addPoint([ data.x, data.y ], true, true);
		}
	},
	GetStatusStr : function(data) {
		var strFormat = $("#divTemplate").html();
		var updateDate = (new Date()).format("hh:mm:ss");// 刷新时间
//		var avgRate = data.avgRate;// 抢票动作成功率
		var successForCount = data.successForCount;
		var sumNewTicketNumber = data.sumNewTicketNumber;// 今日新增抢票次数
		var sumNowOrderingNumber = data.sumNowOrderingNumber;// 出票订单数量
		var sumOrderSuccess = data.sumOrderSuccess;// 抢票占座成功
		var sumOrderNeedTo = data.sumOrderNeedTo;// 今日应抢需求数
		var sumBespeakTotal = data.sumBespeakTotal;// 下单尝试总次数
		var isFailureAlert = data.isFailureAlert;// 查询是否警告
		var failSum = data.failSum;// 查询失败
		var successSum = data.successSum;// 查询成功
		var sumAppointments = data.sumAppointments // 预约抢票数
		var sumRefuse = data.sumRefuse // 据单数
		var avgtime = data.avgtime;
		
	     strFormat = strFormat.replace('{avgtime}', avgtime);
		var Notickets = data.Notickets;// 无余票
		Notickets = Notickets > 0 ? Notickets : '0';
		var Booked = data.Booked;// 已订
		Booked = Booked > 0 ? Booked : '0';
		var Queuingfailure = data.Queuingfailure;// 排队失败
		Queuingfailure = Queuingfailure > 0 ? Queuingfailure : '0';
		var Queryfailed = data.Queryfailed;// 查询失败
		Queryfailed = Queryfailed > 0 ? Queryfailed : '0';
		var Consumption = data.Consumption;// 限制高消费
		Consumption = Consumption > 0 ? Consumption : '0';
		var Travelalone = data.Travelalone;// 儿童不能单独旅行
		Travelalone = Travelalone > 0 ? Travelalone : '0';
		var Gobeyond = data.Gobeyond;// 排队人数现已超过余票数
		Gobeyond = Gobeyond > 0 ? Gobeyond : '0';
		var IdnetityTheft = data.IdnetityTheft;// 身份冒用
		IdnetityTheft = IdnetityTheft > 0 ? IdnetityTheft : "0";
		var IdnetityCheck = data.IdnetityCheck;// 身份核验
		IdnetityCheck = IdnetityCheck > 0 ? IdnetityCheck : "0";
		var Otherreasons = data.Otherreasons;// 其他原因
		Otherreasons = Otherreasons > 0 ? Otherreasons : '0';

		var sumOrdering = data.sumOrdering;
		var sumOrderingOverTime = data.sumOrderingOverTime;
		sumOrderingOverTime = sumOrderingOverTime > 0 ? '<b style="color:red;">'
				+ sumOrderingOverTime + '</b>'
				: sumOrderingOverTime;
		var sumWaitOrder = data.sumWaitOrder;
		var sumWaitOrderOverTime = data.sumWaitOrderOverTime;
		sumWaitOrderOverTime = sumWaitOrderOverTime > 0 ? '<b style="color:red;">'
				+ sumWaitOrderOverTime + '</b>'
				: sumWaitOrderOverTime;
		var sumWaitIssue = data.sumWaitIssue;
		var sumBuyQuestion = data.sumBuyQuestion;
		sumBuyQuestion = sumBuyQuestion > 0 ? '<b style="color:red;">'
				+ sumBuyQuestion + '</b>' : sumBuyQuestion;
		var sumPayQuestion = data.sumPayQuestion;
		sumPayQuestion = sumPayQuestion > 0 ? '<b style="color:red;">'
				+ sumPayQuestion + '</b>' : sumPayQuestion;
		var cancelReason = [ "其他原因", "无余票", "非法席别", "车次未找到", "票价不符", "行程冲突",
				"排队", "提交订单用户过多", "用户登录异常", "存在未完成订单", "身份效验失败", "多次打码失败",
				"下单超时", "接口申请取消" ];
		var cancelTypeStr = "";
		var sumCancelCount = 0;
		for (var i = 0; i < cancelReason.length; i++) {
			var dataItem = data["CancelType" + i];
			if (dataItem) {
				sumCancelCount += dataItem;
			}
		}

		var step = 0;
		var unfix = false;
		for (var i = cancelReason.length; i >= 0; i--) {
			var dataItem = data["CancelType" + i];
			if (dataItem) {
				if (step % 5 == 0) {
					cancelTypeStr = cancelTypeStr + "<tr class='next-td'>";
					unfix = true;
				}
				var cancelRate = (dataItem * 100 / sumCancelCount).toFixed(2);
				cancelTypeStr = cancelTypeStr + "<td>" + cancelReason[i]
						+ "</td><td>" + dataItem + "(" + cancelRate + "%)</td>";
				if (step % 5 == 4) {
					cancelTypeStr = cancelTypeStr + "</tr>";
					unfix = false;
				}
				step++;
			}
		}
		if (unfix) {
			cancelTypeStr = cancelTypeStr + "</tr>";
			unfix = false;
		}

		if (step <= 5) {
			cancelTypeStr = cancelTypeStr
					+ "<tr class='next-td'><td>&nbsp;</td></tr>";
		}

		if (step <= 0) {
			cancelTypeStr = cancelTypeStr
					+ "<tr class='next-td'><td>&nbsp;</td></tr>";
		}
		var sumCancelCounts = sumOrderNeedTo == 0 ? 0
				: (100 * sumOrderSuccess / sumOrderNeedTo).toFixed(2);
		var sumSuccessCounts = sumNowOrderingNumber == 0 ? 0
				: (100 * sumNowOrderingNumber / sumOrderSuccess).toFixed(2);
		strFormat = strFormat.replace('{updateDate}', updateDate);
//		strFormat = strFormat.replace('{avgRate}', avgRate);
		strFormat = strFormat.replace('{sumCancelCounts}', sumCancelCounts);
		strFormat = strFormat.replace('{sumNewTicketNumber}',
				sumNewTicketNumber);
		strFormat = strFormat.replace('{sumNowOrderingNumber}',
				sumNowOrderingNumber);
		strFormat = strFormat.replace('{sumOrderSuccess}', sumOrderSuccess);
		strFormat = strFormat.replace('{sumBespeakTotal}', sumBespeakTotal);
		strFormat = strFormat.replace('{sumOrderNeedTo}', sumOrderNeedTo);
		strFormat = strFormat.replace('{sumOrdering}', sumOrdering);
		strFormat = strFormat.replace('{sumOrderingOverTime}',
				sumOrderingOverTime);

		strFormat = strFormat.replace('{sumAppointments}', sumAppointments);

		strFormat = strFormat.replace('{sumWaitOrder}', sumWaitOrder);
		strFormat = strFormat.replace('{sumWaitOrderOverTime}',
				sumWaitOrderOverTime);
		strFormat = strFormat.replace('{sumWaitIssue}', sumWaitIssue);

		strFormat = strFormat.replace('{sumSuccessCounts}', sumSuccessCounts);
		strFormat = strFormat.replace('{isFailureAlert}', isFailureAlert);
		strFormat = strFormat.replace('{failSum}', failSum);
		strFormat = strFormat.replace('{successSum}', successSum);

		strFormat = strFormat.replace('{sumBuyQuestion}', sumBuyQuestion);
		strFormat = strFormat.replace('{sumPayQuestion}', sumPayQuestion);

		strFormat = strFormat.replace('{Notickets}', Notickets);
		strFormat = strFormat.replace('{Booked}', Booked);
		strFormat = strFormat.replace('{Queuingfailure}', Queuingfailure);
		strFormat = strFormat.replace('{Queryfailed}', Queryfailed);
		strFormat = strFormat.replace('{Consumption}', Consumption);
		strFormat = strFormat.replace('{Travelalone}', Travelalone);
		strFormat = strFormat.replace('{Gobeyond}', Gobeyond);
		strFormat = strFormat.replace('{Otherreasons}', Otherreasons);
		strFormat = strFormat.replace('{IdnetityTheft}', IdnetityTheft);
		strFormat = strFormat.replace('{IdnetityCheck}', IdnetityCheck);

	

		strFormat = strFormat.replace('{sumRefuse}', sumRefuse);
		return strFormat;
	},
	LoadData : function(series) {
		var request = ViewLogic.GetRequest()

	
		var url = "/cn_interface/CountSupplyMethod?year=1";
		$.ajax({
			type : "GET",
			url : url,
			dataType : "json",
			success : function(data) {
			
				seriesRate = series[0];
				seriesTotal = series[1];
				seriesSuccess = series[2];
				totalData = data.series[0];
				successData = data.series[1];
				var now = new Date((new Date()).format("yyyy/MM/dd hh:mm:00"))
						.getTime();
				var flagHaveNow = false;

				for (var i = 0; i < totalData.length; i++) {
					var totalItem = totalData[i];
					var successItem = successData[i];
					var rateItemY = (totalItem.y == 0) ? 0
							: (100 * successItem.y / totalItem.y);
					rateItemY = parseFloat(rateItemY.toFixed(2));
					totalItem.x = new Date(totalItem.x.replace(/\-/g, '/')
							+ ":00").getTime();
					successItem.x = new Date(successItem.x.replace(/\-/g, '/')
							+ ":00").getTime();
					if (totalItem.x == now || successItem.x == now) {
						flagHaveNow = true;
					}
					ViewLogic.RenderData(seriesTotal, totalItem);
					ViewLogic.RenderData(seriesSuccess, successItem);
					ViewLogic.RenderData1(seriesRate, {
						x : successItem.x,
						y : rateItemY
					});
				}

				if (!flagHaveNow) {
					ViewLogic.RenderData(seriesTotal, {
						x : now,
						y : 0
					});
					ViewLogic.RenderData(seriesSuccess, {
						x : now,
						y : 0
					});
					ViewLogic.RenderData(seriesRate, {
						x : now,
						y : 0
					});

				}
				var totalForCount = 0;
				var successForCount = 0;
				for (var i = 0; i < seriesTotal.points.length; i++) {
					totalForCount = totalForCount + seriesTotal.points[i].y;
					successForCount = successForCount
							+ seriesSuccess.points[i].y;
				}
				
				var avgRate = totalForCount == 0 ? 0
						: (100 * successForCount / totalForCount);
				data.status.avgRate = avgRate.toFixed(2);
				data.status.successForCount = successForCount;
				$("#divRealTimeStatus").html(
						ViewLogic.GetStatusStr(data.status));
			}
		});
	},
	Option : {
		chart : {
			renderTo : 'container_day',
			type : 'spline',
			animation : Highcharts.svg, // don't animate in old IE
			margin : [ 50, 70, 70, 70 ],
			events : {
				load : function() {
					var series = this.series
					ViewLogic.LoadData(series);
					ViewLogic.Timer = window.setInterval(function() {
						ViewLogic.LoadData(series);
					}, ViewLogic.TimeInterval);
				}
			}
		},
		title : {
			text : '帐号复用率成功率统计'
		},
		xAxis : {
			type : 'datetime',
			tickPixelInterval : 50
		},
		yAxis : [ {
			title : {
				text : '乘客总数',
				style : {
					color : '#808080'
				}
			},
			plotLines : [ {
				value : 0,
				width : 1,
			} ],
			min : 0
		}, {
			title : {
				text : '成功率',
				style : {
					color : '#4572A7'
				}
			},
			labels : {
				format : '{value} %',
				style : {
					color : '#4572A7'
				}
			},
			opposite : true,
			min : 0,
			tickInterval : 20,
			tickPositions : [ 0, 20, 40, 60, 80, 100 ],
			max : 100
		} ],
		tooltip : {
			formatter : function() {
				return '<b>' + this.series.name + '</b><br/>'
						+ Highcharts.dateFormat('%Y-%m-%d %H:%M', this.x)
						+ '<br/>' + Highcharts.numberFormat(this.y, 2);
			}
		},
		legend : {
			layout : 'horizontal',
			// align : 'right',
			// x : 70,
			// verticalAlign : 'top',
			// y : 50,
			// floating : true,
			backgroundColor : '#FFFFFF'
		},
		exporting : {
			enabled : false
		},
		series : []
	},
	Chart : null,
	Init : function() {
		ViewLogic.Stop();

		Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});

		var initData = ViewLogic.GetInitData();

		ViewLogic.Option.series = [ {
			name : '成功率',
			data : initData,
			type : 'column',
			color : '#89A54E',
			tooltip : {
				valueSuffix : '%'
			},
			yAxis : 1
		// 坐标轴序号
		}, {
			name : '每分钟总次数',
			data : initData,
			color : '#4572A7'
		}, {
			name : '每分钟成功数量',
			data : initData,
			color : '#000000'
		} ]

		ViewLogic.Chart = new Highcharts.Chart(ViewLogic.Option);
	}
}

$(document).ready(function() {
	ViewLogic.Init();
	$('#ddlViewType').change(function() {
		ViewLogic.ChangeViewType();
	});
});
