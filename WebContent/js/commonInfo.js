/**
 * 信息类型<br>
 * 目前 ：<br>
 * 0-主机信息  包括主机 基本硬件信息  以及账号密码
 * 1-常见主机命令信息  包括linux/window等 各种操作系统、软件的命令
 * 2-查询知识  包括各种好的知识文章等
 * 3-测试项目 做完的各种项目
 * 4-数据库信息   TNS、账号密码
 * 5-常用shell脚本
 * 6-常用数据库SQL命令
 * 7-各种常用表结构
 */
var typeInfo =
{
		"0":"主机信息",
		"1":"Linux命令",
		"2":"查询资料",
		"3":"测试项目",
		"4":"数据库信息",
		"5":"shell脚本",
		"6":"SQL命令",
<<<<<<< HEAD
		"7":"表结构",
		"8":"脚本大全"
=======
		"7":"表结构"
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
};

var table; //DT实例
var editInfoHtml;//编辑新增页面代码

var currLayerIndex;//当前开启的layer弹出层index
var currInfoType = "11";//当前知识类别

var currFilePath;
var uploadFlag = true;

$(function() {
	currLayerIndex = layer.load(2);
	var html = '';
	$.each(typeInfo, function(i, n) {
		html += '<button type="button" class="btn btn-default" data-type="' + i + '">' + n + '</button>'		
	});
	$(".btn-group").append(html);	
	table = $(".table").on( 'xhr.dt', function ( e, settings, json, xhr) {		
		if (json.returnCode != 0) {
			layer.alert(json.msg, {icon:5});
		}
	}).DataTable({
		"aaSorting": [[ 1, "desc" ]],//默认第几个排序
		"bStateSave": false,//状态保存
		"processing": false,   //显示处理状态
 		"serverSide": false,  //服务器处理
 		"autoWidth": false,   //自动宽度
 		"scrollX": true,
 		"lengthMenu": [[10, 15, 50], ['10', '15', '50']],  //显示数量设置
        "ajax":"commonInfo/list",
        "columnDefs":[ {"orderable":false,"aTargets":[0,3,4,7]}],
        "columns":[{
        	"data": null,
        	"render": function(data) {
        		return '<input type="checkbox" class="checkbox td-left" value="' + data.infoId + '">';
        	} 
		},{
        	"data":"infoId"
        },{
        	"data":"infoName"
        },{
        	"data":null,
        	"render":function(data) {  		
        		var html = '<button type="button" class="btn btn-danger btn-sm" onclick="showContent(' + data.infoId + ');">查看</button>';
        		return html;
        	}
        },{
        	"data":null,
        	"render":function(data) {
        		var html = '';        		
        		if (data.filePath != "" && data.filePath != null) {
        			html = '<a href="' + data.filePath + '" target="_blank">' 
        			+ (data.filePath).substring(((data.filePath).lastIndexOf("/")) + 1) 
        			+ '</a>';
        		}
        		return html;
        	}
        },{
        	"data":null,
        	"render":function(data) {
        		var tags = new Array();
        		tags = (data.infoTag).split(",");
        		var html = '';
        		$.each(tags, function(i,n) {
        			html += '<span class="label label-success">' + n + '</span>&nbsp;'
        		});
        		return html;
        	}
        },{
        	"data":null,
        	"render":function(data) {
        		return typeInfo[data.infoType];
        	}
        },{
        	"data":"joinTime"
        },{
        	"data":null,
        	"render":function(data) {
     		   var html = '&nbsp;&nbsp;<input type="button" class="btn btn-success btn-sm" onclick="editInfo(\'' + data.infoId + '\',\'' + data.infoName + '\');" value="更新" />'
     		   			+ '&nbsp;&nbsp;<input type="button" class="btn btn-danger btn-sm" onclick="delInfo(this,\'' + data.infoId + '\');" value="删除" />';
     		   return html;
     	   }
        }],
        "language":{
        	url: './js/zh_CN.json'
        },
        "initComplete":function(){ 
        	var html = '';
        	$.each(typeInfo, function (i, n){
        		html += '<option value="' + i + '">' + n + '</option>';
        	});
        	$("#infoType").append(html);
        	editInfoHtml = $("#edit-info-html").html();
        	$("#edit-info-html").html('');
        	
        	$(document).delegate(".current-tag","click",function(){
        		var that = this;
        		layer.confirm("确认删除此标签:[" + $(that).text() + "]？", function(index) {
        			layer.close(index);
        			$(that).remove();
        			infoTagCount();
        		});
        	});
        	
        	$(document).delegate("#infoTag ~ button", "click", function() {
        		var that = this;
        		var currTags = $(that).siblings("span").children("span");
        		if (currTags.length > 2) {
        			layer.msg("最多只能拥有3个标签", {icon:5, time:1500});
        			return;
        		}
        		layer.prompt({
        			  formType: 0,
        			  value: '相关标签',
        			  title: '设置标签',
        			  maxlength:10
        			  //area: ['800px', '350px'] //自定义文本域宽高
        			}, function(value, index, elem){
        				 $(that).siblings("span").append('<span class="label label-success current-tag">' + value + '</span>&nbsp;');
        				 infoTagCount();
        				 layer.close(index);
        			});
        	});       	
        	
        	$(".pull-right button:eq(0)").click(function() {       		
        		layer.open({
        			type:1,
        			title:"增加新的知识",
        			content:editInfoHtml,
        			area: ['680px', '720px'],
        			success:function() {
        				uploadFlag = true
        				currFilePath = '';
        				$("#infoId").parents(".form-group").hide(); 
        				$("#filePathLink").hide();
        				$("#changeUpload").hide();
        				$("#infoType").val((currInfoType == "11") ? "0" : currInfoType);       				
        				
        			}
        		});
        	});
        	
        	$(".pull-right button:eq(1)").click(function() {   
        		var checkboxList = $("td > input:checked");
        		if (checkboxList.length == 0) {
        			return false;
        		}
        		 		
        		layer.confirm("确定删除选中的" + checkboxList.length + "条记录？", {title:"警告"}, function(index) {
        			layer.close(index);
        			currLayerIndex = layer.load(2);
        			var delIds = '';   
        			$.each(checkboxList, function(i, n) {
        				delIds += $(n).attr('value') + ",";
        			});
        			
        			$.post("commonInfo/batchDel", {ids:delIds}, function(json) {
        				json = JSON.parse(json);
        				layer.close(currLayerIndex);
        				if (json.returnCode == 0) {
        					layer.msg("删除成功", {icon:1, time:1500});
        				} else {
        					layer.alert("成功删除" + json.delCount + "条记录,有" + (checkboxList.length - json.delCount) + "记录删除失败!", {icon:5});
        				}
        				table.ajax.reload(null,false);
        			})
        		});
        	});
<<<<<<< HEAD
        	//刷新
=======
        	
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
        	$(".pull-right button:eq(2)").click(function() {   
        		currLayerIndex = layer.load(2);
        		table.ajax.reload(function() {
        			layer.close(currLayerIndex); 
        		}, false);
        	});
<<<<<<< HEAD
        	//导出
        	$(".pull-right button:eq(3)").click(function() {   
        		exportInfos();
        	});
=======
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5
        	
        	$(".pull-left button").click(function() {
        		$(this).addClass("btn-danger").siblings("button").removeClass("btn-danger").addClass("btn-default");
        		var dataType = $(this).attr("data-type");
        		currLayerIndex = layer.load(2);
        		table.ajax.url("commonInfo/list?infoType=" + dataType).load(function() {
        			layer.close(currLayerIndex);
        		}, false);
        		currInfoType = dataType;
        	});        	
        	
        	$("input[type='checkbox']:eq(0)").click(function() {
        		if ($(this).is(":checked")) {
        			$("input[type='checkbox']").prop("checked",true);   
        		} else {
        			$("input[type='checkbox']").prop("checked",false);  
        		}
        	});
        	
        	//ctrl+f弹出搜索框
        	 $(window).keydown (function(e) {
                var keyCode = e.keyCode || e.which || e.charCode;
                var ctrlKey = e.ctrlKey || e.metaKey;
                if(ctrlKey && keyCode == 70) {
                	layer.prompt({
          			  formType: 0,
          			  title: '请输入搜索条件',
          			  maxlength:80
          			  //area: ['800px', '350px'] //自定义文本域宽高
          			}, function(value, index, elem){
          				table.search(value).draw();
          				layer.close(index);
          			});
                	e.preventDefault();
                }               
            });
        	
        	//esc关闭所有弹出层
        	 $(window).keydown (function(e) {
        		 var keycode = event.which;
        		 if(keycode == 27){
        			 layer.closeAll('page');
        			 e.preventDefault();
        		 } 
        	 });
        	
        	layer.close(currLayerIndex);       	
        }
	});
	
});

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
<<<<<<< HEAD
/**
 * 导出信息
 */
function exportInfos() {
	layer.msg('功能未完善,请关注最新版本!', {icon:0, time:1500});
}
=======
>>>>>>> fbf7465acb04f4b6e7c455fc2abe7d0dc782cfb5


function showContent(infoId) {
	$.get("commonInfo/get", {infoId:infoId}, function(json) {
		json = JSON.parse(json);
		if (json.returnCode == 0) {
			var showContent = '<textarea name="" class="form-control show-content" rows="25">' + json.info.infoDetail + '</textarea>';
			var options = {
					type:1,
					shade:0,
					title:typeInfo[json.info.infoType] + "-" + json.info.infoName,
					content:showContent,
					area: ['680px', '580px']			
				};			
			layer.open(options);
		} else {
			layer.alert(json.msg, {icon:5});
		}
	});	
}

function infoTagCount() {
	var tagSpans = $("#infoTag").siblings("span").children("span");
	var infoTag = '';
	if (tagSpans.length > 0) {
		$.each(tagSpans, function(i, n) {
			infoTag += $(n).text();
			
			if (i != (tagSpans.length -1)) {
				infoTag += ",";
			}
		});
	}
	
	$("#infoTag").val(infoTag);
}


function saveInfo() {
	
	if ($("#infoName").val() == null || $("#infoName").val() == "") {
		layer.msg("标题为必填内容!", {icon:5, time:1500});
		return false;
	}
	
	var nextSave = function() {
		var sendData = $(".info-edit-form").serialize();
		
		$.post("commonInfo/edit", sendData, function(json) {
			json = JSON.parse(json);
			if (json.returnCode == 0) {
				table.ajax.reload(null, false);
				layer.closeAll('page');
				layer.msg("操作成功!", {icon:1, time:1500});
			} else {
				layer.alert(json.msg, {icon:5});
			}
		});
	};
	
	if (uploadFlag == true && $("#uploadFile").val() != null && $("#uploadFile").val() != '') {
		
		var formData = new FormData();
		formData.append('file', $('#uploadFile')[0].files[0]);
		$.ajax({
		    url: 'upload',
		    type: 'POST',
		    cache: false,
		    data: formData,
		    processData: false,
		    contentType: false,
		    success:function(json) {
		    	json = JSON.parse(json);
		    	if (json.returnCode == 0) {
		    		$("#filePath").val(json.filePath);
		    		nextSave();
		    	} else {
		    		layer.alert(json.msg, {icon:5});
		    	}
		    }
		})	
	} else {
		nextSave();
	}

	
	
	
}


function delInfo(obj, id) {
	layer.confirm("确认删除该条信息记录吗？", {title:"警告"}, function(index) {
		layer.close(index);
		$.post("commonInfo/del", {infoId:id}, function(json) {
			json = JSON.parse(json);
			if (json.returnCode == 0) {
				table.row($(obj).parents('tr')).remove().draw();
				layer.msg("删除成功!", {icon:1, time:1500});
			} else {
				layer.alert(json.msg, {icon:5});
			}
		});
	});
}

function chanceUpload() {
	var value = $("#changeUpload").val();
	
	if (value == "重新上传") {
		uploadFlag = true;
		$("#changeUpload").val("取消重新上传");
		$("#filePathLink").hide();
		$("#uploadFile").show();
		$("#filePath").val('');
	} else {
		uploadFlag = false;
		$("#changeUpload").val("重新上传");
		$("#filePathLink").show();
		$("#uploadFile").hide();
		$("#filePath").val(currFilePath);
		$("#uploadFile").val('');
	}
	
}

function editInfo(id, name) {
	var index = layer.open({
		type:1,
		title:"编辑-" + name,
		content:editInfoHtml,
		area: ['680px', '760px'],
		success:function() {
				$.post("commonInfo/get", {infoId:id}, function(json) {
					json = JSON.parse(json);
					if (json.returnCode == 0) {
						var info = json.info;
						$("#infoId").val(info.infoId);
						$("#infoName").val(info.infoName);
						$("#infoDetail").val(info.infoDetail);
						$("#infoTag").val(info.infoTag);
						$("#infoType").val(info.infoType);
						$("#joinUserName").val(info.joinUserName);
						var that = $("#infoTag").siblings("span");
						$.each((info.infoTag).split(","), function(i, n) {
							that.append('<span class="label label-success current-tag">' + n + '</span>&nbsp;');
						});								
						
						if (info.filePath != null && info.filePath != "") {
							$("#uploadFile").hide();
							$("#filePathLink").attr("href",info.filePath);
							$("#filePathLink").text(info.filePath);
							$("#filePath").val(info.filePath);
							currFilePath = info.filePath;
						} else {
							$("#filePathLink").hide();
	        				$("#changeUpload").hide();					
						}											
					} else {
						layer.alert(json.msg, {icon:5});
					}
				});
			}
		});
}

/**********************************************************************************************************/
/*设置cookie*/
function setCookie(name, value, Days){
	if(Days == null || Days == ''){
		Days = 300;
	}
	var exp  = new Date();
	exp.setTime(exp.getTime() + Days*24*60*60*1000);
	document.cookie = name + "="+ escape (value) + "; path=/;expires=" + exp.toGMTString();
}
/*获取cookie*/
function getCookie(name) {
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return unescape(arr[2]); 
    else 
        return null; 
}

//清除cookie  
function clearCookie(name) {  
    setCookie(name, "", -1);  
} 