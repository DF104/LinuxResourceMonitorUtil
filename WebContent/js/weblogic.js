var table;//Datatables jquery实例


//保存的动态数据
var weblogicInfos = {};
var dates = {};

//标志当前是否有数据
var hasDataFlag = false;

var saveHtml = ''; //增加主机html代码
var saveMode = "single"; //添加模式

var optionMark = {
		currentSize:"JVM堆当前使用大小[MB]",
		freeSize:"JVM堆当前空闲大小[MB]",
		freePercent:"JVM堆当前空闲百分比[%]",
		maxThreadCount:"活动线程总数",
		pendingCount:"暂挂用户请求数",
		idleCount:"空闲线程数"
};

$(function(){
	
	//刷新页面/离开页面的保存数据事件
	window.onbeforeunload = function(){return saveDataFromStorage();};
	
	table = $(".table").on( 'xhr.dt', function ( e, settings, json, xhr) {
		if (xhr.status != 200) {
			$.each($(".server-connect-status"), function(i, n) {
	    		if ($(n).hasClass("label-success")) {
	    			$(n).removeClass("label-success").addClass("label-danger");
	    			$(n).text("中断");
	    		}
	    	});
	        console.log("ajax获取数据不正常,请检查服务器状态或者网络连接!" + "\nAjax当前状态：" + xhr.status);
		}		
	}).DataTable({
		"aaSorting": [[ 0, "asc" ]],//默认第几个排序
		"bStateSave": true,//状态保存
		"processing": false,   //显示处理状态
 		"serverSide": false,  //服务器处理
 		"autoWidth": false,   //自动宽度
 		"scrollX": true,
 		"lengthChange": false,
 		"paging": false,
        "ajax":"weblogic/getList",
        "columnDefs":[ {"orderable":false,"aTargets":[16]}],
        "columns":[
                   {
                	   "data":"weblogicId"  
                   },
                   {
                	   "data":null,
                	   "render":function(data, type, full, meta) {
                   			return data.host + ":" + data.port;
                	   }
                   },{
<<<<<<< HEAD
                	   "data":"mark",
                	   "visible": false
                   },{
                	   "data":"username",
                	   "visible": false
                   },{
                	   "data":"info.serverName",
                	   "visible": false
=======
                	   "data":"mark"
                   },{
                	   "data":"username"
                   },{
                	   "data":"info.serverName"
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
                   },{
                	   "data":null,
                	   "render":function(data) {
                		   var statusCss = "danger";
                		   if (data.info.status == "RUNNING") {
                			   statusCss = "success";
                		   } 
                		   return '<span class="label label-' + statusCss + '">' + data.info.status + '</span>';
                	   }
                   },{
                	  "data":null,
<<<<<<< HEAD
                	  "visible": false,
=======
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
                	  "render":function(data) {                		 
                		   var statusCss = "danger";
	               		   if (data.info. health == "Health") {
	               			   statusCss = "success";
	               		   } 
	               		   return '<span class="label label-' + statusCss + '">' + data.info. health + '</span>';
                	  }
                   },{
<<<<<<< HEAD
                	   "data":"info.startTime",
                	   "visible": false
=======
                	   "data":"info.startTime"
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
                   },{
                	   "data":null,
                	   "render":function(data) {
                		   return data.info.maxJvm + "MB";
                	   }
                   },{
            		   "data":null,
<<<<<<< HEAD
            		   "visible": false,
=======
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
                	   "render":function(data) {
                		   return '<span class="dynamic-data bg-primary" data-info="jvmInfo" data-name="currentSize">&nbsp;'+data.info.jvmInfo.currentSize + 'MB&nbsp;</span>';
                	   }
                   },{
            		   "data":null,
<<<<<<< HEAD
            		   "visible": false,
=======
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
                	   "render":function(data) {
                		   return '<span class="dynamic-data bg-primary" data-info="jvmInfo" data-name="freeSize">&nbsp;' + data.info.jvmInfo.freeSize + 'MB&nbsp;</span>';
                	   }
                   },{
            		   "data":null,
                	   "render":function(data) {
                		   return '<span class="dynamic-data bg-primary" data-info="jvmInfo" data-name="freePercent">&nbsp;' + data.info.jvmInfo.freePercent + '%&nbsp;</span>';
                	   }
                   },{
                	   "data":null,
                	   "render":function(data) {
                		   return '<span class="dynamic-data bg-primary" data-info="queueInfo" data-name="maxThreadCount">&nbsp;&nbsp;' + data.info.queueInfo.maxThreadCount + '&nbsp;&nbsp;</span>';
                	   }
                	   
                   },{
                	   "data":null,
                	   "render":function(data) {
                		   return '<span class="dynamic-data bg-primary" data-info="queueInfo" data-name="pendingCount">&nbsp;&nbsp;' + data.info.queueInfo.pendingCount + '&nbsp;&nbsp;</span>';
                	   }
                   },{
                	   "data":null,
                	   "render":function(data) {
                		   return '<span class="dynamic-data bg-primary" data-info="queueInfo" data-name="idleCount">&nbsp;&nbsp;' + data.info.queueInfo.idleCount + '&nbsp;&nbsp;</span>';
                	   }
                   },{
                	   "data":null,
<<<<<<< HEAD
                	   "visible": false,
=======
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
                	   "render":function(data) {
                		   
                		   if (data.connectStatus != "true") {
                			   return '<a href="javascript:;" onclick="javascript:layer.alert("' + data.connectStatus + '",{icon:5});"><span class="label label-danger server-connect-status">异常</span></a>';
                		   }
                		   
                		   return '<span class="label label-success server-connect-status">正常</span>';
                	   }
                   },{
                	   "data":null,
<<<<<<< HEAD
                	   "visible": false,
=======
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
                	   "render":function(data) {
                		   var html = '<button type="button" class="btn btn-warning btn-sm" onclick="reconnectWeblogic(' + data.weblogicId + ');">重连</button>'
                		   			+ '&nbsp;&nbsp;<button type="button" class="btn btn-danger btn-sm" onclick="delWeblogic(this,' + data.weblogicId + ');">删除</button>'
                		   			+ '&nbsp;&nbsp;<button type="button" class="btn btn-info btn-sm" onclick="getBaseInfo(' + data.weblogicId + ');">重新获取基本信息</button>';
                		   return html;
                	   }
                   }
        ],
        "language":{
        	url: './js/zh_CN.json'
        },
        "initComplete":function(){
        	//显示隐藏列
            $('.toggle-vis').on('change', function (e) {
                e.preventDefault();
                var column = table.column($(this).attr('data-column'));
                column.visible(!column.visible());
            });
            
            //绑定echarts图表显示事件
            $("table").delegate(".dynamic-data","click", function(){
            	var data = table.row( $(this).parents('tr') ).data(); 
            	var infoName = $(this).attr("data-info");
            	var dataName = $(this).attr("data-name");
            	showEchartView(optionMark[dataName], weblogicInfos[data.weblogicId][infoName][dataName], data.weblogicId);    	
            });
                      
            
        	//指定显示内容
            checkedBox();
            
            //循环获取实时信息
            var intervalId = setInterval(function(){
            	getInfoData(intervalId);
            }, 5000); 
            
        }
	});
	
	//从sessionStorage获取数据
	getDataFromStorage();
	
	//增加新的
    $(".container > button:eq(0)").on('click', function() {
    	if (saveHtml == '') {
			saveHtml = $("#saveSeverHtml").html();
			$("#saveSeverHtml").html('');
		}	
		layer.open({
			type:1,
			title:"添加新的weblogic信息",
			content:saveHtml,
			area: ['600px', '360px']			
		});	
    });
	
	//删除所有内容
    $(".container > button:eq(1)").on('click', function() {
    	delAll();
    });
    
  //查看历史记录
    $(".container > button:eq(2)").on('click', function() {
    	var index = layer.open({
    		type:2,
    		title:"weblogic历史记录",
    		content:"historyServer.html?type=1",
			end:function() {
				var flag = $("#changeFlag").val();
				if (flag == "true") {
					//有改动，需要刷新表格
					if (hasDataFlag == false) {
						hasDataFlag = true;
						getInfoData(0);
						//循环获取实时信息
			            var intervalId = setInterval(function(){
			            	getInfoData(intervalId);
			            }, 5000); 
					}
					$("#changeFlag").val("false");
				}								
			}    	
    	});
    	layer.full(index);
    });
    
    //重新计时
    $(".container > button:eq(3)").on('click', function() {
    	if (!hasDataFlag) {
    		return false;
    	}
    	layer.confirm("确定清除当前监控数据重新开始记录吗？", {title:"警告", icon:0}, function(index) {  
    		weblogicInfos = {};
    		dates = {};
    		layer.close(index);
    		layer.msg("重新计时成功!", {icon:1, time:1500});
    	});
    });
    
});

//匹配显示隐藏列的对应的checkbox状态
function checkedBox() {
	var checkList = $(".toggle-vis");
    var column;
    var that;
    $.each(checkList, function(i, n){
    	that = $(this);
    	column = table.column($(this).attr('data-column'));
    	if (column.visible()) {
    		that.attr("checked","checked");
    	}
    	
    });
}

//刷新信息
function getInfoData(id) {
	table.ajax.reload(function(json){
		if ($.isEmptyObject(json.data)) {
			weblogicInfos = {};
			dates = {};
			hasDataFlag = false;
			clearInterval(id);
			return;
		}		
		hasDataFlag = true;
		$.each(json.data, function(i, n){
			if (weblogicInfos[n.weblogicId] == null) {
				weblogicInfos[n.weblogicId] = {
						isNormal:true,  //当前连接是否正常？
						jvmInfo:{
							currentSize:[],
							freeSize:[],
							freePercent:[]
						},
						queueInfo:{
							maxThreadCount:[],
							pendingCount:[],
							idleCount:[]
						}
				};
			}
			
			
			if (dates[n.weblogicId] == null) {
				dates[n.weblogicId] = [];
			}
			
			dates[n.weblogicId].push(n.info.time);
			
			if (n.connectStatus != "true") {
				weblogicInfos[n.weblogicId]["isNormal"] = false;
			}
			$.each(n.info, function(i1, n1){
				if ((typeof n1) == 'object') {
					$.each(n1, function(i2, n2){
						weblogicInfos[n.weblogicId][i1][i2].push(n2);
					});
				}
			});
			
		});
	},false);
}

//单种类型数据的echarts图表展示
function showEchartView(title, seriesData, weblogicId) {
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
				            data : dates[weblogicId],// X轴的定义
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
		area: ['800px', '460px'],
		success:function(){
			thisechart = echarts.init(document.getElementById("thisId"),'shine');
			thisechart.setOption(option);
			idOfInterval = setInterval(function () {
				thisechart.setOption(option);
			}, 5000);
		},
		cancel:function(){
			clearInterval(idOfInterval);
		}	
	});

}

//重新获取连接
function reconnectWeblogic(id) {
	var bg = function() {
		$.post("weblogic/reconnect", {weblogicId:id}, function(data) {
			data = JSON.parse(data);
			if (data.returnCode == 0) {
				layer.msg("重新连接成功!", {icon:1, time:1500});
			} else {
				layer.alert(data.msg, {icon:5});
			}
		});
	};
	if (weblogicInfos[id]["isNormal"]) {
		layer.confirm("该Weblogic目前连接正常,是否确定需要重新连接并获取数据？", {icon:3, title:"提示"}, function(index){
			layer.close(index);
			bg();
		});
	} else {
		bg();
	}
}

//重新获取基本信息
function getBaseInfo(id) {
	$.post(
		"weblogic/getBaseInfo",
		{weblogicId:id},
		function(json) {
			json = JSON.parse(json);
			if (json.returnCode == 0) {
				layer.msg('重新获取成功,稍后查看!', {icon:1, time:1500});
			} else {
				layer.alert(json.msg, {icon:5});
			}
		}
	);
}


//删除所有信息
function delAll() {
	layer.confirm("确定需要删除当前所有的weblogic信息？", {icon:3, title:"警告"}, function(index) {
		layer.close(index);
		$.post("weblogic/delAll", function(json) {
			json = JSON.parse(json);
			if (json.returnCode == 0) {
				layer.msg('删除全部成功！', {icon:1, time:1500});
				table.clear();
				dates = {};
				weblogicInfos = {};
			} else {
				layer.alert(json.msg, {icon:5});
			}
		});
		
	});
}

//删除单条信息
function delWeblogic(obj, id) {
	layer.confirm("确认删除此weblogic的信息?", {icon:3, title:"警告"}, function(index){
		layer.close(index);
		$.post("weblogic/delWeblogic", {weblogicId:id}, function(json){
			json = JSON.parse(json);
			if (json.returnCode == 0) {
				layer.msg("删除成功!", {icon:1,time:1500});	
				table.row($(obj).parents('tr')).remove().draw();
				delete weblogicInfos[id];
				delete dates[id];
			} else {
				layer.alert(json.msg, {icon:5});
			}
		});
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

//保存单条或者多条信息
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
			layer.msg("请填写完整再提交!",{icon:2,time:1500});
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
	
	var loadIndex = layer.load(2, {shade:0.35});
	$.ajax({
		url:"weblogic/saveWeblogic",
		type:"post",
		data:sendData,
		dataType:"json",
		success:function(json){
			if (json.returnCode != 0) {
				layer.alert(json.msg,{icon:5});	
				layer.close(loadIndex);
				return;
			} else {	
				var tips = '添加成功!';
				if (saveMode == "batch" && json.count != 0) {
					tips = "本次共添加成功" + json.count + "个WeblogicServer!";
				} else if (saveMode == "batch" && json.count == 0) {
					layer.alert("输入的信息可能有误,未添加成功任何Server,请检查!",{icon:2});
					layer.close(loadIndex);
					return ;
				}
				layer.msg(tips, {icon:1, time:1500});
			}
			if (hasDataFlag == false) {
				hasDataFlag = true;
				getInfoData(0);
				//循环获取实时信息
	            var intervalId = setInterval(function(){
	            	getInfoData(intervalId);
	            }, 5000); 
			}
			layer.close(loadIndex);
			layer.closeAll('page');
		}
	});
}


//sessionStorage存取数据
function getDataFromStorage() {
	if(sessionStorage.getItem("weblogicInfos")) {
		weblogicInfos = JSON.parse(sessionStorage.getItem("weblogicInfos"));
		dates = JSON.parse(sessionStorage.getItem("dates"));
	}
}

function saveDataFromStorage() {
	sessionStorage.setItem("weblogicInfos", JSON.stringify(weblogicInfos));
	sessionStorage.setItem("dates", JSON.stringify(dates));
	return "请不要选择\"禁止弹出对话框\"选项!";
}