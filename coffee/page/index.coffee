	
require ['app/MotoMap', 'app/authMachine'], (MotoMap, authMachine) ->
	
	$ ->
		motoMap = new MotoMap("map")
		
		$('#download').click ->
			download(motoMap.map.getBounds())
		
		loginButton = $('#loginButton')
		logoutButton = $('#logoutButton')
		identity = $('#identity')
		
		loginButton.click(authMachine.login)
		logoutButton.click(authMachine.logout)
		
		checkAuth = ->
			if authMachine.identity?
				loginButton.hide()
				logoutButton.show()
				identity.text(authMachine.identity)
			else
				loginButton.show()
				logoutButton.hide()
				identity.text('')
	
		authMachine.onLoginSuccess = checkAuth
		authMachine.onLogoutSuccess = checkAuth
		
		checkAuth()
