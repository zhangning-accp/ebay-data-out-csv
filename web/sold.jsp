<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.io.File" %>
<%@ page import="util.Utils" %>
<%@ page import="util.ApplicationCache" %>
<%@ page import="java.util.Date" %>
<%@ page import="dao.DataSource" %>
<%--
  Created by IntelliJ IDEA.
  User: zn
  Date: 2018/6/28
  Time: 13:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>销量数据批量导出</title>
      <link href="https://cdn.bootcss.com/bootstrap/4.1.1/css/bootstrap.css" rel="stylesheet">
      <link href="css/button.css" rel="stylesheet">
      <link href="http://www.bootcss.com/p/layoutit/css/layoutit.css" rel="stylesheet">
      <script src="https://cdn.bootcss.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
      <script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.js"></script>

      <style type="text/css">
          .demo{
              padding: 2em 0;
              background: linear-gradient(to right, #2c3b4e, #4a688a, #2c3b4e);
          }
          .progress{
              height: 25px;
              background: #262626;
              padding: 5px;
              overflow: visible;
              border-radius: 20px;
              border-top: 1px solid #000;
              border-bottom: 1px solid #7992a8;
              margin-top: 50px;
          }
          .progress .progress-bar{
              border-radius: 20px;
              position: relative;
              animation: animate-positive 2s;
          }
          .progress .progress-value{
              display: block;
              padding: 3px 7px;
              font-size: 13px;
              color: #fff;
              border-radius: 4px;
              background: #191919;
              border: 1px solid #000;
              position: absolute;
              top: -40px;
              right: -10px;
          }
          .progress .progress-value:after{
              content: "";
              border-top: 10px solid #191919;
              border-left: 10px solid transparent;
              border-right: 10px solid transparent;
              position: absolute;
              bottom: -6px;
              left: 26%;
          }
          .progress-bar.active{
              animation: reverse progress-bar-stripes 0.40s linear infinite, animate-positive 2s;
          }
          @-webkit-keyframes animate-positive{
              0% { width: 0; }
          }
          @keyframes animate-positive{
              0% { width: 0; }
          }
      </style>
  </head>
  <body>
  <script type="application/javascript">
      function outData(obj) {
          obj.disabled = true;
          var dbName = $("input[name='dbName']:checked");
          if(dbName.length < 1) {
              alert("请选择一个库!");
              obj.disabled = false;
              return false;
          }
//          if(dbName.length > 4) {
//              alert("只能选择4个库，您选择了" + dbName.length);
//              obj.disabled = false;
//              return false;
//          }
          // 发送ajax请求
          var parameter = "action=sold";
          for(i = 0; i < dbName.length; i ++) {
              if(dbName[i].disabled == false && dbName[i].checked == true) {
                  parameter += "&dbName=" + dbName[i].value;
              }
          }
          $.get("home.jsp",parameter,function(data){
              alert("服务器已接受导出任务...");
//              var file = data;
//              var fileName = data.substring(file.indexOf("/") + 1);
//              var nodeA = $("#file")
//              nodeA.attr("href",file);
//              nodeA.html(fileName);
//              progress.children("div:first-child").css("width","100%");
//              progress.children("div:first-child").children("div:first-child").html("100%");
//              obj.disabled = false;
          });
      }

      /**
        *判断一个对象是否是空
        * @param obj
        */
      function isEmpty(obj) {
          if(typeof (obj) == "string") {
              return obj.length < 1 | obj.trim() == "" | obj.trim() === "";
          }
          return obj == null | obj === null | obj == undefined | obj === undefined;
      }

  </script>
  <div class="list-group">
      <a href="#" class="list-group-item active">
          <h4 class="list-group-item-heading">欢迎使用Ebay 销量数据批量导出功能</h4>
          <p class="list-group-item-text">该网页提供方便快捷的数据导出</p>
      </a>
  </div>
  <div class="container-fluid">
      <div class="row-fluid">
          <div class="span2">
          </div>
          <div class="span6">
              <%--<form>--%>
                  <fieldset>
                      <legend>选择对应的数据库导出数据</legend>
                      <%
                          Map<String, List<DataSource>> dataSourceString = (Map)application.getAttribute("dataSource");
                          Iterator<String> iterator = dataSourceString.keySet().iterator();
                          while(iterator.hasNext()) {
                              String key = iterator.next();%>
                      <hr>
                      <span class="help-block">数据库服务器：<%=key%></span>

                      <div style="border:1px;">
                         <%
                            List<DataSource> dataSources = dataSourceString.get(key);
                             for(DataSource source : dataSources) {
                                 String viewDbName = source.getDbName() + "[" + source.getCount() + "]";
                                 if(source.isExport()) {
                                     viewDbName += "(已导出)";
                                     out.write("<input type='checkbox' name='dbName' value='" + source.getFullDbName() + "'/>" +
                                                     "<span style='color:darkorange;'>" + viewDbName + "</span>");
                                 } 
								 if(source.isCurrent()) {
                                     viewDbName += "(正在导出)";
                                     out.write("<input type='checkbox' name='dbName' checked='checked' disabled='true' value='" + source.getFullDbName() + "'/>" +
                                             "<span style='color:limegreen;'>" + viewDbName + "</span>");
                                 }
								 if(!source.isExport() && !source.isCurrent()) {
                                     out.write("<input type='checkbox' name='dbName' value='" + source.getFullDbName() + "'/>" + viewDbName);
                                 }

                         }%>
                      </div>
                             <%}%>
                      <button class="button button-primary button-rounded button-small" onclick="outData(this)">导出数据</button>
                  </fieldset>
              <%--</form>--%>
                  <div class="alert alert-success" role="alert" style="color:darkorange;">
                      系统将导出单品销量前10w和店铺销量前10w的数据，并合并成一个zip文件供下载使用
                  </div>
              <span class="help-block">导出记录</span>
              <table class="table">
                  <thead>
                  <tr>
                      <th>
                          数据库
                      </th>
                      <th>
                          文件名(KB)
                      </th>
                      <th>
                          导出时间
                      </th>
                      <th>
                          操作
                      </th>
                  </tr>
                  </thead>
                  <tbody>
                  <%
                      Map<String,List<File>> fileMap = Utils.getZipFiles(ApplicationCache.DEFAULT_SOLD_CSV_FILE_PATH);
                      Iterator<String> iteratorkeys = fileMap.keySet().iterator();
                      while(iteratorkeys.hasNext()) {
                          String key = iteratorkeys.next();
                          //String server = key;
                          List<File> files = fileMap.get(key);
                          for(int i = 0; i < files.size(); i ++) {
                              File file = files.get(i);
                              String fileName = file.getName();
                              String downloadUrl = file.getAbsolutePath().replace(ApplicationCache.REAL_PATH,"");
                              String date = new Date(file.lastModified()).toLocaleString();
                              double length = file.length() / 1024;
                              out.write("<tr>");
                              if(i == 0) {
                                  out.write("<td>" + key + "</td>");
                              } else {
                                  out.write("<td></td>");
                              }
                              out.write("<td><a href=" + downloadUrl + ">" + fileName + "(" + length + "KB)</a></td>");
                              out.write("<td>" + date + "</td>");
                              out.write("<td><a href=home.jsp?action=ds&n=" + fileName + "&dn=" + key + ">删除</a></td>");
                              out.write("</tr>");
                        }
                      }
                  %>

                  </tbody>
              </table>
          </div>
          <div class="span4">
          </div>
      </div>
  </div>

  </body>
</html>
