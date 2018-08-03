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
    <title>批量导出</title>
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
          if(dbName.length > 4) {
              alert("只能选择4个库，您选择了" + dbName.length);
              obj.disabled = false;
              return false;
          }
          // 发送ajax请求
          var parameter = "action=batch";
          for(i = 0; i < dbName.length; i ++) {
              parameter += "&dbName=" + dbName[i].value;
          }

          $.get("home.jsp",parameter,function(data){
              alert("服务器已接受到导出任务.预计完成时间11 h");
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
          <h4 class="list-group-item-heading">欢迎使用Ebay 批量数据导出功能</h4>
          <p class="list-group-item-text">该网页提供方便快捷的数据导出,选择需要导出的数据库即可，一次最多只能选择4个库，这4个库导出完毕后，才能继续选择</p>
      </a>
  </div>
  <div class="container-fluid">
      <div class="row-fluid">
          <div class="span2">
          </div>
          <div class="span6">
              <%--<form>--%>
                  <fieldset>
                      <legend>输入项</legend>
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
                             for(DataSource source : dataSources) {%>
                      <input type="checkbox" name="dbName" value="<%=source.getFullDbName()%>"/> <%=source.getDbName() + "[" + source.getCount() + "]" %>
                             <%}%>
                      </div>
                             <%}%>
                      <button class="button button-primary button-rounded button-small" onclick="outData(this)">导出数据</button>
                  </fieldset>
              <%--</form>--%>
                  <div class="alert alert-success" role="alert">
                      系统默认2w个数据一个csv文件，并会每50个csv文件压缩成一个zip文件供下载使用
                  </div>
              <span class="help-block">导出记录</span>
              <table class="table">
                  <thead>
                  <tr>
                      <th>
                          文件地址
                      </th>
                      <th>
                          导出时间
                      </th>
                      <th>
                          导出数量
                      </th>
                      <th>
                          操作
                      </th>
                  </tr>
                  </thead>
                  <tbody>
                  <%--<%--%>
                      <%--List<File> files = Utils.allFiles(ApplicationCache.DEFAULT_CSV_FILE_PATH);--%>
                      <%--for(File file : files) {--%>
                          <%--String fileName = file.getName();--%>
                          <%--String downloadUrl = "export/" + file.getName();--%>
                          <%--String date = new Date(file.lastModified()).toLocaleString();--%>
                          <%--String count = fileName.substring(fileName.lastIndexOf("-") + 1,fileName.indexOf("."));--%>
                          <%--double length = file.length() / 1024;--%>
                  <%--%>--%>
                        <%--<tr>--%>
                            <%--<td>--%>
                                <%--<a href="<%=downloadUrl%>"><%=fileName + "(" + length + "KB)"%></a>--%>
                            <%--</td>--%>
                            <%--<td><%=date%></td>--%>
                            <%--<td><%=count%></td>--%>
                            <%--<td>--%>
                                <%--<a href="home.jsp?action=delete&n=<%=fileName%>">删除</a>--%>
                            <%--</td>--%>
                        <%--</tr>--%>

                      <%--<%}%>--%>
                  </tbody>
              </table>
          </div>
          <div class="span4">
          </div>
      </div>
  </div>

  </body>
</html>
