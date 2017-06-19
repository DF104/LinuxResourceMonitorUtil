<%@page 
import="java.util.*,java.text.*,entiy.*,util.*"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE>
<html>
	<head>  
	    <title>linux内存CPU实时监控</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="lib/bootstrap/css/bootstrap.min.css"/>
		<link rel="stylesheet" href="css/index.css"/>
	</head>  
	<body>	
	<div class="container">	
	<button type="button" class="btn btn-success">添加监控主机</button>&nbsp;&nbsp;<button type="button" class="btn btn-success">隐藏全部图表</button>&nbsp;&nbsp;<button type="button" class="btn btn-success">隐藏列表</button>
	&nbsp;&nbsp;<button type="button" class="btn btn-danger">清空数据</button>&nbsp;&nbsp;<button type="button" class="btn btn-info">历史记录</button>&nbsp;&nbsp;<button type="button" class="btn btn-primary">命令执行</button>&nbsp;&nbsp;<button type="button" class="btn btn-danger">重新计时</button>&nbsp;&nbsp;<button type="button" class="btn btn-info" onclick="javascript:window.open('weblogic.html');">监控weblogic</button>&nbsp;&nbsp;<strong>CPU报警阀值&nbsp;</strong>
		<select id="cpu-max">
			<option value="5">5</option>
	        <option value="10">10</option>
	        <option value="20">20</option>
	        <option value="30">30</option>
	        <option value="40">40</option>
	        <option value="50" selected>50</option>
	        <option value="60">60</option>
	        <option value="70">70</option>
	        <option value="80">80</option>
	        <option value="90">90</option>
	        <option value="100">100</option>
  		</select>
		<br><br>
	<div class="table-responsive">
		<table class="table table-bordered table-hover table-condensed">
   			<thead>
  		 		<tr>
			      <th>#</th>
			      <th>IP</th>
			      <th>类型</th>
			      <th>cpu核数</th>
			      <th>内存</th>
			      <th>用户名</th>
			      <th>备注</th>
			      <th>连接状态</th>	
			      <th>cpu负荷状态</th>		      
			      <th>操作</th>
   				</tr>
   			</thead>
   			<tbody>	  
   			</tbody>
		</table>
	</div>
	<div id="echarts">		 			 	
				 <c:forEach var="linux" items="${linuxInfos}" varStatus="s">
				 	<div class="row">	
				 	<div id="${linux.key}" class="hidden-xs col-sm-8">	
				 	</div>	
				 	<div class="hidden-xs col-sm-4">
				 		<div class="panel panel-primary other-div" server-id="${linux.key}">
							<div class="panel-heading text-center">
							    其他资源实时监控
							</div>
							<div class="panel-body">
							    <div class="row">
							    	<div class="col-sm-7">							    		
							    		<div data-category="tcp">TCP端口：<br>
							    			<div class="row show-echarts-view">
							    				<span class="text-danger col-sm-6">ESTABLISHED</span>
							    				<span class="col-sm-6" data-kind="ESTABLISHED"></span>		
							    			</div>
							    			<div class="row show-echarts-view">
							    				<span class="text-danger col-sm-6">LISTEN</span>
							    				<span class="col-sm-6" data-kind="LISTEN"></span>		
							    			</div>
							    			<div class="row show-echarts-view">
							    				<span class="text-danger col-sm-6">CLOSE_WAIT</span>
							    				<span class="col-sm-6" data-kind="CLOSE_WAIT"></span>		
							    			</div>		
							    			<div class="row show-echarts-view">
							    				<span class="text-danger col-sm-6">TIME_WAIT</span>
							    				<span class="col-sm-6" data-kind="TIME_WAIT"></span>		
							    			</div>	
							    		</div><br>
							    		<div data-category="network">网络带宽：<br>
							    			<div class="row show-echarts-view">
							    				<span class="text-danger col-sm-7">出网</span>
							    				<span class="col-sm-5" data-kind="tx"></span>	
							    			</div>
							    			<div class="row show-echarts-view">
							    				<span class="text-danger col-sm-7">入网</span>
							    				<span class="col-sm-5" data-kind="rx"></span>	
							    			</div>
							    		</div>
							    	</div>
							    	<div class="col-sm-5">
							    		<div data-category="disk">磁盘使用：<br>	
							    			<div class="row show-echarts-view">
							    				<span class="text-danger col-sm-8">根目录：</span>
							    				<span class="col-sm-4" data-kind="rootDisk"></span>	
							    			</div>
							    			<div class="row show-echarts-view">
							    				<span class="text-danger col-sm-8">用户目录：</span>
							    				<span class="col-sm-4" data-kind="userDisk"></span>	
							    			</div>					    			
							    		</div><br>
							    		<div data-category="io">IO等待：<br>							    			
							    		</div><br>
							    		<div data-category="weblogic">weblogic实例数：<br>
							    			<div class="row show-echarts-view">
							    				<span class="text-danger col-sm-6">当前：</span>
							    				<span class="col-sm-6" data-kind="weblogicServerCount"></span>	
							    			</div>					    			
							    		</div><br>
							    	</div>
							    </div>
							</div>
						</div>	
				 	</div>	
				 	</div>	
				 	<br>	 			
				</c:forEach>						
	</div>	
	<div id="saveSeverHtml">
		<form class="form-horizontal save-form">
			<div id="singleSave">
	            <div class="form-group">
	                <label for="username" class="col-sm-3 control-label">主机IP</label>
	                <div class="col-sm-9">
	                    <input type="text" class="form-control" id="host" name="host" placeholder="不添加端口默认为22"/>
	                </div>
	            </div>
	            <div class="form-group">
	                <label for="username" class="col-sm-3 control-label">登录用户名</label>
	                <div class="col-sm-9">
	                    <input type="text" name="username" id="username" class="form-control"/>
	                </div>
	            </div>
				<div class="form-group">
	                <label for="password" class="col-sm-3 control-label">登录密码</label>
	                <div class="col-sm-9">
	                    <input type="text" name="password" id="password" class="form-control"/>
	                </div>
	            </div>
	            <div class="form-group">
	                <label for="mark" class="col-sm-3 control-label">备注</label>
	                <div class="col-sm-9">
	                    <input type="text" name="mark" id="mark" class="form-control" placeholder="如果通过端口转发监控，请填写备注"/>
	                </div>
	            </div>
	            <div class="form-group">
	                <div class="col-sm-offset-3 col-sm-9">
	                    <button type="button" class="btn btn-danger" onclick="saveServer();">保存信息</button>&nbsp;<button onclick="layer.closeAll('page');" type="button" class="btn btn-default">取消</button>
	                    &nbsp;<button type="button" class="btn btn-primary" onclick="batchSave();">批量添加</button>
	                </div>
	            </div>
	    	</div>
	    	<div id="batchSave">
	    		<div class="form-group">
	                <label for="password" class="col-sm-3 control-label">主机列表：</label>
	                <div class="col-sm-9">
	                    <textarea id="serverList" class="form-control" cols="30" rows="10" style="height:180px;" 
	                    	placeholder="主机ip[:port],登录名,密码[,备注]"></textarea>
	                </div>
	            </div>
	    		<div class="form-group">	    			
	                <div class="col-sm-offset-3 col-sm-9">
	                    <button type="button" class="btn btn-danger" onclick="saveServer();">保存信息</button>&nbsp;<button onclick="layer.closeAll('page');" type="button" class="btn btn-default">取消</button>
	                    &nbsp;<button type="button" class="btn btn-primary" onclick="singleSave();">单条添加</button>
	                </div>
	            </div>
	    	</div>
	    </form>
	</div>
</div>	
<input type="hidden" id="changeFlag" value="false"/>		
<script src="js/jquery.js"></script>
<script src="lib/bootstrap/js/bootstrap.js"></script>
<script src="lib/layer/layer.js"></script>
<script src="lib/Echarts/echarts.min.js"></script>
<script src="lib/Echarts/shine.js"></script>
<script src="js/index.js"></script>		 	 
</body>
</html>
