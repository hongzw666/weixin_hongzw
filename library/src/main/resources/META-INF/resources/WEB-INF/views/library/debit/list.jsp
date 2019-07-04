<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- 引入JSTL里面的core标签库，它里面包含了forEach、if、choose等标签，代替JSP的脚本和表达式。 --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>借阅列表</title>

<link href="/hongzw/library/css/main.css" rel="stylesheet" />
</head>
<body>
	<div>
		<a style="text-decoration:none ; color: black" href="/hongzw/library"><button>返回</button></a>
	</div>
	<c:forEach items="${debitList.books }" var="book">
		<div>
			${book.name }
			<a style="text-decoration:none ; color: black" href="/hongzw/library/debit/remove/${book.id }"><button style="background-color: red">删除</button></a>
		</div>
	</c:forEach>
</body>
</html>