#!/bin/sh
# optimize one file in the war/js/app/page dir
# r.js is too damn sensitive to where it gets executed
# $1 should be a pathless file in the page dir
# shoudl be executed from inside the war dir

#r.js -o app.build.js name=app/page/`basename $1 .js`
#r.js -o name=app/page/`basename $1 .js` out=../target/staging/js/app/page/$1 baseUrl=js

r.js -o name=app/page/`basename $1 .js` out=../target/staging/js/app/page/$1 mainConfigFile=config.require.js

