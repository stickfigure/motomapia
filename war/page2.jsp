<!DOCTYPE html>

<html>
	<head>
	</head>

	<body>
		<p>A boring page</p>
		<script type="text/javascript" src="/js/require.js"></script>
		<script type="text/javascript">
			<%@ include file="config.require.js" %>
		</script>
		<script type="text/javascript">
			require(['app/page/page2']);
		</script>
	</body>
</html>
