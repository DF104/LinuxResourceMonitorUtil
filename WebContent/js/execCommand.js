var count = 1;
var isExecing = false;


$(function() {	
	$.ajax({
		url:"linux/getServerList",
		type:"get",
		dataType:"json",
		async: false,
		success:function(data) {	
			var serverListHtml = '';
			$.each(data.list, function(i,n) {
				serverListHtml += '<option value="' + i + '">';
				if (n.mark != "" && n.mark != null) {
					serverListHtml += n.mark;
				} else {
					serverListHtml += n.host + ':' + n.port;
				}
				serverListHtml += '[' + n.uanme + ']' + '</option>'							
			});
			$("#select-server").html(serverListHtml);
		}, 
		error:function(){
			layer.alert("无法加载数据,请检查网络连接!", {icon:5, offset:'260px'});
		}
	});
	
	$("#command").focus();
	
	$("#exec-command").on("click", function() {
		execCommand();
	});
	
	$("#stop-exec").on("click", function() {
		stopExecCommand();
	});
	
	$(document).keyup(function (event) {
		 var keycode = event.which;
		 if(keycode==13){
			 execCommand();
		 }		 
	 });
	
});

//中断执行
function stopExecCommand() {
	
	if (isExecing == false) {
		return false;
	}
	$.post("linux/stopExecCommand",function (json) {
		json = JSON.parse(json);
		if (json.returnCode == 0) {
			layer.msg("中断命令成功,请稍后查看!", {icon:1,time:1500, offset:'260px'});
		} else {
			layer.msg("中断命令失败,请重试!", {icon:4,time:1500, offset:'260px'});
		}
	});	
}


//执行命令，等待返回
function execCommand() {
	var command = $("#command").val();
	var infoId = $("#select-server").val();
	if (command == "") {
		layer.alert("请先输入命令,再点击执行按钮或者回车执行!", {title:"提示", icon:5, offset:'260px'}, function (index) {
			layer.close(index);
			$("#command").focus();			
		});		
		return false;				
	}
	
	if (isExecing == true) {
		layer.msg("请等待上一条命令执行完成!", {icon:5, offset:'260px', time:1000});
		return false;
	}
	
	$("#return-text").val("");
	
	$("#tip").text("正在请求执行命令,请稍等/");
	isExecing = true;
	execTip();
	var intervalId = setInterval(execTip, 1000);
	$.post("linux/execCommand", {command:command, infoId:infoId}, function (json) {
		clearInterval(intervalId);
		json = JSON.parse(json);
		
		if (json.msg != "") {
			$("#return-text").val(json.msg);
		}
		
		if (json.returnCode == 0) {			
			$("#tip").text("命令执行成功!");
		} else {
			$("#tip").text("命令执行失败或者无返回!");
		}		
		count = 1;
		isExecing = false;
	});	
}

//等待
function execTip() {	
	if (count == 10) {
		$("#tip").text("正在请求执行命令,请稍等/");
		count = 1;
		return;
	}
	var tips = $("#tip").text();
	if (count % 2 == 0) {
		$("#tip").text(tips.substring(0, (tips.length -1)) + "." + "/");
	} else {
		$("#tip").text(tips.substring(0, (tips.length -1)) + "." + "\\");
	}	
	count++;
}