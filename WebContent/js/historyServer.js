var serverType; //类型 1为weblogic 0为linux
var table; //DT实例

$(function() {
	serverType = GetQueryString("type");
	table = $(".table").on( 'xhr.dt', function ( e, settings, json, xhr) {
		if (xhr.status != 200) {
			layer.alert("服务器连接错误,请检查!", {icon:5});
	        console.log("ajax获取数据不正常,请检查服务器状态或者网络连接!" + "\nAjax当前状态：" + xhr.status);
		}		
	}).DataTable({
		"aaSorting": [[ 7, "desc" ]],//默认第几个排序
		"bStateSave": false,//状态保存
		"processing": false,   //显示处理状态
 		"serverSide": false,  //服务器处理
 		"autoWidth": false,   //自动宽度
 		"scrollX": true,
 		"lengthMenu": [[10, 15, 50], ['10', '15', '50']],  //显示数量设置
        "ajax":"linux/history?type=" + serverType,
        "columnDefs":[ {"orderable":false,"aTargets":[0,6,8]}],
        "columns":[{
        	"data": null,
        	"render": function(data) {
        		return '<input type="checkbox" class="checkbox td-left" value="' + data.id + '">';
        	} 
		},{
        	"data":"id"
        },{
        	"data":"host"
        },{
        	"data":"port"
        },{
        	"data":"username"
        },{
        	"data":"password"
        },{
        	"data":"mark"
        },{
        	"data":"time"
        },{
        	"data":null,
        	"render":function(data) {
     		   var html = '<button type="button" class="btn btn-warning btn-sm" onclick="useServer(' + data.id + ');">启用</button>'
     		   			+ '&nbsp;&nbsp;<button type="button" class="btn btn-success btn-sm" onclick="editServer(' + data.id + ');">更新</button>'
     		   			+ '&nbsp;&nbsp;<button type="button" class="btn btn-danger btn-sm" onclick="delServer(this,' + data.id + ');">删除</button>';
     		   return html;
     	   }
        }],
        "language":{
        	url: './js/zh_CN.json'
        },
        "initComplete":function(){  
        	$("input[type='checkbox']:eq(0)").click(function() {
        		if ($(this).is(":checked")) {
        			$("input[type='checkbox']").prop("checked",true);   
        		} else {
        			$("input[type='checkbox']").prop("checked",false);  
        		}
        	});
        	
        	$(".tool-bar > button:eq(0)").on("click", function() {
        		var checkboxList = $("td > input:checked");
        		if (checkboxList.length > 0) {
        			var sendIds = '';
        			$.each(checkboxList, function(i, n) {
        				sendIds += $(n).attr('value') + ",";
        			});
        			useServer(sendIds, checkboxList.length);
        		}
        	});
        }
	});
	
});


/**
 * 启用此历史记录
 * @param serverId
 */
function useServer(serverId, count) {
	var tip = "确定启用此条历史记录？";
	if (count != null) {
		tip = "确定启用选中的" + count + "条记录？";
	}
	layer.confirm(tip, {icon:3, title:"提示"}, function(index) {
		layer.close(index);
		var loadIndex = layer.load(2, {shade:0.35});
		var useUrl;
		if (serverType == "0") {
			useUrl = "linux/useHistory";
		}
		
		if (serverType == "1") {
			useUrl = "weblogic/useHistory";
		}
		
		$.post(useUrl, {id:serverId}, function(json) {
			json = JSON.parse(json);
			if (json.returnCode == 0 && json.count > 0) {
				if (count != null && count > json.count) {
					layer.alert("共有" + json.count + "台服务器连接成功," + (count - json.count) + "台连接失败,请检查!", {icon:3});	
				} else {
					layer.msg("连接成功!请返回查看!", {icon:1, time:1500});	
				}				
				parent.$("#changeFlag").val("true");
			} else {
				layer.alert("未能成功连接到任何服务器,请重试!", {icon:5});
			}			
			layer.close(loadIndex);
		});
		
	});
}

/**
 * 更新此历史记录
 * @param serverId
 */
function editServer(serverId) {
	layer.alert("请新增服务器已覆盖错误的历史记录!", {icon:3});
}

/**
 * 删除此记录
 * @param obj
 * @param serverId
 */
function delServer(obj, serverId) {
	layer.confirm("确认删除此信息?", {icon:3, title:"警告"}, function(index){
		layer.close(index);
		$.post("linux/delHistory", {id:serverId}, function(json){
			json = JSON.parse(json);
			if (json.returnCode == 0) {
				table.row($(obj).parents('tr')).remove().draw();
				layer.msg("删除成功!", {icon:1,time:1500});								
			} else {
				layer.alert(json.msg, {icon:5});
			}
		});
	});
}


/**
 * 获取地址栏参数
 * @param name
 * @returns
 */
function GetQueryString(name) {
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r!=null)return decodeURIComponent(r[2]); return null;
}
