var serverList = {}; //服务器列表
var echartsInstances = {};//echarts实例列表

var options = {};  //图表设置
var dates = {};  //时间
var dataCpus = {}; //实时CPU
var dataMems = {}; //实时内存
var dataTcps = {}; //实时tcp端口情况
var dataIos = {}; //实时io情况
var dataNetworks = {}; //实时带宽情况
var dataWeblogicCounts = {}; //实时当前运行weblogic实例
var dataDisks = {};  //实时当前磁盘空间情况

var saveHtml = ''; //增加主机的html页面代码
var saveMode = "single"; //默认新增模式
var maxCpu = 50; //默认CPU报警阀值

var timeoutCounts = {};  //超时次数

//其他资源说明文字
var markTip = {
	ESTABLISHED:"Tcp长连接",
	CLOSE_WAIT:"Tcp等待关闭连接",
	LISTEN:"Tcp监听连接",
	TIME_WAIT:"Tcp超时等待连接",
	rx:"入网总流量",
	tx:"出网总流量",
	weblogicServerCount:"当前主机Weblogic实例个数",
	rootDisk:"根目录磁盘已使用百分比",
	userDisk:"用户目录磁盘已使用百分比"
};

$(function(){
	//刷新页面/离开页面的保存数据事件
	window.onbeforeunload = function(){return saveDataFromStorage();};
	
	//获取服务器列表
	getServerList();	
	
	//绑定打开添加主机页面
	$(".container > button:eq(0)").on('click',function(){
		if (saveHtml == '') {
			saveHtml = $("#saveSeverHtml").html();
			$("#saveSeverHtml").html('');
		}	
		layer.open({
			type:1,
			title:"添加主机信息",
			content:saveHtml,
			offset:'200px',
			area: ['600px', '360px']			
		});		
	});
	
	//绑定显示/隐藏所有主机资源图表事件
	$(".container > button:eq(1)").on('click',function(){  
		if ($(this).text() == "显示所有图表") {
			$.each(serverList, function(i, n) {
				if (!$("#echarts #"+i).parent(".row").is(":visible")) {
					$("#echarts #"+i).parent(".row").show();
					$("#t" + i + ">td:eq(9)").children("button:eq(2)").text("隐藏");
				}
			});
			$(this).text("隐藏所有图表");
			return;
		} 
		
		$.each(serverList, function(i, n) {
			if (!$("#echarts #"+i).parent(".row").is(":hidden")) {
				$("#echarts #"+i).parent(".row").hide();
				$("#t" + i + ">td:eq(9)").children("button:eq(2)").text("显示");
			}
		});
		$(this).text("显示所有图表");
		
	});
	
	//cpu报警阀值切换更改
	$("#cpu-max").change(function(){
		maxCpu = $(this).val();
	});
	
	//数字显示的资源信息点击展示图标
	$(".show-echarts-view").click(function(){
		var flag = $(this).parent("div").attr("data-category"); //tcp io network
		var thisdata = [];
		var serverId = $(this).parents(".panel").attr("server-id");
		var flagX = $(this).children("span").eq(1).attr("data-kind");   //细分
		if (flag == "tcp") {
			thisdata = dataTcps[serverId][flagX];
		}
		
		if (flag == "io") {
			thisdata = dataIos[serverId][flagX];
		}
		
		if (flag == "network") {
			thisdata = dataNetworks[serverId][flagX];
		}
		
		if (flag == "weblogic") {
			thisdata = dataWeblogicCounts[serverId];
		}
		
		if (flag == "disk") {
			thisdata = dataDisks[serverId][flagX];
		}
		
		showEchartView(markTip[flagX] + "实时情况", thisdata, serverId);
	});
	
	
	//绑定显示/隐藏主机列表事件
	$(".container > button:eq(2)").on('click',function(){
		$(".table-responsive").toggle("normal",function(){
			var mark = "显示列表";
			if ($(".container > button:eq(2)").text() == mark) {
				$(".container > button:eq(2)").text("隐藏列表");
			} else {
				$(".container > button:eq(2)").text(mark);
			}
		});	
	});	
	
	//清空数据，删除全部
	$(".container > button:eq(3)").on('click',function(){
<<<<<<< HEAD
		layer.confirm("确定删除所有主机信息吗？",{title:"警告", offset:"300px"},function(index){
=======
		layer.confirm("确定删除所有主机信息吗？",{title:"警告",offset:"300px"},function(index){
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
			var loadIndex = layer.load(2, {offset:'400px', shade:0.35});
			$.post("linux/clearAll",function(json){
				layer.close(loadIndex);
				json = JSON.parse(json);
				if (json.returnCode != 0) {
					layer.alert(json.msg,{icon:5, offset:'400px'});				
				} else {					
					layer.alert("已删除所有主机信息,点击确定刷新页面!",{icon:1, offset:'400px'},function(index){
						window.onbeforeunload = null;
						sessionStorage.clear();
						layer.close(index);
						location.reload();
					});															
				}
			});
		});
	});			
	
	//查看历史记录
    $(".container > button:eq(4)").on('click', function() {
    	var index = layer.open({
    		type:2,
    		title:"Linux历史记录",
    		content:"historyServer.html?type=0",
			end:function() {
				var flag = $("#changeFlag").val();
				if (flag == "true") {
					//有改动，需要刷新表格
					window.onbeforeunload = null;
					saveDataFromStorage();																
					//$("#changeFlag").val("false");
					location.reload();
				}								
			}  
    	});
    	layer.full(index);
    });
	
  //打开命令执行窗口
    $(".container > button:eq(5)").on('click', function() {
    	
    	if ($.isEmptyObject(serverList)) {
    		layer.alert("当前没有可选服务器主机,请先添加!",{icon:5, offset:'380px'});
    		return false;
    	}
    	
    	var index = layer.open({
    		type:2,
    		title:"命令执行",
    		content:"execCommand.html",
    		offset:'150px',
			area: ['800px', '600px'],
			shade: 0
    	});
    });
    
    //重新计时
    $(".container > button:eq(6)").on('click', function() {
    	if ($.isEmptyObject(serverList)) {
    		return false;
    	}
<<<<<<< HEAD
    	layer.confirm("确定清除当前监控数据重新开始记录吗？", {title:"警告", icon:0, offset:"300px"}, function(index) {  
=======
    	layer.confirm("确定清除当前监控数据重新开始记录吗？", {title:"警告", icon:0}, function(index) {  
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
    		dates = {};  //时间
    		dataCpus = {}; //实时CPU
    		dataMems = {}; //实时内存
    		dataTcps = {}; //实时tcp端口情况
    		dataIos = {}; //实时io情况
    		dataNetworks = {}; //实时带宽情况
    		dataWeblogicCounts = {}; //实时当前运行weblogic实例
    		dataDisks = {};  //实时当前磁盘空间情况
    		window.onbeforeunload = null;
			saveDataFromStorage();											
			location.reload();
    		layer.close(index);
    	});
    });
	
	//创建echarts实例，对应的各服务器的图表dom已经在jsp中初始化好了
	$.each(serverList,function(i,n){
		echartsInstances[i] = echarts.init(document.getElementById(i),'shine');
		if (dates[i] == null) {
			dates[i] = [];
			dataCpus[i] = [];
			dataMems[i] = [];
			dataTcps[i] = {
				ESTABLISHED:[],
				LISTEN:[],
				CLOSE_WAIT:[],
				TIME_WAIT:[]
			};
			dataNetworks[i] = {
					rx:[],
					tx:[]
			};
			dataWeblogicCounts[i] = [];
			
			dataDisks[i] = {
					rootDisk:[],
					userDisk:[]
			};
			
			dataIos[i] = {};
			
		}		
		serverList[i]["ifNormal"] = true;
		serverList[i]["ifShow"] = true;
		var title = n.host;
		if (n.mark != "" && n.mark != null) {
			title = n.mark;
		}
		
		options[i] = {
				    title: {// 图表标题，可以通过show:true/false控制显示与否，还有subtext:'二级标题',link:'http://www.baidu.com'等
				        text: '主机' + title,
				        x: 'center',
				        align: 'right',
				        top:'top',
				        textStyle:{
				        	color:'#EF2203'
				        }
				    },
				    tooltip: {
				        trigger: 'axis'
				    },
				    toolbox: {
				    	show: true,
				        feature: {
				        	dataView: {readOnly: true}, //数据视图
				            saveAsImage:{},// 保存为图片				        	
				        }
				    },
				    legend: {
				        data:['空闲CPU[%]', "空闲内存[%]"],
				        x: 'left'
				    },
				    grid: {
				        left: '5%',
				        right: '5%',
				        bottom: '5%',// 这几个属性可以控制图表上下左右的空白尺寸，可以自己试试。
				        containLabel: true
				    },
				    xAxis : [
				        {
				            type : 'category',
				            boundaryGap: false,
				            data : dates[i],// X轴的定义
				            axisLine: {onZero: false},
				        }
				    ],
				    yAxis : [
				        {
				            type : 'value',// Y轴的定义
				            name : '空闲CPU[%]',
				            boundaryGap: [0.1, 0.1],
				            min:0,
				            max:100,
				            data:dataCpus[i]
				        },
				        {
				        	type : 'value',// Y轴的定义
				            name : '空闲内存[%]',
				            boundaryGap: [0.1, 0.1],
				            data:dataMems[i]
				        }
				    ],
				    series: [
				             {
				                 name:'空闲CPU[%]',
				                 type:'line',			                 
				                 smooth:true,
				                 data: dataCpus[i]
				             },
				             {
				            	 name:'空闲内存[%]',
				                 type:'line',
				                 yAxisIndex:1,
				                 smooth:true,
				                 data: dataMems[i]
				             }
				         ]
				};
		});
		$(".container > button:eq(1)").click();
	 	//getInfo();
		$.each(serverList,function(i,n){
			echartsInstances[i].setOption(options[i]); 
		});
	 	var intervalId = setInterval(function(){
	 		if (!$.isEmptyObject(serverList)) {
	 			getInfo();
	 		} else {
	 			clearInterval(intervalId);
	 		}	 		
	 	}, 4500);
		
});

//获取信息
function getInfo() {
	 $.ajax({
    	 url:"linux/getInfo",
    	 type:"get",
    	 dataType:"json",
    	 success:function(os) {
    		 if (!$.isEmptyObject(os) && os != null) {
    			 $.each(serverList,function(i,n){
    				 if (timeoutCounts[i] == null) {
    					 timeoutCounts[i] = 0;
    				 }
        			 if (dates[i].length == 0 || os[i].time != dates[i][dates[i].length - 1]) {
        				 dates[i].push(os[i].time);
        				 dataCpus[i].push(os[i].freeCpu);
        				 dataMems[i].push(os[i].freeMem.replace(/,/g,""));
        				         				 
        				 echartsInstances[i].setOption(options[i]);         				        				 
        				 
        				 $.each(os[i].tcpInfo, function(m, l) {        					 
        					 if ( $("[server-id='" + i + "'] span[data-kind='" + m + "']").length > 0) {
        						 dataTcps[i][m].push(l);
        						 $("[server-id='" + i + "'] span[data-kind='" + m + "']").text(l);
        					 }
        				 });
        				      
        				 $.each(os[i].networkInfo, function(m, l) {  
        					 if ( $("[server-id='" + i + "'] span[data-kind='" + m + "']").length > 0) {
        						 dataNetworks[i][m].push(l);
        						 $("[server-id='" + i + "'] span[data-kind='" + m + "']").text(l + " kB/s");
        					 }
        				 });
        				 
        				 $.each(os[i].diskInfo, function(m, l) {  
        					 if ( $("[server-id='" + i + "'] span[data-kind='" + m + "']").length > 0) {
        						 dataDisks[i][m].push(l);
        						 $("[server-id='" + i + "'] span[data-kind='" + m + "']").text(l + " %");
        					 }
        				 });
        				 
        				 
        				 dataWeblogicCounts[i].push(os[i].weblogicServerCount);   
       				 
        				 $("[server-id='" + i + "'] span[data-kind='weblogicServerCount']").text(os[i].weblogicServerCount);
        				 
        				 changeConnectStatus(i,0);
        				 changeCpuStatus(i, os[i].freeCpu);
        				 serverList[i]["ifNormal"] = true;  
        				 timeoutCounts[i]=0;
        			 } else {
        				 timeoutCounts[i]++;
        				 changeConnectStatus(i,1);
        				 serverList[i]["ifNormal"] = false;  
        			 }
        		 }); 
    		 } else {
    			 changeConnectStatus(0,2);
    		 }
    	 },
    	 error:function(){  
    		 console.log("数据加载失败！请检查数据链接是否正确"); 
    		 changeConnectStatus(0,2);
    	 } 
     });
}

//获取主机列表
function getServerList() {
	$.ajax({
		url:"linux/getServerList",
		type:"get",
		dataType:"json",
		async: false,
		success:function(data) {	
			if (!$.isEmptyObject(data.list)) {
				var tbody = $("tbody");
				var html = '';
				var n;
				var i;
				$.each(data.length, function(p, m){
					n = data.list[m];
					i = m;
					html += '<tr id="t' + i + '"><td>' + i + '</td>'
						  + '<td>' + n.host + ":" + n.port + '</td>'
						  +	'<td>' + n.uanme + '</td>' 
						  +	'<td>' + n.cpuInfo + "核" + '</td>' 
						  +	'<td>' + n.memInfo + "KB" + '</td>' 
						  +	'<td>' + n.username + '</td>' 
						  + '<td>' + n.mark + '</td>' 
						  + '<td>' + '<span class="label label-success">正常</span>' + '</td>'
						  + '<td>' + '<span class="label label-success">正常</span>' + '</td>'
						  + '<td>' + '<button type="button" class="btn btn-warning btn-sm" onclick="reconnectServer(' + i + ');">重连</button>'
						  + '&nbsp;&nbsp;<button type="button" class="btn btn-danger btn-sm" onclick="delServer(this,' + i + ');">删除</button>'
						  + '&nbsp;&nbsp;<button type="button" class="btn btn-primary btn-sm" onclick="showOrHideView(this,' + i + ');">隐藏</button></td></tr>';
				});
				tbody.append(html);
				serverList = data.list;
				getDataFromStorage();
			} else {
				sessionStorage.clear();
			}			
		}, 
		error:function(){
			layer.alert("无法加载数据,请检查网络连接!", {icon:5, offset:'200px'});
		}
	});
}

//保存新的主机信息
function saveServer() {
	var sendData = {};
	if (saveMode == "batch") {
		var serverList = $("#serverList").val();
		if (serverList != null && serverList != "") {
			sendData = {
				serverList:	serverList,
				mode:saveMode
			};
		} else {
			layer.msg("请填写完整再提交!",{icon:2,time:1500,offset:'400px',});
			return false;
		}
	} else {
		var host = $("#host").val();
		var username = $("#username").val();
		var password = $("#password").val();	
		var mark = $("#mark").val();
		if (host != null && host != "" && username != null && username != "" && password != null && password != "") {
			sendData = {
					host:host,
					username:username,
					password:password,
					mark:mark,
					mode:saveMode
			};
		} else {
			layer.msg("请填写完整再提交!",{icon:2,time:1500,offset:'400px',});
			return false;
		}
	}
	
	var loadIndex = layer.load(2, {offset:'400px', shade:0.35});
	$.ajax({
		url:"linux/saveServer",
		type:"post",
		data:sendData,
		dataType:"json",
		success:function(json){
			if (json.returnCode != 0) {
				layer.alert(json.msg,{icon:5, offset:'400px'});				
			} else {	
				var tips = '';
				if (saveMode == "batch" && json.count != 0) {
					tips = "本次共添加成功" + json.count + "台主机!";
				} else if (saveMode == "batch" && json.count == 0) {
					layer.alert("输入的信息可能有误,未添加成功任何主机,请检查!",{icon:2, offset:'400px'});
					layer.close(loadIndex);
					return ;
				}
				tips += "即将刷新本页面!";
				window.onbeforeunload = null;
				layer.alert(tips,{icon:1, offset:'400px'}, function(index) {
					layer.close(index);
					saveDataFromStorage();											
					location.reload();
				});	
			}
			layer.close(loadIndex);
		}
	});	
}

//删除一个主机信息
function delServer(obj,infoId) {	
	layer.confirm("确认删除此条主机信息?", {icon:3, title:"警告",offset:'200px',}, function(index){
		layer.close(index);
		$.post("linux/delServer", {infoId:infoId}, function(json){
			json = JSON.parse(json);
			if (json.returnCode == 0) {
				layer.msg("删除成功!", {icon:1,time:1500,offset:'200px',});
				$(obj).parents('tr').remove();
				$("#echarts #"+infoId).parent(".row").remove();
				delete serverList[infoId];
				delete dates[infoId];
				delete dataCpus[infoId];
				delete dataTcps[infoId];
				delete dataMems[infoId];
				delete dataNetworks[infoId];
				delete dataWeblogicCounts[infoId];
				delete dataDisks[infoId];
			} else {
				layer.alert(json.msg, {icon:5,offset:'200px',});
			}
		});
	});
}

//改变连接状态说明
function changeConnectStatus(i,type) {
	var statusSpan = $("#t" + i + ">td:eq(7)").children("span");
	//正常
	if (type == 0 && statusSpan.hasClass("label-danger")) {
		statusSpan.removeClass("label-danger").addClass("label-success");
		statusSpan.text("正常");
	}
	//异常
	if (type == 1 && statusSpan.hasClass("label-success") && timeoutCounts[i] > 2) {
		statusSpan.removeClass("label-success").addClass("label-danger");
		statusSpan.text("异常");
<<<<<<< HEAD
		reconnectServer(i);
=======
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
	}
	//连接出错
	if(type == 2) {
		$.each(serverList, function(m, n){
			$("#t" + m + ">td:eq(7)").children("span").removeClass().addClass("label label-danger");
			$("#t" + m + ">td:eq(7)").children("span").text("中断");
		});
	}	
}

//改变cpu负荷状态
function changeCpuStatus(i, cpuData) {
	var statusSpan = $("#t" + i + ">td:eq(8)").children("span");
	//正常
	if (Number(cpuData) > Number(maxCpu)) {
		if (statusSpan.hasClass("label-danger")) {
			statusSpan.removeClass("label-danger").addClass("label-success");
			statusSpan.text("正常");
		}
	} else {
		//报警
		if (statusSpan.hasClass("label-success")) {
			statusSpan.removeClass("label-success").addClass("label-danger");
			statusSpan.text("报警");
		}
	}
	
}


//重连服务器
function reconnectServer(i){
	var bg = function(){
		$.post("linux/reconnect", {infoId:i}, function(data) {
			data = JSON.parse(data);
			if (data.returnCode == 0) {
				layer.msg("重新连接成功!", {icon:1, time:1500, offset:'200px',});
			} else {
				layer.alert(data.msg, {icon:5, offset:'200px',});
			}
		});
	};
	if (serverList[i]["ifNormal"]) {
		layer.confirm("该主机目前连接正常,是否确定需要重新连接并获取数据？", {icon:3, title:"提示", offset:'200px',}, function(index){
			layer.close(index);
			bg();
		});
	} else {
		bg();
	}	
}

//操作列表显示或者隐藏事件
function showOrHideView(obj,i) {	
	$("#echarts #"+i).parent(".row").toggle("normal",function(){
		 if ($(obj).text() == "显示") {
			 $(obj).text("隐藏"); 
		 } else { 
			 $(obj).text("显示");  
		 }
	});
}

//保存页面的切换
function batchSave() {
	$("#singleSave").hide("normal",function(){
		$("#batchSave").show("normal");
		saveMode = "batch";
	});
		
}

function singleSave() {
	$("#batchSave").hide("normal",function(){
		$("#singleSave").show("normal");
		saveMode = "single";
	});
	
}

//动态单折线图显示
function showEchartView(title, seriesData, serverId) {
	var option = {
		    title: {
		        text: title,
		        x: 'center',
		        align: 'right',
		        top:'top',
		        textStyle:{
		        	color:'#EF2203'
		        }
		    },		    
		    tooltip: {
		        trigger: 'axis'
		    },
		    toolbox: {
		    	show: true,
		        feature: {
		        	dataView: {readOnly: true}, //数据视图
		            saveAsImage:{},// 保存为图片				        	
		        }
		    },
		    xAxis : [
				        {
				            type : 'category',
				            boundaryGap: false,
				            data : dates[serverId],// X轴的定义
				            axisLine: {onZero: false},
				        }
				    ],
		    yAxis : [
		        {
		            type : 'value',// Y轴的定义
		            boundaryGap: [0.1, 0.1],
		            data:seriesData,
		            max:'dataMax'
		        }
		    ],
		    series: [
		             {
		            	 name:title,
		                 type:'line',			                 
		                 smooth:true,
		                 data: seriesData
		             }
		         ]
		};
	
	var thisechart;
	var idOfInterval;
	layer.open({
		type:1,
		title:title,
		content:'<div class="open-echarts" id="thisId"></div>',
		offset:'300px',
		area: ['800px', '460px'],
		success:function(){
			thisechart = echarts.init(document.getElementById("thisId"),'shine');
			thisechart.setOption(option);
			idOfInterval = setInterval(function () {
				thisechart.setOption(option);
			}, 4500);
		},
		cancel:function(){
			clearInterval(idOfInterval);
		}	
	});

}


//sessionStorage存取数据
function getDataFromStorage() {
	if(sessionStorage.getItem("dates")) {
		dates = JSON.parse(sessionStorage.getItem("dates"));
		dataCpus = JSON.parse(sessionStorage.getItem("dataCpus"));
		dataMems = JSON.parse(sessionStorage.getItem("dataMems"));
		dataTcps = JSON.parse(sessionStorage.getItem("dataTcps"));
		dataNetworks = JSON.parse(sessionStorage.getItem("dataNetworks"));
		dataWeblogicCounts = JSON.parse(sessionStorage.getItem("dataWeblogicCounts"));
		dataDisks = JSON.parse(sessionStorage.getItem("dataDisks"));
	}
}

function saveDataFromStorage() {
	sessionStorage.setItem("dates", JSON.stringify(dates));
	sessionStorage.setItem("dataCpus", JSON.stringify(dataCpus));
	sessionStorage.setItem("dataMems", JSON.stringify(dataMems));	
	sessionStorage.setItem("dataTcps", JSON.stringify(dataTcps));
	sessionStorage.setItem("dataNetworks", JSON.stringify(dataNetworks));
	sessionStorage.setItem("dataWeblogicCounts", JSON.stringify(dataWeblogicCounts));
	sessionStorage.setItem("dataDisks", JSON.stringify(dataDisks));
	return "请不要选择\"禁止弹出对话框\"选项!";
}
