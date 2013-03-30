# Produces an instance of the authMachine.  Doesn't provide the class, rather it provides the instance.

define (require) ->
	cookies = require('cookies')

	require('persona')

	# maintains the authorization state of the user
	class AuthMachine
		identity: null
		
		constructor: ->
			# see AuthCookie.java for format of cookie
			loginCookie = cookies.get('login')
			if loginCookie?
				cookieParts = loginCookie.split(":")
				@identity = decodeURIComponent(cookieParts[1])	# note this is urlencoded
				
			navigator.id.watch
				loggedInEmail: @identity
				onlogin: @_onLogin
				onlogout: @_onLogout

		_onLogin: (assertion) =>
			$.post '/login/persona', { assertion: assertion }, (person) =>
				@identity = person.email
				@onLoginSuccess(person)

		_onLogout: =>
			cookies.delete('login')
			@identity = null
			@onLogoutSuccess()

		login: =>
			params = { siteName: 'Motomapia' }
			navigator.id.request(params)

		logout: =>
			navigator.id.logout()

		# replace this method to cause login to do something else
		onLoginSuccess: (person) =>
			# do nothing

		# replace this method to cause logout to do something else
		onLogoutSuccess: =>
			# do nothing
			
	return new AuthMachine()
