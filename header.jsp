<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script src="./js/header.js"></script>
<header>
<div id="header">
<div id="header-title">
Oregon
</div>
<div id="header-menu">
<ul>
<s:form id="form" name="form">
	<s:if test='#session.mCategoryDTOList!=null && #session.mCategoryDTOList.size()>0'>
	<li><s:select name="categoryId" list="#session.mCategoryDTOList" listValue="categoryName" listKey="categoryId" class="cs-div" id="categoryId"/></li>
	</s:if>
	<li><s:textfield name="keywords" class="txt-keywords" placeholder="検索ワード" /></li>
	<li><s:submit value="商品検索" class="submit_btn" onclick="goSearchItemAction();"/><li>
	<s:if test="#session.logined==1">
	<li><s:submit value="ログアウト" class="submit_btn" onclick="goLogoutAction();"/></li>
	</s:if>
	<s:else>
		<li><s:submit value="ログイン" class="submit_btn" onclick="goGoLoginAction();"/></li>
	</s:else>
	<li><s:submit value="カート" class="submit_btn" onclick="goCartAction();"/><li>
	<li><s:submit value="商品一覧" class="submit_btn" onclick="goProductListAction();"/></li>
	<s:if test="#session.logined==1">
		<li><s:submit value="マイページ" class="submit_btn" onclick="goMyPageAction();"/></li>
	</s:if>
</s:form>
</ul>
</div>
</div>
</header>